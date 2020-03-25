package com.app.sms.retrieval

import android.app.Application
import com.orhanobut.hawk.Hawk

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Hawk.init(baseContext).build()
    }
}