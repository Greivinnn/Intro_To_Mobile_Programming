package com.wenwu.assignment_1_dating_app_ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // All the available animals to choose from
        val giraffeImg: ImageView = findViewById<ImageView>(R.id.giraffeID)
        val kangarooImg: ImageView = findViewById<ImageView>(R.id.kangarooID)
        val pantherImg: ImageView = findViewById<ImageView>(R.id.pantherID)
        val wolfImg: ImageView = findViewById<ImageView>(R.id.wolfID)

        // Placeholder that is invisible until an animal is selected
        val placeHolder: ImageView = findViewById<ImageView>(R.id.PlaceHolderID)
        // Each animal has a setOnClickListener so the images work as a button
        giraffeImg.setOnClickListener {
            placeHolder.setImageResource(R.drawable.giraffe)
            placeHolder.visibility = View.VISIBLE
        }
        kangarooImg.setOnClickListener {
            placeHolder.setImageResource(R.drawable.kangaroo)
            placeHolder.visibility = View.VISIBLE
        }
        pantherImg.setOnClickListener {
            placeHolder.setImageResource(R.drawable.panther)
            placeHolder.visibility = View.VISIBLE
        }
        wolfImg.setOnClickListener {
            placeHolder.setImageResource(R.drawable.wolf)
            placeHolder.visibility = View.VISIBLE
        }

    }
}