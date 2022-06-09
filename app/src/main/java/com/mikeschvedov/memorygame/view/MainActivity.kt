package com.mikeschvedov.memorygame.view

import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Button
import androidx.core.animation.doOnEnd
import androidx.lifecycle.ViewModelProvider
import com.mikeschvedov.memorygame.R
import com.mikeschvedov.memorygame.databinding.ActivityMainBinding
import com.mikeschvedov.memorygame.model.Card
import com.mikeschvedov.memorygame.utils.referencedViews
import com.mikeschvedov.memorygame.viewmodel.AppViewModel
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    /* -------- Activity Properties -------- */
    // ------ setting the _binding to be nullable ------ //
    private var _binding: ActivityMainBinding? = null

    // ------ creating a version that is not nullable ------ //
    private val binding get() = _binding!!

    lateinit var appViewModel: AppViewModel

    val buttonList: MutableList<Button> = mutableListOf()

    /* ------------- onCreate ------------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //  ------ instantiating the ViewModel ------ //
        appViewModel =
            ViewModelProvider(this)[AppViewModel::class.java]
        /* ------------- LiveData Observers ------------- */
        // ------- observing for new list ----- //
        appViewModel.cardListForUI.observe(this) { observedList ->
            updateList(observedList)
        }
        // ------- observing for animation ----- //
        appViewModel.animationTrigger.observe(this) { matchingList ->
            delayAnimation(matchingList)
        }
        // ------- observing for new list ----- //
        appViewModel.isRoundOver.observe(this) { isRoundOver ->
            if(isRoundOver) triggerRoundOver()
        }
        /* ------------- onClick Starting a new game ------------- */
        binding.button.setOnClickListener {
            //restarting the level count
            appViewModel.freshGame()
            println("this level ${appViewModel.levelManager.getCurrentLevel()}")
            println("cards for level ${appViewModel.getCardsForLevel()}")
            startNewRound(appViewModel.getCardsForLevel())
        }
    }

    private fun startNewRound(numOfCards: Int) {
        buttonList.forEach {
            it.visibility = View.GONE
        }
        buttonList.clear()
        binding.flow.setMaxElementsWrap(numOfCards / 2)
        appViewModel.startNewGame(numOfCards)
        (0 until numOfCards).forEach { int ->
            val view = binding.flow.referencedViews()[int]
                .also {
                    it.visibility = View.VISIBLE
                    it.alpha = 1f
                    it.setBackgroundResource(R.drawable.card_bg)
                }
            buttonList.add(view)
            println("NUMBER OF BUTTONS: ${buttonList.size}")
        }
    }

    private fun triggerRoundOver() {
        println("---------- TRIGGERING NEW LEVEL ----------")
        println("previous level ${appViewModel.levelManager.getCurrentLevel()}")
        appViewModel.increaseLevel()
        println("current level ${appViewModel.levelManager.getCurrentLevel()}")
        startNewRound(appViewModel.getCardsForLevel())
    }

    private fun updateList(observedList: List<Card>) {
        println("----UPDATE NEW LIST----:\n $observedList")
        observedList.forEachIndexed { index, uiCard ->
            with(buttonList[index]) {
                // if cards are matched change the alpha and make clickable
                if (uiCard.isMatched) {
                    this.alpha = 0.5f
                    this.isClickable = false
                }
                // card on click listener
                this.setOnClickListener {
                    println("WE CLICKED ON THIS BUTTON: $uiCard ")
                    if (appViewModel.cardTapped(uiCard)) {
                        flipAnimation(it, uiCard)
                        //we check if we have to matching cards
                        appViewModel.checkIfMatched()
                    }
                }
            }
        }
    }

    private fun flipAnimation(view: View, uiCard: Card) {
        println("PERFORMING FLIP ANIMATION ON CARD: $uiCard")
        val anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
            .apply { interpolator = DecelerateInterpolator() }
        val anim2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
            .apply { interpolator = AccelerateInterpolator() }
        val anim3 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1f)
            .apply { interpolator = AccelerateInterpolator()
                startDelay = 2500}
        anim1.doOnEnd {
            view.setBackgroundResource(uiCard.cardPicture)
            anim2.start()
            anim2.doOnEnd {
                anim3.start()
                anim3.doOnEnd {
                    appViewModel.checkIfRoundIsOver()
                }
            }
        }
        anim1.start()
    }

    private fun delayAnimation(list: List<Card>) {
        list.forEach {
            val view = buttonList[it.uniqueId]
            println("delay animation on the button of: ${it.id}")
            val anim1 = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0f)
                .apply {
                    interpolator = DecelerateInterpolator()
                    startDelay = 1000
                }
            val anim2 = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1f)
                .apply { interpolator = AccelerateInterpolator() }
            anim1.doOnEnd {
                view.setBackgroundResource(R.drawable.card_bg)
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

