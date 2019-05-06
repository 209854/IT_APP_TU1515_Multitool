package com.multitool.smartinput.wifikeyboardandmouse.controller.impl

import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandValue

class MoveValue private constructor(val x: Float, val y: Float) : CommandValue {

    override fun toString(): String {
        return String.format("{\"x\": %s, \"y\": %s}", x, y)
    }

    companion object {

        fun of(x: Float, y: Float): MoveValue {
            return MoveValue(x, y)
        }
    }
}
