package com.example.ecommerce.utilities
//T means it can receive ant type of data type so the Resource class is a generic type of class

sealed class Resource<T> (
    val data: T? = null,
    val message: String?= null // the error message for the listener
        ){
    class Loading<T>: Resource<T>()
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(message: String): Resource<T>(message = message)
    class Unspecified<T>: Resource<T>() // to prevent the button from continuously loading or
                                        // acting like it failed or successful at launch
}
