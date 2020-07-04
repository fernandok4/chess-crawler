package com.kanashiro.job

import com.kanashiro.chess.ChessDAO
import com.kanashiro.crawler.ChessDatabaseCrawler
import java.lang.Exception
import java.util.concurrent.Callable

class ChessCrawlerJob(val idGame: Long): Callable<Boolean> {

    val crawler = ChessDatabaseCrawler()
    val chessDAO = ChessDAO()

    override fun call(): Boolean {
        try {
            val moves = crawler.readGame(idGame)
            val idGame = chessDAO.insertChessGame(moves.last().dsMovement.split(" ").last())
            chessDAO.insertMoves(idGame, moves)
            return true
        } catch (e: Exception){
            e.printStackTrace()
            return false
        }
    }
}