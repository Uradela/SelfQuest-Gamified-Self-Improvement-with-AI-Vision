package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class SignInClass : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        val signInButton = findViewById<Button>(R.id.button)
        val signUpTextView = findViewById<TextView>(R.id.textView6)

        signInButton.setOnClickListener {
            val intent = Intent(this@SignInClass, SignUpActivity::class.java)
            startActivity(intent)
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this@SignInClass, Createacc::class.java)
            startActivity(intent)
        }
    }
}