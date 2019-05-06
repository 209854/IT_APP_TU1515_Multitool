package com.multitool.smartinput.wifikeyboardandmouse.connector

import android.util.Log

import com.multitool.smartinput.wifikeyboardandmouse.utils.Settings

import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.ArrayList

import java.lang.Thread.*

class ConnectionManager private constructor() {

    private var state: ConnectionState? = null

    private val listeners = ArrayList<ConnectionManagerListener>()
    private var pw: PrintWriter? = null

    fun attemptConnection() {
        if (state == ConnectionState.CONNECTING || state == ConnectionState.CONNECTED) {
            return
        }

        val host = Settings.instanceOf.host
        val port = Settings.instanceOf.port

        Log.i(TAG, String.format("Attempting connection. Results will be communicated to %s listeners", listeners.size))
        this.state = ConnectionState.CONNECTING
        val that = this

        Thread(Runnable {
            var attemptCounter = 0
            var connected = false
            do {
                attemptCounter++
                try {
                    val socket = Socket(host, port)
                    pw = PrintWriter(socket.getOutputStream(), true)
                    connected = true
                } catch (e: IOException) {
                    Log.e(TAG, "Connection failed, reattempting in 2 seconds.")
                }

                try {
                    sleep(2000)
                } catch (e: InterruptedException) {
                    Log.e(TAG, "Could not sleep for 2 seconds. ")
                }

            } while (!connected && attemptCounter < 5)

            Log.i(TAG, String.format("Connected: %s, attempted: %d, informing %s listeners", connected, attemptCounter, listeners.size))

            if (connected) {
                that.state = ConnectionState.CONNECTED
                for (l in listeners) {
                    l.onConnected()
                }
            } else {
                that.state = ConnectionState.FAILED
                for (l in listeners) {
                    l.onConnectionFailed()
                }
            }
        }).start()
    }

    fun addConnectionManagerListener(listener: ConnectionManagerListener) {
        listeners.add(listener)
        Log.i(TAG, String.format("Added %s listeners", listeners.size))
    }

    fun sendMessage(string: String) {
        pw!!.println(string)
    }

    fun disconnect() {
        state = ConnectionState.DISCONNECTED
        for (l in listeners) {
            l.onDisconnected()
        }
    }

    companion object {
        private val TAG = ConnectionManager::class.java!!.getCanonicalName()

        val instanceOf = ConnectionManager()
    }
}
