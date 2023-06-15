package com.example.ecommerce.dataclasses

sealed class Category(val category: String){
    object  MobilePhones: Category("Samsung")
    object Iphone: Category("Iphone")
    object ConsolesAndGames: Category("Consoles and Games")
    object SmartWatches: Category("Smart watches")
    object Accessories: Category("Accessories")
}
