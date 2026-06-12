package com.wenwu.greivinwen_final_project.model

enum class ItemType(val displayName: String, val emoji: String) {
    POTION ("Health Potion", "🧪"),
    WEAPON ("Weapon Upgrade", "⚔️")
}

data class Item(
    var x: Int,
    var y: Int,
    val type: ItemType
)
