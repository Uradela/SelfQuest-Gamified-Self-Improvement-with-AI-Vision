package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class Profileact : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var profileNameTextView: TextView
    private lateinit var profileEmailTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val profileImageView: ImageView = findViewById(R.id.profile_image)
        profileNameTextView = findViewById(R.id.profile_name)
        profileEmailTextView = findViewById(R.id.profile_email)
        val accountSettingButton: Button = findViewById(R.id.account_setting_button)
        val logoutButton: Button = findViewById(R.id.logout_button)

        val currentUser: FirebaseUser? = auth.currentUser
        currentUser?.let {
            val userId = it.uid

            // Mengambil data dari Firestore
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userName = document.getString("name")
                        val userEmail = document.getString("email")

                        profileNameTextView.text = userName ?: "Nama Pengguna"
                        profileEmailTextView.text = userEmail ?: "email@pengguna.com"
                    } else {

                        profileNameTextView.text = "Nama Pengguna"
                        profileEmailTextView.text = "email@pengguna.com"
                    }
                }
                .addOnFailureListener { exception ->

                    profileNameTextView.text = "Nama Pengguna"
                    profileEmailTextView.text = "email@pengguna.com"
                }
        }

        accountSettingButton.setOnClickListener {
            val intent = Intent(this, Accsettingact::class.java)
            startActivity(intent)
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, Slideact::class.java)
            startActivity(intent)
            finish()
        }
    }
}
