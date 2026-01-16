package com.example.myapplication.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Handles user authentication and Firestore user document creation.
 * Provides methods for registration, login, and logout using Firebase.
 *
 * @property auth FirebaseAuth instance for managing authentication.
 * @property firestore FirebaseFirestore instance for saving user profiles.
 */

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Registers a new user with email, password, and name. After Firebase
     * creates the authentication user, this method also stores a complete
     * profile document in Firestore under `users/{uid}`.
     *
     * @param name User's full name.
     * @param email Email address for login.
     * @param password Password for authentication.
     * @return Result containing Unit on success or an exception on failure.
     */
    suspend fun registerUser(name: String, email: String, password: String): Result<Unit> {
        return try {
            val userResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = userResult.user!!.uid

            val userData = mapOf(
                "id" to userId,
                "name" to name,
                "email" to email,
                "phoneNumber" to "",
                "dateOfBirth" to "",
                "address" to emptyMap<String, Any>(),
                "emergencyPhoneNumber" to "",
                "emergencyName" to "",
                "profilePictureUrl" to ""
            )

            firestore.collection("users").document(userId).set(userData).await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs in an existing user using email and password credentials.
     *
     * @param email User email.
     * @param password User password.
     * @return Result containing Unit on success or an exception on failure.
     */
    suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Logs out the currently authenticated user.
     */
    fun logout() {
        auth.signOut()
    }

    /**
     * Updates the user's emergency contact information in Firestore.
     *
     * @param emergencyName Name of the emergency contact.
     * @param emergencyPhone Phone number of the emergency contact.
     * @return Result containing Unit on success or an exception on failure.
     */
    suspend fun updateEmergencyContact(
        emergencyName: String,
        emergencyPhone: String
    ): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "emergencyName" to emergencyName,
                        "emergencyPhoneNumber" to emergencyPhone
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retrieves the user's emergency contact information from Firestore.
     *
     * @return Result containing a pair of name and phone on success or an
     * exception on failure.
     */
    suspend fun getEmergencyContact(): Result<Pair<String, String>> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(Exception("Not logged in"))

            val doc = firestore.collection("users")
                .document(userId)
                .get()
                .await()

            val name = doc.getString("emergencyName") ?: ""
            val phone = doc.getString("emergencyPhoneNumber") ?: ""

            Result.success(name to phone)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Resets the user's password using Firebase's password reset feature.
     *
     * @param email User email.
     * @return Result containing Unit on success or an exception on failure.
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
