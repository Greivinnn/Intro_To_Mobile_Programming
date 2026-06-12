package com.wenwu.greivinwen_final.dungeon

import com.wenwu.greivinwen_final.model.*
import kotlin.random.Random

data class Room(val x: Int, val y: Int, val w: Int, val h: Int) {
    val centerX get() = x + w / 2
    val centerY get() = y + h / 2
    fun overlaps(other: Room, padding: Int = 1): Boolean {
        return !(x + w + padding <= other.x || other.x + other.w + padding <= x ||
                 y + h + padding <= other.y || other.y + other.h + padding <= y)
    }
}

data class FloorLayout(
    val map: DungeonMap,
    val playerStart: Pair<Int, Int>,
    val stairsPos: Pair<Int, Int>,
    val enemySpawns: List<Pair<Int, Int>>,
    val itemSpawns: List<Pair<Int, Int>>
)

object DungeonGenerator {

    private const val COLS = 16
    private const val ROWS = 16
    private const val MAX_ATTEMPTS = 50  // retry whole map if flood fill fails
    private const val ROOM_ATTEMPTS = 40

    fun generate(floorNumber: Int, random: Random = Random.Default): FloorLayout {
        repeat(MAX_ATTEMPTS) {
            val result = tryGenerate(floorNumber, random)
            if (result != null) return result
        }
        // Fallback: single open room (should never happen in practice)
        return generateFallback()
    }

    private fun tryGenerate(floorNumber: Int, random: Random): FloorLayout? {
        val map = DungeonMap(COLS, ROWS)
        val rooms = mutableListOf<Room>()

        // ── Place rooms ────────────────────────────────────────
        repeat(ROOM_ATTEMPTS) {
            val w = 3 + random.nextInt(4)   // 3–6 wide
            val h = 3 + random.nextInt(4)   // 3–6 tall
            val rx = 1 + random.nextInt(COLS - w - 2)
            val ry = 1 + random.nextInt(ROWS - h - 2)
            val room = Room(rx, ry, w, h)
            if (rooms.none { it.overlaps(room) }) {
                rooms.add(room)
                for (dy in ry until ry + h)
                    for (dx in rx until rx + w)
                        map.setTile(dx, dy, TileType.FLOOR)
            }
        }

        if (rooms.size < 4) return null  // not enough rooms, retry

        // ── Connect rooms with L-shaped corridors ──────────────
        val shuffled = rooms.shuffled(random)
        for (i in 1 until shuffled.size) {
            val a = shuffled[i - 1]; val b = shuffled[i]
            var cx = a.centerX; var cy = a.centerY
            // horizontal then vertical
            while (cx != b.centerX) {
                map.setTile(cx, cy, TileType.FLOOR)
                cx += if (cx < b.centerX) 1 else -1
            }
            while (cy != b.centerY) {
                map.setTile(cx, cy, TileType.FLOOR)
                cy += if (cy < b.centerY) 1 else -1
            }
        }

        // ── Flood fill check ───────────────────────────────────
        val firstRoom = rooms.first()
        if (!map.isFullyConnected(firstRoom.centerX, firstRoom.centerY)) return null

        // ── Place player (center of first room) ────────────────
        val playerStart = Pair(firstRoom.centerX, firstRoom.centerY)

        // ── Place stairs (center of farthest room from player) ─
        val farthestRoom = rooms.maxByOrNull {
            map.distance(it.centerX, it.centerY, playerStart.first, playerStart.second)
        }!!
        val stairsPos = Pair(farthestRoom.centerX, farthestRoom.centerY)
        // Note: stairs tile is set to FLOOR now; GameState reveals it when enemies cleared

        // ── Enemy spawn points ─────────────────────────────────
        val enemyCount = 3 + floorNumber  // scales with floor
        val enemySpawns = pickSpawnPoints(
            map, playerStart, stairsPos,
            count = enemyCount,
            minDistFromPlayer = 6,
            random = random
        )
        if (enemySpawns.size < enemyCount) return null  // not enough space, retry

        // ── Item spawn points ──────────────────────────────────
        // Guarantee 1 weapon in first 3 floors, then random mix
        val itemCount = 2 + random.nextInt(2)  // 2–3 items
        val itemSpawns = pickSpawnPoints(
            map, playerStart, stairsPos,
            count = itemCount,
            minDistFromPlayer = 3,
            exclude = enemySpawns,
            random = random
        )

        return FloorLayout(map, playerStart, stairsPos, enemySpawns, itemSpawns)
    }

    // Picks n random floor tiles respecting min distance from player
    private fun pickSpawnPoints(
        map: DungeonMap,
        playerPos: Pair<Int, Int>,
        stairsPos: Pair<Int, Int>,
        count: Int,
        minDistFromPlayer: Int,
        exclude: List<Pair<Int, Int>> = emptyList(),
        random: Random
    ): List<Pair<Int, Int>> {
        val candidates = map.floorTiles().filter { (x, y) ->
            map.distance(x, y, playerPos.first, playerPos.second) >= minDistFromPlayer &&
            Pair(x, y) != stairsPos &&
            Pair(x, y) !in exclude
        }.shuffled(random)

        val result = mutableListOf<Pair<Int, Int>>()
        for (pos in candidates) {
            if (result.size >= count) break
            if (result.none { map.distance(it.first, it.second, pos.first, pos.second) < 2 })
                result.add(pos)
        }
        return result
    }

    // Fallback: one big open room in case generation keeps failing
    private fun generateFallback(): FloorLayout {
        val map = DungeonMap(COLS, ROWS)
        for (y in 2 until ROWS - 2)
            for (x in 2 until COLS - 2)
                map.setTile(x, y, TileType.FLOOR)
        val playerStart = Pair(3, 3)
        val stairsPos = Pair(COLS - 4, ROWS - 4)
        val enemySpawns = listOf(Pair(8, 8), Pair(10, 6), Pair(6, 10))
        val itemSpawns = listOf(Pair(5, 5), Pair(11, 11))
        return FloorLayout(map, playerStart, stairsPos, enemySpawns, itemSpawns)
    }

    // Build actual Enemy objects from spawn points for a given floor
    fun spawnEnemies(spawns: List<Pair<Int, Int>>, floorNumber: Int, random: Random = Random.Default): List<Enemy> {
        return spawns.map { (x, y) ->
            val type = pickEnemyType(floorNumber, random)
            Enemy(
                x = x, y = y,
                type = type,
                hp = type.baseHp + (floorNumber * 2),
                maxHp = type.baseHp + (floorNumber * 2),
                atk = type.baseAtk + (floorNumber / 2)
            )
        }
    }

    private fun pickEnemyType(floor: Int, random: Random): EnemyType {
        // Only include enemy types that have been introduced by this floor
        val available = EnemyType.values().filter { it.introFloor <= floor }
        // Weight toward newer (harder) types on later floors
        return available.random(random)
    }

    // Build actual Item objects from spawn points
    fun spawnItems(spawns: List<Pair<Int, Int>>, floorNumber: Int, random: Random = Random.Default): List<Item> {
        return spawns.mapIndexed { index, (x, y) ->
            // Guarantee first item on floors 1–3 is a weapon if player hasn't gotten one
            val type = if (index == 0 && floorNumber <= 3) {
                ItemType.WEAPON
            } else {
                if (random.nextFloat() > 0.45f) ItemType.POTION else ItemType.WEAPON
            }
            Item(x, y, type)
        }
    }
}
