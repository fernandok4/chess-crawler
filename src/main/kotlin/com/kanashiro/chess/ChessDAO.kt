package com.kanashiro.chess

import com.kanashiro.database.DatabasePoolConnection
import java.sql.Statement

class ChessDAO {

    fun insertChessGame(resultGame: ChessVO.GameResult) {
        val sql = """
            INSERT INTO tb_game(result, nm_event, nm_site, date, round, nm_white_player, nm_black_player, 
            vl_white_elo, vl_black_elo, cd_eco)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        return DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql).use {
                var i = 0
                it.setString(++i, resultGame.result)
                it.setString(++i, resultGame.nmEvent)
                it.setString(++i, resultGame.nmSite)
                it.setString(++i, resultGame.date)
                it.setString(++i, resultGame.round)
                it.setString(++i, resultGame.nmWhitePlayer)
                it.setString(++i, resultGame.nmBlackPlayer)
                it.setString(++i, resultGame.vlWhiteElo)
                it.setString(++i, resultGame.vlBlackElo)
                it.setString(++i, resultGame.cdEco)
                it.execute()
            }
        }
    }

    fun insertChessGames(games: MutableList<ChessVO.GameResult>) {
        val sql = """
            INSERT INTO tb_game(result, nm_event, nm_site, date, round, nm_white_player, nm_black_player, 
            vl_white_elo, vl_black_elo, cd_eco)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        return DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use {
                for(resultGame in games){
                    var i = 0
                    it.setString(++i, resultGame.result)
                    it.setString(++i, resultGame.nmEvent)
                    it.setString(++i, resultGame.nmSite)
                    it.setString(++i, resultGame.date)
                    it.setString(++i, resultGame.round)
                    it.setString(++i, resultGame.nmWhitePlayer)
                    it.setString(++i, resultGame.nmBlackPlayer)
                    it.setString(++i, resultGame.vlWhiteElo)
                    it.setString(++i, resultGame.vlBlackElo)
                    it.setString(++i, resultGame.cdEco)
                    it.execute()
                    val generatedKeys = it.generatedKeys
                    generatedKeys.next()
                    insertMoves(generatedKeys.getLong(1), resultGame.moves)
                }
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

    fun deleteGames() {
        val sql1 = """
            DELETE FROM tb_game_turns WHERE id_game >= 0
        """.trimIndent()
        val sql2 = """
            DELETE FROM tb_game WHERE id_game >= 0
        """.trimIndent()
        DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql1).execute()
            it.prepareStatement(sql2).execute()
        }
    }

    fun insertGames(games: MutableList<ChessVO.GameResult>) {
        insertChessGames(games)
    }
}