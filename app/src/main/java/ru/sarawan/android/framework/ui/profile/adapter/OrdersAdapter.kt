package ru.sarawan.android.framework.ui.profile.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sarawan.android.R
import ru.sarawan.android.databinding.OrderItemBinding
import ru.sarawan.android.model.data.OrderApprove
import ru.sarawan.android.model.data.OrderStatus

class OrdersAdapter(val itemClickListener: ItemClickListener) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {
    private var list : List<OrderApprove> = ArrayList(5)

    @SuppressLint("NotifyDataSetChanged")
    fun setOrder(list : List<OrderApprove>) {
        this.list = list
        notifyDataSetChanged()
    }
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
        private val context = binding.root.context
        fun bind(order : OrderApprove) = with(binding) {
            val orderName = "№${order.information?.orderName}"
            val status = order.orderStatus
            nameOrderValueTextView.text = orderName
            val currentStatus = when(status) {
                OrderStatus.NEW -> statusReceivedTextView
                OrderStatus.CAN -> statusAssemblyTextView
                else -> null
            }
            currentStatus
                ?.setTextColor(
                    context
                        .resources
                        .getColor(R.color.secondary_orange, null))
            cancelImageButton.setOnClickListener {
                itemClickListener.cancel(absoluteAdapterPosition)
            }

        }
    }
}