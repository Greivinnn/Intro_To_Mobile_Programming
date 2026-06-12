package com.wenwu.greivinwen_final.game

import com.wenwu.greivinwen_final.model.*
import com.wenwu.greivinwen_final.dungeon.*
import kotlin.random.Random

// All possible outcomes of a player action — the UI reacts to these
sealed class TurnEvent {
    data class PlayerMoved(val x: Int, val y: Int) : TurnEvent()
    data class PlayerAttacked(val enemy: Enemy, val damage: Int, val killed: Boolean) : TurnEvent()
    data class PlayerHit(val enemy: Enemy, val damage: Int) : TurnEvent()
    data class PlayerDied(val floor: Int, val score: Int, val kills: Int) : TurnEvent()
    data class ItemPickedUp(val item: Item) : TurnEvent()
    data class StairsRevealed(val x: Int, val y: Int) : TurnEvent()
    data class StairsUsed(val newFloor: Int) : TurnEvent()
    data class PotionUsed(val healAmount: Int) : TurnEvent()
    object NoPotions : TurnEvent()
    object WallBlocked : TurnEvent()
    data class LowHpWarning(val hp: Int, val maxHp: Int) : TurnEvent()
    data class LevelUp(val newLevel: Int, val newMaxHp: Int, val newAtk: Int) : TurnEvent()
    data class EnemyMoved(val enemy: Enemy) : TurnEvent()
}

class GameState(
    val player: Player,
    val map: DungeonMap,
    val enemies: MutableList<Enemy>,
    val items: MutableList<Item>,
    var stairsPos: Pair<Int, Int>,
    var stairsVisible: Boolean = false,
    var floor: Int = 1,
    var score: Int = 0,
    val random: Random = Random.Default
)

class TurnManager(private val state: GameState) {

    // Called by GameActivity when player taps a direction
    fun movePlayer(dx: Int, dy: Int): List<TurnEvent> {
        val events = mutableListOf<TurnEvent>()
        val player = state.player
        val nx = player.x + dx
        val ny = player.y + dy

        // Out of bounds or wall
        if (!state.map.isWalkable(nx, ny)) {
            events.add(TurnEvent.WallBlocked)
            return events
        }

        // Enemy at target tile — attack instead of move
        val enemy = state.enemies.find { it.x == nx && it.y == ny }
        if (enemy != null) {
            events.addAll(attackEnemy(enemy))
            events.addAll(doEnemyTurns())
            checkLowHp(events)
            return events
        }

        // Move player
        player.x = nx
        player.y = ny
        state.map.markExplored(nx, ny, radius = 4)
        events.add(TurnEvent.PlayerMoved(nx, ny))

        // Pick up item
        val item = state.items.find { it.x == nx && it.y == ny }
        if (item != null) {
            state.items.remove(item)
            applyItem(item, events)
        }

        // Check stairs
        if (state.stairsVisible &&
            nx == state.stairsPos.first &&
            ny == state.stairsPos.second) {
            events.addAll(descendStairs())
            return events
        }

        // Enemy turns after player moves
        events.addAll(doEnemyTurns())
        checkLowHp(events)
        return events
    }

    // Player uses a potion from inventory
    fun usePotion(): List<TurnEvent> {
        val events = mutableListOf<TurnEvent>()
        val player = state.player
        if (player.potions <= 0) {
            events.add(TurnEvent.NoPotions)
            return events
        }
        player.potions--
        val heal = 8 + state.random.nextInt(6) // 8–13 HP
        player.hp = minOf(player.maxHp, player.hp + heal)
        events.add(TurnEvent.PotionUsed(heal))
        // Enemy turns still pass when using a potion (costs a turn)
        events.addAll(doEnemyTurns())
        return events
    }

    // ── Combat ────────────────────────────────────────────────────────────────

    private fun attackEnemy(enemy: Enemy): List<TurnEvent> {
        val events = mutableListOf<TurnEvent>()
        val player = state.player

        val dmg = calcPlayerDamage(player)
        enemy.hp -= dmg
        val killed = enemy.hp <= 0

        if (killed) {
            state.enemies.remove(enemy)
            player.enemiesKilled++
            state.score += scoreForKill(enemy, state.floor)
            events.add(TurnEvent.PlayerAttacked(enemy, dmg, killed = true))

            // All enemies cleared — reveal stairs
            if (state.enemies.isEmpty() && !state.stairsVisible) {
                state.stairsVisible = true
                state.map.setTile(state.stairsPos.first, state.stairsPos.second, TileType.STAIRS)
                events.add(TurnEvent.StairsRevealed(state.stairsPos.first, state.stairsPos.second))
            }
        } else {
            events.add(TurnEvent.PlayerAttacked(enemy, dmg, killed = false))
        }
        return events
    }

    private fun calcPlayerDamage(player: Player): Int {
        // Base ATK + small random roll (1–3)
        return player.atk + state.random.nextInt(3) + 1
    }

