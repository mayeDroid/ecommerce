package com.example.ecommerce.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapters.BestDealsAdapter
import com.example.ecommerce.adapters.BestProductsAdapter
import com.example.ecommerce.adapters.SpecialProductsAdapter
import com.example.ecommerce.databinding.FragmentMainOrHomeCategoryBinding
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.MainCategoryOrHomeCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainOrHomeCategoryFragment"
@AndroidEntryPoint
class MainOrHomeCategoryFragment: Fragment(R.layout.fragment_main_or_home_category) {
    private lateinit var binding: FragmentMainOrHomeCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoryOrHomeCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainOrHomeCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        specialProductsRV()
        setUpBestDealsRecyclerView()
        setUpBestProductsRecyclerView()

        lifecycleScope.launchWhenStarted {
            viewModel.specialProductsState.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        Log.e(TAG,it.message.toString())
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsState.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        Log.e(TAG,it.message.toString())
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProductsState.collectLatest {
                when(it){
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestProductAdapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                        Log.e(TAG,it.message.toString())
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setUpBestProductsRecyclerView() {
        bestProductAdapter = BestProductsAdapter()
        binding.recyclerViewBestproducts.apply {
            //layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            // we used grid layout because we want to display it in 2 rows
            adapter = bestProductAdapter
        }
    }


    private fun hideLoading() {
        binding.MainOrHomeCategoryFragmentProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.MainOrHomeCategoryFragmentProgressBar.visibility = View.VISIBLE
    }

    private fun specialProductsRV() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.recyclerViewSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }

    private fun setUpBestDealsRecyclerView() {
        bestDealsAdapter = BestDealsAdapter()
        binding.recyclerViewBestDeals.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

}