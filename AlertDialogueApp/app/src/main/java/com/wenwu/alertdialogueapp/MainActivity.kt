package com.wenwu.alertdialogueapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.myButton_id).setOnClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("My Alert")
            builder.setMessage("This is an alert message")

            val passwordInput = EditText(this)
            builder.setView(passwordInput)



            builder.setPositiveButton("OK") { dialog, which ->
                checkPassword(passwordInput.text.toString())
            }

            builder.setNegativeButton("Cancel") { dialog, which -> }

            val dialog = builder.create()
            dialog.show()
        }
    }

    fun checkPassword(pass:String)
    {

    }
}