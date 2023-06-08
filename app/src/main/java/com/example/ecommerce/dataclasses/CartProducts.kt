package com.example.ecommerce.dataclasses

data class CartProducts(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null,
){
    constructor(): this(Product(), 1, null, null)
}
