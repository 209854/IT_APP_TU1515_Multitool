package com.multitool.smartinput.wifikeyboardandmouse.controller

import com.google.gson.Gson

class Command private constructor(val type: CommandType, val value: CommandValue?) {

    override fun toString(): String {
        return Gson().toJson(this,javaClass)

    }

    companion object {

        fun noOp(): Command {
            return Command(CommandType.NO_OP, null)
        }

        fun of(type: CommandType, value: CommandValue?): Command {
            return Command(type, value)
        }
    }
}
