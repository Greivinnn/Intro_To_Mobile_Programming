package com.wenwu.greivinwen_final.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wenwu.greivinwen_final.R
import com.wenwu.greivinwen_final.data.GameDatabase
import kotlinx.coroutines.*

class MainMenuActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        findViewById<Button>(R.id.btnPlay).setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        findViewById<Button>(R.id.btnLeaderboard).setOnClickListener {
            startActivity(Intent(this, LeaderboardActivity::class.java))
        }

        loadBestStats()
    }

    override fun onResume() {
        super.onResume()
        // Refresh stats every time we return to the menu
        loadBestStats()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun loadBestStats() {
        scope.launch {
            val db = GameDatabase.getInstance(this@MainMenuActivity)
            val highScore    = withContext(Dispatchers.IO) { db.scoreDao().getHighScore() }
            val deepestFloor = withContext(Dispatchers.IO) { db.scoreDao().getDeepestFloor() }

            findViewById<TextView>(R.id.tvHighScore).text    = highScore?.toString()    ?: "---"
            findViewById<TextView>(R.id.tvDeepestFloor).text = deepestFloor?.toString() ?: "---"
        }
    }
}
