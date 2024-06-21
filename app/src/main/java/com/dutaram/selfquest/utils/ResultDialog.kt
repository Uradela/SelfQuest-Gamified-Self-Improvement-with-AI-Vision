package com.dutaram.selfquest.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import com.dutaram.selfquest.R
import java.io.File

class ResultDialog(context: Context, private val imageFile: File) : Dialog(context) {

    private lateinit var previewImage: ImageView
    private lateinit var tvHasil: TextView
    private lateinit var tvProbab: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_image_preview)

        window?.setBackgroundDrawableResource(android.R.color.transparent)

        previewImage = findViewById(R.id.previewImage)
        tvHasil = findViewById(R.id.tvHasil)
        tvProbab = findViewById(R.id.tvProbability)

        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
        previewImage.setImageBitmap(bitmap)
    }
}