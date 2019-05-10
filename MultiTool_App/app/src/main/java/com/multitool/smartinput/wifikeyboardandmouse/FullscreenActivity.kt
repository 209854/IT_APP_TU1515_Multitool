package com.multitool.smartinput.wifikeyboardandmouse

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import kotlinx.android.synthetic.main.activity_fullscreen.*


class FullscreenActivity : AppCompatActivity(),  ConnectionManagerListener {

    private val TAG = FullscreenActivity::class.java.canonicalName
    private var hostEditText: EditText? = null
    private var portEditText: EditText? = null
    private var connected: Boolean = false
    private lateinit var context: Context
    private lateinit var detector: GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen)
        this.context = this
        detector = GestureDetector(this, GestureTap())

        val actionBar = supportActionBar
        actionBar?.hide()

        settings_button.setOnClickListener {
            openSettingsDialog()
        }

        keyboard_toggle.setOnClickListener {
            toggleSoftKeyboard()
        }

        touch_screen.setOnTouchListener { _, event ->
            if (connected) {
                detector.onTouchEvent(event)
            }
            true
        }

        MessageSender.init()
        ConnectionManager.addConnectionManagerListener(this)
    }

    private fun openSettingsDialog() {
        val builder = AlertDialog.Builder(this@FullscreenActivity)
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
        val port = portString.toInt()
        Settings.host = host
        Settings.port = port
        ConnectionManager.attemptConnection()
    }

    private fun toggleSoftKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.i(TAG, "onKeyDown: ${keyCode}, ${event}")
        Log.i(TAG, "of: ${event.unicodeChar}")

        if (connected) {

            CommandQueue.push(Command.of(CommandType.KEYBOARD_INPUT, KeyValue.of(event)))
        }
        return super.onKeyDown(keyCode, event)
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
