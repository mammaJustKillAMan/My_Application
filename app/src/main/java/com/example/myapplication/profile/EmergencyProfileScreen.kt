package com.example.myapplication.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.auth.AuthViewModel
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.auth.AuthState

@Composable
fun EmergencyProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var state: EmergencyProfileState.Loading by remember { mutableStateOf(EmergencyProfileState.Idle) }

    // Load existing contact
    LaunchedEffect(Unit) {
        authViewModel.loadEmergencyContact { loadedName, loadedPhone ->
            name = loadedName
            phone = loadedPhone
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text("Emergency Contact", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Emergency Contact Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Emergency Phone Number") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.updateEmergencyContact(name, phone) },
            modifier = Modifier.fillMaxWidth(),
            enabled = state != EmergencyProfileState.Loading
        ) {
            Text("Save")
        }

        Spacer(Modifier.height(16.dp))

        when (state) {
            is EmergencyProfileState.Loading ->
                CircularProgressIndicator()

            is EmergencyProfileState.Success ->
                Text("Saved successfully", color = MaterialTheme.colorScheme.primary)

            is EmergencyProfileState.Error ->
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error
                )

            else -> {}
        }

        Spacer(Modifier.height(24.dp))

        TextButton(onClick = onBackClick) {
            Text("Back")
        }
    }
}