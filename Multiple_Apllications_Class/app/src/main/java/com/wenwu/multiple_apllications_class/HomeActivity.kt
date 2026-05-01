package com.wenwu.multiple_apllications_class

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText

class HomeActivity : AppCompatActivity() {

    lateinit var cityNameTextView: EditText

    override fun onResume() {
        super.onResume()

        cityNameTextView.text.clear()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        val loginButton = findViewById<Button>(R.id.loginButton_id)
        val inputText = findViewById<TextView>(R.id.nameEditText_id)

        cityNameTextView = findViewById(R.id.nameEditText_id)

        loginButton.setOnClickListener {

            val intent = Intent(this, AuthActivity::class.java)

            var city = inputText.text.toString()

            intent.putExtra("city", city)

            startActivity(intent);
        }
    }
}