package com.example.ecommerce.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommerce.dataclasses.Address
import com.example.ecommerce.utilities.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BillingViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ViewModel() {

    private val _address = MutableStateFlow<Resource<List<Address>>>(Resource.Unspecified())
    val address = _address.asStateFlow()

    init {
        getUserAddresses()
    }

    fun getUserAddresses(){
        viewModelScope.launch {
            _address.emit(Resource.Loading())
        }
        firestore.collection("users").document(auth.uid!!).collection("address")
            .addSnapshotListener { value, error ->
                /**
                 * We do the snapshot listener because when the user migrates from billing to
                 * address fragment he might want to change add or delete her address
                 */
                if(error != null){
                    viewModelScope.launch { _address.emit(Resource.Error(error.message.toString())) }
                    return@addSnapshotListener
                }
                val addressss = value?.toObjects(Address::class.java)
                viewModelScope.launch { _address.emit(Resource.Success(addressss!!)) }
            }
    }
}