package com.app.sms.retrieval.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import com.app.sms.retrieval.model.SmsReceiveDao
import com.orhanobut.hawk.Hawk


object SmsServiceManager {


    fun sendSMS(context: Context, phoneNumber: String, message: String) {

        if (phoneNumber.isEmpty() || message.isEmpty()) {
            Toast.makeText(context, "Please enter phone number or message.", Toast.LENGTH_LONG).show()
        } else {
            val newMessage = message
            val smsMgr: SmsManager = SmsManager.getDefault()
            var multipleSMS: ArrayList<String> = arrayListOf()
            multipleSMS = smsMgr.divideMessage(newMessage)
            try {
                val SENT = "SMS_SENT"
                val sentPendingIntents = ArrayList<PendingIntent>()
                val sentPI = PendingIntent.getBroadcast(context, 0, Intent(SENT), 0)

                context.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(arg0: Context, arg1: Intent) {
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                Intent("SMS").apply {
                                    putExtra("isSuccess", true)
                                    context.sendBroadcast(this)
                                }.also {
                                    Toast.makeText(context, "SMS sent", Toast.LENGTH_SHORT).show()
                                }
                            }
                            SmsManager.RESULT_ERROR_NO_SERVICE -> {
                                Intent("SMS").apply {
                                    context.sendBroadcast(this)
                                }.also {
                                    Toast.makeText(context, "No service", Toast.LENGTH_SHORT).show()
                                }
                            }
                            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(context, "Generic failure", Toast.LENGTH_SHORT).show()
                            SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(context, "Null PDU", Toast.LENGTH_SHORT).show()
                            SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(context, "Radio off", Toast.LENGTH_SHORT).show()
                        }
                        context.unregisterReceiver(this)
                    }
                }, IntentFilter(SENT))


                for (i in 0 until multipleSMS.size) {
                    sentPendingIntents.add(i, sentPI)
                }

                smsMgr.sendMultipartTextMessage(
                    phoneNumber,
                    null,
                    multipleSMS,
                    sentPendingIntents,
                    null
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}