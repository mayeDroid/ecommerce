package com.example.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerce.R
import com.example.ecommerce.databinding.OrderItemBinding
import com.example.ecommerce.dataclasses.Order
import com.example.ecommerce.dataclasses.OrderStatus
import com.example.ecommerce.dataclasses.getOrderStatus

class AllOrdersAdapter: RecyclerView.Adapter<AllOrdersAdapter.OrdersViewHolder>() {
    inner class OrdersViewHolder(private val binding: OrderItemBinding): ViewHolder(binding.root){

        fun bind(order: Order){
            binding.apply {
                tvOrderId.text = order.orderID.toString()
                tvOrderDate.text = order.date.toString()

                val resource = itemView.resources

                //help us determine the color of the order status
                val colorDrawable = when (getOrderStatus(order.orderStatus)){
                    is OrderStatus.Ordered -> ColorDrawable(resource.getColor(R.color.g_blue))
                    is OrderStatus.Confirmed -> ColorDrawable(resource.getColor(R.color.g_gray500))
                    is OrderStatus.Canceled -> ColorDrawable(resource.getColor(R.color.g_white))
                    is OrderStatus.Returned -> ColorDrawable(resource.getColor(R.color.g_light_red))
                    is OrderStatus.Delivered -> ColorDrawable(resource.getColor(R.color.black))
                }

                imageOrderState.setImageDrawable(colorDrawable)

            }
        }
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Order>(){

        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.products == newItem.products
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffUtil)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        return OrdersViewHolder(
            OrderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: OrdersViewHolder, position: Int){
        val order = differ.currentList[position]
        holder.bind(order)

        holder.itemView.setOnClickListener {
            onClick?.invoke(order)
        }
    }

    var onClick: ((Order) -> Unit)? = null

}