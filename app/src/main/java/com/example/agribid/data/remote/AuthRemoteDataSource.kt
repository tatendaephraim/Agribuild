package com.example.agribid.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    fun getAuthStateFlow(): Flow<FirebaseUser?> {
        return callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                trySend(firebaseAuth.currentUser)
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }
    }

    suspend fun signInAnonymously(): Result<FirebaseUser?> {
        return try {
            val result = auth.signInAnonymously().await()
            Log.d("AuthDataSource", "Anonymous sign-in success: ${result.user?.uid}")
            Result.success(result.user)
        } catch (e: Exception) {
            Log.e("AuthDataSource", "Anonymous sign-in failed", e)
            Result.failure(e)
        }
    }

    fun getUserDocument(uid: String): DocumentReference {
        return firestore.collection("users").document(uid)
    }

    fun signOut() {
        auth.signOut()
    }
}
