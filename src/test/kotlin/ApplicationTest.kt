import com.kanashiro.Application
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class ApplicationTest: Spek({

    describe("run"){
        on("run"){
            Application.main(arrayOf())
        }
    }
})