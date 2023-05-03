package com.example.ecommerce.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.User
import com.example.ecommerce.utilities.*
import com.example.ecommerce.utilities.Constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val databaseFirestore: FirebaseFirestore)
    : ViewModel() {

    private val _registerStateFlows = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    // to observe the flows from loading, successful to failure,
    // check the Resource class in utilities folder

    val registerStateFlows: Flow<Resource<User>> = _registerStateFlows

    private val _validation = Channel<RegistrationFieldState>(){}
    val validation = _validation.receiveAsFlow()

    fun createAccountAndEmailWithPassword(user: User, password: String) {
        if (checkValidation(user, password)) {
            runBlocking {
                _registerStateFlows.emit(Resource.Loading())
            }

            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                //to create a new user account with firebase

                .addOnSuccessListener { // a response to know if the registration was successful
                    it.user?.let {
                        //_registerStateFlows.value = Resource.Success(it)

                        // we only want to save the user info to fire store only when it is successful
                        saveUserInfo(it.uid, user)
                    }
                }
                .addOnFailureListener {
                    _registerStateFlows.value = Resource.Error(it.message.toString())
                }
        }
        else{
            val registrationFieldState = RegistrationFieldState(
                validateEmail(user.email), validatePassword(password), validateFirstName(user.firstName),
                validateLastName(user.lastname)
            )
            runBlocking {
                _validation.send(registrationFieldState)
            }
        }
    }

    // a function to save user info if successful in the firebase/store
    private fun saveUserInfo(userUniqueID: String, user: User) {
        databaseFirestore.collection(USER_COLLECTION)    // because of this we create a class "Constants" in utilities where we save all the collection of users
            .document(userUniqueID)
            .set(user)
            .addOnSuccessListener {
                _registerStateFlows.value = Resource.Success(user)
            }

            .addOnFailureListener {
                _registerStateFlows.value = Resource.Error(it.message.toString())
            }
    }

    //here we create a function to che
    private fun checkValidation(user: User, password: String): Boolean {
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        val firstNameValidation = validateFirstName(user.firstName)
        val lastNameValidation = validateLastName(user.lastname)
        val shouldRegister = firstNameValidation is RegistrationValidation.Success &&
                lastNameValidation is RegistrationValidation.Success &&
                emailValidation is RegistrationValidation.Success &&
                passwordValidation is RegistrationValidation.Success

        return shouldRegister
    }
}

