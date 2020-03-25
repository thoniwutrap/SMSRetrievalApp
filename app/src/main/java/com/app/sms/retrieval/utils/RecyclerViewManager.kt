package com.app.sms.retrieval.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

internal fun RecyclerView.init() {
    this.setHasFixedSize(true)
    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this.context)
    this.layoutManager = layoutManager
}

internal fun RecyclerView.initHorizontal() {
    this.setHasFixedSize(true)
    val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this.context,LinearLayoutManager.HORIZONTAL ,false)
    this.layoutManager = layoutManager
}