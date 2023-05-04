package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.utilities.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor
    (private val firebaseAuth: FirebaseAuth) :
    ViewModel() {

    private val _login =
        MutableSharedFlow<Resource<FirebaseUser>>()  //we want to send a one time event

    // so when ever login is successful then we will navigate to the another activity or snack-bar
    val login = _login.asSharedFlow()   //this will convert the mutable to immutable shared flow

    private val _validation = Channel<LoginFieldState>() {}
    val validation = _validation.receiveAsFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    // we use this if we want flows
/*    fun loginWithEmailWithPassword(email: String, password: String) {
        if (checkValidation(email, password)) {
            runBlocking {
                _login.emit(Resource.Loading())
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                //to create a new user account with firebase

                .addOnSuccessListener { // a response to know if the registration was successful
                    viewModelScope.launch {
                        it.user?.let {
                            _login.emit(Resource.Success(it))
                        }

                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _login.emit(Resource.Error(it.message.toString()))
                    }
                }

        } else {
            val registrationFieldState = LoginFieldState(
                validateEmail(email), validatePassword(password)
            )
            runBlocking {
                _validation.send(registrationFieldState)
            }
        }
    }*/

    // we can use this if we don't want to use flows ie those messages that comes up when text is empty
    fun login(email: String, password: String){
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }

        firebaseAuth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            }

            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Success(email))
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _resetPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }


    private fun checkValidation(email: String, password: String): Boolean {
        val emailValidation = validateEmail(email)
        val passwordValidation = validatePassword(password)
        val shouldRegister = emailValidation is RegistrationValidation.Success &&
                passwordValidation is RegistrationValidation.Success

        return shouldRegister
    }
}