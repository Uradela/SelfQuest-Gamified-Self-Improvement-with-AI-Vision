package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class SignUpActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var createAccountTextView: TextView

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

        firestore = FirebaseFirestore.getInstance()

        usernameEditText = findViewById(R.id.username)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.login_button)
        createAccountTextView = findViewById(R.id.create_account)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this@SignUpActivity, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = HashMap<String, Any>()
            user["username"] = username
            user["password"] = password

            firestore.collection("users")
                .add(user)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(this@SignUpActivity, "User registered successfully", Toast.LENGTH_SHORT).show()

                    saveUserData(username, password)

                    startActivity(Intent(this@SignUpActivity, MainActivity::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this@SignUpActivity, "Error registering user", Toast.LENGTH_SHORT).show()
                }
        }

        createAccountTextView.setOnClickListener {
            startActivity(Intent(this@SignUpActivity, Createacc::class.java))
        }
    }

    private fun saveUserData(username: String, password: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("password", password)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }
}
