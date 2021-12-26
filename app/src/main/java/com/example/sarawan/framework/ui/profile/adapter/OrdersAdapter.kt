package com.example.sarawan.framework.ui.profile.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sarawan.R
import com.example.sarawan.databinding.OrderItemBinding
import com.example.sarawan.model.data.OrderApprove

class OrdersAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    private var list : List<OrderApprove> = ArrayList(5)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrdersAdapter.OrderViewHolder =
        OrderViewHolder(
            LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.order_item,
                parent,
                false
            ))

    override fun onBindViewHolder(holder: OrdersAdapter.OrderViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int  = list.size

    inner class OrderViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        private val binding = OrderItemBinding.bind(item)
        fun bind(order : OrderApprove) = with(binding) {

        }
    }
}