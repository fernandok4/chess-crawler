package com.kanashiro.chess

class ChessVO {

    data class GameResult(
            var result: String = "",
            var nmEvent: String = "",
            var nmSite: String = "",
            var date: String = "",
            var round: String = "",
            var nmWhitePlayer: String = "",
            var nmBlackPlayer: String = "",
            var vlWhiteElo: String = "",
            var vlBlackElo: String = "",
            var cdEco: String = "",
            var moves: MutableList<CrawledGameMoves> = mutableListOf()
    )

    data class CrawledGameMoves(
            val idMove: Int = 1,
            var dsMovement: String = ""
    )
}