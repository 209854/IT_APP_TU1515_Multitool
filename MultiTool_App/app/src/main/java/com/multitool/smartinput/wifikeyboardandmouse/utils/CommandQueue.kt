package com.multitool.smartinput.wifikeyboardandmouse.utils

import android.util.Log

import com.multitool.smartinput.wifikeyboardandmouse.controller.Command

import java.util.ArrayList

object CommandQueue  {
    private val TAG = javaClass.canonicalName
    private val commands = ArrayList<Command>()

    fun push(command: Command) {
        commands.add(command)
        Log.d(TAG, command.toString())
    }

    fun pop(): Command {
        return if (commands.size == 0) {
            Command.noOp()
        } else commands.removeAt(0)
    }
}
