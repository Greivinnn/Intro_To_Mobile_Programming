package com.wenwu.memorygame

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

// Each tile holds a pair value, plus flip/match state
data class Tile(
    val pairValue: Int,          // 1–(gridSize*gridSize/2), two tiles share the same value
    var isFlipped: Boolean = false,
    var isMatched: Boolean = false
)

class AppData {
    companion object {
        const val GRID_SIZE: Int = 4  // 4×4 = 16 tiles = 8 pairs

        var tiles: ArrayList<Tile> = ArrayList()

        fun createTiles() {
            tiles.clear()
            val pairCount = (GRID_SIZE * GRID_SIZE) / 2
            // Create two tiles for every pair value, then shuffle
            for (value in 1..pairCount) {
                tiles.add(Tile(pairValue = value))
                tiles.add(Tile(pairValue = value))
            }
            tiles.shuffle()
        }
    }
}

class MainActivity : AppCompatActivity() {

    lateinit var restartButton: Button
    lateinit var gameViewRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        AppData.createTiles()
        cacheOutlets()
    }
}