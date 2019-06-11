package multitool.server

//import redlaboratory.jvjoyinterface.VJoy
import multitool.server.models.GamePadButtons
import org.json.JSONObject
import redlaboratory.jvjoyinterface.VJoy
import redlaboratory.jvjoyinterface.VjdStat
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.InputEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.ServerSocket
import javax.swing.*


public class Main {
    object Main {

        private var robot: Robot? = null
        private val vJoy: VJoy = VJoy()
        private val gameButtons: Array<Boolean> = Array(8) { i -> false }
        private val dPad = mutableMapOf("up" to false, "down" to false, "left" to false, "right" to false)
        val soc = ServerSocket()


        val ipLabel = JLabel("Local IP")
        val ipArea = JTextArea()
        val socketLabel = JLabel("Socket")
        val socketArea = JTextArea()
        val startedArea = JTextArea()
        val connectedArea = JTextArea()
        val startButton = JButton("Start Server")
        val thread = SimpleThread()

        class SimpleThread : Thread() {
            public override fun run() {
                while (true) {
                    println("finding a connection!")
                    val socket = soc.accept()
                    println("Connection establised")
                    connectedArea.text = "Connected"
                    connectedArea.background = Color.GREEN
                    val inputStream = socket.getInputStream()
                    handleSocketConnection(inputStream)
                }
            }
        }

        @Throws(IOException::class, AWTException::class)
        @JvmStatic
        public fun main(args: Array<String>) {

            ipLabel.setBounds(60, 10, 120, 20)
            ipArea.setBounds(60, 30, 120, 30)
            ipArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)
            ipArea.isEditable = false

            socketLabel.setBounds(60, 60, 120, 20)
            socketArea.setBounds(60, 80, 120, 30)
            socketArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)

