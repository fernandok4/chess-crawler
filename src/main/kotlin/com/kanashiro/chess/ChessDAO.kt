package com.kanashiro.chess

import com.kanashiro.database.DatabasePoolConnection
import java.sql.Statement

class ChessDAO {

    val fetchSize = 2500

    fun insertChessGames(games: MutableList<ChessVO.GameResult>) {
        val sql = """
            INSERT INTO tb_game(id_game, result, nm_event, nm_site, date, round, nm_white_player, nm_black_player, 
            vl_white_elo, vl_black_elo, cd_eco)
            VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()
        return DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS).use {
                for((index, resultGame) in games.withIndex()){
                    var i = 0
                    resultGame.idGame = index.toLong() + 1
                    it.setLong(++i, resultGame.idGame)
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
                    it.addBatch()
                    if(index % fetchSize == (fetchSize - 1) && resultGame.idGame != games.size.toLong()){
                        it.executeBatch()
                    }
                }
                it.executeBatch()
            }
        }
    }

    fun getAllGames(page: Int, limit: Int): MutableMap<Long, ChessVO.GameResult> {
        val sql = """
            SELECT 
                id_game, result, nm_event, nm_site, date, round, nm_white_player, nm_black_player, 
                vl_white_elo, vl_black_elo, cd_eco, id_turn, ds_turn_moves
            FROM (
                SELECT * FROM tb_game LIMIT $limit OFFSET ${page * limit}
            ) tb_game
            INNER JOIN tb_game_turns USING(id_game)
        """.trimIndent()
        val result = mutableMapOf<Long, ChessVO.GameResult>()
        DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql).executeQuery().use {
                while (it.next()){
                    if(result[it.getLong("id_game")] == null){
                        result[it.getLong("id_game")] = ChessVO.GameResult(
                                result = it.getString("result"),
                                nmEvent = it.getString("nm_event"),
                                nmSite = it.getString("nm_site"),
                                date = it.getString("date"),
                                round = it.getString("round"),
                                nmWhitePlayer = it.getString("nm_white_player"),
                                nmBlackPlayer = it.getString("nm_black_player"),
                                vlWhiteElo = it.getString("vl_white_elo"),
                                vlBlackElo = it.getString("vl_black_elo"),
                                cdEco = it.getString("cd_eco"),
                                idGame = it.getLong("id_game")
                        )
                    }
                    result[it.getLong("id_game")]!!.moves.add(ChessVO.CrawledGameMoves(it.getInt("id_turn"), it.getString("ds_turn_moves")))
                }
            }
        }
        return result
    }

    fun deleteGames() {
        val sql1 = """
            DELETE FROM tb_game_turns
        """.trimIndent()
        val sql2 = """
            DELETE FROM tb_game
        """.trimIndent()
        DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql1).execute()
            it.prepareStatement(sql2).execute()
        }
    }

    fun insertChessGamesMovements(games: MutableList<ChessVO.GameResult>) {
        val sql = """
            INSERT INTO tb_game_turns(id_game, id_turn, ds_turn_moves)
            VALUES(?, ?, ?)
        """.trimIndent()
        var fetchCount = 0
        DatabasePoolConnection.getConnection().use {
            it.prepareStatement(sql).use {
                for(game in games){
                    for(move in game.moves){
                        var i = 0
                        it.setLong(++i, game.idGame)
                        it.setInt(++i, move.idMove)
                        it.setString(++i, move.dsMovement)
                        it.addBatch()
                        fetchCount++
                        if(fetchCount % fetchSize == (fetchSize - 1) && fetchCount != games.size){
                            it.executeBatch()
                            fetchCount = 0
                        }
                    }
                }
                if(fetchCount != 0){
                    it.executeBatch()
                }
            }
        }
    }
}