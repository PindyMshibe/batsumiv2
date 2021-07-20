package com.example.batsumiv2

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class ExampleAdapter
    (
    private val context: Context, private var contactsArrayList: ArrayList<Contacts>) : RecyclerView.Adapter<ExampleAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false))
    }

    fun filterList(filterllist: ArrayList<Contacts>) {
        contactsArrayList = filterllist
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = contactsArrayList[position]
        holder.contactTextView.text = user.name
        holder.itemView.setOnClickListener {
            val i = Intent(context, userDetails::class.java)
            i.putExtra("name", user.name)
            i.putExtra("contact", user.number)
            context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return contactsArrayList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val contactImageView: ImageView
        val contactTextView: TextView

        init {
            contactImageView = itemView.findViewById(R.id.imageContact)
            contactTextView = itemView.findViewById(R.id.textContactName)
        }
    }
}