            connectedArea.setBounds(60, 120, 120, 30)
            connectedArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)
            connectedArea.text = "Disconnected"
            connectedArea.background = Color.RED

            startButton.setBounds(60, 160, 120, 30)
            startedArea.setBounds(60, 200, 120, 30)
            startedArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)
            startedArea.text = "Stopped"
            startedArea.background = Color.RED

            startButton.addActionListener(object : ActionListener {

                @Override
                override fun actionPerformed(e: ActionEvent) {
                    startedArea.text = "Started"
                    startedArea.background = Color.GREEN

                    var port = socketArea.getText().toInt();
                    soc.reuseAddress = true
                    soc.bind(InetSocketAddress(port))

                    thread.start()
                    println(e)

                }
            })

            val frame = JFrame("MultiTool Server")
            val pane = JPanel()
            pane.layout = null
            pane.add(ipArea)
            pane.add(ipLabel)
            pane.add(socketArea)
            pane.add(socketLabel)
            pane.add(connectedArea)
            pane.add(startButton)
            pane.add(startedArea)
            frame.contentPane.add(pane)
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
            frame.setSize(Dimension(260, 300))
            frame.setLocationRelativeTo(null)
            frame.setVisible(true)


            robot = Robot()
            val address = InetAddress.getLocalHost()
            val ip = address.hostAddress
            ipArea.append(ip)
            socketArea.append("8888")


        }

        /*
        fun main(args: Array<String>) {
            val server = ServerSocket(9999)
            robot = Robot()
            val address = InetAddress.getLocalHost()
            val ip = address.hostAddress

            println("IP Address = $ip")
            val ni = NetworkInterface.getNetworkInterfaces()
            while (ni.hasMoreElements()) {
                val n = ni.nextElement() as NetworkInterface
                val en = n.inetAddresses
                while (en.hasMoreElements()) {
                    val i = en.nextElement() as InetAddress
                    val address = i.hostName
                    print("server started at "+ address.toString()+":" + server.localPort.toString()+"\n")
                    //print("server started at %s:%s" + address.toString() + server.localPort.toString())
                }
            }
            while (true) {
                val socket = server.accept()
                println("Connection establised")
                val inputStream = socket.getInputStream()
                handleSocketConnection(inputStream)
            }
        }
    */
        @Throws(IOException::class)
        private fun handleSocketConnection(inputStream: InputStream) {
            Thread {
                val br = BufferedReader(InputStreamReader(inputStream))
                var text = ""
                try {
                    text = br.readLine()
                    while (text != null) {

                        println("before reading = $text")
                        process(JSONObject(text))
                        text = br.readLine()

                        println("after reading $text")

                    }
                } catch (e: IOException) {
                }
            }.start()
        }

        private fun process(json: JSONObject) {
//        println(json)
            Thread {
                val type = json.getString("type")

                println(type)

                when (type) {
                    "MOUSE_SCROLL" -> {
                        val value = json.getJSONObject("value")
                        val y = value.getFloat("y")
                        robot!!.mouseWheel(y.toInt())
                    }
                    "MOUSE_LEFT_CLICK" -> {
                        robot!!.mousePress(InputEvent.BUTTON1_MASK)
                        robot!!.mouseRelease(InputEvent.BUTTON1_MASK)
                    }
                    "MOUSE_DOUBLE_CLICK" -> {
                        robot!!.mousePress(InputEvent.BUTTON1_MASK)
                        robot!!.mouseRelease(InputEvent.BUTTON1_MASK)
                        robot!!.mousePress(InputEvent.BUTTON1_MASK)
                        robot!!.mouseRelease(InputEvent.BUTTON1_MASK)
                    }
                    "MOUSE_RIGHT_CLICK" -> {
                        robot!!.mousePress(InputEvent.BUTTON3_MASK)
                        robot!!.mouseRelease(InputEvent.BUTTON3_MASK)
                    }
                    "MOUSE_MOVE" -> {
                        val value = json.getJSONObject("value")

                        val dx = value.getFloat("x")
                        val dy = value.getFloat("y")

                        val location = MouseInfo.getPointerInfo()
                                .location

                        val x = location.getX() - dx
                        val y = location.getY() - dy

                        robot!!.mouseMove(x.toInt(), y.toInt())
                    }
                    "GAME_PAD_MOVE" -> {
                        val value = json.getJSONObject("value")

                        //GAMEPAD AXES
                        println("gamepad move $value")

                        val rID = 1
                        fun normalize(input: Double): Long {
                            return ((((input) / 100) + 1) * (32768 / 2)).toLong()
                        }

                        vJoy.setAxis(normalize(value.getDouble("yAxis")), rID, VJoy.HID_USAGE_Y)
                        vJoy.setAxis(normalize(value.getDouble("xAxis")), rID, VJoy.HID_USAGE_X)

                        VJoy.AXIS_MAX_VALUE


                    }
                    "GAME_PAD_BUTTONS" -> {
                        val value = json.getJSONObject("value").toString()

                        println("got buttons $value")
                        val rID = 1
                        val status = vJoy.getVJDStatus(rID)
                        if (status == VjdStat.VJD_STAT_OWN || status == VjdStat.VJD_STAT_FREE && !vJoy.acquireVJD(rID)) {
                            println("Failed to acquire vJoy device number $rID")
                        } else {
                            println("Acquired: vJoy device number $rID")
                        }


                        val gamePadButtons = GamePadButtons.fromJson(value)
                        gamePadButtons?.aButton?.let { vJoy.setBtn(it, rID, 1) }
                        gamePadButtons?.bButton?.let { vJoy.setBtn(it, rID, 2) }
                        gamePadButtons?.xButton?.let { vJoy.setBtn(it, rID, 3) }
                        gamePadButtons?.yButton?.let { vJoy.setBtn(it, rID, 4) }
                        gamePadButtons?.lb?.let { vJoy.setBtn(it, rID, 5) }
                        gamePadButtons?.rb?.let { vJoy.setBtn(it, rID, 6) }
                        gamePadButtons?.lt?.let { vJoy.setBtn(it, rID, 7) }
                        gamePadButtons?.rt?.let { vJoy.setBtn(it, rID, 8) }

//                            val butVal = gameButtons[button - 1]
//                            vJoy.setBtn(!butVal, rID, button)
//                            gameButtons[button - 1] = !butVal
//
                    }
                }
            }.start()
        }
    }
}