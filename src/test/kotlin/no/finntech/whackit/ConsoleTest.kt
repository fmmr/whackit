package no.finntech.whackit

import com.pi4j.util.Console
import org.junit.jupiter.api.Test

class WhackitApplicationTests {

    @Test
    fun contextLoads() {
        val console = Console()
        console.box("Just testing Console", "from pi4j")
        console.goodbye()
    }

}
