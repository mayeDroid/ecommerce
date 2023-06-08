package com.example.ecommerce.fragments.categories

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerce.dataclasses.Category
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.ViewModelCategorySamsungIphoneAndOthers
import com.example.ecommerce.viewmodel.factory.BaseCategoryViewModelFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SamsungFragment: BaseCategoryFragment() {
    @Inject
    lateinit var firestore: FirebaseFirestore

    val viewModel by viewModels<ViewModelCategorySamsungIphoneAndOthers> {BaseCategoryViewModelFactory(firestore, Category.MobilePhones)  }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.offerProducts.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        showProgressBarOfferProducts()
                    }
                    is Resource.Success -> {
                        offerAdapter.differ.submitList(it.data)
                        hideProgressBarOfferProducts()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        showProgressBarBestProducts()
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        hideProgressBarBarBestProducts()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    // for paging
    override fun onBestProductsPagingRequests() {
        viewModel.fetchBestProducts()
    }

    override fun onOfferPagingRequest() {
        viewModel.fetchOfferProducts()
    }
}