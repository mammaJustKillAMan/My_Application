package com.example.myapplication.medical

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * Screen that allows the user to make emergency calls.
 *
 * Displays buttons for dialing public emergency services (112) and,
 * if the user is logged in and has a saved contact, a private emergency contact.
 * Provides clear visual cues with large buttons and text for quick access.
 *
 * @param isLoggedIn Boolean indicating whether the user is logged in.
 * @param emergencyContact Optional phone number of the user's private emergency contact.
 * @param onBackClick Callback invoked when the back button in the top app bar is pressed.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyCallScreen(
    isLoggedIn: Boolean,
    emergencyContact: String?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Calls") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- EMERGENCY SERVICES BUTTON ---
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Large height for big text
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.large
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CALL", fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("EMERGENCY", fontSize = 32.sp, fontWeight = FontWeight.Black)
                    Text("SERVICES", fontSize = 32.sp, fontWeight = FontWeight.Black)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- PRIVATE CONTACT BUTTON ---
            if (isLoggedIn && emergencyContact != null) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$emergencyContact"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("CALL", fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Text("PRIVATE", fontSize = 32.sp, fontWeight = FontWeight.Black)
                        Text("CONTACT", fontSize = 32.sp, fontWeight = FontWeight.Black)
                    }
                }
            } else {
                Text(
                    text = if (!isLoggedIn) "Log in to call private contact" else "No emergency contact saved",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}