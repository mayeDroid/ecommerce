package com.example.ecommerce.utilities

sealed class RegistrationValidation {
    object Success: RegistrationValidation()       // when all inputs from the user during registration is correct
    data class Failed (val message: String): RegistrationValidation()   // we use data class because we are going to send a message
// when its not correct a message will be shown to the user
}

data class RegistrationFieldState(      // ie the editText fields we need to check
    val emailValidation: RegistrationValidation,
    val passwordValidation: RegistrationValidation,
    val firstNameValidation: RegistrationValidation,
    val lastNameValidation: RegistrationValidation
)