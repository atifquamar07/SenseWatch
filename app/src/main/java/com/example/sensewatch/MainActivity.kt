package com.example.sensewatch

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    private lateinit var btnRotation: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnRotation = findViewById(R.id.btn_rotation)
        btnRotation.setOnClickListener {
            val intent = Intent(this, GeomagneticRotation::class.java)
            startActivity(intent)
        }
    }
}