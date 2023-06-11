package com.example.ecommerce.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.ProductRvItemBinding
import com.example.ecommerce.dataclasses.Product
import com.example.ecommerce.helperclasses.getProductPriceAfterPercentage
import com.squareup.picasso.Picasso

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>(){
    inner class BestProductsViewHolder(private var binding: ProductRvItemBinding): ViewHolder(binding.root){

        fun bind(product: Product){
            binding.apply {
                   Glide.with(itemView).load(product.images[0]).into(imgViewPo)
                   //Picasso.get().load(product.images[0]).into(imgViewPo)
                    product.offerPercentage?.let {
                        //val percentageOff = (100 - product.offerPercentage) * product.price!!/100
                        val percentageOff = product.offerPercentage.getProductPriceAfterPercentage(product.price!!)
                        tvProductNewPrice.text = "₦ ${String.format("%.2f", percentageOff)}"
                        tvProductPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    }

                if (product.offerPercentage == null) tvProductNewPrice.visibility = View.GONE
                tvProductPrice.text = "₦ ${product.price}"
                tvProductName.text = product.name
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(ProductRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null
}