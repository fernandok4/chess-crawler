package com.kanashiro

import com.kanashiro.crawler.ChessPgnMentorCrawler

class ReadPgnMentorApplication {
    companion object {

        val downloadAllGames = System.getenv().getOrDefault("ENV_DOWNLOAD_ALL_GAMES", "0")
        val crawler = ChessPgnMentorCrawler()

        @JvmStatic
        fun main(args: Array<String>) {
            if(downloadAllGames == "1"){
                crawler.downloadFiles()
            }
            crawler.parsePgnFiles()
        }
    }
}