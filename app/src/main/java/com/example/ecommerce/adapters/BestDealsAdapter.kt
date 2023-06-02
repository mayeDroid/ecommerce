package com.example.ecommerce.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.BestDealsRvItemBinding
import com.example.ecommerce.dataclasses.Product

class BestDealsAdapter : RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>() {
    inner class BestDealsViewHolder(private var binding: BestDealsRvItemBinding) :
        ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageViewBestDeal)
                product.offerPercentage?.let {
                    val percentageOff = (100 - product.offerPercentage) * product.price!!/100
                   /* val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price!!
                    //tvNewPrice.text = "₦ ${priceAfterOffer}"*/
                    tvDealNewPrice.text = "₦ ${String.format("%.2f", percentageOff)}" // to get 2 digits from the decimal
                }
                tvDealOldPrice.text = "₦ ${product.price}"
                tvDealProductName.text = product.name
            }

        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }
}