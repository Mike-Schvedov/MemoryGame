package com.mikeschvedov.memorygame.model

import com.mikeschvedov.memorygame.R

class MemoryGame(private var cards: MutableList<Card> = mutableListOf()) {


    fun cardTapped(card: Card): MutableList<Card> {
        println("flipping the card | card: $card")
        card.isFaceUp = true
        println("returning new list")
        return cards
    }

    // creating a new deck/list of cards
    fun startGame(numberOfCards: Int): MutableList<Card> {
        //TODO check if number is even

        cards.clear()

        var idCounter = 0
        var idGiver = 0
        var uniqueIdCounter = 0

        val arrOfPictures = listOf(
            R.drawable.card1,
            R.drawable.card2,
            R.drawable.card3,
            R.drawable.card4,
            R.drawable.card5,
            R.drawable.card6,
            R.drawable.card7,
            R.drawable.card8
        )

        for (i in 0 until numberOfCards) {
            cards.add(
                Card(
                    uniqueIdCounter,
                    idGiver,
                    false,
                    false,
                    arrOfPictures[idGiver]
                )
            )
            idCounter++
            uniqueIdCounter++
            if (idCounter == 2) {
                idGiver++
                idCounter = 0
            }
        }
        return cards
    }

    fun checkIfMatched(): MutableList<Card> {
        println("---------------------")
        println("check if matched")
        val cardBox: MutableList<Card> = mutableListOf()
        // we find all cards that are facedUP
        cards.forEach { card ->
            if (card.isFaceUp) cardBox.add(card)
        }
        println("this is the card box: $cardBox")
        // we check if we have two faced up cards
        if (cardBox.size == 2) {
            println("cardbox size is 2")
            // if those cards are matching
            if (cardBox[0].id == cardBox[1].id) {
                println("the cards are matching")
                cardBox[0].apply {
                    isMatched = true
                    isFaceUp = false
                }
                cardBox[1].apply {
                    isMatched = true
                    isFaceUp = false
                }
                return cards
            } else { // if they are not matching
                println("the cards are not matching")
                cardBox[0].apply {
                    isFaceUp = false
                }
                cardBox[1].apply {
                    isFaceUp = false
                }
                return cardBox
            }

        }// if we don't have two faceup cards
        println("cardbox size is not 2")
        return cards
    }

    fun checkIfRoundOver(): Boolean {
        cards.forEach {
            // if at least one card is not matched
            if (!it.isMatched) {
                return false
            }
        }
        return true
    }
}