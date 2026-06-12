package com.wenwu.greivinwen_final.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.wenwu.greivinwen_final.R
import com.wenwu.greivinwen_final.data.GameDatabase
import com.wenwu.greivinwen_final.data.ScoreEntity
import kotlinx.coroutines.*

class LeaderboardActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener { finish() }

        loadScores()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    private fun loadScores() {
        scope.launch {
            val db     = GameDatabase.getInstance(this@LeaderboardActivity)
            val scores = withContext(Dispatchers.IO) { db.scoreDao().getTopScores() }
            displayScores(scores)
        }
    }

    private fun displayScores(scores: List<ScoreEntity>) {
        val container = findViewById<LinearLayout>(R.id.scoresContainer)
        container.removeAllViews()

        if (scores.isEmpty()) {
            val empty = TextView(this).apply {
                text = "No runs yet. Get out there!"
                textSize = 14f
                setTextColor(0xFF666688.toInt())
                gravity = Gravity.CENTER
                setPadding(0, 48, 0, 0)
                typeface = android.graphics.Typeface.MONOSPACE
            }
            container.addView(empty)
            return
        }

        scores.forEachIndexed { index, score ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 12, 16, 12)
                // Alternate row background
                setBackgroundColor(
                    if (index % 2 == 0) 0xFF111118.toInt() else 0xFF0d0d0f.toInt()
                )
            }

            // Rank color — gold/silver/bronze for top 3
            val rankColor = when (index) {
                0 -> 0xFFf39c12.toInt()  // gold
                1 -> 0xFFaaaaaa.toInt()  // silver
                2 -> 0xFFcd7f32.toInt()  // bronze
                else -> 0xFF555577.toInt()
            }

            fun cell(text: String, weight: Float, color: Int = 0xFFe8e0d0.toInt()): TextView {
                return TextView(this).apply {
                    this.text = text
                    textSize = 13f
                    setTextColor(color)
                    typeface = android.graphics.Typeface.MONOSPACE
                    layoutParams = LinearLayout.LayoutParams(0,
                        LinearLayout.LayoutParams.WRAP_CONTENT, weight)
                }
            }

            row.addView(cell("${index + 1}", 0.5f, rankColor))
            row.addView(cell(score.score.toString(), 2f, rankColor))
            row.addView(cell("Floor ${score.floorReached}", 1.5f))
            row.addView(cell("${score.enemiesKilled} ☠️", 1.5f))
            row.addView(cell(score.killedBy, 2f, 0xFFe74c3c.toInt()))

            container.addView(row)
        }
    }
}
