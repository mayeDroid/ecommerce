package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.CartProducts
import com.example.ecommerce.firebase.FirebaseCommonOrAddToAndUpdateCart
import com.example.ecommerce.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsOfCartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    val auth: FirebaseAuth,
    private val firebaseCommonOrAddToAndUpdateCart: FirebaseCommonOrAddToAndUpdateCart
): ViewModel(){

    private val _addToCart = MutableStateFlow<Resource<CartProducts>>(Resource.Unspecified())
    val addToCart =_addToCart.asStateFlow()

    // to check if an item is already in cart so we just increase the number
    fun addUpdateOrUpdateCart(cartProducts: CartProducts){

        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
       firestore.collection("users").document(auth.uid!!).collection("cart")
           .whereEqualTo("product.id", cartProducts.product.id).get()
           .addOnSuccessListener {
               it.documents.let {
                   if (it.isEmpty()){   // add a new product
                       addNewProductToCart(cartProducts)
                   }
                   else{
                       val product = it.first().toObject(cartProducts::class.java)
                       if(product == cartProducts){ // increase a already existing product quantity
                           val documentId = it.first().id
                           increaseQuantityInCart(documentId, cartProducts)
                       }
                       else{
                           // add a new product
                           addNewProductToCart(cartProducts)
                       }
                   }
               }
           }
           .addOnFailureListener {
               viewModelScope.launch { _addToCart.emit(Resource.Error(it.message.toString())) }
           }
    }

    private fun addNewProductToCart(cartProducts: CartProducts){
        firebaseCommonOrAddToAndUpdateCart.addProductToCart(cartProducts){addedProduct, e ->
            viewModelScope.launch {
                if (e == null)_addToCart.emit(Resource.Success(addedProduct!!))
                else _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantityInCart(documentId: String, cartProducts: CartProducts){
        firebaseCommonOrAddToAndUpdateCart.increaseQuantity(documentId){
            _, errorMessage ->
            viewModelScope.launch {
                if (errorMessage == null)_addToCart.emit(Resource.Success(cartProducts))
                else _addToCart.emit(Resource.Error(errorMessage.message.toString()))
            }
        }
    }
}