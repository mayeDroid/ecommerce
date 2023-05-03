package com.example.ecommerce.dataclasses

data class User(
    val firstName: String,
    val lastname: String,
    val email: String,
    var imagePath: String = ""  //to upload image or profile image
){
    constructor(): this("", "", "", "")
}

