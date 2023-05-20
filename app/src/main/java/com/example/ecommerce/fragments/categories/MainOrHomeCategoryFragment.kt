package com.example.ecommerce.fragments.categories

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapters.SpecialProductsAdapter
import com.example.ecommerce.databinding.FragmentMainOrHomeCategoryBinding
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.MainCategoryOrHomeCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val TAG = "MainOrHomeCategoryFragment"
@AndroidEntryPoint
class MainOrHomeCategoryFragment: Fragment(R.layout.fragment_main_or_home_category) {
    private lateinit var binding: FragmentMainOrHomeCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
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

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
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
}