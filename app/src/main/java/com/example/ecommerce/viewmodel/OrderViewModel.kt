package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.Order
import com.example.ecommerce.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel(){
    private val _order = MutableStateFlow<Resource<Order>>(Resource.Unspecified())
    val order = _order.asStateFlow()

    fun placeOrder(orderr: Order){
        viewModelScope.launch {
            _order.emit(Resource.Loading())
        }
        firestore.runBatch {batch ->
            /**
             i. Add order into user-orders collection
             ii Add the order into orders collection
             iii Delete the products from user-cart collection
             */

            // i.
            firestore.collection("users")
                .document(auth.uid!!)
                .collection("orders")
                .document()
                .set(orderr)

            //ii
            firestore.collection("orders").document().set(orderr)

            /**
             iii
             since we cannot delete all collection in firebase we want to loop through all the documents so as to delete them
             */

            firestore.collection("users").document(auth.uid!!).collection("cart").get()
                .addOnSuccessListener {
                    it.documents.forEach {
                        it.reference.delete()
                    }
                }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _order.emit(Resource.Success(orderr))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _order.emit(Resource.Error(it.message.toString()))
            }
        }
    }


}