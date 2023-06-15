package com.example.ecommerce.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.CartProductsOrItemsAdapter
import com.example.ecommerce.databinding.FragmentCartBinding
import com.example.ecommerce.firebase.FirebaseCommonOrAddToAndUpdateCart
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.utilities.VerticalItemDecoration
import com.example.ecommerce.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {
    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductsOrItemsAdapter() }
    private val viewModel by activityViewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpCartRV()

        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    binding.tvTotalPrice.text = "₦ $price"
                }
            }
        }

        // when you click a product in the card it will take you to the product details
        cartAdapter.onClickProductInCart = {
            val bundle = Bundle().apply { putParcelable("products", it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, bundle)
        }

        cartAdapter.onPlusClick = {
            viewModel.changeQuantity(it, FirebaseCommonOrAddToAndUpdateCart.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick = {
            viewModel.changeQuantity(it, FirebaseCommonOrAddToAndUpdateCart.QuantityChanging.DECREASE)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialogOrIndicatorForDeleteCartItems.collectLatest {
                val alertDialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Delete item from cart")
                    setMessage("Do you want to delete this item from your cart?")
                        .setNegativeButton("No"){
                            dialog, _  -> dialog.dismiss()
                        }
                        .setPositiveButton("Yes"){
                            dialog, _ ->
                            viewModel.deleteCartProduct(it)
                            dialog.dismiss()
                        }
                }
                alertDialog.create()
                alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProductsss.collectLatest {
                when (it){
                    is  Resource.Loading -> {
                        binding.progressBarCart.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressBarCart.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }
                        else hideEmptyCart()
                        showOtherViews()
                        cartAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        binding.progressBarCart.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }


    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility = View.VISIBLE
            tvTotalBoxContainer.visibility = View.VISIBLE
            buttonCheckOut.visibility = View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility = View.GONE
            tvTotalBoxContainer.visibility = View.GONE
            buttonCheckOut.visibility = View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.GONE
        }
    }

    private fun showEmptyCart() {
        binding.apply {
            layoutCartEmpty.visibility = View.VISIBLE
        }
    }

    // this adds space between items in the recycler
    private fun setUpCartRV() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = cartAdapter
            addItemDecoration(VerticalItemDecoration())
        }
    }
}