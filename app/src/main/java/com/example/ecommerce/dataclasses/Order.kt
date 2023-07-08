package com.example.ecommerce.dataclasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random.Default.nextLong

@Parcelize
data class Order(
    val orderStatus: String = "",
    val totalPrice : Float = 0f,
    val products: List<CartProducts> = emptyList(),
    val address: Address = Address(),
    val date: String = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(Date()),
    val orderID: Long = nextLong(0, 100_000_000_000_000_000) + totalPrice.toLong() // ensures no orders have the same id
): Parcelable
