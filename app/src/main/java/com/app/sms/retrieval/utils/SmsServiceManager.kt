package com.app.sms.retrieval.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.widget.Toast


object SmsServiceManager {

    fun sendSMS(context: Context, phoneNumber: String, message: String){

        if(phoneNumber.isEmpty() || message.isEmpty()){
            Toast.makeText(context,"Please enter phone number or message.", Toast.LENGTH_LONG).show()
        }else{

            var newMessage = SystemCipherSecurity.cipher(message)
            val smsMgr: SmsManager = SmsManager.getDefault()
            var multipleSMS: ArrayList<String> = ArrayList()
            Toast.makeText(context, newMessage, Toast.LENGTH_SHORT).show()
            try {
                val SENT = "SMS_SENT"
                val sentPI = PendingIntent.getBroadcast(context, 0, Intent(SENT), 0)
                context.registerReceiver(object : BroadcastReceiver() {
                    override fun onReceive(arg0: Context, arg1: Intent) {
                        val resultCode = resultCode
                        when (resultCode) {
                            Activity.RESULT_OK -> Toast.makeText(
                                context,
                                "SMS sent",
                                Toast.LENGTH_LONG
                            ).show()
                            SmsManager.RESULT_ERROR_GENERIC_FAILURE -> Toast.makeText(
                                context,
                                "Generic failure",
                                Toast.LENGTH_LONG
                            ).show()
                            SmsManager.RESULT_ERROR_NO_SERVICE -> Toast.makeText(
                                context,
                                "No service",
                                Toast.LENGTH_LONG
                            ).show()
                            SmsManager.RESULT_ERROR_NULL_PDU -> Toast.makeText(
                                context,
                                "Null PDU",
                                Toast.LENGTH_LONG
                            ).show()
                            SmsManager.RESULT_ERROR_RADIO_OFF -> Toast.makeText(
                                context,
                                "Radio off",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }, IntentFilter(SENT))

                multipleSMS = smsMgr.divideMessage(newMessage)
                smsMgr.sendMultipartTextMessage(phoneNumber, null, multipleSMS, null, null)
              //  smsMgr.sendTextMessage(phoneNumber, null, message, sentPI, null)

            }
            catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }
}