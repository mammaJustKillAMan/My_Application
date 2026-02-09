package com.example.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Displays a screen informing the user that location permission is required.
 *
 * Provides a button to trigger the permission request callback.
 *
 * @param onRequestClick Callback invoked when the user presses the "Grant Permission" button.
 */
@Composable
fun PermissionRequiredScreen(onRequestClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Location permission is required to track altitude.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(24.dp))

        Button(onClick = onRequestClick) {
            Text("Grant Permission")
        }
    }
}