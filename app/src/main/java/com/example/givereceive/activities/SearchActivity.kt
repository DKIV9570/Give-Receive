package com.example.givereceive.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.givereceive.R
import kotlinx.android.synthetic.main.activity_create_post.*
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setupActionBar()
    }

    private fun setupActionBar(){
        setSupportActionBar(toolbar_search_activity)
        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
            actionBar.title = resources.getString(R.string.search)
        }

        toolbar_search_activity.setNavigationOnClickListener { onBackPressed() }
    }


}