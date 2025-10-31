package com.example.agribid.data.repository

import android.util.Log
import com.example.agribid.data.mapper.toDomainModel // Correct import from mapper package
import com.example.agribid.data.remote.AuthRemoteDataSource
import com.example.agribid.data.remote.dto.UserDto
import com.example.agribid.domain.model.User
import com.example.agribid.domain.model.UserRole
import com.example.agribid.domain.repository.UserRepository
import com.example.agribid.utils.Resource
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.snapshots // Correct import for snapshots Flow
import com.google.firebase.firestore.ktx.toObject
import com.google.rpc.context.AttributeContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
// In UserRepositoryImpl.kt
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map // Correct import for Flow map operator
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.String

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: AuthRemoteDataSource,
    private val firebaseAuth: FirebaseAuth // <-- ADD THIS
) : UserRepository { // Implement the interface

    // Implement the interface property using the AuthRemoteDataSource function
    override val currentAuthUser: Flow<FirebaseUser?> = remoteDataSource.getAuthStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    // Implement the interface method
    override suspend fun getOrSignInUser(): FirebaseUser? {
        val currentAuth = currentAuthUser.first() // Check current state first
        if (currentAuth != null) {
            Log.d("UserRepoImpl", "User already authenticated: ${currentAuth.uid}")
            return currentAuth // Return existing user
        }

        // If no user, try anonymous sign-in
        Log.d("UserRepoImpl", "Attempting anonymous sign-in.")
        val anonUserResult = remoteDataSource.signInAnonymously()
        val anonUser = anonUserResult.getOrNull()

        if (anonUser != null) {
            Log.d("UserRepoImpl", "Anonymous sign-in success: ${anonUser.uid}")
            ensureUserDocumentExists(anonUser) // Ensure Firestore doc exists
        } else {
            Log.e("UserRepoImpl", "Anonymous sign-in failed.", anonUserResult.exceptionOrNull())
        }
        return anonUser // Return the newly signed-in user or null
    }

    // Implement the interface property
    @OptIn(ExperimentalCoroutinesApi::class)
    override val currentUserData: Flow<User?> = currentAuthUser.flatMapLatest { firebaseUser ->
        if (firebaseUser != null) {
            // Get user data flow if authenticated
            getUserDataFlow(firebaseUser.uid)
        } else {
            // Emit null if not authenticated
            flow { emit(null) }
        }
    }

    // Private helper to get user data from Firestore as a Flow
    private fun getUserDataFlow(uid: String): Flow<User?> {
        Log.d("UserRepoImpl", "Setting up Firestore listener for user data: $uid")
        return remoteDataSource.getUserDocument(uid)
            .snapshots() // Use KTX snapshots() extension for Flow
            .map { snapshot ->
                try {
                    val userDto = snapshot?.toObject<UserDto>() // Convert snapshot to DTO
                    Log.d("UserRepoImpl", "User data snapshot received for $uid. DTO: ${userDto?.name}")
                    userDto?.toDomainModel() // Map DTO to Domain User model using mapper
                } catch (e: Exception) {
                    Log.e("UserRepoImpl", "Error converting snapshot to UserDto for $uid", e)
                    null // Emit null if conversion fails
                }
            }
    }

    suspend fun signOut() { // Implement interface method
        try {
            remoteDataSource.signOut() // Call signOut on the data source
            Log.d("UserRepoImpl", "Sign out successful.")
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error during sign out", e)
        }
    }

    // Helper to create a default Firestore document for new users
    private suspend fun ensureUserDocumentExists(user: FirebaseUser) {
        try {
            val docRef = remoteDataSource.getUserDocument(user.uid)
            val docSnapshot = docRef.get().await()
            if (!docSnapshot.exists()) {
                Log.d("UserRepoImpl", "User document for ${user.uid} does not exist. Creating...")
                val newUserDto = UserDto(
                    userId = user.uid,
                    name = user.displayName ?: "AgriBid User ${user.uid.take(4)}",
                    role = "BUYER", // Default role
                    country = "Unknown", province = "Unknown", district = "Unknown",
                    latitude = 0.0, longitude = 0.0, rating = 0.0, isExporter = false,
                    businessName = "", certifications = emptyList()
                )
                docRef.set(newUserDto).await()
                Log.d("UserRepoImpl", "Firestore document created for ${user.uid}")
            } else {
                Log.d("UserRepoImpl", "User document already exists for ${user.uid}")
            }
        } catch (e: Exception) {
            Log.e("UserRepoImpl", "Error ensuring user document exists for ${user.uid}", e)
        }
    }

    override fun getCurrentUser(): Flow<User> { // <-- 1. Replace [SomeUserType] with your REAL class name (e.g., User)

        // 2. Return a new, hardcoded "demo" user
        // Make sure to fill in ALL required fields your data class has
        return flowOf(
            User(
                userId = "123",
                role = UserRole.BUYER,
                name= "Tatenda Chipwanya",
                country = "Zimbabwe",
                province = "Manicaland",
                district = "Makoni",
                latitude = 0.0,
                longitude = 0.0,
                rating = 0.0,
                isExporter = false,
                businessName = "Stocker Holdings",
                certifications= emptyList()
                // ... add any other fields that your User class requires
            )
        )

    }

    override suspend fun signInAnonymously(): Resource<AuthResult> {// Make sure your return type matches
        return try {
            // This is the Firebase function to sign in anonymously
            val authResult = firebaseAuth.signInAnonymously().await()
            Resource.Success(authResult)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred during anonymous sign-in")
        }
    }

    override suspend fun getUserById(userId: String): User? {
        TODO("Not yet implemented")
    }
}

