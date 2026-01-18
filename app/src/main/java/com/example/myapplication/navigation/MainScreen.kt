package com.example.myapplication.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.altitude.RiskLevel
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.medical.MedicalGuideScreen
import com.example.myapplication.session.SessionStateViewModel

@Composable
fun MainScreen(
    onEmergencyProfileClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    onMedicalGuideClick: () -> Unit,
    onEmergencyCallClick: () -> Unit,
    altitudeViewModel: AltitudeViewModel,
    authViewModel: AuthViewModel,
    loggerViewModel: SessionLoggerViewModel,
    sessionStateViewModel: SessionStateViewModel,
    navController: NavHostController
) {
    val altitudeState by altitudeViewModel.state.collectAsState()
    val riskLevel = sessionStateViewModel.riskLevel
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    val riskColor = when(riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.tertiaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.secondaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
    }

    val riskTextColor = when(riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.onSecondaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
    }

    LaunchedEffect(true) { altitudeViewModel.startTracking(sessionStateViewModel) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Altitude Safety Companion") },
                actions = {
                    // Logout button
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate(RootScreen.Auth.route) {
                            popUpTo(RootScreen.Main.route) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Altitude box
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(riskColor, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Current Altitude: %.1f m".format(altitudeState.currentAltitude),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    "Ascent Rate: %.1f m/min".format(altitudeState.ascentRate),
                    style = MaterialTheme.typography.bodyLarge,
                    color = riskTextColor
                )
            }

            Spacer(Modifier.height(16.dp))

            // Logger buttons
            if (authState is com.example.myapplication.auth.AuthState.Authenticated) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { loggerViewModel.startSession() },
                        enabled = !loggerViewModel.isLogging,
                        modifier = Modifier.weight(1f)
                    ) { Text("Start Recording") }

                    Button(
                        onClick = { loggerViewModel.stopSession() },
                        enabled = loggerViewModel.isLogging,
                        modifier = Modifier.weight(1f)
                    ) { Text("Stop Session") }
                }

            Spacer(Modifier.height(16.dp))
        }

        // Quick-access buttons row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEmergencyProfileClick,
                modifier = Modifier.weight(1f)
            ) { Text("Emergency") }

            Button(
                onClick = onSymptomsClick,
                modifier = Modifier.weight(1f)
            ) { Text("Symptoms") }

            Button(
                onClick = onMedicalGuideClick,
                modifier = Modifier.weight(1f)
            ) { Text("Medical Guide") }
        }
    }
}