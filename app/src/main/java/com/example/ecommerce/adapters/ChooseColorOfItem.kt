package com.example.ecommerce.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.ecommerce.databinding.RvChooseColorOfItemBinding

class ChooseColorOfItem: RecyclerView.Adapter<ChooseColorOfItem.ChooseColorOfItemViewHolder>() {

    private var selectedPosition = -1
    inner class ChooseColorOfItemViewHolder(private val binding: RvChooseColorOfItemBinding): ViewHolder(binding.root) {

        fun bind(color: Int, position: Int) {
            val imageDrawable = ColorDrawable(color)    // here we want to set the image color to the color selected
            binding.imageColor.setImageDrawable(imageDrawable)

            if (position == selectedPosition){ // meaning color is selected
                binding.apply {
                    imageShadow.visibility = View.VISIBLE
                    imagePicked.visibility = View.VISIBLE
                }
            } else{ // when no color is selected
                binding.apply {
                    imageShadow.visibility = View.INVISIBLE
                    imagePicked.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Int>(){
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChooseColorOfItemViewHolder {
        return ChooseColorOfItemViewHolder(RvChooseColorOfItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ChooseColorOfItemViewHolder, position: Int) {
        val color = differ.currentList[position]
        holder.bind(color, position)

        holder.itemView.setOnClickListener {
            if (selectedPosition >= 0) notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClicked?.invoke(color)
        }
    }

    var onItemClicked: ((Int) -> Unit)? = null
}