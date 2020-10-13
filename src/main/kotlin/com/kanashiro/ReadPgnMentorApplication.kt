package com.kanashiro

import com.kanashiro.chess.ChessDAO
import com.kanashiro.chess.ChessVO
import com.kanashiro.crawler.ChessPgnMentorCrawler

class ReadPgnMentorApplication {
    companion object {

        val downloadAllGames = System.getenv().getOrDefault("ENV_DOWNLOAD_ALL_GAMES", "0")
        val crawler = ChessPgnMentorCrawler()
        val chessDAO = ChessDAO()

        @JvmStatic
        fun main(args: Array<String>) {
            if(downloadAllGames == "1"){
                crawler.downloadFiles()
            }
            val games = crawler.parsePgnFiles()
            insertChessGames(games)
        }

        private fun insertChessGames(games: MutableList<ChessVO.GameResult>) {
            chessDAO.deleteGames()
            chessDAO.insertChessGames(games)
            chessDAO.insertChessGamesMovements(games)
        }
    }
}