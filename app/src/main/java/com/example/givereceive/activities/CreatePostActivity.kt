package com.example.givereceive.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
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
import kotlinx.android.synthetic.main.activity_my_profile.*
import java.io.IOException
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class CreatePostActivity : BaseActivity() {

    private var mSelectedImageFileUri: Uri? = null

    private lateinit var mUserName: String
    private lateinit var mUserID: String

    private var mPostImageURL: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)
        setupActionBar()

        if(intent.hasExtra(Constants.NAME)){
            mUserName = intent.getStringExtra(Constants.NAME).toString()
        }
        if (intent.hasExtra(Constants.ID)){
            mUserID = intent.getStringExtra(Constants.ID).toString()
        }

        iv_post_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){
                Constants.showImageChooser(this)
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
            }
        }

        btn_create.setOnClickListener {
            if(mSelectedImageFileUri != null){
                uploadPostImage()
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                createPost()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createPost(){

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
            formatter
        )

        FirestoreClass().createPost(this,post)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun uploadPostImage(){
        showProgressDialog(resources.getString(R.string.please_wait))

        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            "POST_IMAGE"+ System.currentTimeMillis()
                    + "." + Constants.getFileExtension(this,mSelectedImageFileUri)
        )
        sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
            Log.i(
                "Firebase Image URL",
                taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
            )
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                Log.i("Downloadable Image URL", uri.toString())
                mPostImageURL = uri.toString()

                createPost()
            }
        }.addOnFailureListener{
                exception ->
            Toast.makeText(this,
                exception.message,
                Toast.LENGTH_LONG).show()

            hideProgressDialog()
        }
    }

    fun postCreatedSuccessfully(){
        hideProgressDialog()

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_create_post_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.create_Post_title)
        }

        toolbar_create_post_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_IMAGE_REQUEST_CODE &&
            data!!.data!= null){
            mSelectedImageFileUri = data.data

            try{
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_post_place_holder)
                    .into(iv_post_image)
            }catch (e: IOException){
                e.printStackTrace()
            }

        }
    }
}