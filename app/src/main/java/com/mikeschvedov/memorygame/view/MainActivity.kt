package com.mikeschvedov.memorygame.view

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mikeschvedov.memorygame.R
import com.mikeschvedov.memorygame.databinding.ActivityMainBinding
import com.mikeschvedov.memorygame.model.Card
import com.mikeschvedov.memorygame.model.Level
import com.mikeschvedov.memorygame.utils.Constants
import com.mikeschvedov.memorygame.viewmodel.AppViewModel


class MainActivity : AppCompatActivity() {
    /* -------- Activity Properties -------- */
    // ------ setting the binding  ------ //
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    // creating a list to hold the button views
    private val buttonList: MutableList<Button> = mutableListOf()

    // declaring the view model
    lateinit var appViewModel: AppViewModel

    // to check if it is the first round since app created
    var isFirstEver: Boolean = true
    var isDataReady: Boolean = false



    /* ------------- onCreate ------------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  ------ instantiating the ViewModel ------ //
        appViewModel =
            ViewModelProvider(this)[AppViewModel::class.java]
        lifecycleScope.launchWhenCreated {
            appViewModel.requestApiData()
        }
        /* ------------- LiveData Observers ------------- */
        // ------- observing for new list ----- //
        appViewModel.cardListForUI.observe(this) { observedList ->
            updateList(observedList)
        }
        // ------- observing for animation ----- //
        appViewModel.animationTrigger.observe(this) { matchingList ->
            delayAnimation(matchingList)
        }
        // ------- observing for round end----- //
        appViewModel.isRoundOver.observe(this) { isRoundOver ->
            if (isRoundOver) triggerRoundOver()
        }
        // ------- observing for the timer----- //
        appViewModel.timer.observe(this) { timer ->
            binding.timerTextview.text = timer.toString()
        }
        // ------- observing for data fetch----- //
        appViewModel.isDataReady.observe(this) { ready ->
            isDataReady = ready
        }
        /* ------------- onClick Starting a new game ------------- */
        binding.button.setOnClickListener {
            // if its the first time we run the game, get api data
            if (isFirstEver){

            }
            //restarting the level count
            appViewModel.freshGame()
            if(isDataReady){
                startNewRound(appViewModel.getLevel())
            }else{
                Toast.makeText(this, "Data not ready yet, try again or check your internet connection", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun startNewRound(levelData: Level) {
        // checking if its the first run, so we make sure the timer is initialized
        if (!isFirstEver) {
            appViewModel.cancelTimer()
        }
        isFirstEver = false
        // removing previous buttons and ids
        resetOldButtons()
        // creating new buttons
        createButtons(levelData.cardsForLevel)
        // setting MaxElementsWrap according to numOfCards
        decideElementWrap(levelData.cardsForLevel)
        // creating a new list of Card models
        appViewModel.startNewGame(levelData)
        // setting the current level ui
        setCurrentLevel(appViewModel.getCurrentLevel())
        startTimer()
    }

    private fun setCurrentLevel(currentLevel: String) {
        binding.currentLevelTextview.text = currentLevel
    }

    private fun startTimer() {
        appViewModel.startTimer()
    }

    private fun decideElementWrap(numOfCards: Int) {
        when (numOfCards) {
            4 -> binding.flow.setMaxElementsWrap(2) //2x2
            12 -> binding.flow.setMaxElementsWrap(4)
            16 -> binding.flow.setMaxElementsWrap(4)
            36 -> binding.flow.setMaxElementsWrap(6)
        }
    }

    private fun resetOldButtons() {
        // removing all previous buttons created
        buttonList.forEach {
            binding.root.removeView(it)
        }
        // removing previous ids referenced in flow
        binding.flow.referencedIds = intArrayOf()
        // clearing the button list
        buttonList.clear()
    }

    private fun createButtons(numOfCards: Int) {
        repeat(numOfCards) {
            // creating a new button
            val button = Button(this)
            // generating an id for it
            button.id = View.generateViewId()
            // setting the card back side image
            button.setBackgroundResource(Constants.BACK_IMAGE)
            // setting width and height
            button.layoutParams = ConstraintLayout.LayoutParams(0, 0)
            //text size
            button.textSize = 64F
            button.gravity = Gravity.CENTER
            //text color
            button.setTextColor(Color.WHITE)
            //text font
            button.typeface = Typeface.MONOSPACE
            // setting the radio
            (button.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
            // adding button to root layout
            binding.root.addView(button)
            // adding the button to the flows referenced Id's
            binding.flow.referencedIds += button.id
            // adding the button to button list
            buttonList.add(button)
        }
    }

    private fun triggerRoundOver() {
        appViewModel.cancelTimer()
        // increasing level count
        appViewModel.increaseLevel()
        // starting new round according to new level
        startNewRound(appViewModel.getLevel())
    }

    private fun updateList(observedList: List<Card>) {
        observedList.forEachIndexed { index, uiCard ->
            with(buttonList[index]) {
                // if cards are matched change the alpha and make clickable
                if (uiCard.isMatched) {
                    this.alpha = 0.5f
                    this.isClickable = false
                }
                // card on click listener
                this.setOnClickListener {
                    if (appViewModel.cardTapped(uiCard)) {
                        flipAnimation(it as Button, uiCard)
                        //we check if we have to matching cards
                        appViewModel.checkIfMatched()
                    }
                }
            }
        }
    }

    private fun flipAnimation(view: Button, uiCard: Card) {
        val anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
            .apply { interpolator = DecelerateInterpolator() }
        val anim2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            .apply { interpolator = AccelerateInterpolator() }
        anim1.doOnEnd {
            view.setBackgroundResource(Constants.FRONT_IMAGE)
            view.text = uiCard.cardContent
            anim2.start()
            Handler().postDelayed({
                appViewModel.checkIfRoundIsOver()
            }, 2000)
        }
        anim1.start()
    }

    private fun delayAnimation(list: List<Card>) {
        list.forEach {
            val view = buttonList[it.uniqueId]
            val anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
                .apply {
                    interpolator = DecelerateInterpolator()
                    startDelay = 1000
                }
            val anim2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
                .apply { interpolator = AccelerateInterpolator() }
            anim1.doOnEnd {
                view.setBackgroundResource(Constants.BACK_IMAGE)
                anim2.start()
            }
            anim1.start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

