package com.example.myapplication.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for coordinating authentication logic between
 * the UI and the AuthRepository. Exposes a StateFlow to allow the UI
 * to react to authentication state changes.
 *
 * @property repository Repository handling Firebase authentication calls.
 */
class AuthViewModel (
    private val repository: AuthRepository = AuthRepository()
) : ViewModel(){

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    /**
     * Registers a new user and updates the UI state accordingly.
     *
     * @param name User's name.
     * @param email Login email.
     * @param password Login password.
     */
    fun register(name: String, email: String, password: String){
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.registerUser(name, email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    /**
     * Logs in an existing user using email and password.
     *
     * @param email User email.
     * @param password User password.
     */
    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.loginUser(email, password)
            _authState.value = if (result.isSuccess) {
                AuthState.Authenticated
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    /**
     * Logs out the currently authenticated user and resets
     * authentication state.
     */
    fun logout() {
        repository.logout()
        _authState.value = AuthState.Unauthenticated
    }

    /**
     * Updates the user's emergency contact information.
     *
     * @param name Name of the emergency contact.
     * @param phone Phone number of the emergency contact.
     * @return Result containing Unit on success or an exception on failure.
     */
    fun updateEmergencyContact(
        name: String,
        phone: String
    ) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.updateEmergencyContact(name, phone)
            _authState.value = if (result.isSuccess) {
                AuthState.Idle
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Update failed")
            }
        }
    }

    /**
     * Retrieves the user's emergency contact information.
     *
     * @param onLoaded Callback executed with the loaded data.
     * @return Result containing Unit on success or an exception on failure.
     */
    fun loadEmergencyContact(
        onLoaded: (String, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.getEmergencyContact()
            result.onSuccess { onLoaded(it.first, it.second) }
        }
    }

    /**
     * Resets the user's password using the provided email.
     *
     * @param email User email.
     * @return Result containing Unit on success or an exception on failure.
     */
    fun resetPassword(email: String) {
        _authState.value = AuthState.Loading

        viewModelScope.launch {
            val result = repository.resetPassword(email)
            _authState.value = if (result.isSuccess) {
                AuthState.Idle
            } else {
                AuthState.Error(result.exceptionOrNull()?.message ?: "Reset failed")
            }
        }
    }

}