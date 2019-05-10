package com.multitool.smartinput.wifikeyboardandmouse.controller.impl

import com.google.gson.Gson
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandValue

class MoveValue private constructor(val x: Float, val y: Float) : CommandValue {

    override fun toString(): String {

        return Gson().toJson(this,javaClass)
//        return String.format("{\"x\": %s, \"y\": %s}", x, y)
    }

    companion object {

        fun of(x: Float, y: Float): MoveValue {
            return MoveValue(x, y)
        }
    }
}
