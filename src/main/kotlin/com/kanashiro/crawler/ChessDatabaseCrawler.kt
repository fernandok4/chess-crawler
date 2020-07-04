package com.kanashiro.crawler

import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClients


class ChessDatabaseCrawler {
//    https://www.chessgames.com/perl/chessgame?gid=1013642

    private val CHESS_GAMES_URL = "https://www.chessgames.com"

    fun readGame(idGame: Long){
        val httpClient = HttpClients.custom().setConnectionManager(HttpPoolConnection.poolManager).build()
        val httpGetUrl = HttpGet("www.google.com.br")
        val request = httpClient.execute(httpGetUrl)
        println(request)
    }
}