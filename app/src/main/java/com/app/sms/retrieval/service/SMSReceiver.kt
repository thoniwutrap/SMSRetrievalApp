package com.app.sms.retrieval.service

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.telephony.SmsMessage
import com.app.sms.retrieval.MainActivity
import com.app.sms.retrieval.R
import com.app.sms.retrieval.model.SmsReceiveDao
import com.app.sms.retrieval.utils.compareDolphinKey
import com.app.sms.retrieval.utils.datetimeNow
import com.app.sms.retrieval.utils.deleteDolphinKey
import com.application.isradeleon.notify.Notify
import com.orhanobut.hawk.Hawk
import java.lang.Exception


class SMSReceiver : BroadcastReceiver() {

    lateinit var serviceProviderNumber: String
    lateinit var serviceProviderSmsCondition: String
    private var listener: Listener? = null


    internal interface Listener {
        fun onTextReceived(text: String)
    }


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

            if(smsBody.compareDolphinKey()) {
                saveReceiver(context, smsSender, smsBody)
            }
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

    fun saveReceiver(context: Context,smsSender : String, smsBody : String){

        Notify.create(context)
            .setTitle(smsSender)
            .setContent(smsBody)
            .setImportance(Notify.NotificationImportance.MAX)
            .setSmallIcon(R.drawable.ic_sms_black_24dp)
            .setColor(R.color.colorPrimary)
            .setAction(Intent(context, MainActivity::class.java))
            .circleLargeIcon()
            .show();

        val intent =  Intent("Updated");
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.component = ComponentName(context.getString(R.string.package_name_dolphin), context.getString(R.string.package_name_dolphin_receiver))
        intent.putExtra("phoneNo", smsSender)
        intent.putExtra("msg", smsBody.deleteDolphinKey())
        intent.putExtra("time", datetimeNow())
        context.sendBroadcast(intent);
    }

}