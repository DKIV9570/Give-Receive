package com.example.givereceive.activities

import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.givereceive.R
import com.example.givereceive.adapters.PostItemsAdapter
import com.example.givereceive.firebase.FirestoreClass
import com.example.givereceive.models.Post
import com.example.givereceive.models.User
import com.example.givereceive.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_POST_REQUEST_CODE: Int = 12
    }

    private lateinit var mUserName: String
    private lateinit var mUserID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupActionBar()
        nav_view.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this,true)

        fab_create_post.setOnClickListener{
            val intent = Intent(this,CreatePostActivity::class.java)
            intent.putExtra(Constants.NAME,mUserName)
            intent.putExtra(Constants.ID,mUserID)
            startActivityForResult(intent, CREATE_POST_REQUEST_CODE)
        }

        fab_search_post.setOnClickListener{
            val intent = Intent(this,SearchActivity::class.java)
            startActivity(intent)
        }
    }


    fun populatePostsListToUI(postsList:ArrayList<Post>){
        hideProgressDialog()

        if(postsList.size > 0){
            rv_posts_list.visibility = View.VISIBLE
            tv_no_posts_available.visibility = View.GONE

            rv_posts_list.layoutManager = LinearLayoutManager(this)
            rv_posts_list.setHasFixedSize(true)

            val adapter = PostItemsAdapter(this, postsList)
            rv_posts_list.adapter = adapter

            adapter.setOnClickListener(object: PostItemsAdapter.OnClickListener{
                override fun onClick(position: Int, model: Post) {
                    val intent: Intent = Intent(this@MainActivity,PostDetailActivity::class.java)
                    intent.putExtra(Constants.POST_ID, model.postId)
                    startActivity(intent)
                }
            })

        }else{
            rv_posts_list.visibility = View.GONE
            tv_no_posts_available.visibility = View.VISIBLE
        }
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_main_activity)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_action_navigation_menu)
        toolbar_main_activity.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer(){
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }

    override fun onBackPressed() {
        if(drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }else{
            doubleBackToExit()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == MY_PROFILE_REQUEST_CODE){
            FirestoreClass().loadUserData(this)
        }else if(resultCode == Activity.RESULT_OK
            && requestCode == CREATE_POST_REQUEST_CODE){
            FirestoreClass().getPostsList(this)
        }else{
            Log.e("cancalled","Cancalled")
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.nav_my_profile -> {
                val intent = Intent(this, MyProfileActivity::class.java)
                startActivityForResult(intent, MY_PROFILE_REQUEST_CODE)
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true

    }

    fun updateNavigationUserDetails(user: User, readPostsList: Boolean) {

        mUserName = user.name
        mUserID = user.id

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_user_image)

        tv_username.text = user.name

        if(readPostsList){
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getPostsList(this)
        }
    }


}