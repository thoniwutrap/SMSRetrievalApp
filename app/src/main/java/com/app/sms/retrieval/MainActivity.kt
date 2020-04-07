package com.app.sms.retrieval

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.app.sms.retrieval.utils.SmsServiceManager.sendSMS
import com.app.sms.retrieval.utils.dolphinKey
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.lang.Exception


class MainActivity : AppCompatActivity(){

    private var progDialog: ProgressDialog? = null
    val SMS_FLAG = "SMS"

    var pkgName : String = ""
    var phoneNo : String = ""
    var tranId : String = ""
    var apiId : String = ""
    var msgData : String = ""

    init {
        progDialog = ProgressDialog.shared()
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.flags
            val bundle = intent.extras
            val isSuccess = bundle?.getBoolean("isSuccess",false)
            try {
                progDialog?.dismissAllowingStateLoss()
                Intent().apply {
                    putExtra("isSuccess",isSuccess)
                    setResult(Activity.RESULT_OK,this)
                    finish()
                }}catch (e : Exception){
                    e.printStackTrace()
                }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermission()
    }



    fun initValue(){
        intent?.extras?.getString("packageClass","").also {
            pkgName = it.toString()
        }
        if(pkgName == getString(R.string.package_name_dolphin)){
            intent?.extras?.apply {
                getString("phoneNo","").apply {
                    phoneNo = this
                }
                getString("tranId","").apply {
                    tranId = this
                }
                getString("apiId","").apply {
                    apiId = this
                }
                getString("msgData","").apply {
                    msgData = this
                }
            }
            progDialog.let {
                it?.show(supportFragmentManager,"Loading")
                val msgSend = "${tranId}${apiId}${msgData}"
                sendSMS(this,phoneNo, msgSend.dolphinKey())
            }
        }else{
            finish()
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
                            initValue()
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
