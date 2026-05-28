package com.wenwu.oopinkotlin

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class NextActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_next)

        findViewById<TextView>(R.id.textView).text = AppData.vehicles.count().toString()

        val myMap: HashMap<String, Any?> = hashMapOf("name" to "Mercedes",
            "color" to "Black",
            "BodyType" to "Sedan",
            "Year" to 2010,
            "VIN" to null,
            "Country" to "USA",
            )
    }
}