package chess

import com.google.gson.Gson
import com.kanashiro.chess.ChessDAO
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on
import java.io.File

class ChessDAOTest: Spek({
    describe("getAllGames"){
        on("getAllGames"){
            val result = ChessDAO().getAllGames()
            for(item in result){
                val file = File("out/${item.key}.json")
                file.writeText(Gson().toJson(item.value))
            }
        }
    }
})