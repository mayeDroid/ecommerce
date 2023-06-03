package com.example.ecommerce.fragments.categories

// we want all other fragments (chairs, cupboard, ...) to inherit from this fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapters.BestProductsAdapter
import com.example.ecommerce.databinding.FragmentBaseCategoryBinding

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    private lateinit var offerAdapter: BestProductsAdapter
    private lateinit var bestProducts: BestProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOfferRecyclerView()
        setUpBestProducts()
    }

    private fun setUpBestProducts() {
        bestProducts = BestProductsAdapter()
        binding.recyclerViewBestProducts.apply {
            //layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            // we used grid layout because we want to display it in 2 rows
            adapter = bestProducts
        }
    }

    private fun setUpOfferRecyclerView() {
        offerAdapter = BestProductsAdapter()
        binding.recyclerViewBestProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }
}