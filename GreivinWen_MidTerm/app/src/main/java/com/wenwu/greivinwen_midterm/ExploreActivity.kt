package com.wenwu.greivinwen_midterm

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ExploreActivity : AppCompatActivity() {

    // ── Bot data pool ──────────────────────────────────────────────
    data class BotProfile(
        val name: String,
        val occupation: String,
        val height: Int,   // cm
        val weight: Int,   // kg
        val age: Int,
        val eye: String,
        val hair: String,
        val body: String,
        val keywords: List<String>,
        val pfp: Int
    )

    val botPool = listOf(
        BotProfile("Jorge",  "Teacher",    165, 58, 25, "Brown", "Black",  "Athletic", listOf("music","travel","coffee"), R.drawable.pfp1),
        BotProfile("Maria",  "Engineer",   170, 65, 28, "Blue",  "Brown",  "Slim",     listOf("hiking","gaming","books"), R.drawable.pfp2),
        BotProfile("Sofia",  "Designer",   160, 55, 22, "Green", "Blonde", "Slim",     listOf("art","travel","yoga"),     R.drawable.pfp3),
        BotProfile("James",  "Doctor",     180, 80, 30, "Hazel", "Brown",  "Athletic", listOf("sports","cooking","music"),R.drawable.pfp4),
        BotProfile("Lena",   "Nurse",      158, 52, 24, "Brown", "Black",  "Curvy",    listOf("travel","movies","coffee"),R.drawable.pfp5)
    )

    // ── Scoring weights (total = 100) ──────────────────────────────
    // Height  ±5 cm   → up to 20 pts
    // Weight  ±5 kg   → up to 15 pts
    // Age     ±3 yrs  → up to 15 pts
    // Eye color exact → 10 pts
    // Hair color exact→ 10 pts
    // Body type exact → 10 pts
    // Keywords overlap→ up to 20 pts
    // Match threshold : ≥ 70 %


    fun calcScore(bot: BotProfile): Int {
        var score = 0

        // ── Numerical (proximity) ──────────────────────────────────
        val userHeight = HomeActivity.cHeight
        val userWeight = HomeActivity.cWeight
        val userAge    = HomeActivity.cAge

        if (userHeight != null)
            score += if (Math.abs(bot.height - userHeight) <= 5) 20 else 0

        if (userWeight != null)
            score += if (Math.abs(bot.weight - userWeight) <= 5) 15 else 0

        if (userAge != null)
            score += if (Math.abs(bot.age - userAge) <= 3) 15 else 0

        // ── Categorical (exact match) ──────────────────────────────
        if (!HomeActivity.cEye.isNullOrEmpty() && bot.eye.equals(HomeActivity.cEye, ignoreCase = true))
            score += 10

        if (!HomeActivity.cHair.isNullOrEmpty() && bot.hair.equals(HomeActivity.cHair, ignoreCase = true))
            score += 10

        if (!HomeActivity.cBody.isNullOrEmpty() && bot.body.equals(HomeActivity.cBody, ignoreCase = true))
            score += 10

        // ── Keywords overlap ───────────────────────────────────────
        val userKeywords = HomeActivity.cPref
            ?.split(",")
            ?.map { it.trim().lowercase() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()

        val overlap = bot.keywords.count { it.lowercase() in userKeywords }
        // Each matching keyword = up to 20pts split across 3 keywords max
        score += when {
            userKeywords.isEmpty() -> 0
            else -> ((overlap.toFloat() / maxOf(bot.keywords.size, userKeywords.size)) * 20).toInt()
        }

        return score.coerceIn(0, 100)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.explore_layout)

        // Pick a random bot
        val bot = botPool.random()
        val score = calcScore(bot)
        val isMatch = score >= 70

        // ── Populate bot TextViews ─────────────────────────────────
        findViewById<TextView>(R.id.botName).text       = bot.name
        findViewById<TextView>(R.id.botOccupation).text = bot.occupation
        findViewById<TextView>(R.id.botHeight).text     = "${bot.height} cm"
        findViewById<TextView>(R.id.botWeight).text     = "${bot.weight} kg"
        findViewById<TextView>(R.id.botAge).text        = "${bot.age} yrs"
        findViewById<TextView>(R.id.botEye).text        = bot.eye
        findViewById<TextView>(R.id.botHair).text       = bot.hair
        findViewById<TextView>(R.id.botBody).text       = bot.body
        findViewById<TextView>(R.id.botPreferences).text   = bot.keywords.joinToString(", ")

        // ── Match percent inside the heart TextView ────────────────
        findViewById<TextView>(R.id.matchPercentage).text   = "$score%"

        // ── Bot profile picture ────────────────────────────────────
        findViewById<ImageView>(R.id.botPfp).setImageResource(bot.pfp)

        // ── Match / No-match image ─────────────────────────────────
        val resultImage = findViewById<ImageView>(R.id.matchResult)
        if (isMatch) {
            resultImage.setImageResource(R.drawable.match)   // your heart.png
        } else {
            resultImage.setImageResource(R.drawable.no_match)   // swap for a "no match" drawable if you have one
        }
    }
}