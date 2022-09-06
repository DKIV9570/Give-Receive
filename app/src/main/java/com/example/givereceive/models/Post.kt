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
    val giveList: List<String> = ArrayList(),
    val receiveList: List<String> = ArrayList(),
    val postTime: String = "",
    val postAddress: String = "",
    val postLatitude: Double = 0.0,
    val postLongitude: Double = 0.0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble()
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
        parcel.writeString(postTime)
        parcel.writeString(postAddress)
        parcel.writeDouble(postLatitude)
        parcel.writeDouble(postLongitude)
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