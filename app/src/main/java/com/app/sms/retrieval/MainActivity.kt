package com.app.sms.retrieval

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.webkit.PermissionRequest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.sms.retrieval.model.SmsReceiveDao
import com.app.sms.retrieval.utils.SmsServiceManager.sendSMS
import com.app.sms.retrieval.utils.hideKeyboard
import com.app.sms.retrieval.utils.init
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), MessageAdapter.Listener {

    val RECEIVED_SMS_FLAG = "SMS_RECEIVED"
    var adapter: MessageAdapter? = null


    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.flags
            val bundle = intent.extras
            val number = bundle?.getString("number")
            val message = bundle?.getString("message")
            Log.e("MESSAGE", "Message received from: $number - msg: $message")
            adapter?.updateMsg(Hawk.get("smsReceiveDao"))
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        requestPermission()
    }

    override fun onResume() {
        super.onResume()
        adapter?.updateMsg(Hawk.get("smsReceiveDao"))
    }


    override fun onItemClick(item: SmsReceiveDao) {
        Toast.makeText(
            this@MainActivity,
             item.smsMessage,
            Toast.LENGTH_SHORT
        ).show()
    }


    fun initView(){
        rvMessage.init()
        edtPhoneNo.setText("0972947756")
        edtMessage.setText(getJson())
        btnSendMessage.setOnClickListener {
            hideKeyboard()
            sendSMS(this, edtPhoneNo.text.toString(), edtMessage.text.toString())
            edtMessage.text.clear()
        }
        adapter = MessageAdapter(mutableListOf(), this).apply { rvMessage.adapter = this }
    }



    fun getJson() : String{

        val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        var jsonReceived = JSONObject()
        jsonReceived.put("id", Random().nextInt(10001))
        jsonReceived.put("action", "Pick Up")
        jsonReceived.put("type", "critical")
        jsonReceived.put("time", currentDate)
        jsonReceived.put("status", "Incomplete")
        jsonReceived.put("reason", "Service connection failure")
        jsonReceived.put("username", "albert")
        jsonReceived.put("run", "CIT123")
        //jsonReceived.put("user_description", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.")
        //2 SMS -> encripted -> 4 SMS
        //3 SMS -> encripted -> 8 SMS

        return jsonReceived.toString()

    }

    fun requestPermission() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.SEND_SMS,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            registerReceiver(broadcastReceiver, IntentFilter(RECEIVED_SMS_FLAG))
                        } else {
                            finish()
                            Toast.makeText(
                                this@MainActivity,
                                "Please allow sms permission.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .withErrorListener {}
            .check()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver);
    }

}
