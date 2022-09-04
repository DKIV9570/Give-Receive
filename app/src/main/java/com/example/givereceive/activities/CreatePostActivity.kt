package com.example.givereceive.activities

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.givereceive.R
import com.example.givereceive.firebase.FirestoreClass
import com.example.givereceive.models.Post
import com.example.givereceive.utils.Constants
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create_post.*
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

class CreatePostActivity : BaseActivity(), LocationListener {

    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserName: String
    private lateinit var mUserID: String

    private var mPostImageURL: String = ""

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private lateinit var mLocation: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        setupActionBar()

        if (intent.hasExtra(Constants.NAME)) {
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        if (intent.hasExtra(Constants.ID)) {
            mUserID = intent.getStringExtra(Constants.ID).toString()
        }

        iv_post_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        btn_create.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadPostImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                createPost()
            }
        }

        btn_get_location.setOnClickListener {
            getLocation()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPost() {

        val formatter = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())

        var post = Post(
            UUID.randomUUID().toString(),
            mUserID,
            et_post_title.text.toString(),
            mPostImageURL,
            mUserName,
            et_post_content.text.toString(),
            et_give_list.text?.split(",") as ArrayList<String>,
            et_receive_list.text?.split(",") as ArrayList<String>,
            formatter,
            mLocation
        )

        FirestoreClass().createPost(this, post)
    }

    private fun getLocation() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                locationPermissionCode
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault());

        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        val address: String = addresses[0]
            .getAddressLine(0)
        val city: String = addresses[0].locality
        val state: String = addresses[0].adminArea
        val country: String = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val knownName = addresses[0].featureName

        mLocation = address
        Toast.makeText(this, mLocation, Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadPostImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            "POST_IMAGE" + System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this, mSelectedImageFileUri)
        )
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener { taskSnapshot ->
            Log.i(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener { uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mPostImageURL = uri.toString()

                createPost()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(
                this,
                exception.message,
                Toast.LENGTH_LONG
            ).show()

            hideProgressDialog()
        }
    }

    fun postCreatedSuccessfully() {
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar_create_post_activity)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_Post_title)
        }

        toolbar_create_post_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null
        ) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_post_place_holder)
                    .into(iv_post_image)
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}