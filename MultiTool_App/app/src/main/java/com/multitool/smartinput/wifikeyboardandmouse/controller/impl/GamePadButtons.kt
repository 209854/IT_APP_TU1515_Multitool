package com.multitool.smartinput.wifikeyboardandmouse.controller.impl

import com.google.gson.Gson
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandValue

data class GamePadButtons(
        var yButton: Boolean = false,
        var xButton: Boolean = false,
        var bButton: Boolean = false,
        var aButton: Boolean = false,
        var lb: Boolean = false,
        var lt: Boolean = false,
        var rb: Boolean = false,
        var rt: Boolean = false
):CommandValue {
    override fun toString(): String {
        return Gson().toJson(this, GamePadButtons::class.java)
    }
}