package com.wenwu.greivinwen_midterm

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    companion object
    {
        var cHeight: Int? = null
        var cWeight: Int? = null
        var cAge: Int? = null
        var cEye: String? = null
        var cHair: String? = null
        var cBody: String? = null
        var cPref: String? = null
        var cName: String? = null
        var cPfp: Int? = null
    }

    override fun onResume() {
        super.onResume()
        // When returning from UserAccountActivity, apply the saved pfp
        HomeActivity.cPfp?.let {
            findViewById<ImageView>(R.id.homePagePhoto).setImageResource(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.home_layout)

        findViewById<Button>(R.id.accountButton).setOnClickListener {
            val intent = Intent(this, UserAccountActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.exploreButton).setOnClickListener {
            val intent = Intent(this, ExploreActivity::class.java)
            startActivity(intent)
        }
    }
}