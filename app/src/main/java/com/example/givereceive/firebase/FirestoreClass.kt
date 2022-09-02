package com.example.givereceive.firebase

import android.app.Activity
import android.util.Log
import com.example.givereceive.activities.*
import com.example.givereceive.models.Post
import com.example.givereceive.models.User
import com.example.givereceive.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity,userInfo: User){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener {
                e ->
                Log.e(activity.javaClass.simpleName,"Error writing documents")
            }
    }

    fun getPostDetail(activity: PostDetailActivity, postId: String){

        mFireStore.collection(Constants.POSTS)
            .document(postId)
            .get()
            .addOnSuccessListener {
                    document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //pass post to detail
                activity.postDetails(document.toObject(Post::class.java)!!)

            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error when loading post detail")
            }
    }

    fun createPost(activity: CreatePostActivity, post:Post){
        mFireStore.collection(Constants.POSTS)
            .document()
            .set(post, SetOptions.merge())
            .addOnSuccessListener {
                activity.postCreatedSuccessfully()
            }.addOnFailureListener {
                exception->
                activity.hideProgressDialog()
            }
    }

    fun getPostsList(activity: MainActivity){
        //TODO also search feature by whereArrayContains
        mFireStore.collection(Constants.POSTS)
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val postList:ArrayList<Post> = ArrayList()
                for(i in document.documents){
                    val post = i.toObject(Post::class.java)
                    post!!.postId = i.id
                    postList.add(post)
                }

                activity.populatePostsListToUI(postList)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String,Any>){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName,"Profile updated successfully")
                activity.profileUpdateSuccess()
            }.addOnFailureListener {
                e->
                activity.hideProgressDialog()
            }
    }

    fun loadUserData(activity: Activity, readPostsList:Boolean = false){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when(activity){
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readPostsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }


            }.addOnFailureListener {
                e ->
                Log.e(activity.javaClass.simpleName,"Error reading documents")
        }
    }

    fun getCurrentUserId(): String{
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if(currentUser != null){
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


}