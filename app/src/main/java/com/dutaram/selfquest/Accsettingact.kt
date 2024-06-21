package com.dutaram.selfquest

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Accsettingact : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var ageTextView: TextView
    private lateinit var genderTextView: TextView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2

    private lateinit var firestore: FirebaseFirestore
    private lateinit var currentUserUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accsett)

        firestore = FirebaseFirestore.getInstance()
        currentUserUid = generateUserId()

        profileImageView = findViewById(R.id.profile_image_edit)
        usernameTextView = findViewById(R.id.username_text_view)
        emailTextView = findViewById(R.id.email_text_view)
        ageTextView = findViewById(R.id.age_text_view)
        genderTextView = findViewById(R.id.gender_text_view)

        val editProfileImageButton: Button = findViewById(R.id.edit_profile_image_button)
        val saveProfileButton: Button = findViewById(R.id.save_profile_button)

        loadUserData()

        editProfileImageButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        saveProfileButton.setOnClickListener {
            saveProfileChanges()
            finish()
        }

        setupEditListeners()
    }

    private fun generateUserId(): String {
        return "user_${System.currentTimeMillis()}"
    }

    private fun loadUserData() {
        firestore.collection("users").document(currentUserUid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val user = document.toObject(User::class.java)
                    user?.let {
                        usernameTextView.text = it.username
                        emailTextView.text = it.email
                        ageTextView.text = it.age.toString()
                        genderTextView.text = it.gender
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "loadUserData:failed", e)
            }
    }

    private fun setupEditListeners() {
        usernameTextView.setOnClickListener {
            showEditDialog("Username", usernameTextView) { newValue ->
                usernameTextView.text = newValue
                saveProfileChanges()
            }
        }

        emailTextView.setOnClickListener {
            showEditDialog("Email", emailTextView) { newValue ->
                emailTextView.text = newValue
                saveProfileChanges()
            }
        }

        ageTextView.setOnClickListener {
            showEditDialog("Age", ageTextView) { newValue ->
                ageTextView.text = newValue
                saveProfileChanges()
            }
        }

        genderTextView.setOnClickListener {
            showEditDialog("Gender", genderTextView) { newValue ->
                genderTextView.text = newValue
                saveProfileChanges()
            }
        }
    }

    private fun showEditDialog(title: String, textView: TextView, onConfirm: (String) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit $title")

        val input = EditText(this)
        input.setText(textView.text)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val newValue = input.text.toString()
            onConfirm(newValue)
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel()
        }

        builder.show()
    }

    private fun dispatchTakePictureIntent() {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")

        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                        takePictureIntent.resolveActivity(packageManager)?.also {
                            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                        }
                    }
                }
                options[item] == "Choose from Gallery" -> {
                    val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhotoIntent, REQUEST_IMAGE_GALLERY)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    profileImageView.setImageBitmap(imageBitmap)
                }
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImageUri = data?.data
                    profileImageView.setImageURI(selectedImageUri)
                }
            }
        }
    }

    private fun saveProfileChanges() {
        val updatedUser = User(
            usernameTextView.text.toString(),
            emailTextView.text.toString(),
            ageTextView.text.toString().toInt(),
            genderTextView.text.toString()
        )

        firestore.collection("users").document(currentUserUid)
            .set(updatedUser)
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "saveProfileChanges:failed", e)
            }
    }

    companion object {
        private const val TAG = "Accsettingact"
    }
}
