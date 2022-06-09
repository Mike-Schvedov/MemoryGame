package com.mikeschvedov.memorygame.model

class LevelManager() {
    private var currentLevel: Int = 1
    val cardsForLevel get() = currentLevel * 4

    fun increaseLevel() {
        currentLevel += 1
    }

    fun getCurrentLevel(): Int {
        return currentLevel
    }

    fun freshGame(): Int {
        currentLevel = 1
        return currentLevel
    }
}