package com.wenwu.greivinwen_final.dungeon

import com.wenwu.greivinwen_final.model.Tile
import com.wenwu.greivinwen_final.model.TileType

class DungeonMap(val cols: Int = 16, val rows: Int = 16) {

    val grid: Array<Array<Tile>> = Array(rows) { Array(cols) { Tile(TileType.WALL) } }

    fun tileAt(x: Int, y: Int): Tile? {
        if (x < 0 || x >= cols || y < 0 || y >= rows) return null
        return grid[y][x]
    }

    fun isWalkable(x: Int, y: Int): Boolean {
        val tile = tileAt(x, y) ?: return false
        return tile.type != TileType.WALL
    }

    fun setTile(x: Int, y: Int, type: TileType) {
        if (x < 0 || x >= cols || y < 0 || y >= rows) return
        grid[y][x] = Tile(type)
    }

    // Returns all floor tile positions
    fun floorTiles(): List<Pair<Int, Int>> {
        val list = mutableListOf<Pair<Int, Int>>()
        for (y in 0 until rows)
            for (x in 0 until cols)
                if (grid[y][x].type == TileType.FLOOR)
                    list.add(Pair(x, y))
        return list
    }

    // Manhattan distance between two points
    fun distance(x1: Int, y1: Int, x2: Int, y2: Int): Int {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2)
    }

    // Mark tiles as explored for fog of war
    fun markExplored(cx: Int, cy: Int, radius: Int) {
        for (dy in -radius..radius)
            for (dx in -radius..radius)
                if (Math.abs(dx) + Math.abs(dy) <= radius)
                    tileAt(cx + dx, cy + dy)?.explored = true
    }

    // Flood fill — checks all floor tiles are reachable from a start point
    // Returns true if map is fully connected
    fun isFullyConnected(startX: Int, startY: Int): Boolean {
        val floors = floorTiles().toSet()
        if (floors.isEmpty()) return false

        val visited = mutableSetOf<Pair<Int, Int>>()
        val queue = ArrayDeque<Pair<Int, Int>>()
        val start = Pair(startX, startY)
        queue.add(start)
        visited.add(start)

        val dirs = listOf(0 to -1, 0 to 1, -1 to 0, 1 to 0)
        while (queue.isNotEmpty()) {
            val (cx, cy) = queue.removeFirst()
            for ((dx, dy) in dirs) {
                val nx = cx + dx; val ny = cy + dy
                val next = Pair(nx, ny)
                if (next in floors && next !in visited) {
                    visited.add(next)
                    queue.add(next)
                }
            }
        }
        return visited.size == floors.size
    }
}
