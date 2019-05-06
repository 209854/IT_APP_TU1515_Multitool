package com.multitool.smartinput.wifikeyboardandmouse.utils

import android.util.Log

import com.multitool.smartinput.wifikeyboardandmouse.controller.Command

import java.util.ArrayList

class CommandQueue private constructor() {

    fun push(command: Command) {
        commands.add(command)
        Log.d(TAG, command.toString())
    }

    fun pop(): Command {
        return if (commands.size == 0) {
            Command.noOp()
        } else commands.removeAt(0)
    }

    companion object {
        private val TAG = CommandQueue::class.java!!.getCanonicalName()

        val instance = CommandQueue()

        private val commands = ArrayList<Command>()
    }
}
