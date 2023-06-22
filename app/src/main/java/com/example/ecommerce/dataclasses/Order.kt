package com.example.ecommerce.dataclasses

data class Order(
    val orderStatus: String,
    val totalPrice : Float,
    val products: List<CartProducts>,
    val address: Address
)
