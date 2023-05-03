package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.User
import com.example.ecommerce.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor
    (private val  firebaseAuth: FirebaseAuth):
    ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()  //we want to send a one time event
    // so when ever login is successful then we will navigate to the another activity or snack-bar

    val login = _login.asSharedFlow()   //this will convert the mutable to immutable shared flow

    fun login(email: String, password: String){
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

}