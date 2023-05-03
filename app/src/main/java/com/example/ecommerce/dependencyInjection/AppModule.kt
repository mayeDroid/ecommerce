package com.example.ecommerce.dependencyInjection

import com.google.firebase.auth.FirebaseAuth
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
    fun provideFirebasFireStoreDatabase() = Firebase.firestore
}