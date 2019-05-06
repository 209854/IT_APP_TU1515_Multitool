package com.multitool.smartinput.wifikeyboardandmouse

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.Toast

import com.multitool.smartinput.wifikeyboardandmouse.communication.MessageSender
import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManager
import com.multitool.smartinput.wifikeyboardandmouse.connector.ConnectionManagerListener
import com.multitool.smartinput.wifikeyboardandmouse.controller.Command
import com.multitool.smartinput.wifikeyboardandmouse.controller.CommandType
import com.multitool.smartinput.wifikeyboardandmouse.controller.impl.KeyValue
import com.multitool.smartinput.wifikeyboardandmouse.utils.CommandQueue
import com.multitool.smartinput.wifikeyboardandmouse.utils.GestureTap
import com.multitool.smartinput.wifikeyboardandmouse.utils.Settings


class FullscreenActivity : AppCompatActivity(), View.OnTouchListener, View.OnClickListener, DialogInterface.OnClickListener, ConnectionManagerListener {

    private val TAG = FullscreenActivity::class.java.canonicalName
    private var hostEditText: EditText? = null
    private var portEditText: EditText? = null
    private var connected: Boolean = false
    private var context: Context? = null
    private var detector: GestureDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        this.context = this
        detector = GestureDetector(this, GestureTap())

        val actionBar = supportActionBar
        actionBar?.hide()

        val settings = findViewById<Button>(R.id.settings_button)
        settings.setOnClickListener(this)

        val keyboardSwitch = findViewById<Button>(R.id.keyboard_toggle)
        keyboardSwitch.setOnClickListener(this)

        val touchscreen = findViewById<RelativeLayout>(R.id.touch_screen)
        touchscreen.setOnTouchListener(this)

        MessageSender.instance.init()
        ConnectionManager.instanceOf.addConnectionManagerListener(this)
    }

    override fun onClick(view: View) {
        val msg = String.format("click is happening on %s", view.id)
        when (view.id) {
            R.id.keyboard_toggle -> {
                toggleSoftKeyboard()
            }
            R.id.settings_button -> {
                val builder = AlertDialog.Builder(this@FullscreenActivity)
                val dialogView = layoutInflater.inflate(R.layout.dialog_settings, null)
                hostEditText = dialogView.findViewById(R.id.button_host)
                portEditText = dialogView.findViewById(R.id.button_port)

                if (Settings.instanceOf.host != null && !Settings.instanceOf.host!!.isEmpty()) {
                    hostEditText!!.setText(Settings.instanceOf.host)
                }
                if (Settings.instanceOf.port != 0) {
                    portEditText!!.setText(String.format("%s", Settings.instanceOf.port))
                }

                if (connected) {
                    builder.setNegativeButton("Disconnect", this)
                } else {
                    builder.setPositiveButton("Connect", this)
                }
                builder.setNeutralButton("Cancel", this)
                builder.setView(dialogView)
                builder.setCancelable(false)
                builder.show()
            }
        }
        Log.i(TAG, msg)
    }

    private fun toggleSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (connected) {
            CommandQueue.instance.push(Command.of(CommandType.KEYBOARD_INPUT, KeyValue.of(event)))
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {

        if (connected) {
            detector!!.onTouchEvent(motionEvent)
        }

        return true
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        Log.i(TAG, "I: $i")
        when (i) {
            -1 -> {
                // positive
                val host = hostEditText!!.text.toString()
                val portString = portEditText!!.text.toString()
                if (host.isEmpty() || portString.isEmpty()) {
                    return
                }
                val port = Integer.parseInt(portString)
                Settings.instanceOf.host = host
                Settings.instanceOf.port = port
                ConnectionManager.instanceOf.attemptConnection()
            }
            -2 -> {
                // negative
                ConnectionManager.instanceOf.disconnect()
            }
        }
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
