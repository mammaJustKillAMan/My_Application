package com.example.myapplication.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.auth.AuthRepository
import kotlinx.coroutines.launch

class EmergencyProfileViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    var name: String = ""
        private set

    var phone: String = ""
        private set

    var state: EmergencyProfileState = EmergencyProfileState.Idle
        private set

    fun loadEmergencyContact() {
        state = EmergencyProfileState.Loading

        viewModelScope.launch {
            val result = repository.getEmergencyContact()
            result.onSuccess {
                name = it.first
                phone = it.second
                state = EmergencyProfileState.Idle
            }.onFailure {
                state = EmergencyProfileState.Error(it.message ?: "Failed to load")
            }
        }
    }

    fun updateEmergencyContact(newName: String, newPhone: String) {
        if (newName.isBlank() || newPhone.isBlank()) {
            state = EmergencyProfileState.Error("Name and phone cannot be empty")
            return
        }

        state = EmergencyProfileState.Loading

        viewModelScope.launch {
            val result = repository.updateEmergencyContact(newName, newPhone)
            state = if (result.isSuccess) {
                name = newName
                phone = newPhone
                EmergencyProfileState.Success
            } else {
                EmergencyProfileState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to save"
                )
            }
        }
    }
}