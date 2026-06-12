package com.wenwu.greivinwen_final_project.ui

import android.content.Context
import android.graphics.*
import android.view.View
import com.wenwu.greivinwen_final_project.dungeon.GameState
import com.wenwu.greivinwen_final_project.model.*

class GameView(context: Context) : View(context) {

    var gameState: GameState? = null

    // Low HP flash overlay
    var showLowHpFlash: Boolean = false
        set(value) { field = value; invalidate() }

    // Stairs revealed flash
    var showStairsFlash: Boolean = false
        set(value) { field = value; invalidate() }

    private var tileSize: Float = 0f

    // ── Paints ────────────────────────────────────────────────────────────────

    private val wallPaint = Paint().apply {
        color = Color.parseColor("#1a1525")
        style = Paint.Style.FILL
    }
    private val wallEdgePaint = Paint().apply {
        color = Color.parseColor("#2e2840")
        style = Paint.Style.FILL
    }
    private val floorPaint = Paint().apply {
        color = Color.parseColor("#1e2235")
        style = Paint.Style.FILL
    }
    private val floorAltPaint = Paint().apply {
        color = Color.parseColor("#1a1e30")
        style = Paint.Style.FILL
    }
    private val stairsPaint = Paint().apply {
        color = Color.parseColor("#b8860b")
        style = Paint.Style.FILL
    }
    private val fogPaint = Paint().apply {
        color = Color.parseColor("#E6090910")   // very dark overlay for unseen
        style = Paint.Style.FILL
    }
    private val fogUnexploredPaint = Paint().apply {
        color = Color.parseColor("#F0090910")
        style = Paint.Style.FILL
    }
    private val hpBarBgPaint = Paint().apply {
        color = Color.parseColor("#2a2a35")
        style = Paint.Style.FILL
    }
    private val lowHpFlashPaint = Paint().apply {
        color = Color.parseColor("#55e74c3c")
        style = Paint.Style.FILL
    }
    private val stairsFlashPaint = Paint().apply {
        color = Color.parseColor("#44f39c12")
        style = Paint.Style.FILL
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val stairsTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#f39c12")
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    // ── Size ──────────────────────────────────────────────────────────────────

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val state = gameState ?: return
        tileSize = minOf(w.toFloat() / state.map.cols, h.toFloat() / state.map.rows)
    }

    // ── Draw ──────────────────────────────────────────────────────────────────

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val state = gameState ?: return
        if (tileSize == 0f) {
            tileSize = minOf(width.toFloat() / state.map.cols, height.toFloat() / state.map.rows)
        }

        val player = state.player
        val ts = tileSize

        // Build visible tile set (Manhattan radius 4 from player)
        val visibleTiles = mutableSetOf<Long>()
        for (dy in -4..4)
            for (dx in -4..4)
                if (Math.abs(dx) + Math.abs(dy) <= 4)
                    visibleTiles.add(tileKey(player.x + dx, player.y + dy))

        // ── Draw tiles ────────────────────────────────────────────────────────
        for (row in 0 until state.map.rows) {
            for (col in 0 until state.map.cols) {
                val tile = state.map.grid[row][col]
                val px = col * ts
                val py = row * ts
                val isVisible = visibleTiles.contains(tileKey(col, row))
                val isExplored = tile.explored

                when (tile.type) {
                    TileType.WALL -> {
                        canvas.drawRect(px, py, px + ts, py + ts, wallPaint)
                        if (isVisible || isExplored) {
                            canvas.drawRect(px + 2, py + 2, px + ts - 2, py + ts - 2, wallEdgePaint)
                        }
                    }
                    TileType.FLOOR, TileType.STAIRS -> {
                        val floorP = if ((col + row) % 2 == 0) floorPaint else floorAltPaint
                        canvas.drawRect(px, py, px + ts, py + ts, floorP)

                        // Stairs indicator
                        if (tile.type == TileType.STAIRS && state.stairsVisible && isVisible) {
                            canvas.drawRect(px + 2, py + 2, px + ts - 2, py + ts - 2, stairsPaint)
                            stairsTextPaint.textSize = ts * 0.55f
                            canvas.drawText("▼", px + ts / 2f, py + ts * 0.68f, stairsTextPaint)
                        }
                    }
                }

                // Fog overlay
                if (!isVisible) {
                    val fogP = if (isExplored) fogPaint else fogUnexploredPaint
                    canvas.drawRect(px, py, px + ts, py + ts, fogP)
                }
            }
        }

        // ── Draw items ────────────────────────────────────────────────────────
        for (item in state.items) {
            if (!visibleTiles.contains(tileKey(item.x, item.y))) continue
            val px = item.x * ts
            val py = item.y * ts
            textPaint.textSize = ts * 0.6f
            val emoji = item.type.emoji
            canvas.drawText(emoji, px + ts / 2f, py + ts * 0.68f, textPaint)
        }

        // ── Draw enemies ──────────────────────────────────────────────────────
        for (enemy in state.enemies) {
            if (!visibleTiles.contains(tileKey(enemy.x, enemy.y))) continue
            val px = enemy.x * ts
            val py = enemy.y * ts
            drawEnemy(canvas, px, py, ts, enemy)
        }

