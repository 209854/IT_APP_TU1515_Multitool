package com.multitool.smartinput.wifikeyboardandmouse.controller.impl

import android.view.KeyEvent
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandValue

class KeyValue private constructor(
        @SerializedName("keyCode")
        val keyCode: Int) : CommandValue {

    @SerializedName("shift")
    var isShiftPressed: Boolean = false
        private set

    @SerializedName("alt")
    var isAltPressed: Boolean = false
        private set


    @SerializedName("ctrl")
    var isCtrlPressed: Boolean = false
        private set


    @SerializedName("num")
    var isNumLockPressed: Boolean = false
        private set


    @SerializedName("scroll")
    var isScrollLockPressed: Boolean = false
        private set


    @SerializedName("fn")
    var isFunctionPressed: Boolean = false
        private set


    @SerializedName("meta")
    var isMetaPressed: Boolean = false
        private set

    @SerializedName("sym")
    var isSymPressed: Boolean = false
        private set


    @SerializedName("caps")
    var isCapsLockPressed: Boolean = false
        private set


    @SerializedName("print")
    var isPrintKeyPressed: Boolean = false
        private set

    override fun toString(): String {


        return Gson().toJson(this,javaClass).toString()

//        return String.format("{\"keyCode\": %s, \"alt\": %s, \"caps\": %s, \"ctrl\": %s, \"fn\": %s, \"meta\": %s, \"num\": %s, \"print\": %s, \"scroll\": %s, \"shift\": %s, \"sym\": %s}",
//                keyCode, isAltPressed, isCapsLockPressed, isCtrlPressed, isFunctionPressed, isMetaPressed, isNumLockPressed, isPrintKeyPressed, isScrollLockPressed, isShiftPressed, isSymPressed)
    }

    companion object {
        private val TAG = KeyValue::class.java.simpleName
        fun of(event: KeyEvent): KeyValue {
            val keyChar = event.unicodeChar
            val value = KeyValue(keyChar)

            if (keyChar == 0) {
                when (event.keyCode) {
                    KeyEvent.KEYCODE_DEL -> {
                    }
                    KeyEvent.KEYCODE_FORWARD_DEL -> {
                    }
                    KeyEvent.KEYCODE_3 -> {
                    }
                    KeyEvent.KEYCODE_8 -> {
                    }
                    KeyEvent.KEYCODE_9 -> {
                    }
                }
            }
            value.isAltPressed = event.isAltPressed
            value.isCapsLockPressed = event.isCapsLockOn
            value.isCtrlPressed = event.isCtrlPressed
            value.isFunctionPressed = event.isFunctionPressed
            value.isMetaPressed = event.isMetaPressed
            value.isNumLockPressed = event.isNumLockOn
            value.isPrintKeyPressed = event.isPrintingKey
            value.isScrollLockPressed = event.isScrollLockOn
            value.isShiftPressed = event.isShiftPressed
            value.isSymPressed = event.isSymPressed
            return value
        }
    }
}
