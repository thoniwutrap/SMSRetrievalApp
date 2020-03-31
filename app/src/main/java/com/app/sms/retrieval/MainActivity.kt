package com.app.sms.retrieval

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.sms.retrieval.model.SmsReceiveDao
import com.app.sms.retrieval.utils.SmsServiceManager
import com.app.sms.retrieval.utils.SmsServiceManager.sendSMS
import com.app.sms.retrieval.utils.dolphinKey
import com.app.sms.retrieval.utils.hideKeyboard
import com.app.sms.retrieval.utils.init
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), MessageAdapter.Listener{

    val SMS_FLAG = "SMS"
    var adapterSender: MessageAdapter? = null
    var adapterReceiver: MessageAdapter? = null

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.flags
            val bundle = intent.extras
            val type = bundle?.getString("type")
            val number = bundle?.getString("number")
            val message = bundle?.getString("message")
            Log.e("MESSAGE", "Message received from: $number - msg: $message")
            if(type == "SEND"){
                adapterSender?.updateMsg(Hawk.get("smsSenderDao"))
            }else{
                adapterReceiver?.updateMsg(Hawk.get("smsReceiveDao"))
            }
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
        adapterSender?.updateMsg(Hawk.get("smsSenderDao"))
        adapterReceiver?.updateMsg(Hawk.get("smsReceiveDao"))
    }


    override fun onItemClick(item: SmsReceiveDao) {
        Toast.makeText(
            this@MainActivity,
             item.smsMessage,
            Toast.LENGTH_SHORT
        ).show()
    }


    fun initView(){
        rvMessageSend.init()
        rvMessageReceive.init()
        Hawk.put("dolphinKey", true)
        toggleKey.setToggleOn()
        edtPhoneNo.setText("0972947756")
        toggleKey.setOnToggleChanged {
            if(it){
                Hawk.put("dolphinKey", true)
                Toast.makeText(
                    this@MainActivity,
                    "Dolphin key is ON",
                    Toast.LENGTH_SHORT
                ).show()
            }else{
                Hawk.put("dolphinKey", false)
                Toast.makeText(
                    this@MainActivity,
                    "Dolphin key is OFF",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        btnSendMessage.setOnClickListener {
            hideKeyboard()
            val msgSend = "${edtMessageApiId.text}${edtMessageRequestId.text}${edtMessageData.text}"
            sendSMS(this, edtPhoneNo.text.toString(), msgSend.dolphinKey())
            edtMessageApiId.text.clear()
            edtMessageRequestId.text.clear()
            edtMessageData.text.clear()
        }
        adapterSender = MessageAdapter(mutableListOf(), this).apply {
            rvMessageSend.adapter = this
        }
        adapterReceiver = MessageAdapter(mutableListOf(), this).apply {
            rvMessageReceive.adapter = this
        }
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
                            registerReceiver(broadcastReceiver, IntentFilter(SMS_FLAG))
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
