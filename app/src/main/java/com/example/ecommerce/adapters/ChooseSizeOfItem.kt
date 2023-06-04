package com.example.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerce.databinding.RvChooseSizeOfItemBinding

class ChooseSizeOfItem: RecyclerView.Adapter<ChooseSizeOfItem.ChooseSizeOfItemViewHolder>() {

    private var selectedPosition = -1

    inner class ChooseSizeOfItemViewHolder(private val binding: RvChooseSizeOfItemBinding ): ViewHolder(binding.root){
        fun bind(size: String, position: Int) {
            binding.textViewSize.text = size
            if (position == selectedPosition){ // meaning size is selected
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                }
            } else{ // when no color is selected
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseSizeOfItem.ChooseSizeOfItemViewHolder {
        return ChooseSizeOfItemViewHolder(RvChooseSizeOfItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChooseSizeOfItem.ChooseSizeOfItemViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClicked?.invoke(size)
        }
    }

    var onItemClicked: ((String) -> Unit)? = null
}

