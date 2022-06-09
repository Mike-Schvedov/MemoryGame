package com.mikeschvedov.memorygame.model

class Card(
    var uniqueId: Int,
    var id: Int,
    var isFaceUp: Boolean = false,
    var isMatched: Boolean = false,
    var cardPicture: Int
){
    override fun toString(): String {
        return "ID:{$id} uniqueId:{$uniqueId} isFaceUp:{$isFaceUp} isMatched:{$isMatched}\n"
    }
}