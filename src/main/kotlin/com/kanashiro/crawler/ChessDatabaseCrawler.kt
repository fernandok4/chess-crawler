package com.kanashiro.crawler

import com.kanashiro.utils.Exceptions
import org.apache.commons.logging.LogFactory
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup

class ChessDatabaseCrawler {

    private val CHESS_GAMES_URL = "https://www.chessgames.com"
    val logger = LogFactory.getLog(ChessDatabaseCrawler::class.java)

    fun readGame(idGame: Long): MutableList<ChessMoves> {
        logger.info("Starting to read game id: ${idGame}")
        val httpClient = HttpClients.custom().setConnectionManager(HttpPoolConnection.poolManager).build()
        val gameUrl = "$CHESS_GAMES_URL/perl/chessgame?gid=$idGame"
        val request = HttpGet(gameUrl)
        val response = httpClient.execute(request)
        val bodyResponse = EntityUtils.toString(response.entity)
        val gameMoves = getGameMoves(bodyResponse)
        logger.info("Finishing to read game id: ${idGame}")
        return gameMoves
    }

    fun getGameMoves(html: String): MutableList<ChessMoves> {
        val document = Jsoup.parse(html)
        val gameInfo = document.getElementById("olga-data")
        if(gameInfo == null){
            throw Exceptions.InexistantGame()
        }
        val attribute = gameInfo.attr("pgn").split("]").last()
        val turnsRegex = Regex("(\\d+. ?[O0]-[O0] [O0]-[O0]-[O0])|(\\d+. ?[O0]-[O0] [O0]-[O0])|(\\d+. ?[O0]-[O0]-[O0] [O0]-[O0])|(\\d+. ?[O0]-[O0]-[O0] [O0]-[O0]-[O0])|(\\d+. ?[O0]-[O0] \\w+\\+?)|(\\d+. ?\\w+\\+? [O0]-[O0])|(\\d+. ?[O0]-[O0]-[O0] \\w+)|(\\d+. ?\\w+\\+? [O0]-[O0]-[O0])|(\\d+. ?\\w+\\=\\D\\+? \\w+\\=\\D)|(\\d+. ?\\w+\\+? \\w+\\=\\D\\+?)|(\\d+. ?\\w+\\=\\D\\+? \\w+\\+?)|(\\d+. ?\\w+\\+? \\w+\\+?)")
        val gameResultRegex = Regex("(1/2-1/2)|(1-0)|(0-1)")
        val result = gameResultRegex.findAll(attribute).first().value
        val moves = turnsRegex.findAll(attribute).toList().mapTo(mutableListOf()) {
            val move = it.value.split(".")
            ChessMoves(move.first().toInt(), move.last().removeSuffix(result))
        }
        moves.last().dsMovement = moves.last().dsMovement.removeSuffix("1").removeSuffix("0")
        moves.add(ChessMoves(moves.size, result))
        return moves
    }

    data class ChessMoves(
        val idMove: Int = 1,
        var dsMovement: String = ""
    )
}