        // ── Draw player ───────────────────────────────────────────────────────
        drawPlayer(canvas, player.x * ts, player.y * ts, ts)

        // ── Overlays ──────────────────────────────────────────────────────────
        if (showLowHpFlash) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), lowHpFlashPaint)
        }
        if (showStairsFlash) {
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), stairsFlashPaint)
        }
    }

    // ── Entity Drawing ────────────────────────────────────────────────────────

    private fun drawPlayer(canvas: Canvas, px: Float, py: Float, ts: Float) {
        // Shadow
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#44000000")
            style = Paint.Style.FILL
        }
        canvas.drawOval(
            RectF(px + ts * 0.25f, py + ts * 0.8f, px + ts * 0.75f, py + ts * 0.95f),
            shadowPaint
        )
        // Body
        val bodyPaint = Paint().apply { color = Color.parseColor("#7b6fd4") }
        canvas.drawRect(px + ts*0.28f, py + ts*0.42f, px + ts*0.72f, py + ts*0.82f, bodyPaint)
        // Head
        val headPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#f4c98e") }
        canvas.drawCircle(px + ts/2f, py + ts*0.30f, ts*0.22f, headPaint)
        // Hat
        val hatPaint = Paint().apply { color = Color.parseColor("#4a3f8e") }
        canvas.drawRect(px + ts*0.28f, py + ts*0.10f, px + ts*0.72f, py + ts*0.22f, hatPaint)
        // Sword
        val swordPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#c9bfff")
            strokeWidth = ts * 0.07f
            style = Paint.Style.STROKE
        }
        canvas.drawLine(px + ts*0.72f, py + ts*0.45f, px + ts*0.90f, py + ts*0.28f, swordPaint)
    }

    private fun drawEnemy(canvas: Canvas, px: Float, py: Float, ts: Float, enemy: Enemy) {
        val color = when (enemy.type) {
            EnemyType.GOBLIN     -> Color.parseColor("#c0392b")
            EnemyType.ORC        -> Color.parseColor("#e67e22")
            EnemyType.BAT        -> Color.parseColor("#8e44ad")
            EnemyType.TROLL      -> Color.parseColor("#27ae60")
            EnemyType.DARK_MAGE  -> Color.parseColor("#2980b9")
        }
        // Shadow
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.parseColor("#44000000"); style = Paint.Style.FILL
        }
        canvas.drawOval(
            RectF(px + ts*0.25f, py + ts*0.8f, px + ts*0.75f, py + ts*0.95f), shadowPaint
        )
        // Body
        val bodyPaint = Paint().apply { this.color = darken(color, 0.6f) }
        canvas.drawRect(px + ts*0.28f, py + ts*0.42f, px + ts*0.72f, py + ts*0.82f, bodyPaint)
        // Head
        val headPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = color }
        canvas.drawCircle(px + ts/2f, py + ts*0.32f, ts*0.25f, headPaint)
        // Eyes
        val eyePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = Color.WHITE }
        val pupilPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.color = Color.BLACK }
        canvas.drawCircle(px + ts*0.40f, py + ts*0.28f, ts*0.07f, eyePaint)
        canvas.drawCircle(px + ts*0.60f, py + ts*0.28f, ts*0.07f, eyePaint)
        canvas.drawCircle(px + ts*0.40f, py + ts*0.29f, ts*0.04f, pupilPaint)
        canvas.drawCircle(px + ts*0.60f, py + ts*0.29f, ts*0.04f, pupilPaint)
        // Emoji label (small, above head)
        textPaint.textSize = ts * 0.28f
        canvas.drawText(enemy.type.emoji, px + ts/2f, py + ts*0.10f, textPaint)
        // HP bar
        drawEnemyHpBar(canvas, px, py, ts, enemy)
    }

    private fun drawEnemyHpBar(canvas: Canvas, px: Float, py: Float, ts: Float, enemy: Enemy) {
        val barW = ts - 4f
        val barH = ts * 0.10f
        val barX = px + 2f
        val barY = py + ts * 0.88f
        canvas.drawRect(barX, barY, barX + barW, barY + barH, hpBarBgPaint)
        val hpPct = enemy.hp.toFloat() / enemy.maxHp.toFloat()
        val fillColor = when {
            hpPct > 0.5f -> Color.parseColor("#2ecc71")
            hpPct > 0.25f -> Color.parseColor("#f39c12")
            else -> Color.parseColor("#e74c3c")
        }
        val fillPaint = Paint().apply { color = fillColor }
        canvas.drawRect(barX, barY, barX + barW * hpPct, barY + barH, fillPaint)
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    // Encode tile coordinates into a single Long for fast set lookup
    private fun tileKey(x: Int, y: Int): Long = x.toLong() shl 32 or y.toLong()

    // Darken a color by a factor (0 = black, 1 = original)
    private fun darken(color: Int, factor: Float): Int {
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.rgb(r, g, b)
    }
}
