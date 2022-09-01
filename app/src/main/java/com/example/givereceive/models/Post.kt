package com.example.givereceive.models

import android.os.Parcel
import android.os.Parcelable

data class Post (
    var postId: String = "",
    var userId: String = "",
    val title: String = "",
    val image: String = "",
    val createdBy: String = "",
    val content: String = "",
    val giveList: ArrayList<String> = ArrayList(),
    val receiveList: ArrayList<String> = ArrayList()
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)  = with(parcel){
        parcel.writeString(postId)
        parcel.writeString(userId)
        parcel.writeString(title)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeString(content)
        writeStringList(giveList)
        writeStringList(receiveList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }

}