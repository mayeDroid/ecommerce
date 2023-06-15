package com.example.ecommerce.firebase

import com.example.ecommerce.dataclasses.CartProducts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommonOrAddToAndUpdateCart(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection = firestore.collection("users").document(auth.uid!!).collection("cart")

    fun addProductToCart(cartProducts: CartProducts, onResult: (CartProducts?, Exception?) -> Unit){
        cartCollection.document().set(cartProducts)
            .addOnSuccessListener {
            onResult(cartProducts, null)
        }
            .addOnFailureListener {
            onResult(null, it)
        }
    }

    fun increaseQuantity(documentID: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { // run transaction to read and write while run batch just to read data
            transaction ->
            val documentRef = cartCollection.document(documentID)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProducts::class.java)
            productObject?.let {
                cartProd ->
                val newQuantity = cartProd.quantity + 1
                val newProductObject = cartProd.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentID, null)
        }
            .addOnFailureListener {
                onResult(null, it)
        }
    }


    fun decreaseQuantity(documentID: String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction { // run transaction to read and write while run batch just to read data
                transaction ->
            val documentRef = cartCollection.document(documentID)
            val document = transaction.get(documentRef)
            val productObject = document.toObject(CartProducts::class.java)
            productObject?.let {
                    cartProducts ->
                val newQuantity = cartProducts.quantity - 1
                val newProductObject = cartProducts.copy(quantity = newQuantity)
                transaction.set(documentRef, newProductObject)
            }
        }
            .addOnSuccessListener {
                onResult(documentID, null)
            }
            .addOnFailureListener {
                onResult(null, it)
            }
    }

    //helps us distinguish bw increase and decrease functions
   enum class QuantityChanging {
       INCREASE, DECREASE
   }
}