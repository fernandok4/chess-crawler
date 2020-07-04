import com.kanashiro.Application
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.on

class ApplicationTest: Spek({

    val application = Application()

    describe("run"){
        on("run"){
            application.run()
        }
    }
})