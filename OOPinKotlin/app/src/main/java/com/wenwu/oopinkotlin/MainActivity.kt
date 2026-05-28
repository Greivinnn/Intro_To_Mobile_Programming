package com.wenwu.oopinkotlin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.wenwu.oopinkotlin.MainActivity
import java.io.BufferedReader
import java.util.UUID

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        findViewById<TextView>(R.id.textView_id).text = "there are ${AppData.vehicles.count()} vehicles"

        findViewById<Button>(R.id.button_id).setOnClickListener {
            val intent = Intent(this, NextActivity::class.java)
            startActivity(intent)
        }
    }
    class Vehicle (var name: String, val uid: UUID = UUID.randomUUID())
}