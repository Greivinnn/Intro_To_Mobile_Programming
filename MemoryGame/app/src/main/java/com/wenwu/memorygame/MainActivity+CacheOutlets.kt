package com.wenwu.memorygame

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TileViewHolder(val root: FrameLayout) : RecyclerView.ViewHolder(root) {
    val label: TextView = root.findViewById(R.id.tileLabel_id)
}


class GameAdapter : RecyclerView.Adapter<TileViewHolder>() {


    private var firstFlippedIndex: Int = -1
    private var secondFlippedIndex: Int = -1

    private var isBoardLocked: Boolean = false

    override fun getItemCount(): Int = AppData.tiles.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val root = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.tile_layout, parent, false) as FrameLayout
        return TileViewHolder(root)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        renderTile(holder, AppData.tiles[position])

        holder.root.setOnClickListener {
            val currentPosition = holder.bindingAdapterPosition
            if (currentPosition == RecyclerView.NO_ID.toInt()) return@setOnClickListener

            val tile = AppData.tiles[currentPosition]

            if (isBoardLocked) return@setOnClickListener
            if (tile.isMatched) return@setOnClickListener
            if (tile.isFlipped) return@setOnClickListener

            tile.isFlipped = true
            notifyItemChanged(currentPosition)

            if (firstFlippedIndex == -1) {
                firstFlippedIndex = currentPosition
            } else {
                secondFlippedIndex = currentPosition
                isBoardLocked = true

                val firstTile = AppData.tiles[firstFlippedIndex]
                val secondTile = AppData.tiles[secondFlippedIndex]

                if (firstTile.pairValue == secondTile.pairValue) {
                    firstTile.isMatched = true
                    secondTile.isMatched = true
                    notifyItemChanged(firstFlippedIndex)
                    notifyItemChanged(secondFlippedIndex)
                    resetSelection()
                    isBoardLocked = false
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        firstTile.isFlipped = false
                        secondTile.isFlipped = false
                        notifyItemChanged(firstFlippedIndex)
                        notifyItemChanged(secondFlippedIndex)
                        resetSelection()
                        isBoardLocked = false
                    }, 900L)
                }
            }
        }
    }

    // ── Visual state ─────────────────────────────────────────────────────────

    private fun renderTile(holder: TileViewHolder, tile: Tile) {
        when {
            tile.isMatched -> {
                holder.label.text = tile.pairValue.toString()
                holder.root.setBackgroundColor(Color.parseColor("#A5D6A7")) // light green
                holder.label.setTextColor(Color.parseColor("#1B5E20"))
            }
            tile.isFlipped -> {
                holder.label.text = tile.pairValue.toString()
                holder.root.setBackgroundColor(Color.WHITE)
                holder.label.setTextColor(Color.parseColor("#212121"))
            }
            else -> {
                holder.label.text = ""
                holder.root.setBackgroundColor(Color.parseColor("#1565C0"))
                holder.label.setTextColor(Color.TRANSPARENT)
            }
        }
    }

    private fun resetSelection() {
        firstFlippedIndex = -1
        secondFlippedIndex = -1
    }
    fun reset() {
        resetSelection()
        isBoardLocked = false
    }
}
fun MainActivity.cacheOutlets() {
    restartButton = findViewById(R.id.restartButton_id)
    restartButton.setOnClickListener(restart())

    gameViewRv = findViewById(R.id.GameViewRv_id)
    gameViewRv.layoutManager = GridLayoutManager(this, AppData.GRID_SIZE)
    gameViewRv.adapter = GameAdapter()
}