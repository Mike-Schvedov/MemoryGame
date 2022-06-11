package com.mikeschvedov.memorygame.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mikeschvedov.memorygame.model.Card
import com.mikeschvedov.memorygame.model.Level
import com.mikeschvedov.memorygame.model.LevelManager
import com.mikeschvedov.memorygame.model.MemoryGame

import com.mikeschvedov.memorygame.network.RetrofitManager
import com.mikeschvedov.memorygame.utils.Logger


class AppViewModel : ViewModel() {

    // model instance
    lateinit var  game: MemoryGame
    private val levelManager = LevelManager{
        //updating the livedata from the callback
        _timerCount.postValue(formatTime(it))
    }

    var responseLetters: List<String> = listOf()
    var responseNumbers: List<String> = listOf()

    private var apiManager = RetrofitManager { numbers, letters, isReady ->
        if (numbers != null) {
            responseNumbers = numbers.map { it.number.toString() }
        }
        if (letters != null) {
            responseLetters = letters.map { it.letter }
        }
        _isDataReady.postValue(isReady)
    }

    // the private livedata we update
    private val _uiCardList = MutableLiveData<List<Card>>()
    private val _animationTrigger = MutableLiveData<List<Card>>()
    private val _isRoundOver = MutableLiveData<Boolean>()
    private val _timerCount = MutableLiveData<String>()
    private val _isDataReady = MutableLiveData<Boolean>()

    // the observable/exposed data for the ui
    val cardListForUI get() = _uiCardList
    val animationTrigger get() = _animationTrigger
    val isRoundOver get() = _isRoundOver
    val timer get() = _timerCount
    val isDataReady get() = _isDataReady

    fun startNewGame(levelData: Level) {
        game = MemoryGame()
        // get subject data according to code
        val listOfContent = getCurrentSubjectData(levelData.subjectForLevel)
        // get the list from the Model
        val list = game.startGame(levelData.cardsForLevel, listOfContent)
        // update the livedata with this list
        _uiCardList.postValue(list)
    }

    private fun getCurrentSubjectData(subjectForLevel: Int): List<String> {
        return when(subjectForLevel){
            1-> responseLetters
            2 -> responseNumbers
            else-> {
                Logger.e("TEST", "RETURNED EMPTY")
                listOf()
            }
        }
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

    fun getLevel(): Level {
       return levelManager.getLevel()
    }

    fun freshGame(): Int {
        return levelManager.freshGame()
    }

    fun startTimer(){
        levelManager.startNewTimer()
    }

    fun cancelTimer(){
        levelManager.cancelTimer()
    }

    private fun formatTime(_timerCount: Int) : String {
        val h = _timerCount / 60
        val m = _timerCount % 60
        return String.format("%02d:%02d", h, m) // output : "02:00"
    }

    fun getCurrentLevel(): String {
        return "Level: ${levelManager.getCurrentLevel()}"
    }

    suspend fun requestApiData() {
        apiManager.getNumbersResponse()
        apiManager.getLettersResponse()
    }


}