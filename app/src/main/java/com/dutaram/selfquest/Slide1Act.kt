package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Slide1Act : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private var currentUserUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userdata1)

        db = FirebaseFirestore.getInstance()

        val maleIcon = findViewById<ImageView>(R.id.maleIcon)
        val femaleIcon = findViewById<ImageView>(R.id.femaleIcon)
        val nextButton1 = findViewById<Button>(R.id.nextButton1)

        var selectedGender: String? = null

        maleIcon.setOnClickListener {
            selectedGender = "Male"
            saveGenderAndMoveToNext(selectedGender!!)
        }

        femaleIcon.setOnClickListener {
            selectedGender = "Female"
            saveGenderAndMoveToNext(selectedGender!!)
        }

        nextButton1.setOnClickListener {
            if (selectedGender != null) {
                saveGenderAndMoveToNext(selectedGender!!)
            } else {
                Toast.makeText(this, "Please select a gender", Toast.LENGTH_SHORT).show()
            }
        }


        currentUserUid = generateUserId()
    }

    private fun generateUserId(): String {
        return "user_${System.currentTimeMillis()}"
    }

    private fun saveGenderAndMoveToNext(gender: String) {
        currentUserUid?.let { uid ->
            val userRef = db.collection("users").document(uid)

            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        // Update existing document
                        userRef.update("gender", gender)
                            .addOnSuccessListener {
                                moveToNextScreen(gender)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save gender", Toast.LENGTH_SHORT).show()

                            }
                    } else {
                        val userData = hashMapOf(
                            "gender" to gender

                        )

                        userRef.set(userData)
                            .addOnSuccessListener {
                                moveToNextScreen(gender)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Failed to save gender", Toast.LENGTH_SHORT).show()

                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error checking user document", Toast.LENGTH_SHORT).show()
                }
        } ?: run {
            Toast.makeText(this, "Failed to get user ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun moveToNextScreen(gender: String) {
        val intent = Intent(this, Slide2Act::class.java)
        intent.putExtra("GENDER", gender)
        startActivity(intent)
        finish()
    }
}
