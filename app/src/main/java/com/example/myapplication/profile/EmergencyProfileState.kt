package com.example.myapplication.profile

sealed class EmergencyProfileState {
    object Idle : EmergencyProfileState()
    object Loading : EmergencyProfileState()
    object Success : EmergencyProfileState()
    data class Error(val message: String) : EmergencyProfileState()
}