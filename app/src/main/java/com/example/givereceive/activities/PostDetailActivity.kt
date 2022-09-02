package com.example.givereceive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.givereceive.R
import com.example.givereceive.firebase.FirestoreClass
import com.example.givereceive.models.Post
import com.example.givereceive.utils.Constants
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.item_post.view.*

class PostDetailActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        var postId = ""
        if(intent.hasExtra(Constants.POST_ID)){
            postId = intent.getStringExtra(Constants.POST_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getPostDetail(this, postId)

    }

    private fun setupActionBar(title: String){
        setSupportActionBar(toolbar_post_detail_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = title
        }

        toolbar_post_detail_activity.setNavigationOnClickListener { onBackPressed() }
    }

    fun postDetails(post:Post){
        hideProgressDialog()
        setupActionBar(post.title)
        Glide
            .with(this)
            .load(post.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_postdetail_image)

        tv_postdetail_detail.text = post.content
        for(item in post.giveList){
            val tag = TextView(this)
            tag.text = item
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams.setMargins(10, 10, 10, 10)
            tag.setPadding(2,2,2,2)
            tag.setLayoutParams(layoutParams)
            tag.setBackgroundResource(R.drawable.give_tag)
            ll_postdetail_give_list.addView(tag)
        }

        for(item in post.receiveList){
            val tag = TextView(this)
            tag.text = item
            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutParams.setMargins(10, 10, 10, 10)
            tag.setLayoutParams(layoutParams)
            tag.setPadding(2,2,2,2)
            tag.setBackgroundResource(R.drawable.receive_tag)
            ll_postdetail_receive_list.addView(tag)
        }
    }
}