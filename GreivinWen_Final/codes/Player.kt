package com.wenwu.greivinwen_final_project.model

data class Player(
    var x: Int = 0,
    var y: Int = 0,
    var hp: Int = 20,
    var maxHp: Int = 20,
    var atk: Int = 4,
    var potions: Int = 2,
    var level: Int = 1,         // levels up every 3 floors
    var enemiesKilled: Int = 0, // tracked for death recap
    var weaponTier: Int = 0     // 0 = none, goes up with weapon pickups
) {
    val hpPercent: Float get() = hp.toFloat() / maxHp.toFloat()
    val isDead: Boolean get() = hp <= 0

    // Called every 3 floors automatically
    fun levelUp() {
        level++
        maxHp += 2
        hp = minOf(hp + 2, maxHp)  // heal a little on level up
        atk += 1
    }
}
