package com.example.ecommerce.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.ecommerce.adapters.BillingProductsAdapter
import com.example.ecommerce.databinding.FragmentOrderDetailBinding
import com.example.ecommerce.dataclasses.Order
import com.example.ecommerce.dataclasses.OrderStatus
import com.example.ecommerce.dataclasses.getOrderStatus
import com.example.ecommerce.utilities.VerticalItemDecoration

class OrdersDetailFragment: Fragment() {
    private lateinit var binding: FragmentOrderDetailBinding
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }
    private val args by  navArgs<OrdersDetailFragmentArgs>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderDetailBinding.inflate(inflater)
        return binding.root
    }
    /**
     * we don't need a viewModel because we will get the orders from the all orders fragment
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val order = args.order

        setUpOrderRV()

        binding.apply {
            tvOrderId.text = "Order #${order.orderID}"

            //setting up the stepView library ie the icon that shows the orderStatus from ordered to delivered

            stepView.setSteps(
                mutableListOf(
                OrderStatus.Ordered.orderStatus,
                    OrderStatus.Canceled.orderStatus,
                OrderStatus.Confirmed.orderStatus,
                OrderStatus.Delivered.orderStatus,
                    OrderStatus.Returned.orderStatus

                )
            )

            val currentOrderState = when(getOrderStatus(order.orderStatus)){
                is OrderStatus.Ordered -> 0
                is OrderStatus.Canceled -> 1
                is OrderStatus.Confirmed -> 2
                is OrderStatus.Delivered -> 3
                is OrderStatus.Returned -> 4
            }

            stepView.go(currentOrderState, false)

            if (currentOrderState == 4)stepView.done(true)

            tvFullName.text = order.address.fullName
            tvAddress.text = "${order.address.street} ${order.address.city}"
            tvPhoneNumber.text = "${order.address.phone}"
            tvTotalPrice.text = order.totalPrice.toString()
        }
        // update products in our adapter
        billingProductsAdapter.differ.submitList(order.products)

    }

    private fun setUpOrderRV() {
        binding.rvProducts.apply {
            adapter = billingProductsAdapter
            layoutManager = LinearLayoutManager(requireContext(),RecyclerView.VERTICAL, false)
            addItemDecoration(VerticalItemDecoration())
        }
    }
}