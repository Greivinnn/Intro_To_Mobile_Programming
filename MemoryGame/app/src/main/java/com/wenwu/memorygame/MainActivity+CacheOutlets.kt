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

// The GameAdapter is the bridge between AppData.tiles and the RecyclerView
// it creates tiles views, binds data to the tiles, and handles the tap logic
class GameAdapter : RecyclerView.Adapter<TileViewHolder>() {


    private var firstFlippedIndex: Int = -1 // first tiles -1 = not flipped 0 = flipped
    private var secondFlippedIndex: Int = -1 // second tiles -1 = not flipped 0 = flipped

    // locks the board, when players click on 2 tiles that do not match the board is locked until the 2 tiles get flicked back
    private var isBoardLocked: Boolean = false

    override fun getItemCount(): Int = AppData.tiles.size

    // called when the RecyclerView needs a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val root = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.tile_layout, parent, false) as FrameLayout
        return TileViewHolder(root)
    }

    // called everytime a tiles needs to display data
    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        renderTile(holder, AppData.tiles[position])

        holder.root.setOnClickListener {
            // ask the ViewHolder for its current position at tap time
            val currentPosition = holder.bindingAdapterPosition

            if (currentPosition == RecyclerView.NO_ID.toInt()) return@setOnClickListener

            // get data if the null check is passed
            val tile = AppData.tiles[currentPosition]

            // ignores tap events if the board is locked
            // how the return@setOnClickListener works
            // this is telling the compiler that we want to exit the lambda function
            // not the whole function, so it just skips to the rest of the code if the board is locked
            if (isBoardLocked) return@setOnClickListener
            if (tile.isMatched) return@setOnClickListener
            if (tile.isFlipped) return@setOnClickListener

            tile.isFlipped = true
            notifyItemChanged(currentPosition)

            // check which one is being flip second or first tile
            if (firstFlippedIndex == -1) {
                firstFlippedIndex = currentPosition
            } else {
                secondFlippedIndex = currentPosition
                isBoardLocked = true

                // stores the firs and second tile flipped data
                val firstTile = AppData.tiles[firstFlippedIndex]
                val secondTile = AppData.tiles[secondFlippedIndex]

                // check if they match
                if (firstTile.pairValue == secondTile.pairValue) {
                    firstTile.isMatched = true
                    secondTile.isMatched = true
                    notifyItemChanged(firstFlippedIndex)
                    notifyItemChanged(secondFlippedIndex)
                    resetSelection()
                    isBoardLocked = false
                    // if no match then flip it back to original postion
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        firstTile.isFlipped = false
                        secondTile.isFlipped = false
                        notifyItemChanged(firstFlippedIndex)
                        notifyItemChanged(secondFlippedIndex)
                        resetSelection()
                        isBoardLocked = false
                    }, 500L)
                }
            }
        }
    }

    // how the tiles look when flipped, matched, or not flipped
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
                holder.root.setBackgroundColor(Color.parseColor("#F44336"))
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