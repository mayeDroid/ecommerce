package com.example.ecommerce.dataclasses

sealed class OrderStatus(val orderStatus: String){
    object Ordered: OrderStatus("Ordered")
    object Canceled: OrderStatus("Canceled")
    object Confirmed: OrderStatus("Confirmed")
    object Delivered: OrderStatus("Delivered")
    object Returned: OrderStatus("Returned")
}
