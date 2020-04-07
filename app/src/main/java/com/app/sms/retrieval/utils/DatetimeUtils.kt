package com.app.sms.retrieval.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
internal fun Long.datetime(): String {
    return try {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm")
        val netDate = Date(this)
        sdf.format(netDate)
    } catch (e: Exception) {
        ""
    }
}


fun datetimeNow() : Long{
    return System.currentTimeMillis()
}