package com.kanashiro.crawler

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup

class ChessDatabaseCrawler {
//    https://www.chessgames.com/perl/chessgame?gid=1013642

    private val CHESS_GAMES_URL = "https://www.chessgames.com"

    fun readGame(idGame: Long): MutableList<ChessMoves> {
        val httpClient = HttpClients.custom().setConnectionManager(HttpPoolConnection.poolManager).build()
        val request = HttpGet("$CHESS_GAMES_URL/perl/chessgame?gid=$idGame")
        val response = httpClient.execute(request)
        val bodyResponse = EntityUtils.toString(response.entity)
        return getGameMoves(bodyResponse)
    }

    fun getGameMoves(html: String): MutableList<ChessMoves> {
        val document = Jsoup.parse(html)
        val gameInfo = document.getElementById("olga-data")
        val attribute = gameInfo.attr("pgn").split("]").last()
        val turnsRegex = Regex("(\\d+.O-O \\w+)|(\\d+.\\w+\\+? O-O)|(\\d+.O-O-O \\w+)|(\\d+.\\w+\\+? O-O-O)|(\\d+.O-O O-O-O)|(\\d+.O-O O-O)|(\\d+.O-O-O O-O)|(\\d+.O-O-O O-O-O)|(\\d+.\\w+\\+? \\w+\\+?)")
        val gameResultRegex = Regex("(1/2-1/2)|(1-0)|(0-1)")
        val result = gameResultRegex.findAll(attribute).first().value
        val moves = turnsRegex.findAll(attribute).toList().mapTo(mutableListOf()) {
            val move = it.value.split(".")
            ChessMoves(move.first().toInt(), move.last())
        }
        moves[moves.lastIndex].dsMovement = moves.last().dsMovement.removeSuffix("1").removeSuffix("0") + result
        return moves
    }

    data class ChessMoves(
        val idMove: Int = 1,
        var dsMovement: String = ""
    )
}