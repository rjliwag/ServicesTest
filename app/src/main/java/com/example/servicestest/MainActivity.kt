package com.example.servicestest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStartService: Button = findViewById(R.id.btnStartService)

        btnStartService.setOnClickListener {

            val serviceIntent = Intent(this, TimerService::class.java)
            serviceIntent.putExtra("startTime", 30000)
            startService(serviceIntent)
        }
    }
}