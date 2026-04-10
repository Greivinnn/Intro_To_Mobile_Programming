package com.WenWu.myfirstandroidapp

import android.graphics.Color
import android.os.Bundle
import android.text.style.BackgroundColorSpan
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        // cache the object with id myTextViewID
        val myTextView: TextView = findViewById<TextView>(R.id.myTextViewID)
        myTextView.text = "Hello LCV";
        myTextView.setBackgroundColor(Color.BLUE);

        val myImageView: ImageView = findViewById<ImageView>(R.id.myImageViewID)
        val myButton: Button = findViewById<Button>(R.id.myButtonID)
        var myBool: Boolean = false;
        myButton.setOnClickListener {
            if (myBool) {
                myImageView.setImageResource(R.drawable.doggoofficer)
            } else {
                myImageView.setImageResource(R.drawable.arthurthief)
            }
            myBool = !myBool;
        }
    }
}