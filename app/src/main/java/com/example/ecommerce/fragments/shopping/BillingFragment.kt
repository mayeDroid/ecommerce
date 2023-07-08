package com.example.ecommerce.fragments.shopping

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.AddressAdapter
import com.example.ecommerce.adapters.BillingProductsAdapter
import com.example.ecommerce.databinding.FragmentBillingBinding
import com.example.ecommerce.dataclasses.Address
import com.example.ecommerce.dataclasses.CartProducts
import com.example.ecommerce.dataclasses.Order
import com.example.ecommerce.dataclasses.OrderStatus
import com.example.ecommerce.utilities.HorizontalItemDecoration
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.BillingViewModel
import com.example.ecommerce.viewmodel.OrderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment: Fragment() {
    private lateinit var binding: FragmentBillingBinding
    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val billingViewModel by viewModels<BillingViewModel>()
    private  val args by navArgs<BillingFragmentArgs>() // to get the price from val action created in cart fragment
    private var productsArgs = emptyList<CartProducts>()
    private var totalPricee = 0f

    private var selectedAddress: Address? = null
    private val orderViewModel by viewModels<OrderViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBillingBinding.inflate(inflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productsArgs = args.products.toList()
        totalPricee = args.totalPrice.toFloat()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpBillingProductsRecyclerView()
        setUpAddressRecyclerView()

        // if we don't have an order we don't want to show these
        if (!args.payment){
            binding.apply {
                buttonPlaceOrder.visibility = View.INVISIBLE
                totalBoxContainer.visibility = View.INVISIBLE
                middleLine.visibility = View.INVISIBLE
                bottomLine.visibility = View.INVISIBLE
            }
        }

        binding.imageAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_billingFragment_to_addressFragment)
        }

        lifecycleScope.launchWhenStarted {
            billingViewModel.address.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        binding.progressbarAddress.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.order.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        binding.buttonPlaceOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        findNavController().navigateUp()
                        Snackbar.make(requireView(), "Your order has been placed", Snackbar.LENGTH_SHORT).show()
                    }
                    is Resource.Error -> {
                        binding.buttonPlaceOrder.revertAnimation()
                        Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        billingProductsAdapter.differ.submitList(productsArgs)

        binding.tvTotalPrice.text = "₦ $totalPricee"


        addressAdapter.onClick = {
            selectedAddress = it        // to update our selected address

            // here if we are in the profile fragment when we click address we want to be able to
            // edit it, but when we are in checkout we don't want to show address fragment
            if (!args.payment) {
                val bundle = Bundle().apply {
                    putParcelable("address", selectedAddress)
                }
                findNavController().navigate(R.id.action_billingFragment_to_addressFragment, bundle)
            }
        }

        binding.imageCloseBilling.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.buttonPlaceOrder.setOnClickListener {
            if (selectedAddress == null){
                Toast.makeText(requireContext(), "Please select an address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            showOrderConfirmationDialog()
        }
    }

    private fun showOrderConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setTitle("Order items")
            setMessage("Do you want to order items in your cart?")
                .setNegativeButton("No"){
                        dialog, _  -> dialog.dismiss()
                }
                .setPositiveButton("Yes"){
                        dialog, _ ->
                    val order = Order(
                        OrderStatus.Ordered.orderStatus,
                        totalPricee,
                        productsArgs,
                        selectedAddress!!
                    )
                    orderViewModel.placeOrder(order)
                    dialog.dismiss()
                }
        }
        alertDialog.create()
        alertDialog.show()
    }

    private fun setUpAddressRecyclerView() {
        binding.rvAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = addressAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setUpBillingProductsRecyclerView() {
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
            adapter = billingProductsAdapter
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}