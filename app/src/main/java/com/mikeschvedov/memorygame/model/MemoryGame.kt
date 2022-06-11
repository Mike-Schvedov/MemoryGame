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
    fun startGame(numberOfCards: Int, listOfContent: List<String>): MutableList<Card> {
        //TODO check if number is even

        cards.clear()

        var idCounter = 0
        var idGiver = 0
        var uniqueIdCounter = 0

        for (i in 0 until numberOfCards) {
            cards.add(
                Card(
                    uniqueIdCounter,
                    idGiver,
                    false,
                    false,
                    listOfContent[uniqueIdCounter]
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
        val cardBox: MutableList<Card> = mutableListOf()
        // we find all cards that are facedUP
        cards.forEach { card ->
            if (card.isFaceUp) cardBox.add(card)
        }
        // we check if we have two faced up cards
        if (cardBox.size == 2) {
            // if those cards are matching
            if (cardBox[0].id == cardBox[1].id) {
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
                cardBox[0].apply {
                    isFaceUp = false
                }
                cardBox[1].apply {
                    isFaceUp = false
                }
                return cardBox
            }
        }// if we don't have two faceup cards
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