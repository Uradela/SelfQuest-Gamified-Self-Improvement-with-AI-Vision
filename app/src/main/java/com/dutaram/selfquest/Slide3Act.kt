package com.dutaram.selfquest

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Slide3Act : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.userdata3)

        db = FirebaseFirestore.getInstance()

        val weightPicker = findViewById<NumberPicker>(R.id.agePicker)
        val nextButton3 = findViewById<Button>(R.id.nextButton2)

        weightPicker.minValue = 30
        weightPicker.maxValue = 200
        weightPicker.value = 70

        nextButton3.setOnClickListener {
            val selectedWeight = weightPicker.value
            saveWeightToFirebase(selectedWeight) { success ->
                if (success) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("WEIGHT", selectedWeight)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Failed to save weight", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveWeightToFirebase(weight: Int, callback: (Boolean) -> Unit) {
        val userId = db.collection("users").document().id
        val userRef = db.collection("users").document(userId)
        userRef.set(hashMapOf(
            "weight" to weight
        ))
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener { e ->
                callback(false)
                e.printStackTrace()
            }
    }
}
