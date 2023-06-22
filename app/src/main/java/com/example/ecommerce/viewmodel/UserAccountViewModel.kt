package com.example.ecommerce.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.EcommerceApplication

import com.example.ecommerce.dataclasses.User
import com.example.ecommerce.utilities.RegistrationValidation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.utilities.validateEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private var storage: StorageReference,
    app: Application

): AndroidViewModel(app) {
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateOrEditInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateOrEditInfo = _updateOrEditInfo.asStateFlow()

    private val _resetPassword = MutableSharedFlow<Resource<String>>()
    val resetPassword = _resetPassword.asSharedFlow()

    init {
        getUser()
    }

    fun getUser(){
        viewModelScope.launch {
            _user.emit(Resource.Loading())
        }
        firestore.collection("users").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun updateOrEditUserInfo(user: User, imageUri: Uri?){
        val areInputsValid = validateEmail(user.email) is RegistrationValidation.Success
                && user.firstName.trim().isNotEmpty()
                && user.lastname.trim().isNotEmpty()

        if (!areInputsValid){
            viewModelScope.launch {
                _user.emit(Resource.Error("Check your inputs"))
            }
            return
        }

        viewModelScope.launch {
            _updateOrEditInfo.emit(Resource.Loading())
        }

        if (imageUri == null){      // if theres no profile image
            saveUserInformation(user, true)
        }
        else{
            saveUserInformationWithNewImage(user, imageUri)
        }
    }

    private fun saveUserInformationWithNewImage(user: User, imageUri: Uri) {
        viewModelScope.launch {
            // get the image
            try {
                val imageBitmap = MediaStore.Images.Media.getBitmap(
                    getApplication<EcommerceApplication>().contentResolver,imageUri
                )
                // compress the image
                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 96, byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()

                // save the compressed image
                val imageDirectory = storage.child("profile images/${auth.uid!!}/${UUID.randomUUID().toString()}")
                val result = imageDirectory.putBytes(imageByteArray).await()

                //get the image url
                val imageUrl = result.storage.downloadUrl.await().toString()

                //save the data
                saveUserInformation(user.copy(imagePath = imageUrl), false)

            } catch (e: Exception){
                viewModelScope.launch {
                    _user.emit(Resource.Error(e.message.toString()))
                }

            }
        }


    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _resetPassword.emit(Resource.Loading())
        }
        auth.sendPasswordResetEmail(email)
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

    private fun saveUserInformation(user: User, shouldRetrieveImage: Boolean) {
        // if shouldRetrieveImage is true ->
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("users").document(auth.uid!!)
            val currentUser = transaction.get(documentRef).toObject(User::class.java)
            if (shouldRetrieveImage){
                val newUser = user.copy(imagePath = currentUser?.imagePath?: "")
                transaction.set(documentRef, newUser)
            }
            else{
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateOrEditInfo.emit(Resource.Success(user))
            }

        }
            .addOnFailureListener {
                viewModelScope.launch {
                    _user.emit(Resource.Error(it.message.toString()))
                }
            }

    }
}