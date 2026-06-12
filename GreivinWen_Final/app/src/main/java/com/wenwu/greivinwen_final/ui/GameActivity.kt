package com.wenwu.greivinwen_final.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.wenwu.greivinwen_final.R
import com.wenwu.greivinwen_final.data.GameDatabase
import com.wenwu.greivinwen_final.data.ScoreEntity
import com.wenwu.greivinwen_final.dungeon.*
import com.wenwu.greivinwen_final.game.*
import com.wenwu.greivinwen_final.model.*
import kotlinx.coroutines.*

class GameActivity : AppCompatActivity() {

    // ── Views ─────────────────────────────────────────────────────────────────
    private lateinit var gameView: GameView
    private lateinit var tvHp: TextView
    private lateinit var tvAtk: TextView
    private lateinit var tvPotions: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvFloor: TextView
    private lateinit var tvLog: TextView
    private lateinit var hpBar: ProgressBar

    // ── Game objects ──────────────────────────────────────────────────────────
    private lateinit var gameState: GameState
    private lateinit var turnManager: TurnManager
    private val handler = Handler(Looper.getMainLooper())
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        bindViews()
        setupButtons()
        startNewGame()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private fun bindViews() {
        tvHp      = findViewById(R.id.tvHp)
        tvAtk     = findViewById(R.id.tvAtk)
        tvPotions = findViewById(R.id.tvPotions)
        tvScore   = findViewById(R.id.tvScore)
        tvFloor   = findViewById(R.id.tvFloor)
        tvLog     = findViewById(R.id.logText)
        hpBar     = findViewById(R.id.hpBar)
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnUp).setOnClickListener    { handleMove(0, -1) }
        findViewById<Button>(R.id.btnDown).setOnClickListener  { handleMove(0, 1) }
        findViewById<Button>(R.id.btnLeft).setOnClickListener  { handleMove(-1, 0) }
        findViewById<Button>(R.id.btnRight).setOnClickListener { handleMove(1, 0) }
        findViewById<Button>(R.id.btnPotion).setOnClickListener { handlePotion() }
    }

    private fun startNewGame() {
        val layout = DungeonGenerator.generate(1)
        val player = Player()
        player.x = layout.playerStart.first
        player.y = layout.playerStart.second

        val enemies = DungeonGenerator.spawnEnemies(layout.enemySpawns, 1).toMutableList()
        val items   = DungeonGenerator.spawnItems(layout.itemSpawns, 1).toMutableList()

        gameState = GameState(
            player       = player,
            map          = layout.map,
            enemies      = enemies,
            items        = items,
            stairsPos    = layout.stairsPos,
            stairsVisible = false,
            floor        = 1,
            score        = 0
        )
        turnManager = TurnManager(gameState)

        // Mark starting area as explored
        gameState.map.markExplored(player.x, player.y, radius = 4)

        // Attach GameView
        val container = findViewById<FrameLayout>(R.id.gameViewContainer)
        container.removeAllViews()
        gameView = GameView(this)
        gameView.gameState = gameState
        container.addView(gameView)

        updateHud()
        pushLog("You descend into the dungeon... Good luck.")
    }

    // ── Input handlers ────────────────────────────────────────────────────────

    private fun handleMove(dx: Int, dy: Int) {
        val events = turnManager.movePlayer(dx, dy)
        processEvents(events)
    }

    private fun handlePotion() {
        val events = turnManager.usePotion()
        processEvents(events)
    }

    // ── Event processing ──────────────────────────────────────────────────────

    private fun processEvents(events: List<TurnEvent>) {
        var needsRedraw = false
        var playerDied = false

        for (event in events) {
            when (event) {
                is TurnEvent.PlayerMoved -> {
                    needsRedraw = true
                }
                is TurnEvent.PlayerAttacked -> {
                    needsRedraw = true
                    if (event.killed) {
                        pushLog("You defeated the ${event.enemy.type.displayName}! (+${scoreForLog(event.enemy)} pts)")
                    } else {
                        pushLog("You hit the ${event.enemy.type.displayName} for ${event.damage} dmg!")
                    }
                }
                is TurnEvent.PlayerHit -> {
                    needsRedraw = true
                    pushLog("${event.enemy.type.displayName} hits you for ${event.damage}!")
                }
                is TurnEvent.PlayerDied -> {
                    playerDied = true
                    needsRedraw = true
                }
                is TurnEvent.ItemPickedUp -> {
                    needsRedraw = true
                    when (event.item.type) {
                        ItemType.POTION -> pushLog("You found a health potion! 🧪")
                        ItemType.WEAPON -> pushLog("Weapon upgrade! ATK is now ${gameState.player.atk} ⚔️")
                    }
                }
                is TurnEvent.StairsRevealed -> {
                    needsRedraw = true
                    pushLog("✨ All enemies defeated! The stairs appear...")
                    flashStairs()
                }
                is TurnEvent.StairsUsed -> {
                    loadNextFloor(event.newFloor)
                    return // floor transition handles its own redraw
                }
                is TurnEvent.PotionUsed -> {
                    needsRedraw = true
                    pushLog("You drink a potion and recover ${event.healAmount} HP! 🧪")
                }
                is TurnEvent.NoPotions -> {
                    pushLog("No potions left!")
                }
                is TurnEvent.WallBlocked -> { /* silent */ }
                is TurnEvent.LowHpWarning -> {
                    needsRedraw = true
                    flashLowHp()
                    pushLog("⚠️ Warning: Low HP! (${event.hp}/${event.maxHp})")
                }
                is TurnEvent.LevelUp -> {
                    pushLog("⬆️ Level Up! You are now level ${event.newLevel}. Max HP: ${event.newMaxHp}, ATK: ${event.newAtk}")
                }
                is TurnEvent.EnemyMoved -> {
                    needsRedraw = true
                }
            }
        }

        if (needsRedraw) {
            updateHud()
            gameView.invalidate()
        }
        if (playerDied) showGameOverDialog()
    }

    // ── Floor transition ──────────────────────────────────────────────────────

    private fun loadNextFloor(newFloor: Int) {
        val layout = DungeonGenerator.generate(newFloor)
        val player = gameState.player
        player.x = layout.playerStart.first
        player.y = layout.playerStart.second

        val enemies = DungeonGenerator.spawnEnemies(layout.enemySpawns, newFloor).toMutableList()
        val items   = DungeonGenerator.spawnItems(layout.itemSpawns, newFloor).toMutableList()

        gameState = GameState(
            player        = player,
            map           = layout.map,
            enemies       = enemies,
            items         = items,
            stairsPos     = layout.stairsPos,
            stairsVisible = false,
            floor         = newFloor,
            score         = gameState.score  // carry score forward!
        )
        turnManager = TurnManager(gameState)
        gameState.map.markExplored(player.x, player.y, radius = 4)

        gameView.gameState = gameState
        gameView.invalidate()

        updateHud()
        pushLog("⬇️ Floor $newFloor! Enemies grow stronger...")
    }

    // ── HUD ───────────────────────────────────────────────────────────────────

    private fun updateHud() {
        val p = gameState.player
        tvHp.text      = "${p.hp} / ${p.maxHp}"
        tvAtk.text     = "⚔️ ${p.atk}"
        tvPotions.text = "🧪 ${p.potions}"
        tvScore.text   = gameState.score.toString()
        tvFloor.text   = gameState.floor.toString()
        hpBar.progress = ((p.hpPercent) * 100).toInt()
    }

    // ── Log ───────────────────────────────────────────────────────────────────

    private val logLines = ArrayDeque<String>(3)

    private fun pushLog(msg: String) {
        if (logLines.size >= 2) logLines.removeFirst()
        logLines.addLast(msg)
        tvLog.text = logLines.joinToString("\n")
    }

    // ── Visual effects ────────────────────────────────────────────────────────

    private fun flashLowHp() {
        gameView.showLowHpFlash = true
        handler.postDelayed({ gameView.showLowHpFlash = false }, 350)
    }

    private fun flashStairs() {
        gameView.showStairsFlash = true
        handler.postDelayed({ gameView.showStairsFlash = false }, 600)
    }

    // ── Game Over ─────────────────────────────────────────────────────────────

    private fun showGameOverDialog() {
        val player = gameState.player
        val floor  = gameState.floor
        val score  = gameState.score
        val kills  = player.enemiesKilled

        // Find what killed the player (last enemy that hit them)
        val killedBy = gameState.enemies.firstOrNull()?.type?.displayName ?: "the dungeon"

        // Save score to Room DB
        scope.launch {
            val db = GameDatabase.getInstance(this@GameActivity)
            db.scoreDao().insert(
                ScoreEntity(
                    score        = score,
                    floorReached = floor,
                    enemiesKilled = kills,
                    killedBy     = killedBy
                )
            )
        }

        AlertDialog.Builder(this)
            .setTitle("💀 You Died")
            .setMessage(
                "Floor reached: $floor\n" +
                "Enemies killed: $kills\n" +
                "Killed by: $killedBy\n\n" +
                "Final Score: $score"
            )
            .setPositiveButton("Play Again") { _, _ -> startNewGame() }
            .setNegativeButton("Leaderboard") { _, _ ->
                startActivity(Intent(this, LeaderboardActivity::class.java))
                finish()
            }
            .setCancelable(false)
            .show()
    }

    private fun scoreForLog(enemy: Enemy): Int {
        return when (enemy.type) {
            EnemyType.GOBLIN     -> 15
            EnemyType.ORC        -> 25
            EnemyType.BAT        -> 15
            EnemyType.TROLL      -> 50
            EnemyType.DARK_MAGE  -> 40
        }
    }
}
