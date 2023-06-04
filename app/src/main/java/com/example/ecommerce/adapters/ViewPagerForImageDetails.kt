package com.example.ecommerce.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ViewpagerForImageDetailsBinding

class ViewPagerForImageDetails: RecyclerView.Adapter<ViewPagerForImageDetails.ViewPagerForImageDetailsViewHolder>() {
    inner class ViewPagerForImageDetailsViewHolder(val binding: ViewpagerForImageDetailsBinding): ViewHolder(binding.root){

        fun bind(imagePath: String){
            Glide.with(itemView).load(imagePath).into(binding.imageProductDetails)
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
           return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewPagerForImageDetailsViewHolder {
        return ViewPagerForImageDetailsViewHolder(ViewpagerForImageDetailsBinding.inflate(
            LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ViewPagerForImageDetailsViewHolder, position: Int) {
        val image = differ.currentList[position]
        holder.bind(image)
    }
}