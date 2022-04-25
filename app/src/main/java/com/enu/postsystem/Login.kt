package com.enu.postsystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Login : AppCompatActivity() {
    private lateinit var userMail: EditText
    private lateinit var userPassword:EditText
    private lateinit var btnLogin: Button
    private lateinit var loginProgress: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private var HomeActivity: Intent? = null
    private lateinit var logToRegBtn: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        logToRegBtn = findViewById(R.id.loginToReg)
        logToRegBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        })
        userMail = findViewById(R.id.login_mail)
        userPassword = findViewById<EditText>(R.id.login_password)
        btnLogin = findViewById(R.id.loginnBtn)
        loginProgress = findViewById(R.id.login_progress)
        mAuth = FirebaseAuth.getInstance()
        HomeActivity = Intent(this, MainActivity::class.java)
        loginProgress.setVisibility(View.INVISIBLE)
        btnLogin.setOnClickListener(View.OnClickListener {
            loginProgress.setVisibility(View.VISIBLE)
            btnLogin.setVisibility(View.INVISIBLE)
            val mail = userMail.getText().toString()
            val password: String = userPassword.getText().toString()
            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Please Verify All Field")
                btnLogin.setVisibility(View.VISIBLE)
                loginProgress.setVisibility(View.INVISIBLE)
            } else {
                signIn(mail, password)
            }
        })
    }

    private fun signIn(mail: String, password: String) {
        mAuth!!.signInWithEmailAndPassword(mail, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginProgress!!.visibility = View.INVISIBLE
                btnLogin!!.visibility = View.VISIBLE
                updateUI()
            } else {
                showMessage(task.exception!!.message)
                btnLogin!!.visibility = View.VISIBLE
                loginProgress!!.visibility = View.INVISIBLE
            }
        }
    }

    private fun updateUI() {
        startActivity(HomeActivity)
        finish()
    }

    private fun showMessage(text: String?) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth!!.currentUser
        if (user != null) {
            //user is already connected  so we need to redirect him to home page
            updateUI()
        }
    }
}