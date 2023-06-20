package com.example.ecommerce.dataclasses

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartProducts(
    val product: Product,
    val quantity: Int,
    val selectedColor: Int? = null,
    val selectedSize: String? = null,
): Parcelable {
    constructor(): this(Product(), 1, null, null)
}
