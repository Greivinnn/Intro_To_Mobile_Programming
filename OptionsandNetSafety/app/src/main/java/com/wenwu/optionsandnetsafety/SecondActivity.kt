package com.wenwu.optionsandnetsafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_layout)

        findViewById<Button>(R.id.secondButton_id).setOnClickListener()
        {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.secondPlus_id).setOnClickListener()
        {
            FirstActivity.counter++
            findViewById<TextView>(R.id.secondText_id).text = FirstActivity.counter.toString()
        }
    }
}