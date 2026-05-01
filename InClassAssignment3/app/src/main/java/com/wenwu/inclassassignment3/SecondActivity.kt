package com.wenwu.inclassassignment3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.second_layout)

        findViewById<Button>(R.id.registerButton_id).setOnClickListener {
            val chosedName = findViewById<TextView>(R.id.registerName_id).text.toString()
            val chosedPass = findViewById<TextView>(R.id.registerPass_id).text.toString()
            val chosedPassConf = findViewById<TextView>(R.id.registerPassConf_id).text.toString()

            if (chosedPass == chosedPassConf) {
                FirstActivity.username = chosedName
                FirstActivity.password = chosedPass

                val intent = Intent(this, FirstActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
        }

    }
}