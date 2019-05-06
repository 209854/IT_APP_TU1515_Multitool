package com.multitool.smartinput.wifikeyboardandmouse.utils

class Settings private constructor() {
    var host: String? = null
    var port: Int = 0

    companion object {
        val instanceOf = Settings()
    }
}
