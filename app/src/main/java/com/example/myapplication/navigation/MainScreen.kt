package com.example.myapplication.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.altitude.RiskLevel
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.session.SessionStateViewModel

/**
 * The main screen of the application displaying altitude tracking, risk status, and user actions.
 *
 * Features:
 *  - Live GPS map with current location and hike path (for logged-in users).
 *  - Start/Stop hike session buttons integrated with [SessionLoggerViewModel] and [TrackingViewModel].
 *  - Risk dashboard showing current altitude, ascent rate, and risk level.
 *  - Menu buttons for accessing Symptoms, Medical Guide, Emergency Profile, and Emergency Calls.
 *  - History section to view previous hikes (only for authenticated users).
 *
 * @param onEmergencyProfileClick Callback invoked when the emergency profile button is pressed.
 * @param onSymptomsClick Callback invoked when the Symptoms button is pressed.
 * @param onMedicalGuideClick Callback invoked when the Guide button is pressed.
 * @param onEmergencyCallClick Callback invoked when the Emergency Actions button is pressed.
 * @param onPreviousHikesClick Callback invoked when the View Previous Hikes item is pressed.
 * @param altitudeViewModel The [AltitudeViewModel] providing current altitude and risk data.
 * @param authViewModel The [AuthViewModel] managing authentication state.
 * @param loggerViewModel The [SessionLoggerViewModel] for logging hiking sessions.
 * @param sessionStateViewModel The [SessionStateViewModel] managing symptom and session state.
 * @param navController The [NavHostController] used for navigation between screens.
 * @param trackingViewModel The [TrackingViewModel] handling GPS tracking and hike paths.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onEmergencyProfileClick: () -> Unit,
    onSymptomsClick: () -> Unit,
    onMedicalGuideClick: () -> Unit,
    onEmergencyCallClick: () -> Unit,
    onPreviousHikesClick: () -> Unit,
    altitudeViewModel: AltitudeViewModel,
    authViewModel: AuthViewModel,
    loggerViewModel: SessionLoggerViewModel,
    sessionStateViewModel: SessionStateViewModel,
    navController: NavHostController,
    trackingViewModel: TrackingViewModel
) {
    val context = LocalContext.current
    val altitudeState by altitudeViewModel.state.collectAsState()
    val authState by authViewModel.authState.collectAsState()

    // Explicitly Typed State to prevent inference errors
    val trackingState: TrackingState by trackingViewModel.state.collectAsState()

    val scrollState = rememberScrollState()
    val isGuest = authState !is AuthState.Authenticated

    // 1. Sync Symptoms to Risk Calculation
    LaunchedEffect(sessionStateViewModel.symptoms.map { it.checked }) {
        val checkedNames = sessionStateViewModel.symptoms
            .filter { it.checked }
            .map { it.name }
        altitudeViewModel.updateRisk(checkedNames)
    }

    val riskLevel = altitudeState.riskLevel

    // 2. Risk Theme Colors
    val riskColor = when (riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.secondaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.tertiaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
    }

    val riskTextColor = when(riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.onSecondaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
    }

    LaunchedEffect(Unit) { altitudeViewModel.startTracking(sessionStateViewModel) }

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
                            imageVector = Icons.Filled.ExitToApp,
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
                .padding(paddingValues)
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
                    ) { Text("Stop Recording") }
                }

                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = onEmergencyCallClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(
                    text = "CALL FOR HELP",
                    color = MaterialTheme.colorScheme.onError,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))

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
}