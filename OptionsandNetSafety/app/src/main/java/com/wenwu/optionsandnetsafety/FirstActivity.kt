package com.wenwu.optionsandnetsafety

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class FirstActivity : AppCompatActivity() {

    companion object
    {
        var counter = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.first_layout)

        findViewById<Button>(R.id.firstButton_id).setOnClickListener()
        {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.firstPlus_id).setOnClickListener()
        {
            counter++
            findViewById<TextView>(R.id.firstText_id).text = counter.toString()
        }
    }
}