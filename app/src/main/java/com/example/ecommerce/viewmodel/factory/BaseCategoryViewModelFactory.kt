package com.example.ecommerce.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ecommerce.dataclasses.Category
import com.example.ecommerce.viewmodel.ViewModelCategoryMobilePhonesLaptopsConsolesAccessories
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFactory(
    private val fireStore: FirebaseFirestore,
    private val category: Category
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ViewModelCategoryMobilePhonesLaptopsConsolesAccessories(fireStore, category) as T
    }
}