package com.rabbito.securecontacts.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.rabbito.securecontacts.Fragments.phoneBookFragmentDirections
import com.rabbito.securecontacts.Model.Contact
import com.rabbito.securecontacts.R
import kotlinx.android.synthetic.main.recyclerviewphone_book.view.*

class PhoneBookListAdapter(var mContext: Context) :
    RecyclerView.Adapter<PhoneBookListAdapter.MyViewHolder>() {

    private var userList = emptyList<Contact>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recyclerviewphone_book, parent, false)
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]
        var text = currentItem.firstName + " " + currentItem.lastName
        text = text.replace("\\s+".toRegex(), " ")
        holder.itemView.namePhoneBook.text = text
        holder.itemView.numberPhoneBook.text = currentItem.number.toString()


        holder.itemView.itemRow.setOnClickListener {
            try {
                val action =
                    phoneBookFragmentDirections.actionPhoneBookFragmentToCallFragment(currentItem)
                holder.itemView.findNavController().navigate(action)
            } catch (e: Exception) {
                Toast.makeText(mContext, "Something Weired!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun setData(user: List<Contact>) {
        this.userList = user
        notifyDataSetChanged()
    }


}