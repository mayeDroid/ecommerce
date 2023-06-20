package com.example.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerce.R
import com.example.ecommerce.databinding.AddressRvItemBinding
import com.example.ecommerce.dataclasses.Address

class AddressAdapter: RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    inner class AddressViewHolder(val binding: AddressRvItemBinding): ViewHolder(binding.root){

        fun bind(address: Address?, isSelected: Boolean) {
            binding.apply{
                buttonAddress.text = address?.addressTitle
                if (isSelected){
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_blue))
                }
                else {
                    buttonAddress.background = ColorDrawable(itemView.context.resources.getColor(R.color.g_white))
                }

            }

        }

    }

    private val diffUtil = object : DiffUtil.ItemCallback<Address>(){
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.addressTitle == newItem.addressTitle && oldItem.fullName == newItem.fullName
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        return AddressViewHolder(AddressRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    private var selectedAddress = -1    // we used this so as to change the color of a selected address

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, selectedAddress == position) // if selected address is in that position then change color

        holder.binding.buttonAddress.setOnClickListener {
            if(selectedAddress >= 0) {notifyItemChanged(selectedAddress)}
            selectedAddress = holder.adapterPosition
            notifyItemChanged(selectedAddress)
            onClick?.invoke(address)
        }
    }

    // helps to solve an issue where 2 addresses were selected after clicking the plus button on billing fragment
    init {
        differ.addListListener { _, _->
            notifyItemChanged(selectedAddress)
        }
    }

    var onClick: ((Address) -> Unit)? = null
}