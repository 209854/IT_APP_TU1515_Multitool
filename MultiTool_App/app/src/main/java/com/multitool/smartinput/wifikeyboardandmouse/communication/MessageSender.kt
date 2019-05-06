package com.multitool.smartinput.wifikeyboardandmouse.communication

import android.util.Log

import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManager
import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManagerListener
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandType
import com.multitool.smartinput.wifikeyboardandmouse.utils.CommandQueue

class MessageSender private constructor() : ConnectionManagerListener {
    private var connected = false

    init {
        ConnectionManager.instanceOf.addConnectionManagerListener(this)
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
                val command = CommandQueue.instance.pop()
                if (command.type == CommandType.NO_OP) {
                    continue
                }
                ConnectionManager.instanceOf.sendMessage(command.toString())
            }
        }).start()
    }

    companion object {

        private val TAG = MessageSender::class.java.canonicalName

        val instance = MessageSender()
    }
}
