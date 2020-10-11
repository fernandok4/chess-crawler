package chess

import com.kanashiro.chess.ChessDAO
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class ChessDAOTest: Spek({
    describe("getAllGames"){
        on("getAllGames"){
            val result = ChessDAO().getAllGames()
            println(result)
        }
    }
})