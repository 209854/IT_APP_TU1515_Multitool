package com.multitool.smartinput.gamepad

import android.os.Build
import android.os.Bundle
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import com.multitool.smartinput.wifikeyboardandmouse.R
import com.multitool.smartinput.wifikeyboardandmouse.communication.MessageSender
import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManager
import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManagerListener
import com.multitool.smartinput.wifikeyboardandmouse.controller.Command
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandType
import com.multitool.smartinput.wifikeyboardandmouse.controller.impl.GamePadButtons
import com.multitool.smartinput.wifikeyboardandmouse.controller.impl.JoysticValue
import com.multitool.smartinput.wifikeyboardandmouse.utils.CommandQueue
import com.multitool.smartinput.wifikeyboardandmouse.utils.Settings
import kotlinx.android.synthetic.main.activity_game_pad.*

class GamePadActivity : AppCompatActivity(), ConnectionManagerListener {

    private var connected: Boolean = false
    private var context = this;
    private val TAG = GamePadActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= 16) {
            getWindow().setFlags(AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT, AccessibilityNodeInfoCompat.ACTION_NEXT_HTML_ELEMENT)
            getWindow().getDecorView().setSystemUiVisibility(3328)
        } else {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_pad)

        val actionBar = supportActionBar
        actionBar?.hide()
        context = this;

        settings_button.setOnClickListener {
            openSettingsDialog()
        }
        analogStick.setOnMoveListener { angle, strength ->


            Log.i(TAG, "onCreate: angle =$angle, strength = $strength  y= ${strength * Math.sin(Math.toRadians(angle))} x= ${strength * Math.cos(Math.toRadians(angle))}")
//            var a = (((strength * Math.sin(Math.toRadians(angle))) / 100) + 1) * (32768/2)
//            Log.i(TAG, "onCreate value = : $a")




            CommandQueue.push(Command.of(CommandType.GAME_PAD_MOVE, JoysticValue(strength * Math.sin(Math.toRadians(angle)), strength * Math.cos(Math.toRadians(angle)))))

        }

        gamepadButtonsInit()
        MessageSender.init()
        ConnectionManager.addConnectionManagerListener(this)

    }

    var gamePadButtons: GamePadButtons = GamePadButtons();
    private fun gamepadButtonsInit() {


        yButton.setOnTouchListener { v, event ->

            Log.i(TAG, "gamepadButtonsInit: $event")

            gamePadButtons.yButton = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE

            Log.i(TAG, "gamepadButtonsInit: $gamePadButtons")

            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }

        xButton.setOnTouchListener { v, event ->
            gamePadButtons.xButton = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
        bButton.setOnTouchListener { v, event ->
            gamePadButtons.bButton = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
        aButton.setOnTouchListener { v, event ->
            gamePadButtons.aButton = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
        lb.setOnTouchListener { v, event ->
            gamePadButtons.lb = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
        lt.setOnTouchListener { v, event ->
            gamePadButtons.lt = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
        rb.setOnTouchListener { v, event ->
            gamePadButtons.rb = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
        rt.setOnTouchListener { v, event ->
            gamePadButtons.rt = event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE
            CommandQueue.push(Command.of(CommandType.GAME_PAD_BUTTONS, gamePadButtons))
            return@setOnTouchListener true
        }
    }


    private fun openSettingsDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
        val hostEditText: EditText = dialogView.findViewById(R.id.button_host)
        val portEditText: EditText = dialogView.findViewById(R.id.button_port)

        if (Settings.host != null && !Settings.host!!.isEmpty()) {
            hostEditText.setText(Settings.host)
        }
        if (Settings.port != 0) {
            portEditText.setText(String.format("%s", Settings.port))
        }

        if (connected) {
            builder.setNegativeButton("Disconnect") { _, _ ->
                ConnectionManager.disconnect()
            }
        } else {
            builder.setPositiveButton("Connect") { _, _ ->
                connect(hostEditText.text.toString(), portEditText.text.toString())

            }
        }
        builder.setNeutralButton("Cancel") { _, _ -> }
        builder.setView(dialogView)
        builder.setCancelable(false)
        builder.show()
    }

    private fun connect(host: String, portString: String) {

        if (host.isEmpty() || portString.isEmpty()) {
            return
        }

        Log.i(TAG, "connect: connecting!")

        val port = portString.toInt()
        Settings.host = host
        Settings.port = port
        ConnectionManager.attemptConnection()
    }

    override fun onConnected() {
        connected = true
        val msg = "Connection established with host machine"
        Log.i(TAG, msg)
        this.runOnUiThread { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
    }

    override fun onDisconnected() {
        connected = false
        val msg = "Connection disrupted"
        Log.e(TAG, msg)
        //        ConnectionManager.getInstanceOf().attemptConnection();
        this.runOnUiThread { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
    }

    override fun onConnectionFailed() {
        connected = false
        val msg = "Could not establish connection with host machine"
        Log.e(TAG, msg)
        this.runOnUiThread { Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
    }
}
