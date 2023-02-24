package com.example.skysys.screen

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.example.skysys.R

class CustomAdapter(private val messages: MutableList<Message>):
RecyclerView.Adapter<ContactHolder>()
{
    private val listOfMessages = messages
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):  ContactHolder{
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_line_view,parent,false)
        return ContactHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.tvName.text = listOfMessages[position].title
        holder.tvName.setTextColor(R.color.red)
    }

    override fun getItemCount(): Int = listOfMessages.size
}

class ContactHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val tvName:TextView = itemView.findViewById(R.id.rcText)
}