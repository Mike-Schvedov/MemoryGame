package com.mikeschvedov.memorygame.model

import android.os.CountDownTimer
import com.mikeschvedov.memorygame.utils.GameSubjects

class LevelManager(
    private val listener: OnTimerChangeListener
) {
    private var currentLevel: Int = 1
    val cardsForLevel get() = decideCardForThisLevel(currentLevel)
    private val subjectForLevel get() = decideSubjectForLevel(currentLevel)
    var roundTimer: Int = 0
    lateinit var timer: CountDownTimer


    fun increaseLevel() {
        currentLevel += 1
    }

    fun getLevel(): Level {
        return Level(cardsForLevel, subjectForLevel)
    }

    private fun decideSubjectForLevel(currentLevel: Int): Int {
        return when (currentLevel){
            1 -> GameSubjects.NUMBERS.code
            2 -> GameSubjects.NUMBERS.code
            3 -> GameSubjects.NUMBERS.code
            else -> {
                1
            }
        }
    }

    private fun decideCardForThisLevel(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 4 // 2x2
            2 -> 12 // 2x3
            3 -> 16 // 4x4
            else -> {
                0
            }
        }
    }

    fun freshGame(): Int {
        this.currentLevel = 1
        return this.currentLevel
    }


    fun startNewTimer() {
        timer = object : CountDownTimer(999000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                roundTimer++
                listener.onTimerChanged(roundTimer)
            }
            override fun onFinish() {
            }
        }
        timer.start()
    }

    fun cancelTimer() {
        roundTimer = 0
        timer.cancel()
    }

    fun getCurrentLevel(): Int {
        return this.currentLevel
    }

    fun interface OnTimerChangeListener {
        fun onTimerChanged(time: Int)
    }
}