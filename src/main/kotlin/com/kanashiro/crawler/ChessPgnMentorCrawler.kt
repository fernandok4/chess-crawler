package com.kanashiro.crawler

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
    val turnsRegex = Regex("(\\d+. ?[O0]-[O0] [O0]-[O0]-[O0])|(\\d+. ?[O0]-[O0] [O0]-[O0])|(\\d+. ?[O0]-[O0]-[O0] [O0]-[O0])|(\\d+. ?[O0]-[O0]-[O0] [O0]-[O0]-[O0])|(\\d+. ?[O0]-[O0] \\w+\\+?)|(\\d+. ?\\w+\\+? [O0]-[O0])|(\\d+. ?[O0]-[O0]-[O0] \\w+)|(\\d+. ?\\w+\\+? [O0]-[O0]-[O0])|(\\d+. ?\\w+\\=\\D\\+? \\w+\\=\\D)|(\\d+. ?\\w+\\+? \\w+\\=\\D\\+?)|(\\d+. ?\\w+\\=\\D\\+? \\w+\\+?)|(\\d+. ?\\w+\\+? \\w+\\+?)")
    val gameResultRegex = Regex("((1\\/2-1\\/2)|(1-0)|(0-1))(?!\")")

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

    fun parsePgnFiles() {
        Files.walk(Path.of("downloads/players/")).forEach {
            val file = File(it.toUri())
            if(file.isDirectory){
                return@forEach
            }
            val pgnSplited = file.readText().split(gameResultRegex)
        }
    }

    private fun getMovements(){

    }
}