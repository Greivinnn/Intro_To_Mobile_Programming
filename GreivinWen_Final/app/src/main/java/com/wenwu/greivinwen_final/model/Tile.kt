package com.wenwu.greivinwen_final.model

enum class TileType {
    WALL,
    FLOOR,
    STAIRS   // hidden until all enemies cleared
}

data class Tile(
    val type: TileType,
    var explored: Boolean = false  // for fog of war memory
)
