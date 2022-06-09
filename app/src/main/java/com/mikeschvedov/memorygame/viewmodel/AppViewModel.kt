package com.mikeschvedov.memorygame.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mikeschvedov.memorygame.model.Card
import com.mikeschvedov.memorygame.model.LevelManager
import com.mikeschvedov.memorygame.model.MemoryGame

class AppViewModel : ViewModel() {
    // model instance
    lateinit var  game: MemoryGame
    val levelManager: LevelManager = LevelManager()

    // the private livedata we update
    private val _uiCardList = MutableLiveData<List<Card>>()
    private val _animationTrigger = MutableLiveData<List<Card>>()
    private val _isRoundOver = MutableLiveData<Boolean>()

    // the observable/exposed data for the ui
    val cardListForUI get() = _uiCardList
    val animationTrigger get() = _animationTrigger
    val isRoundOver get() = _isRoundOver

    fun startNewGame(numOfCards: Int) {
        game = MemoryGame()
        // get the list from the Model
        val list = game.startGame(numOfCards)
        // update the livedata with this list
        _uiCardList.postValue(list)
    }

    fun cardTapped(card: Card): Boolean {
        // flip the card only if it is not faced up and not matched
        return if (!card.isFaceUp && !card.isMatched) {
            println("card is not face up")
            _uiCardList.postValue(game.cardTapped(card))
            true
        } else {
            println("card is face up - doing nothing")
            false
        }
    }

    fun checkIfMatched() {
        //checking if we have a match
       val newList = game.checkIfMatched()
        if(newList.size == 2){
            // if we only get two cards (no matching)
            _animationTrigger.postValue(newList)
        }else{
            // if we get the full list (did get a match)
            _uiCardList.postValue(newList)
        }
    }

    fun checkIfRoundIsOver(){
        println("CHECKING IF ROUND IS OVER")
        // check if game over and update the livedata
        println("Is ROUND OVER: ${game.checkIfRoundOver()}")
        _isRoundOver.postValue(game.checkIfRoundOver())
    }

    fun increaseLevel(){
        levelManager.increaseLevel()
    }

    fun getCardsForLevel(): Int {
       return levelManager.cardsForLevel
    }

    fun freshGame(): Int {
        return levelManager.freshGame()
    }
}