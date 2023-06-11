package com.example.ecommerce.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.CartProducts
import com.example.ecommerce.firebase.FirebaseCommonOrAddToAndUpdateCart
import com.example.ecommerce.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SnapshotMetadata
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel

class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommonOrAddToAndUpdateCart: FirebaseCommonOrAddToAndUpdateCart
): ViewModel(){
    private val _cartProducts = MutableStateFlow<Resource<List<CartProducts>>>(Resource.Unspecified())
    val cartProductsss = _cartProducts.asStateFlow()

    private var cartProductsDocument = emptyList<DocumentSnapshot>()

    init {
        getCartProductFromCartCollection()
    }

    private fun getCartProductFromCartCollection() {
        viewModelScope.launch {
            firestore.collection("users").document(auth.uid!!).collection("cart") // to retrieve the results
                .addSnapshotListener { value, error ->  // we use this because everytime the user
                    // adds a new product we want to refresh the UI in (ie in the bottom navigation)
                    if (error != null || value == null){
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Error(error?.message.toString()))
                        }
                    }else{
                        cartProductsDocument = value.documents
                        val cartProduct = value.toObjects(CartProducts::class.java)
                        viewModelScope.launch {
                            _cartProducts.emit(Resource.Success(cartProduct))
                        }
                    }
                }
        }

        fun changeQuantity(cartProducts: CartProducts,
                           quantityChanging: FirebaseCommonOrAddToAndUpdateCart.QuantityChanging) {

            val index = cartProductsss.value.data?.indexOf(cartProducts)
            /**
             * index could be equal to -1 if the function [getCartProductFromCartCollection] delays (ie the user presses the remove from cart button continuously)
             * which will also delay the results we expect to be inside the [_cartProducts]
             */
            if (index != null && index != -1){
                val documentId = cartProductsDocument[index].id
                when(quantityChanging){
                    FirebaseCommonOrAddToAndUpdateCart.QuantityChanging.INCREASE -> {
                        increaseQuantity(documentId)
                    }
                    FirebaseCommonOrAddToAndUpdateCart.QuantityChanging.DECREASE -> {
                        decreaseQuantity(documentId)
                    }
                }
            }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommonOrAddToAndUpdateCart.increaseQuantity(documentId){result, exception ->
            if (exception != null){
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommonOrAddToAndUpdateCart.decreaseQuantity(documentId){result, exception ->
            if (exception != null){
                viewModelScope.launch { _cartProducts.emit(Resource.Error(exception.message.toString())) }
            }
        }
    }
}