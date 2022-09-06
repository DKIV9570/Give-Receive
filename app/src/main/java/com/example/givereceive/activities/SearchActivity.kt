package com.example.givereceive.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.example.givereceive.R
import com.example.givereceive.firebase.FirestoreClass
import com.example.givereceive.models.Post
import com.example.givereceive.utils.Constants
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        btn_search_give.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().searchPostByGive(this,et_search_give_list.text.toString())
        }

        btn_search_receive.setOnClickListener {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().searchPostByReceive(this,et_search_receive_list.text.toString())
        }

        btn_search_go_back.setOnClickListener{
            onBackPressed()
        }
    }


    fun SearchSuccessfully(postList:ArrayList<Post>){
        hideProgressDialog()
        val intent: Intent = Intent()
        intent.putExtra(Constants.SEARCH_RESULT,postList)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }


}