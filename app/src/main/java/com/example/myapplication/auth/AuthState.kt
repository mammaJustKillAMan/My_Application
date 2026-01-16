package com.example.myapplication.auth

/**
 * Represents the UI state for authentication-related actions such
 * as login and registration.
 */
sealed class AuthState {
    /** Default idle state when nothing is happening. */
    object Idle: AuthState()

    /** Represents a loading state during authentication operations. */
    object Loading: AuthState()

    /** Indicates successful authentication. */
    object Authenticated: AuthState()

    /** Represents an unauthenticated state. */
    object Unauthenticated: AuthState()

    /**
     * Represents a failure state containing an error message.
     *
     * @property message Error description meant for UI display.
     */
    data class Error(val message: String): AuthState()
}
