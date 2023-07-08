package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.Product
import com.example.ecommerce.utilities.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


import javax.inject.Inject

@HiltViewModel
class MainCategoryOrHomeCategoryViewModel @Inject constructor(private var firestore: FirebaseFirestore) :
    ViewModel() {
    private val _specialProductsState =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProductsState: StateFlow<Resource<List<Product>>> = _specialProductsState

    private val _bestProductsState =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProductsState: StateFlow<Resource<List<Product>>> = _bestProductsState

    private val _bestDealsState =
        MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsState: StateFlow<Resource<List<Product>>> = _bestDealsState

    private val pagingInfo = PagingInfo()

    init {
        fetchSpecialProductsFromFireStore()
        fetchBestDeals()
        fetchBestProducts()
    }

    fun fetchSpecialProductsFromFireStore() {
        viewModelScope.launch {
            _specialProductsState.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Mobile phone").get()
            .addOnSuccessListener { result ->
                // val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    val specialProductsList = result.toObjects(Product::class.java)
                    _specialProductsState.emit(Resource.Success(specialProductsList))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _specialProductsState.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestDeals() {
        viewModelScope.launch {
            _bestDealsState.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Mobile phones").get()
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    val bestDealsList = result.toObjects(Product::class.java)
                    _bestDealsState.emit(Resource.Success(bestDealsList))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsState.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun fetchBestProducts() {
        if (!pagingInfo.isPagingEnd) {
            viewModelScope.launch {
                _bestProductsState.emit(Resource.Loading())
            }
            // firestore.collection("Products").limit(pagingInfo.bestProductPaging * 2)

            firestore.collection("Products").whereEqualTo("category", "Samsung")
                .orderBy("id", Query.Direction.ASCENDING).limit(pagingInfo.bestProductPaging * 4)
                .get()

                /*  since we want to display all the products,
                   .limit means we only want to fetch only 3 products when we launch the app, to dynamically fix this,
                   we use a method called paging as seen in paging info internal data class
                   to load all images once just remove the limit function
                   */

                .addOnSuccessListener { result ->
                    val bestProductsList = result.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _bestProductsState.emit(Resource.Success(bestProductsList))
                        pagingInfo.isPagingEnd = bestProductsList == pagingInfo.oldBestProductPaging
                        pagingInfo.oldBestProductPaging = bestProductsList
                    }
                    pagingInfo.bestProductPaging++   // this adds a new product after scrolling pass 3 products

                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestProductsState.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}

//helps with paging so when paging ends we don't want to keep updating firebase anymore when we scroll up and down due to the cost as we pay for paging
internal data class PagingInfo(
    var bestProductPaging: Long = 1, // if we put 2 it will multiple 2 by the 3 then load 6 images
    var oldBestProductPaging: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)