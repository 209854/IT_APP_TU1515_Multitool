package multitool.server


import org.junit.After
import org.junit.AfterClass
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import java.net.Socket


internal class MainTest
{
    private var program = multitool.server.Main.Main
    @Test
    public fun serverTest1()
    {
        program.main(emptyArray())
        program.socketArea.text = "7777"
        program.startButton.doClick()
        assert(program.soc.isBound())
        assert(program.soc.localPort.toString() == "7777")

        //println(program.soc.localPort)
    }

    @Test
    public fun serverTest2()
    {
        assert(program.thread.isAlive())
    }

    @Test
    public fun serverTest3()
    {
        val sock = Socket("localhost", 7777)

        //println("socket read $read")
        //val socket = program.soc.accept()
        //println(program.soc.accept())
    }

}