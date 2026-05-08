package com.wenwu.greivinwen_midterm

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class UserAccountActivity : AppCompatActivity() {

    fun updateSelectedPfp(drawableId: Int) {
        HomeActivity.cPfp = drawableId
        // Update the main avatar ImageView to show selected picture
        findViewById<ImageView>(R.id.previewPfp).setImageResource(drawableId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.account_layout)

        HomeActivity.cPfp?.let {
            findViewById<ImageView>(R.id.previewPfp).setImageResource(it)
        }

        findViewById<ImageView>(R.id.pfpOne).setOnClickListener {
            updateSelectedPfp(R.drawable.pfp1)
        }
        findViewById<ImageView>(R.id.pfpTwo).setOnClickListener {
            updateSelectedPfp(R.drawable.pfp2)
        }
        findViewById<ImageView>(R.id.pfpThree).setOnClickListener {
            updateSelectedPfp(R.drawable.pfp3)
        }
        findViewById<ImageView>(R.id.pfpFour).setOnClickListener {
            updateSelectedPfp(R.drawable.pfp4)
        }
        findViewById<ImageView>(R.id.pfpFive).setOnClickListener {
            updateSelectedPfp(R.drawable.pfp5)
        }

        findViewById<Button>(R.id.saveInfoButton).setOnClickListener {
            val chosenName = findViewById<TextView>(R.id.nameTextBox).text.toString().trim()
            val chosenOccupation = findViewById<TextView>(R.id.occupationTextBox).text.toString().trim()
            val chosenHeightStr = findViewById<TextView>(R.id.heightTextBox).text.toString().trim()
            val chosenWeightStr = findViewById<TextView>(R.id.weightTextBox).text.toString().trim()
            val chosenAgeStr = findViewById<TextView>(R.id.ageTextBox).text.toString().trim()
            val chosenEye = findViewById<TextView>(R.id.eyeTextBox).text.toString().trim()
            val chosenHair = findViewById<TextView>(R.id.hairTextBox).text.toString().trim()
            val chosenBody = findViewById<TextView>(R.id.bodyTextBox).text.toString().trim()
            val chosenPref = findViewById<TextView>(R.id.preferencesTextBox).text.toString().trim()

            var hasError = false

            // Validate height - whole numbers only
            val height = chosenHeightStr.toIntOrNull()
            if (chosenHeightStr.isEmpty() || height == null) {
                findViewById<TextView>(R.id.heightTextBox).error = "Please enter a whole number"
                hasError = true
            }

            // Validate weight - whole numbers only
            val weight = chosenWeightStr.toIntOrNull()
            if (chosenWeightStr.isEmpty() || weight == null) {
                findViewById<TextView>(R.id.weightTextBox).error = "Please enter a whole number"
                hasError = true
            }

            // Validate age - whole numbers only
            val age = chosenAgeStr.toIntOrNull()
            if (chosenAgeStr.isEmpty() || age == null) {
                findViewById<TextView>(R.id.ageTextBox).error = "Please enter a whole number"
                hasError = true
            }

            // Parse preferences - split by comma and trim each keyword
            val preferencesList = chosenPref
                .split(",")
                .map { it.trim() }
                .filter { it.isNotEmpty() }

            if (!hasError) {
                // Save everything to companion object
                HomeActivity.cName = chosenName
                HomeActivity.cHeight = height
                HomeActivity.cWeight = weight
                HomeActivity.cAge = age
                HomeActivity.cEye = chosenEye
                HomeActivity.cHair = chosenHair
                HomeActivity.cBody = chosenBody
                HomeActivity.cPref = preferencesList.joinToString(", ")

                // Give user feedback and go back
                Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}