package com.enu.postsystem.ui.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.enu.postsystem.R
import com.enu.postsystem.ui.dashboard.OrderAdapter.*

class OrderAdapter(private val list: List<OrderItems>, var context: Context): RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    class ViewHolder(ItemView: View):RecyclerView.ViewHolder(ItemView) {
        val nameOrder:TextView = itemView.findViewById(R.id.nameOrder)
        val textOrder:TextView = itemView.findViewById(R.id.txetOrder)
        val phoneNumber:TextView = itemView.findViewById(R.id.phoneNumber)
        val location:TextView = itemView.findViewById(R.id.location)
        val price:TextView = itemView.findViewById(R.id.price)
        val date:TextView = itemView.findViewById(R.id.date)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_layout, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items = list[position]
        holder.nameOrder.text = items.name
        holder.textOrder.text = items.textDelivery
        holder.date.text = items.dateConf
        holder.location.text = items.city
        holder.phoneNumber.text = items.phone
        holder.price.text = items.price.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}