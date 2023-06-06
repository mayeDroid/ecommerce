package com.example.ecommerce.dataclasses

sealed class Category(val category: String){
    object  MobilePhones: Category("Mobile Phones")
    object LaptopsAndComputers: Category("Laptops and Computers")
    object ConsolesAndGames: Category("Consoles and Games")
    object SmartWatches: Category("Smart watches")
    object Accessories: Category("Accessories")
}
