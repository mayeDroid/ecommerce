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
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProductsState

    init {
        fetchSpecialProductsFromFireStore()
    }

    fun fetchSpecialProductsFromFireStore() {
        viewModelScope.launch {
            _specialProductsState.emit(Resource.Loading())
        }
        firestore.collection("Products").whereEqualTo("category", "Mobile phone").get()
            .addOnSuccessListener {result->
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
}