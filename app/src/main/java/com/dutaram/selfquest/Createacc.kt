package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Createacc : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var usernameEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_createacc)


        firestore = FirebaseFirestore.getInstance()


        emailEditText = findViewById(R.id.emailsingup)
        passwordEditText = findViewById(R.id.pwsingup)
        usernameEditText = findViewById(R.id.username)
        val singupButton: Button = findViewById(R.id.singup_button)
        val loginTextView: TextView = findViewById(R.id.login)
        val backwardButton: ImageView = findViewById(R.id.imageView6)


        singupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim() // Mendapatkan nilai username dari EditText

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                registerUser(email, password, username)
            } else {
                if (email.isEmpty()) {
                    emailEditText.error = "Email tidak boleh kosong"
                }
                if (password.isEmpty()) {
                    passwordEditText.error = "Password tidak boleh kosong"
                }
                if (username.isEmpty()) {
                    usernameEditText.error = "Username tidak boleh kosong"
                }
            }
        }


        loginTextView.setOnClickListener {
            val intent = Intent(this@Createacc, SignUpActivity::class.java)
            startActivity(intent)
        }


        backwardButton.setOnClickListener {
            finish()
        }
    }

    private fun registerUser(email: String, password: String, username: String) {
        firestore.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    createUserInFirestore(email, password, username)
                } else {
                    // Email sudah terdaftar
                    emailEditText.error = "Email sudah digunakan"
                }
            }
            .addOnFailureListener { e ->
                // Error saat mencari email di Firestore
                Log.e(TAG, "Error searching email in Firestore", e)
                emailEditText.error = "Error: ${e.message}"
            }
    }

    private fun createUserInFirestore(email: String, password: String, username: String) {
        // Create a new user document in "users" collection
        firestore.collection("users")
            .add(mapOf(
                "email" to email,
                "username" to username,
                "password" to password,
                "fullName" to "",
                "gender" to "",
                "dateOfBirth" to "",
                "address" to "",
                "cities" to "",
                "weight" to "",
                "height" to "",
                "age" to ""
            ))
            .addOnSuccessListener { documentReference ->
                // Registration successful, proceed to next activity
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                val intent = Intent(this@Createacc, Slide1Act::class.java)
                startActivity(intent)
                finish()
            }
            .addOnFailureListener { e ->
                // Failed to add data to Firestore
                Log.e(TAG, "Error adding document to Firestore", e)
                emailEditText.error = "Registrasi gagal: ${e.message}"
                passwordEditText.error = "Registrasi gagal: ${e.message}"
            }
    }

    companion object {
        private const val TAG = "Createacc"
    }
}
