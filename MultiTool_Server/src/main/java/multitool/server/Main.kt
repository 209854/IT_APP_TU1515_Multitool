package multitool.server

import org.json.JSONObject
import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.ServerSocket
import javax.swing.*


object Main {

    private var robot: Robot? = null

    @Throws(IOException::class, AWTException::class)
    @JvmStatic
    fun main(args: Array<String>)
    {
        val ipLabel = JLabel("Local IP")
        val ipArea = JTextArea()
        val socketLabel = JLabel("Socket")
        val socketArea = JTextArea()
        val connectedArea = JTextArea()

        ipLabel.setBounds(50,10,120,20)
        ipArea.setBounds(50,30,120,30)
        ipArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)
        ipArea.isEditable = false

        socketLabel.setBounds(50,60,120,20)
        socketArea.setBounds(50,80,120,30)
        socketArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)

        connectedArea.setBounds(50,120,120,30)
        connectedArea.border = BorderFactory.createLineBorder(Color.BLACK, 1)
        connectedArea.text = "Disconnected"
        connectedArea.background = Color.RED

        val frame = JFrame("MultiTool Server")
        val pane = JPanel()
        pane.layout = null
        pane.add(ipArea)
        pane.add(ipLabel)
        pane.add(socketArea)
        pane.add(socketLabel)
        pane.add(connectedArea)
        frame.contentPane.add(pane)
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
        frame.setSize(Dimension(300, 200))
        frame.setLocationRelativeTo(null)
        frame.setVisible(true)

        robot = Robot()

        val server = ServerSocket(9999)
        socketArea.append(server.localPort.toString())
        val address = InetAddress.getLocalHost()
        val ip = address.hostAddress
        ipArea.append(ip)
        while (true) {
            val socket = server.accept()
            println("Connection establised")
            connectedArea.text = "Connected"
            connectedArea.background = Color.GREEN
            val inputStream = socket.getInputStream()
            handleSocketConnection(inputStream)
        }

    }

    class KotlinSwingSimpleEx(title: String) : JFrame() {

        init {
            createUI(title)
        }

        private fun createUI(title: String) {

            setTitle(title)
            val ipArea = JTextArea()
            ipArea.setBounds(20,20,100,20)

            val address = InetAddress.getLocalHost()
            val ip = address.hostAddress
            ipArea.append(ip)

            add(ipArea)
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            setSize(300, 200)
            setLocationRelativeTo(null)
        }
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
                text=br.readLine()
                while (text != null) {
                    val json = JSONObject(text)
                    process(json)
                    text=br.readLine()
                }
            } catch (e: IOException) {
            }
        }.start()
    }

    private fun process(json: JSONObject) {
        Thread {
            val type = json.getString("type")
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
                "KEYBOARD_INPUT" -> {
                    val value = json.getJSONObject("value")
                    //				System.out.println(value);
                    val keyCode = value.getInt("keyCode")
                    if (keyCode != 0)
                    {
                    val isShiftPressed = value.getBoolean("shift")
                    val event = KeyEvent.getExtendedKeyCodeForChar(keyCode)

                    if (isShiftPressed) {
                        robot!!.keyPress(KeyEvent.VK_SHIFT)
                    }
                    robot!!.keyPress(event)
                    robot!!.keyRelease(event)
                    if (isShiftPressed) {
                        robot!!.keyRelease(KeyEvent.VK_SHIFT)
                    }
                    }
                }
            }
        }.start()
    }
}