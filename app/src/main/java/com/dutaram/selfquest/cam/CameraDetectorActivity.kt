package com.dutaram.selfquest.cam

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.dutaram.selfquest.MainActivity
import com.dutaram.selfquest.R

class CameraDetectorActivity : AppCompatActivity() {

    private val REQUEST_CODE_PERMISSION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_detector)

        if (ContextCompat.checkSelfPermission(this, MainActivity.REQUIRED_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(MainActivity.REQUIRED_PERMISSION), REQUEST_CODE_PERMISSION)
        }
    }

    private fun openCamera() {
        // Logic to open camera
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with camera operation
                openCamera()
            } else {

            }
        }
    }
}
