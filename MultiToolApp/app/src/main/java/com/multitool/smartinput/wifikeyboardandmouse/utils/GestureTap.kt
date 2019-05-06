package com.multitool.smartinput.wifikeyboardandmouse.utils

import android.view.GestureDetector
import android.view.MotionEvent

import com.multitool.smartinput.wifikeyboardandmouse.controller.Command
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandType
import com.multitool.smartinput.wifikeyboardandmouse.controller.impl.MoveValue

class GestureTap : GestureDetector.SimpleOnGestureListener() {

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (e2.pointerCount == 1) {
            CommandQueue.instance.push(Command.of(CommandType.MOUSE_MOVE, MoveValue.of(distanceX, distanceY)))
        } else {
            CommandQueue.instance.push(Command.of(CommandType.MOUSE_SCROLL, MoveValue.of(distanceX, distanceY)))
        }
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        CommandQueue.instance.push(Command.of(CommandType.MOUSE_DOUBLE_CLICK, null))
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        super.onLongPress(e)
        CommandQueue.instance.push(Command.of(CommandType.MOUSE_RIGHT_CLICK, null))
    }

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        CommandQueue.instance.push(Command.of(CommandType.MOUSE_LEFT_CLICK, null))
        return true
    }
}
