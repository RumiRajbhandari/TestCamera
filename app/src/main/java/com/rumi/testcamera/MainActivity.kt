package com.rumi.testcamera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.SensorManager
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imgPath = data?.getStringExtra("imgPath")
            loadImage(img, imgPath?:"")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_camera.setOnClickListener {
            val intent = CameraActivity.getIntent(this)
            resultLauncher.launch(intent)
        }
    }
}