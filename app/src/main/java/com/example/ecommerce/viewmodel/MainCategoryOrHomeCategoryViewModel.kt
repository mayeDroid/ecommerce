package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.Product
import com.example.ecommerce.utilities.Resource
import com.google.firebase.firestore.FirebaseFirestore
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

    init {
        fetchSpecialProductsFromFireStore()
        fetchBestDeals()
        fetchBestProducts()
    }

    private fun fetchSpecialProductsFromFireStore() {
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

    private fun fetchBestDeals() {
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

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProductsState.emit(Resource.Loading())
        }
        firestore.collection("Products").get()// since we want to display all the products
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    val bestProductsList = result.toObjects(Product::class.java)
                    _bestProductsState.emit(Resource.Success(bestProductsList))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProductsState.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}