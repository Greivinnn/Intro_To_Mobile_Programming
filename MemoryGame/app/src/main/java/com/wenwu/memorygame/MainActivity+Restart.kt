package com.wenwu.memorygame

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

fun MainActivity.restart(): View.OnClickListener {
    return View.OnClickListener {
        // Reset tile data
        AppData.createTiles()

        // Reset adapter state and refresh the grid
        val adapter = GameAdapter()
        gameViewRv.adapter = adapter
    }
}