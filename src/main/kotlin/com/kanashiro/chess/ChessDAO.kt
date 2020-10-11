package com.kanashiro.chess

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

    fun insertMoves(idGame: Long, moves: MutableList<ChessVO.CrawledGameMoves>) {
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

    fun getAllGames(): MutableMap<Long, ChessVO.GameResult> {
        val sql = """
            SELECT 
                id_game, result, id_turn, ds_turn_moves
            FROM tb_game
            INNER JOIN tb_game_turns USING(id_game)
        """.trimIndent()
        val result = mutableMapOf<Long, ChessVO.GameResult>()
        DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql).executeQuery().use {
                while (it.next()){
                    if(result[it.getLong("id_game")] == null){
                        result[it.getLong("id_game")] = ChessVO.GameResult(result = it.getString("result"))
                    }
                    result[it.getLong("id_game")]!!.moves.add(ChessVO.CrawledGameMoves(it.getInt("id_turn"), it.getString("ds_turn_moves")))
                }
            }
        }
        return result
    }
}