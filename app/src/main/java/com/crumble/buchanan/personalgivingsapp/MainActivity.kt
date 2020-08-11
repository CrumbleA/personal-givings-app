package com.crumble.buchanan.personalgivingsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            goToGivings(currentUser, currentUser.email)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = Firebase.auth

        submitButton.setOnClickListener {
            val fwoNumberString = fwoEditTextNumber.text.toString().padStart(3, '0')

            if (fwoEditTextNumber.text.toString().isEmpty() || pinEditTextPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Sign in failed. Try again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(
                "${fwoNumberString}@example.com",
                pinEditTextPassword.text.toString()
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("firebase sign in", "signInWithEmail:success")
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        goToGivings(user, user.email)
                    }
                } else {
                    Log.w("firebase sign in", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, "Sign in failed. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToGivings(user: FirebaseUser, email: String?) {
        val intent = Intent(this, MainGivingsActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
    }

    private fun showIncorrectCredentialsError() {
        Toast.makeText(
            this,
            getString(R.string.incorrect_credentials_error_text),
            Toast.LENGTH_SHORT
        ).show()
    }
}