package com.example.givereceive.activities

import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.example.givereceive.R
import com.example.givereceive.firebase.FirestoreClass
import com.example.givereceive.models.Post
import com.example.givereceive.utils.Constants
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.activity_post_detail.*
import kotlinx.android.synthetic.main.item_post.view.*
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class PostDetailActivity : BaseActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun postDetails(post:Post){
        hideProgressDialog()

        tv_postdetail_title.text = post.title
        tv_postdetail_author.text = "Author : ${post.createdBy}"
        Glide
            .with(this)
            .load(post.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(iv_postdetail_image)

        tv_postdetail_detail.text = post.content


        for(item in post.giveList){
            val tag = Chip(this)
            tag.text = item
            ll_postdetail_give_list.addView(tag)
        }

        for(item in post.receiveList){
            val tag = Chip(this)
            tag.text = item
            ll_postdetail_receive_list.addView(tag)
        }

        btn_postdetail_share.visibility = View.GONE
        btn_postdetail_go_back.setOnClickListener{ onBackPressed() }

        tv_postdetail_time.text = post.postTime


    }
}