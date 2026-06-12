package com.wenwu.greivinwen_final_project.model

enum class EnemyType(
    val displayName: String,
    val emoji: String,
    val baseHp: Int,
    val baseAtk: Int,
    val moveRange: Int,      // tiles moved per turn
    val attackRange: Int,    // 1 = melee, 2 = ranged
    val aggroRadius: Int,    // how far it detects the player
    val introFloor: Int      // first floor it can appear on
) {
    GOBLIN  ("Goblin",     "👺", baseHp = 4,  baseAtk = 2, moveRange = 1, attackRange = 1, aggroRadius = 4, introFloor = 1),
    ORC     ("Orc",        "👹", baseHp = 8,  baseAtk = 4, moveRange = 1, attackRange = 1, aggroRadius = 4, introFloor = 2),
    BAT     ("Bat",        "🦇", baseHp = 3,  baseAtk = 2, moveRange = 2, attackRange = 1, aggroRadius = 6, introFloor = 4),
    TROLL   ("Troll",      "🧌", baseHp = 14, baseAtk = 5, moveRange = 1, attackRange = 1, aggroRadius = 3, introFloor = 6),
    DARK_MAGE("Dark Mage", "🧙", baseHp = 5,  baseAtk = 6, moveRange = 1, attackRange = 2, aggroRadius = 5, introFloor = 8)
}

data class Enemy(
    var x: Int,
    var y: Int,
    val type: EnemyType,
    var hp: Int,
    val maxHp: Int,
    val atk: Int,
    val moveRange: Int = type.moveRange,
    val attackRange: Int = type.attackRange,
    val aggroRadius: Int = type.aggroRadius,
    var isAggro: Boolean = false,   // has the player been spotted?
    var regenCooldown: Int = 0      // used by Troll regen
)
