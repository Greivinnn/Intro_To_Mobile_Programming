package com.wenwu.optionsandnetsafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.third_layout)

        findViewById<Button>(R.id.thirdPlus_id).setOnClickListener {
            FirstActivity.counter++
            findViewById<TextView>(R.id.thirdText_id).text = FirstActivity.counter.toString()
        }
    }
}