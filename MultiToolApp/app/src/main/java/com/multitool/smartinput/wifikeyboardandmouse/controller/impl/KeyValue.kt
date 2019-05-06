package com.multitool.smartinput.wifikeyboardandmouse.controller.impl

import android.view.KeyEvent

import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandValue

class KeyValue private constructor(val keyCode: Int) : CommandValue {
    var isShiftPressed: Boolean = false
        private set
    var isAltPressed: Boolean = false
        private set
    var isCtrlPressed: Boolean = false
        private set
    var isNumLockPressed: Boolean = false
        private set
    var isScrollLockPressed: Boolean = false
        private set
    var isFunctionPressed: Boolean = false
        private set
    var isMetaPressed: Boolean = false
        private set
    var isSymPressed: Boolean = false
        private set
    var isCapsLockPressed: Boolean = false
        private set
    var isPrintKeyPressed: Boolean = false
        private set

    override fun toString(): String {
        return String.format("{\"keyCode\": %s, \"alt\": %s, \"caps\": %s, \"ctrl\": %s, \"fn\": %s, \"meta\": %s, \"num\": %s, \"print\": %s, \"scroll\": %s, \"shift\": %s, \"sym\": %s}",
                keyCode, isAltPressed, isCapsLockPressed, isCtrlPressed, isFunctionPressed, isMetaPressed, isNumLockPressed, isPrintKeyPressed, isScrollLockPressed, isShiftPressed, isSymPressed)
    }

    companion object {

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
