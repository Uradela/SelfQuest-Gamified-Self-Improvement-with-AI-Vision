package com.dutaram.selfquest

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dutaram.selfquest.cam.CameraDetectorActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var tvGreeting: TextView
    private lateinit var tvWorkoutPlan: TextView
    private lateinit var btnRequestCamera: Button
    private lateinit var profileImage: ImageView

    companion object {
        const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
        const val PREF_KEY_IS_LOGGED_IN = "is_logged_in"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)


        if (isLoggedIn()) {
            setupUI()
        } else {
            navigateToSlideActivity()
        }
    }

    private fun isLoggedIn(): Boolean {

        return sharedPreferences.getBoolean(PREF_KEY_IS_LOGGED_IN, false)
    }

    private fun setupUI() {
        tvGreeting = findViewById(R.id.tvGreeting)
        tvWorkoutPlan = findViewById(R.id.tvWorkoutPlan)
        btnRequestCamera = findViewById(R.id.btnRequestCamera)
        profileImage = findViewById(R.id.profile_image)


        btnRequestCamera.setOnClickListener {
            val intent = Intent(this, CameraDetectorActivity::class.java)
            startActivity(intent)
        }

        profileImage.setOnClickListener {
            navigateToAccountSettings()
        }
    }

    private fun navigateToAccountSettings() {
        val intent = Intent(this, Profileact::class.java)
        startActivity(intent)
    }

    private fun navigateToSlideActivity() {
        val intent = Intent(this, Slideact::class.java)
        startActivity(intent)
        finish()
    }
}
