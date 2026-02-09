package com.example.myapplication.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.myapplication.auth.AuthState
import com.example.myapplication.auth.AuthViewModel

/**
 * A screen for viewing and editing the user's emergency contact information.
 *
 * This screen allows the user to:
 *  - View their current emergency contact name and phone number.
 *  - Edit and save updated contact information.
 *  - See feedback if an error occurs during the update.
 *
 * @param authViewModel The [AuthViewModel] used to load and save emergency contact data.
 * @param onBackClick Lambda invoked when the back button is pressed to navigate back.
 */
@Composable
fun EmergencyProfileScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var state by remember { mutableStateOf<EmergencyProfileState>(EmergencyProfileState.Idle) }

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
            .padding(16.dp)
    ) {
        // --- Top Header (Back Arrow + Title) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                "Emergency Contact",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        // --- Centered Content ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f), // Takes up remaining space
            verticalArrangement = Arrangement.Center, // Centers content vertically
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Edit Information",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )

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
            onClick = {
                state = EmergencyProfileState.Loading
                authViewModel.updateEmergencyContact(name, phone)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state != EmergencyProfileState.Loading
        ) {
            Text("Save")
        }

        Spacer(Modifier.height(16.dp))

        // Observe authState for success/error feedback
        val authState by authViewModel.authState.collectAsState()
        when (authState) {
            is AuthState.Loading -> CircularProgressIndicator()
            is AuthState.Error -> Text(
                text = (authState as AuthState.Error).message,
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