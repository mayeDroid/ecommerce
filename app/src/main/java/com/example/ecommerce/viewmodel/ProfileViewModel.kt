package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.User
import com.example.ecommerce.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel

class ProfileViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth

) : ViewModel(){

    private  val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

    fun getUser(){
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }

        firestore.collection("users").document(auth.uid!!)
            .addSnapshotListener { value, error ->  // we use a snapShot because we want to update the profile pics whenever the user changes it
                if (error != null){
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                        }
                    }
                    else {
                        val userObj = value?.toObject(User::class.java)
                        userObj?.let {
                            viewModelScope.launch {
                                _user.emit(Resource.Success(userObj))
                            }
                        }
                    }

                }

    }

    fun logOut() {
        auth.signOut()
    }
}