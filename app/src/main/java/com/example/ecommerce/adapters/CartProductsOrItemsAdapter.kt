package com.example.ecommerce.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommerce.databinding.CartItemsItemsInCartBinding
import com.example.ecommerce.dataclasses.CartProducts
import com.example.ecommerce.helperclasses.getProductPriceAfterPercentage

class CartProductsOrItemsAdapter: RecyclerView.Adapter<CartProductsOrItemsAdapter.CartProductsOrItemsViewHolder>() {
    inner class CartProductsOrItemsViewHolder(var binding: CartItemsItemsInCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProducts: CartProducts) {
            binding.apply {
                Glide.with(itemView).load(cartProducts.product.images[0]).into(imageCartItems)
                tvItemsInCartName.text = cartProducts.product.name
                tvCartItemsQuantity.text = cartProducts.quantity.toString()

                //val percentageOff = (100 - product.offerPercentage) * product.price!!/100
                // we just created a helper file to do this, its not really necessary, the one above is fine
                val percentageOff = cartProducts.product.offerPercentage.getProductPriceAfterPercentage(cartProducts.product.price!!)
                tvItemsInCartPrice.text = "₦ ${String.format("%.2f", percentageOff)}"

                imageCartProductColor.setImageDrawable(ColorDrawable(cartProducts.selectedColor?:Color.TRANSPARENT))
                tvCartProductSize.text = cartProducts.selectedSize?:"".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }

            }

        }
    }

    //DiffUtil makes RV faster because it will not refresh all items inside the RV but only items that got updated
    private val diffCallBack = object : DiffUtil.ItemCallback<CartProducts>() {
        override fun areItemsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProducts, newItem: CartProducts): Boolean {
            return oldItem == newItem
        }
    }

    // the Diff responsible for updating the list
    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsOrItemsViewHolder {
        return CartProductsOrItemsViewHolder(
            CartItemsItemsInCartBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsOrItemsViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }

        holder.binding.imagePlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.imageMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }

    var onProductClick: ((CartProducts) -> Unit)? = null
    var onPlusClick: ((CartProducts) -> Unit)? = null
    var onMinusClick: ((CartProducts) -> Unit)? = null

}