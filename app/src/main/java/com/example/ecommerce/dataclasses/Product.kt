package com.example.ecommerce.dataclasses

data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price: Float?,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>
){
    constructor(): this("a", "", " ", 0f, images = emptyList()) // to get products from firebase we need an empty constructor
}
