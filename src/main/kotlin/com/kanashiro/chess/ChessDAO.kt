package com.kanashiro.chess

import com.kanashiro.crawler.ChessDatabaseCrawler
import com.kanashiro.database.DatabasePoolConnection

class ChessDAO {

    fun insertChessGame(idGame: Long, resultGame: String) {
        val sql = """
            INSERT INTO tb_game(id_game, result)
            VALUES(?, ?)
        """.trimIndent()
        return DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql).use {
                it.setLong(1, idGame)
                it.setString(2, resultGame)
                it.execute()
            }
        }
    }

    fun insertMoves(idGame: Long, moves: MutableList<ChessDatabaseCrawler.ChessMoves>) {
        val sql = """
            INSERT INTO tb_game_turns(id_game, id_turn, ds_turn_moves)
            VALUES(?, ?, ?)
        """.trimIndent()
        DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql).use {
                for(move in moves){
                    it.setLong(1, idGame)
                    it.setInt(2, move.idMove)
                    it.setString(3, move.dsMovement)
                    it.addBatch()
                }
                it.executeBatch()
            }
        }
    }
}