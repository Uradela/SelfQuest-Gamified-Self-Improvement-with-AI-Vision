package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Slide2Act : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userdata2)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        val agePicker = findViewById<NumberPicker>(R.id.agePicker)
        val nextButton2 = findViewById<Button>(R.id.nextButton2)

        agePicker.minValue = 1
        agePicker.maxValue = 100
        agePicker.value = 25

        nextButton2.setOnClickListener {
            val selectedAge = agePicker.value
            saveAgeToFirebase(selectedAge) { success ->
                if (success) {

                    val intent = Intent(this, Slide3Act::class.java)
                    intent.putExtra("AGE", selectedAge)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save age", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveAgeToFirebase(age: Int, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = db.collection("users").document(userId)
            userRef.set(hashMapOf(
                "age" to age
            ))
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener { e ->
                    callback(false)
                    e.printStackTrace()
                }
        } else {
            callback(false)
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
