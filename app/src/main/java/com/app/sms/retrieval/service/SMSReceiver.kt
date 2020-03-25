package com.app.sms.retrieval.service

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.util.Log.e
import com.app.sms.retrieval.MainActivity
import com.app.sms.retrieval.model.SmsReceiveDao
import com.orhanobut.hawk.Hawk
import io.karn.notify.Notify
import java.lang.Exception


class SMSReceiver : BroadcastReceiver() {


    private var TAG = "SmsBroadcastReceiver"
    lateinit var serviceProviderNumber: String
    lateinit var serviceProviderSmsCondition: String
    private var listener: Listener? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            var smsSender = ""
            var smsBody = ""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    smsSender = smsMessage.displayOriginatingAddress
                    smsBody += smsMessage.messageBody
                }
            } else {
                val smsBundle = intent.extras
                if (smsBundle != null) {
                    val pdus = smsBundle.get("pdus") as Array<Any>
                    val messages = arrayOfNulls<SmsMessage>(pdus.size)
                    for (i in messages.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        smsBody += messages[i]!!.messageBody
                    }
                    smsSender = messages[0]!!.originatingAddress.toString()
                }
            }

            //INSERT TO HAWL
            try {
                val currentTimestamp = System.currentTimeMillis()
                var smsReceiveHawl: MutableList<SmsReceiveDao>? = Hawk.get("smsReceiveDao")
                if (smsReceiveHawl == null) {
                    smsReceiveHawl = mutableListOf()
                }
                smsReceiveHawl.add(
                    SmsReceiveDao(
                        smsSender = smsSender,
                        smsMessage = smsBody,
                        smsReceiveTime = currentTimestamp
                    )
                )
                Hawk.put("smsReceiveDao", smsReceiveHawl)
            } catch (e: Exception) {
                e.printStackTrace()
            }


            Notify
                .with(context)
                .meta { // this: Payload.Meta
                    clickIntent = PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0)
                }
                .content {
                    title = smsSender
                    text = smsBody
                }
                .show()


            //var settingsManager = SettingsManager(context)

            //  PostReceivedMessage().execute(settingsManager.receiveURL, settingsManager.deviceId, smsBody, smsSender)

            val i = Intent("SMS_RECEIVED")
            i.putExtra("number", smsSender)
            i.putExtra("message", smsBody)
            context.sendBroadcast(i)

            if (::serviceProviderNumber.isInitialized && smsSender == serviceProviderNumber && smsBody.startsWith(
                    serviceProviderSmsCondition
                )
            ) {
                if (listener != null) {
                    listener!!.onTextReceived(smsBody)
                }
            }
        }
    }


    internal interface Listener {
        fun onTextReceived(text: String)
    }
}