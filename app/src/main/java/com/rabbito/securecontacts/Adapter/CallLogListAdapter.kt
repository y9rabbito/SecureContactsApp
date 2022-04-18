package com.rabbito.securecontacts.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.rabbito.securecontacts.Model.CallLogContact
import com.rabbito.securecontacts.R
import kotlinx.android.synthetic.main.recyclerview_calllog.view.*


class CallLogListAdapter(val data: List<CallLogContact>, var mContext: Context) :
    RecyclerView.Adapter<CallLogListAdapter.viewHolder>() {


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnCreateContextMenuListener {
        init {
            itemView.setOnCreateContextMenuListener(this)
        }

        val number: TextView = itemView.findViewById(R.id.callLogNumber)
        val callType: ImageView = itemView.findViewById(R.id.callLogType)
        val duration: TextView = itemView.findViewById(R.id.callLogDuration)
        val date: TextView = itemView.findViewById(R.id.callLogDate)
        val name: TextView = itemView.findViewById(R.id.callLogName)

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            menu?.add(adapterPosition, 101, 0, "Add to Contacts")
            menu?.add(adapterPosition, 102, 1, "Delete")
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_calllog, parent, false)


        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {


        val itemModel = data[position]

        holder.number.text = itemModel.number
        holder.duration.text = itemModel.duration
        holder.date.text = itemModel.date.toString()
        holder.name.text = itemModel.name
        when (itemModel.typeCall) {
            1 -> holder.callType.setImageResource(R.drawable.ic_incomingcall)
            2 -> holder.callType.setImageResource(R.drawable.ic_outgoingcall)
            3 -> holder.callType.setImageResource(R.drawable.ic_missedcall)
            10 -> holder.callType.setImageResource(R.drawable.ic_missedoutgoing)
            else -> holder.callType.setImageResource(R.drawable.ic_rejectedcall)
        }

        holder.itemView.currentItem.setOnClickListener {
            try {
                val dial = "tel:${itemModel.number}"
                mContext.startActivity(Intent(Intent.ACTION_CALL, Uri.parse(dial)))
            } catch (e: Exception) {
                Toast.makeText(mContext, "Please allow the permission!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


}