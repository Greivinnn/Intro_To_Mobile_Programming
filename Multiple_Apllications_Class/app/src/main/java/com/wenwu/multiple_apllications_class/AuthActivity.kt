package com.wenwu.multiple_apllications_class

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.auth_layout)

        val cityNameTextView = findViewById<TextView>(R.id.cityText_id)

        val logOutButton = findViewById<Button>(R.id.logOutButton_id)

        cityNameTextView.text = intent.getStringExtra("city")
        logOutButton.setOnClickListener {



            finish();
        }
    }
}