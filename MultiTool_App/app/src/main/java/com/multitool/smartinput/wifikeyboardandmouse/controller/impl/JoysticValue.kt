package com.multitool.smartinput.wifikeyboardandmouse.controller.impl

import com.google.gson.Gson
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandValue

data class JoysticValue(
        val yAxis: Double = 0.0,
        val xAxis: Double = 0.0) : CommandValue {
    override fun toString(): String {
        return Gson().toJson(this, JoysticValue::class.java)
    }
}