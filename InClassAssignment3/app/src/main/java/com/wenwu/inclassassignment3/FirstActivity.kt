package com.wenwu.inclassassignment3

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FirstActivity : AppCompatActivity() {

    companion object
    {
        var username: String? = null
        var password: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.first_layout)

        findViewById<TextView>(R.id.register_id).setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<TextView>(R.id.loginButton_id).setOnClickListener {
            val usernameText = findViewById<TextView>(R.id.nameText_id).text.toString()
            val passwordText = findViewById<TextView>(R.id.passwordText_id).text.toString()

            if (username != null && password != null
                && usernameText == username
                && passwordText == password)
            {
                val intent = Intent(this, ThirdActivity::class.java)
                startActivity(intent)
            }
            else
            {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }



    }
}