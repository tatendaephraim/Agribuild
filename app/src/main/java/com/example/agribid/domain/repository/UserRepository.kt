package com.example.agribid.domain.repository

import com.example.agribid.domain.model.User
import com.example.agribid.utils.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    val currentAuthUser: Flow<FirebaseUser?>
    val currentUserData: Flow<User?>
    suspend fun getOrSignInUser(): FirebaseUser?
    fun getCurrentUser(): Flow<User>
    suspend fun signInAnonymously(): Resource<AuthResult>
    suspend fun getUserById(userId: String): User?
}
