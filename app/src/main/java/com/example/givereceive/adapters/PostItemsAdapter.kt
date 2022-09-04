package com.example.givereceive.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.givereceive.R
import com.example.givereceive.models.Post
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import kotlinx.android.synthetic.main.activity_my_profile.*
import kotlinx.android.synthetic.main.item_post.view.*


open class PostItemsAdapter(private val context: Context,
                            private var list: ArrayList<Post>):
        RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context)
                .inflate(R.layout.item_post,parent,false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if(holder is MyViewHolder){
            Glide
                .with(context)
                .load(model.image)
                .centerCrop()
                .placeholder(R.drawable.ic_post_place_holder)
                .into(holder.itemView.iv_post_image)

            holder.itemView.tv_name.text = model.title

            //remove all the tags before add new one to avoid repeat
            holder.itemView.ll_give_list.removeAllViews()

            for(item in model.giveList){
                val tag = Chip(holder.itemView.context)
                tag.text = item
                tag.setBackgroundResource(R.drawable.give_tag)
                holder.itemView.ll_give_list.addView(tag)
            }

            holder.itemView.ll_receive_list.removeAllViews()
            for(item in model.receiveList){
                val tag = Chip(holder.itemView.context)
                tag.text = item
                tag.setBackgroundResource(R.drawable.receive_tag)
                holder.itemView.ll_receive_list.addView(tag)
            }

            holder.itemView.setOnClickListener{
                if(onClickListener != null){
                    onClickListener!!.onClick(position,model)
                }
            }
        }
    }

    interface OnClickListener{
        fun onClick(position: Int,model:Post)
    }

    fun setOnClickListener(onClickListener:OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class MyViewHolder(view: View): RecyclerView.ViewHolder(view)

}