package com.wenwu.fragmentclass

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), FragmentListener {

    override fun removeButtonClicked() {
        supportFragmentManager.findFragmentByTag("MyFragTag")?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(it)
                .commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val addFragButton = findViewById<Button>(R.id.addFragentButton_id)
        addFragButton.setOnClickListener {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.container_id,
                    MyFragment(),
                    "MyFragTag")
                .commit()
        }

        val removeFragButton = findViewById<Button>(R.id.removeFragmentButton_id)
        removeFragButton.setOnClickListener {

            supportFragmentManager.findFragmentByTag("MyFragTag")?.let {
                supportFragmentManager
                    .beginTransaction()
                    .remove(it)
                    .commit()
            }

        }
    }
}