package com.multitool.smartinput.wifikeyboardandmouse.communication

import android.util.Log

import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManager
import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManagerListener
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandType
import com.multitool.smartinput.wifikeyboardandmouse.utils.CommandQueue

object MessageSender : ConnectionManagerListener {
    private var connected = false
    private const val TAG = "MessageSender"

    init {
        ConnectionManager.addConnectionManagerListener(this)
    }

    override fun onConnected() {
        Log.i(TAG, "onConnected")
        connected = true
    }

    override fun onDisconnected() {
        Log.i(TAG, "onDisconnect")
        connected = false
    }

    override fun onConnectionFailed() {
        Log.i(TAG, "onConnectionFailed")
        connected = false
    }

    fun init() {
        Thread(Runnable {
            while (true) {
                if (!connected) {
                    continue
                }
                val command = CommandQueue.pop()
                if (command.type == CommandType.NO_OP) {
                    continue
                }
                ConnectionManager.sendMessage(command.toString())
            }
        }).start()
    }


}
