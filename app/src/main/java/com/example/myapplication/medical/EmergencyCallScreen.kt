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

@Composable
fun EmergencyCallScreen(
    isLoggedIn: Boolean,
    emergencyContact: String?,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var confirmNumber by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Back button
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }

        Spacer(Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                // Emergency Services button (red)
                EmergencyButton(
                    text = "CALL EMERGENCY SERVICES",
                    color = MaterialTheme.colorScheme.error,
                    onClick = { confirmNumber = "112" }
                )

                // User's emergency contact (yellow) if logged in
                if (isLoggedIn && !emergencyContact.isNullOrBlank()) {
                    Spacer(Modifier.height(24.dp))
                    EmergencyButton(
                        text = "CALL PERSONAL CONTACT",
                        color = Color(0xFFFFC107),
                        onClick = { confirmNumber = emergencyContact }
                    )
                }
            }
        }
    }

    // Confirmation dialog
    confirmNumber?.let { number ->
        AlertDialog(
            onDismissRequest = { confirmNumber = null },
            title = { Text("Confirm Call") },
            text = { Text("Are you sure you want to call $number?") },
            confirmButton = {
                TextButton(
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