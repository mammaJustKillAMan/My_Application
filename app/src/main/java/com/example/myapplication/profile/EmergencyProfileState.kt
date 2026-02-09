package com.example.myapplication.profile

/**
 * Represents the UI state for the emergency profile screen.
 *
 * States:
 *  - [Idle]: No action is currently being performed.
 *  - [Loading]: Data is being loaded or saved.
 *  - [Success]: Data was successfully loaded or saved.
 *  - [Error]: An error occurred, containing a descriptive [message].
 */
sealed class EmergencyProfileState {
    object Idle : EmergencyProfileState()
    object Loading : EmergencyProfileState()
    object Success : EmergencyProfileState()
    data class Error(val message: String) : EmergencyProfileState()
}