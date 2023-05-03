package com.example.ecommerce.utilities

import android.util.Patterns

// here are the contents that will validate the email and password

fun validateEmail(email: String): RegistrationValidation {
    if (email.isEmpty())
        return RegistrationValidation.Failed("Email cannot be empty")

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        return RegistrationValidation.Failed("Wrong email format")

    return RegistrationValidation.Success
}

fun validatePassword(password: String): RegistrationValidation {
    if (password.isEmpty())
        return RegistrationValidation.Failed("Password cannot be empty")

    if (password.length < 6)
        return RegistrationValidation.Failed("Password should have more than 6 characters")

    return RegistrationValidation.Success
}

fun validateFirstName(firstName: String): RegistrationValidation {

    if (firstName.isEmpty()) {
        return RegistrationValidation.Failed("First name cannot be empty")
    }

    for (numbers in 0..9)
        if (firstName.contains(numbers.toString()))
            return RegistrationValidation.Failed("First name cannot contain a number")
    return RegistrationValidation.Success
}

fun validateLastName(lastName: String): RegistrationValidation {
    if (lastName.isEmpty())
        return RegistrationValidation.Failed("Last name cannot be empty")

    for (numbers in 0..9)
        if (lastName.contains(numbers.toString()))
            return RegistrationValidation.Failed("Last name cannot contain a number")

    return RegistrationValidation.Success
}

/*if (firstName.contains("1", false))
    return RegistrationValidation.Failed("First name cannot contain a number")*/




