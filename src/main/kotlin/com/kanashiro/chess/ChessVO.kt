package com.kanashiro.chess

class ChessVO {

    data class GameResult(
            var result: String = "",
            var moves: MutableList<CrawledGameMoves> = mutableListOf()
    )

    data class CrawledGameMoves(
            val idMove: Int = 1,
            var dsMovement: String = ""
    )
}