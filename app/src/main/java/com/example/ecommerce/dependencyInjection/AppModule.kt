package com.example.ecommerce.dependencyInjection

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.example.ecommerce.firebase.FirebaseCommonOrAddToAndUpdateCart
import com.example.ecommerce.utilities.Constants.INTRODUCTION_SHARED_PREFERENCES
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// this is for dagger hilt ie for dependency injection

@Module
@InstallIn(SingletonComponent::class) //the dependencies in this component will only stay alive as the app is alive

object AppModule {
    @Provides
    @Singleton

    fun provideFirebaseAuthentication() = FirebaseAuth.getInstance()
    @Provides
    @Singleton
    fun provideFirebaseFireStoreDatabase() = Firebase.firestore

    @Provides
    fun provideIntroductionSharedPreferencesToHideIntroActivity(    // This will help hide Introduction fragment permanently after registration
        application: Application
    ) = application.getSharedPreferences(INTRODUCTION_SHARED_PREFERENCES, MODE_PRIVATE)
    @Provides
    @Singleton
    fun provideFirebaseCommon(firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore) = FirebaseCommonOrAddToAndUpdateCart(firestore, firebaseAuth)
}