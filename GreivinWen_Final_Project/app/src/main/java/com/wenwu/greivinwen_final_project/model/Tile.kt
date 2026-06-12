package com.wenwu.greivinwen_final_project.model

enum class TileType {
    WALL,
    FLOOR,
    STAIRS   // hidden until all enemies cleared
}

data class Tile(
    val type: TileType,
    var explored: Boolean = false  // for fog of war memory
)
