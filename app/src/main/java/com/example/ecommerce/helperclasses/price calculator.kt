package com.example.ecommerce.helperclasses

fun Float?.getProductPriceAfterPercentage(price: Float): Float{
    if (this == null) return price
    val percentageOff = (100 - this) * price /100
    return percentageOff
}