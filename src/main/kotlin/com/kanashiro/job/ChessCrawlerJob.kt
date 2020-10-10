package com.kanashiro.job

import com.kanashiro.chess.ChessDAO
import com.kanashiro.crawler.ChessDatabaseCrawler
import com.kanashiro.utils.Exceptions
import java.lang.Exception
import java.util.concurrent.Callable

class ChessCrawlerJob(val idGame: Long): Callable<Boolean> {

    val crawler = ChessDatabaseCrawler()
    val chessDAO = ChessDAO()

    override fun call(): Boolean {
        try {
            val moves = crawler.readGame(idGame)
            chessDAO.insertChessGame(idGame, moves.last().dsMovement)
            moves.removeAt(moves.size - 1)
            chessDAO.insertMoves(idGame, moves)
            return true
        } catch (e: Exceptions.InexistantGame){
            return true
        } catch (e: Exception){
            println("Erro no jogo: " + idGame)
            e.printStackTrace()
            return false
        }
    }
}