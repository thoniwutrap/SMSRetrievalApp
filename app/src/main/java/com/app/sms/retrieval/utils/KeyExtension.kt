package com.app.sms.retrieval.utils

import com.orhanobut.hawk.Hawk

internal fun String.dolphinKey() : String {
    return if(Hawk.get("dolphinKey", true)){
        "DPSMS$this"
    }else{
        this
    }
}

internal fun String.compareDolphinKey() : Boolean {
    return this.contains("DPSMS",true)
}
