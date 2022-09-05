package com.example.givereceive.firebase

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.givereceive.R
import com.example.givereceive.activities.*
import com.example.givereceive.models.Post
import com.example.givereceive.models.User
import com.example.givereceive.utils.Constants
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val mFireStore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error writing documents")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getPostDetail(activity: PostDetailActivity, postId: String) {

        mFireStore.collection(Constants.POSTS)
            .document(postId)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                //pass post to detail
                activity.postDetails(document.toObject(Post::class.java)!!)

            }.addOnFailureListener {
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error when loading post detail")
            }
    }

    fun createPost(activity: CreatePostActivity, post: Post) {
        mFireStore.collection(Constants.POSTS)
            .document()
            .set(post, SetOptions.merge())
            .addOnSuccessListener {
                activity.postCreatedSuccessfully()
            }.addOnFailureListener { exception ->
                activity.hideProgressDialog()
            }
    }

    fun getPostsList(activity: Activity) {
        mFireStore.collection(Constants.POSTS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val postList: ArrayList<Post> = ArrayList()
                for (i in document.documents) {
                    val post = i.toObject(Post::class.java)
                    post!!.postId = i.id
                    postList.add(post)
                }
                if (activity is MainActivity) {
                    activity.populatePostsListToUI(postList)
                }
            }
    }

    fun renderPostOnMap(activity: Activity, mMap: GoogleMap) {
        mFireStore.collection(Constants.POSTS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val postList: ArrayList<Post> = ArrayList()
                for (i in document.documents) {
                    val post = i.toObject(Post::class.java)
                    post!!.postId = i.id
                    postList.add(post)
                }
                if (activity is MapsActivity) {
                    for (item in postList) {
                        val pos = LatLng(item.postLatitude, item.postLongitude)
                        val marker = mMap.addMarker(MarkerOptions()
                            .position(pos)
                            .title(item.title))
//                            .icon(BitmapDescriptorFactory
//                                .fromResource(R.drawable.attbuh)))
                        marker!!.tag = item.postId
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10.0f))
                    }

                }
            }
    }

    fun searchPostByGive(activity: SearchActivity, give: String) {
        mFireStore.collection(Constants.POSTS)
            .whereArrayContains("giveList", give)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val postList: ArrayList<Post> = ArrayList()
                for (i in document.documents) {
                    val post = i.toObject(Post::class.java)
                    post!!.postId = i.id
                    postList.add(post)
                }
                activity.SearchSuccessfully(postList)
            }
    }

    fun searchPostByReceive(activity: SearchActivity, receive: String) {
        mFireStore.collection(Constants.POSTS)
            .whereArrayContains("receiveList", receive)
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val postList: ArrayList<Post> = ArrayList()
                for (i in document.documents) {
                    val post = i.toObject(Post::class.java)
                    post!!.postId = i.id
                    postList.add(post)
                }
                activity.SearchSuccessfully(postList)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile updated successfully")
                activity.profileUpdateSuccess()
            }.addOnFailureListener { e ->
                activity.hideProgressDialog()
            }
    }

    fun loadUserData(activity: Activity, readPostsList: Boolean = false) {
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
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


            }.addOnFailureListener { e ->
                Log.e(activity.javaClass.simpleName, "Error reading documents")
            }
    }

    fun getCurrentUserId(): String {
        var currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""
        if (currentUser != null) {
            currentUserID = currentUser.uid
        }
        return currentUserID
    }


}