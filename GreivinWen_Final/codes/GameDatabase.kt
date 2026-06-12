package com.wenwu.greivinwen_final_project.data

import android.content.Context
import androidx.room.*

// ── Entity ────────────────────────────────────────────────────────────────────

@Entity(tableName = "scores")
data class ScoreEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int,
    val floorReached: Int,
    val enemiesKilled: Int,
    val killedBy: String,       // e.g. "Troll" or "Orc"
    val timestamp: Long = System.currentTimeMillis()
)

// ── DAO ───────────────────────────────────────────────────────────────────────

@Dao
interface ScoreDao {
    @Insert
    suspend fun insert(score: ScoreEntity)

    // Top 10 scores ordered by score descending
    @Query("SELECT * FROM scores ORDER BY score DESC LIMIT 10")
    suspend fun getTopScores(): List<ScoreEntity>

    // Personal best score
    @Query("SELECT MAX(score) FROM scores")
    suspend fun getHighScore(): Int?

    // Personal best floor
    @Query("SELECT MAX(floorReached) FROM scores")
    suspend fun getDeepestFloor(): Int?

    @Query("DELETE FROM scores")
    suspend fun clearAll()
}

// ── Database ──────────────────────────────────────────────────────────────────

@Database(entities = [ScoreEntity::class], version = 1, exportSchema = false)
abstract class GameDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile private var INSTANCE: GameDatabase? = null

        fun getInstance(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "dungeon_scores.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
