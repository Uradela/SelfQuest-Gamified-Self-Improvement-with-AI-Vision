package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator

class Slideact : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slide)

        db = FirebaseFirestore.getInstance()

        // Check if user is logged in
        if (isLoggedIn()) {
            Log.d("Slideact", "User is logged in, redirecting to MainActivity")
            navigateToMainActivity()
        } else {
            setupSlideActivity()
        }
    }

    private fun setupSlideActivity() {
        val viewPager = findViewById<ViewPager2>(R.id.view_pager)
        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.dots_indicator)
        val slideAdapter = Slideadapter(this)

        viewPager.adapter = slideAdapter
        dotsIndicator.setViewPager2(viewPager)
    }

    private fun isLoggedIn(): Boolean {
        val user = FirebaseAuth.getInstance().currentUser
        return user != null && user.isEmailVerified
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
