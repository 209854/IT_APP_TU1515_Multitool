package multitool.server

import java.awt.AWTException
import java.awt.MouseInfo
import java.awt.Point
import java.awt.Robot
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.util.Enumeration

import org.json.JSONObject

object Main {

    private var robot: Robot? = null

    @Throws(IOException::class, AWTException::class)
    @JvmStatic
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
//        println(json)
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
                    if (keyCode==0){
                        robot?.keyPress(KeyEvent.VK_BACK_SPACE)
                        robot?.keyRelease(KeyEvent.VK_BACK_SPACE)
                    }
                }
            }
        }.start()
    }
}