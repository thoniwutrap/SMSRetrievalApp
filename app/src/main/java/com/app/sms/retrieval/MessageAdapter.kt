package com.app.sms.retrieval

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.sms.retrieval.model.SmsReceiveDao
import com.app.sms.retrieval.utils.datetime
import kotlinx.android.synthetic.main.list_item_msg.view.*

class MessageAdapter(private var msg: List<SmsReceiveDao>?,
                     private val listener: Listener) : RecyclerView.Adapter<MessageAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_msg, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return try {
            msg!!.size
        }catch (ex : Exception){
            0
        }
    }

    fun updateMsg(msgUpdate : List<SmsReceiveDao>?){
        msg = msgUpdate?.sortedWith(compareByDescending{it.smsReceiveTime})
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(msg!![position], listener,position)
    }


    interface Listener {
        fun onItemClick(item : SmsReceiveDao)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun bind(item : SmsReceiveDao, listener: Listener, position : Int) {
            itemView.txtMobileNo.text = item.smsSender
            itemView.txtMsg.text = item.smsMessage
            itemView.txtDatetime.text = item.smsReceiveTime.datetime()
            itemView.btnShowAll.setOnClickListener{ listener.onItemClick(item) }
        }

    }
}