    private fun scoreForKill(enemy: Enemy, floor: Int): Int {
        val base = when (enemy.type) {
            EnemyType.GOBLIN    -> 15
            EnemyType.ORC       -> 25
            EnemyType.BAT       -> 15
            EnemyType.TROLL     -> 50
            EnemyType.DARK_MAGE -> 40
        }
        // Floor multiplier — deeper = more points
        val multiplier = 1.0f + (floor - 1) * 0.15f
        return (base * multiplier).toInt()
    }

    // ── Enemy AI ──────────────────────────────────────────────────────────────

    private fun doEnemyTurns(): List<TurnEvent> {
        val events = mutableListOf<TurnEvent>()
        val player = state.player

        for (enemy in state.enemies.toList()) { // toList() to avoid ConcurrentModification
            val dist = state.map.distance(enemy.x, enemy.y, player.x, player.y)

            // Aggro check — once spotted, always aggro
            if (dist <= enemy.aggroRadius) enemy.isAggro = true
            if (!enemy.isAggro) continue

            // Troll regeneration — heals 1 HP per turn if not at max
            if (enemy.type == EnemyType.TROLL && enemy.hp < enemy.maxHp) {
                enemy.hp = minOf(enemy.maxHp, enemy.hp + 1)
            }

            // Dark Mage — ranged attack (doesn't need to be adjacent)
            if (enemy.type == EnemyType.DARK_MAGE && dist <= enemy.attackRange + 1) {
                val dmg = calcEnemyDamage(enemy)
                player.hp -= dmg
                events.add(TurnEvent.PlayerHit(enemy, dmg))
                if (player.isDead) {
                    events.add(TurnEvent.PlayerDied(state.floor, state.score, player.enemiesKilled))
                    return events
                }
                continue
            }

            // Try to move toward player (up to moveRange tiles per turn)
            repeat(enemy.moveRange) {
                val currentDist = state.map.distance(enemy.x, enemy.y, player.x, player.y)

                // Adjacent to player — attack
                if (currentDist == 1) {
                    val dmg = calcEnemyDamage(enemy)
                    player.hp -= dmg
                    events.add(TurnEvent.PlayerHit(enemy, dmg))
                    if (player.isDead) {
                        events.add(TurnEvent.PlayerDied(state.floor, state.score, player.enemiesKilled))
                    }
                    return@repeat
                }

                // Step toward player using best available direction
                val moved = stepToward(enemy, player.x, player.y)
                if (moved) events.add(TurnEvent.EnemyMoved(enemy))
            }

            if (player.isDead) return events
        }
        return events
    }

    private fun calcEnemyDamage(enemy: Enemy): Int {
        return enemy.atk + state.random.nextInt(2) // atk + 0 or 1
    }

    // Move enemy one tile toward target using cardinal directions
    // Tries horizontal first, then vertical, then any open direction
    private fun stepToward(enemy: Enemy, targetX: Int, targetY: Int): Boolean {
        val dx = Integer.signum(targetX - enemy.x)
        val dy = Integer.signum(targetY - enemy.y)

        val preferred = mutableListOf<Pair<Int, Int>>()
        if (dx != 0) preferred.add(Pair(dx, 0))
        if (dy != 0) preferred.add(Pair(0, dy))
        // Fallback directions
        val fallback = listOf(Pair(0, -1), Pair(0, 1), Pair(-1, 0), Pair(1, 0))
            .filter { it !in preferred }

        for ((stepX, stepY) in preferred + fallback) {
            val nx = enemy.x + stepX
            val ny = enemy.y + stepY
            if (state.map.isWalkable(nx, ny) && !isTileOccupied(nx, ny)) {
                enemy.x = nx
                enemy.y = ny
                return true
            }
        }
        return false
    }

    // Check if a tile has another enemy on it (prevents stacking)
    private fun isTileOccupied(x: Int, y: Int): Boolean {
        val player = state.player
        if (player.x == x && player.y == y) return true
        return state.enemies.any { it.x == x && it.y == y }
    }

    // ── Floor Transition ──────────────────────────────────────────────────────

    private fun descendStairs(): List<TurnEvent> {
        val events = mutableListOf<TurnEvent>()
        val player = state.player

        // Bonus score for clearing floor
        state.score += 50 + (state.floor * 10)

        // Bonus for surviving with low HP (risky play)
        if (player.hpPercent < 0.35f) state.score += 25

        // Bonus for using no potions this floor (not tracked yet — stretch goal)

        state.floor++

        // Level up every 3 floors
        if (state.floor % 3 == 0) {
            player.levelUp()
            events.add(TurnEvent.LevelUp(player.level, player.maxHp, player.atk))
        }

        events.add(TurnEvent.StairsUsed(state.floor))
        return events
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun applyItem(item: Item, events: MutableList<TurnEvent>) {
        val player = state.player
        when (item.type) {
            ItemType.POTION -> {
                player.potions++
                state.score += 5
            }
            ItemType.WEAPON -> {
                player.atk += 2
                player.weaponTier++
                state.score += 10
            }
        }
        events.add(TurnEvent.ItemPickedUp(item))
    }

    private fun checkLowHp(events: MutableList<TurnEvent>) {
        val player = state.player
        if (player.hpPercent <= 0.30f && !player.isDead) {
            events.add(TurnEvent.LowHpWarning(player.hp, player.maxHp))
        }
    }
}
