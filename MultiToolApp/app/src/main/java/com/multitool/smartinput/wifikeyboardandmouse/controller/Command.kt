package com.multitool.smartinput.wifikeyboardandmouse.controller

class Command private constructor(val type: CommandType, val value: CommandValue?) {

    override fun toString(): String {
        return String.format("{\"type\": \"%s\", \"value\": %s}", type, value)
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
