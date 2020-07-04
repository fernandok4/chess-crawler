package com.kanashiro.chess

import com.kanashiro.crawler.ChessDatabaseCrawler
import com.kanashiro.database.DatabasePoolConnection
import com.mysql.jdbc.Statement

class ChessDAO {

    fun insertChessGame(resultGame: String): Long {
        val sql = """
            INSERT INTO tb_game(result)
            VALUES(?)
        """.trimIndent()
        return DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use {
                it.setString(1, resultGame)
                it.execute()
                val keys = it.generatedKeys
                keys.next()
                keys.getLong(1)
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