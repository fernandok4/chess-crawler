package com.kanashiro.crawler

import com.kanashiro.chess.ChessVO
import com.kanashiro.http.HttpPoolConnection
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.jsoup.Jsoup
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipInputStream

class ChessPgnMentorCrawler {

    private val URL = "https://www.pgnmentor.com/"
    val turnsRegex = Regex("([O0]-[O0]-[O0])|([O0]-[O0])|([QKRBN]?[abcdefgh]?[12345678]?x?[QKRBN]?[abcdefgh]+[12345678])=?[QRBN]?")
    val gameResultRegex = Regex("((1\\/2-1\\/2)|(1-0)|(0-1))(?!\")")
    val valuesRegex = Regex("\"(.*?)\"")
    val keysRegex = Regex("\\[\\w+(?= )")

    fun downloadFiles(){
        val httpClient = HttpClients.custom().setConnectionManager(HttpPoolConnection.poolManager).build()
        val request = HttpGet("${URL}files.html")
        val response = httpClient.execute(request)
        val bodyResponse = EntityUtils.toString(response.entity)
        val playersGamesUrl = getPlayersGameUrl(bodyResponse)
        for(url in playersGamesUrl){
            downloadFile(url, httpClient)
        }
    }

    private fun getPlayersGameUrl(html: String): MutableSet<String> {
        val document = Jsoup.parse(html)
        val urls = mutableSetOf<String>()
        val tagElements = document.getElementsByTag("a")
        for(tagElement in tagElements){
            val href = tagElement.attr("href")
            if(href == null || !href.contains("players/") || !href.endsWith(".zip")){
                continue
            }
            urls.add(href)
        }
        return urls
    }

    private fun downloadFile(url: String, httpClient: CloseableHttpClient){
        val request = HttpGet("${URL}${url}")
        val response = httpClient.execute(request)
        val content = response.entity.getContent()
        val zis = ZipInputStream(content)
        val file = File("downloads/${url.removeSuffix(".zip")}")
        zis.nextEntry
        file.writeBytes(zis.readAllBytes())
        content.close()
        zis.closeEntry()
    }

    fun parsePgnFiles(): MutableList<ChessVO.GameResult> {
        val games = mutableListOf<ChessVO.GameResult>()
        for(player in Files.walk(Path.of("downloads/players/"))){
            val file = File(player.toUri())
            if(file.isDirectory){
                continue
            }
            val pgnSplited = file.readText().split(gameResultRegex)
            pgnSplited.parallelStream().forEach{ pgn ->
                val keys = keysRegex.findAll(pgn).toList().map { it.value.replace(Regex("\\["), "") }
                val values = valuesRegex.findAll(pgn).toList().map { it.value.replace("\"", "") }
                val game = ChessVO.GameResult()
                if(values.isEmpty() || keys.isEmpty()){
                    return@forEach
                }
                game.moves = getGameMoves(valuesRegex.split(pgn).last().replace(Regex("\n"), "").replace(Regex("\r"), " ").replace(Regex("\\{(.*?)}"), ""))
                if(game.moves.isEmpty()){
                    return@forEach
                }
                game.nmEvent = getValueByIndexText("Event", keys, values)
                game.nmSite = getValueByIndexText("Site", keys, values)
                game.date = getValueByIndexText("Date", keys, values)
                game.round = getValueByIndexText("Round", keys, values)
                game.nmWhitePlayer = getValueByIndexText("White", keys, values)
                game.nmBlackPlayer = getValueByIndexText("Black", keys, values)
                game.result = getValueByIndexText("Result", keys, values)
                game.vlWhiteElo = getValueByIndexText("WhiteElo", keys, values)
                game.vlBlackElo = getValueByIndexText("BlackElo", keys, values)
                game.cdEco = getValueByIndexText("ECO", keys, values)
                games.add(game)
            }
        }
        return games
    }

    private fun getValueByIndexText(key: String, keys: List<String>, values: List<String>): String {
        val keyIndex = keys.indexOfFirst { it == key}
        return if(keyIndex == -1) "" else values[keyIndex]
    }

    fun getGameMoves(gameMoves: String): MutableList<ChessVO.CrawledGameMoves> {
        val moves = turnsRegex.findAll(gameMoves).toList().mapIndexedTo(mutableListOf()) { index, matchResult ->
            ChessVO.CrawledGameMoves(index, matchResult.value)
        }
        if(moves.isEmpty()){
            return moves
        }
        return moves.fold(mutableListOf()){ acc, move ->
            if(move.idMove % 2 == 0){
                val turn = move.idMove / 2
                acc.add(ChessVO.CrawledGameMoves(turn, move.dsMovement))
            } else {
                val lastMove = acc.last()
                lastMove.dsMovement += " ${move.dsMovement}"
            }
            acc
        }
    }
}