package com.multitool.smartinput.wifikeyboardandmouse.connector

interface ConnectionManagerListener {
    fun onConnected()

    fun onDisconnected()

    fun onConnectionFailed()
}
