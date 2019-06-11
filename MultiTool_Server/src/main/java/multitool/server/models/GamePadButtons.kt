package multitool.server.models

import com.google.gson.Gson

data class GamePadButtons(
        var yButton: Boolean = false,
        var xButton: Boolean = false,
        var bButton: Boolean = false,
        var aButton: Boolean = false,
        var lb: Boolean = false,
        var lt: Boolean = false,
        var rb: Boolean = false,
        var rt: Boolean = false
) {
    override fun toString(): String {
        return Gson().toJson(this, GamePadButtons::class.java)
    }

    companion object {
        fun fromJson(value: String): GamePadButtons? {

            return Gson().fromJson<GamePadButtons>(value, GamePadButtons::class.java)

        }
    }
}