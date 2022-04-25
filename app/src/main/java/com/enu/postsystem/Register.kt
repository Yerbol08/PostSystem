package com.enu.postsystem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Register : AppCompatActivity() {
    var userPhoto: ImageView? = null
    var PreReqCode = 1
    var REQUESCODE = 1
    var pickedImgUri: Uri? = null

    private val TAG: String? = null
    private lateinit var userEmail: TextInputEditText
    private lateinit var userPassword:TextInputEditText
    private lateinit var userPAssword2:TextInputEditText
    private lateinit var userName:TextInputEditText
    private lateinit var loadingProgress: ProgressBar
    private lateinit var regBtn: Button
    private lateinit var signBtn:android.widget.Button
    var fStore: FirebaseFirestore? = null
    private var mAuth: FirebaseAuth? = null
    var userID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        userEmail = findViewById(R.id.regMail)
        userPassword = findViewById<TextInputEditText>(R.id.regPassword)
        userPAssword2 = findViewById<TextInputEditText>(R.id.regPassword2)
        userName = findViewById<TextInputEditText>(R.id.regName)
        loadingProgress = findViewById(R.id.regProgressBar)
        regBtn = findViewById(R.id.regBtn)
        loadingProgress.setVisibility(View.INVISIBLE)
        mAuth = FirebaseAuth.getInstance()
        fStore = FirebaseFirestore.getInstance()
        regBtn.setOnClickListener(View.OnClickListener {
            regBtn.setVisibility(View.INVISIBLE)
            loadingProgress.setVisibility(View.VISIBLE)
            val email = userEmail.getText().toString()
            val password: String = userPassword.getText().toString()
            val password2: String = userPAssword2.getText().toString()
            val name: String = userName.getText().toString()
            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || password != password2) {
                showMessage("Please Verify all fields")
                regBtn.setVisibility(View.VISIBLE)
                loadingProgress.setVisibility(View.INVISIBLE)
            } else {
                CreateUserAccount(email, name, password)
            }
        })

    }

    private fun CreateUserAccount(email: String, name: String, password: String) {
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    showMessage("Account created")
                    userID = mAuth!!.currentUser!!.uid
                    val documentReference = fStore!!.collection("user_profile").document(
                        userID!!
                    )
                    val user: MutableMap<String, Any> =
                        HashMap()
                    user["Name"] = name
                    user["Email"] = email
                    user["Password"] = password
                    documentReference.set(user).addOnSuccessListener {
                        Log.d(
                            TAG,
                            "onSuccess: user Profile is created for $userID"
                        )
                    }.addOnFailureListener { e -> Log.d(TAG, "onFailure: $e") }
                    if (pickedImgUri != null) {
                        updateUserInfo(name, pickedImgUri!!, mAuth!!.currentUser)
                    } else {
                        updateUserInfoWithoutPhoto(name, mAuth!!.currentUser)
                    }
                }
                else {

                    showMessage("account creation failed" + task.exception!!.message)
                    regBtn!!.visibility = View.VISIBLE
                    loadingProgress!!.visibility = View.INVISIBLE
                }
            }
    }

    private fun updateUserInfo(name: String, pickedImgUri: Uri, currentUser: FirebaseUser?) {
        val mStorage: StorageReference =
            FirebaseStorage.getInstance().getReference().child("users_photos")

    }

    private fun updateUserInfoWithoutPhoto(name: String, currentUser: FirebaseUser?) {
        // first we need to upload user photo to firebase storage and get url
        val profleUpdate = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        currentUser!!.updateProfile(profleUpdate)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // user info updated successfully
                    showMessage("Register Complete")
                    updateUI()
                }
            }
    }

    private fun updateUI() {
        val homeActivity = Intent(applicationContext, MainActivity::class.java)
        startActivity(homeActivity)
        finish()
    }

    fun clickSingIn(view: View?) {
        startActivity(Intent(this@Register, Login::class.java))
    }

    private fun showMessage(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    private fun openGalery() {
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, REQUESCODE)
    }

    private fun checkAndReqForPermission() {
        if (ContextCompat.checkSelfPermission(
                this@Register,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@Register,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Toast.makeText(
                    this@Register,
                    "Please accept for required permission",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                ActivityCompat.requestPermissions(
                    this@Register, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PreReqCode
                )
            }
        } else {
            openGalery()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUESCODE && data != null) {

            // the user has successfully picked an image
            // we need to save its reference to a Uri variable
            pickedImgUri = data.data
            userPhoto!!.setImageURI(pickedImgUri)
        }
    }
}