package com.kanashiro

import com.google.gson.Gson
import com.kanashiro.chess.ChessDAO
import java.io.File

class ExportGamesToJsonJobApplication {

    companion object {

        val dao = ChessDAO()

        @JvmStatic
        fun main(args: Array<String>) {
            var i = 0
            var result = dao.getAllGames(i++, 20000)
            while(result.isNotEmpty()){
                for(item in result){
                    val file = File("out/${item.key}.json")
                    file.writeText(Gson().toJson(item.value))
                }
                result = dao.getAllGames(i++, 20000)
            }
        }
    }
}