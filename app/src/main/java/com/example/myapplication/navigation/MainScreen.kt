package com.example.myapplication.navigation

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.altitude.RiskLevel
import com.example.myapplication.auth.AuthState
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.session.SessionStateViewModel
import com.example.myapplication.tracking.TrackingState
import com.example.myapplication.tracking.TrackingViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

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
    val riskTextColor = when (riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.onSecondaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.onTertiaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
    }

    LaunchedEffect(Unit) { altitudeViewModel.startTracking() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Altitude Safety") },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        navController.navigate("auth_graph_route") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(if (isGuest) Icons.Default.Lock else Icons.Default.ExitToApp,
                            contentDescription = "Logout")
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
            // --- MAP SECTION ---
            Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                AndroidView(
                    factory = { ctx ->
                        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.MAPNIK)
                            setMultiTouchControls(true)
                            val provider = GpsMyLocationProvider(ctx)
                            val locationOverlay = object : MyLocationNewOverlay(provider, this) {
                                override fun onLocationChanged(location: Location?, source: org.osmdroid.views.overlay.mylocation.IMyLocationProvider?) {
                                    super.onLocationChanged(location, source)
                                    if (!isGuest && location != null) {
                                        trackingViewModel.addLocationPoint(location.latitude, location.longitude, location.altitude, riskLevel)
                                    }
                                }
                            }
                            locationOverlay.enableMyLocation()
                            locationOverlay.enableFollowLocation()
                            overlays.add(locationOverlay)
                            controller.setZoom(17.0)
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.removeAll { it is Polyline }
                        if (trackingState.currentPath.isNotEmpty()) {
                            val line = Polyline().apply {
                                setPoints(trackingState.currentPath)
                                color = android.graphics.Color.RED
                                width = 8f
                            }
                            mapView.overlays.add(0, line)
                        }
                        mapView.invalidate()
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // --- GUEST VS USER TRACKING CONTROLS ---
                Box(modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)) {
                    if (isGuest) {
                        Surface(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp)) {
                            Text("Login to Enable GPS Tracking", color = Color.White, modifier = Modifier.padding(12.dp))
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (trackingState.isTracking) {
                                Surface(color = Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(8.dp)) {
                                    Text("Duration: ${trackingState.durationSeconds}s", color = Color.White, modifier = Modifier.padding(8.dp))
                                }
                                Spacer(Modifier.height(8.dp))
                                Button(onClick = { trackingViewModel.toggleTracking(riskLevel); loggerViewModel.stopSession() },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                    Text("STOP HIKE")
                                }
                            } else {
                                Button(onClick = { trackingViewModel.toggleTracking(riskLevel); loggerViewModel.startSession(trackingViewModel, riskLevel) }) {
                                    Text("START HIKE")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- RISK DASHBOARD ---
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(riskColor, shape = MaterialTheme.shapes.medium).padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("STATUS: ${riskLevel.name}", style = MaterialTheme.typography.titleLarge, color = riskTextColor)
                Text("%.0f m".format(altitudeState.currentAltitude), style = MaterialTheme.typography.headlineLarge)
                Text("Ascent: %.1f m/min".format(altitudeState.ascentRate), style = MaterialTheme.typography.bodyMedium, color = riskTextColor)
            }

            Spacer(Modifier.height(24.dp))

            // --- MENU BUTTONS ---
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onSymptomsClick, modifier = Modifier.weight(1f)) { Text("Symptoms") }
                Button(onClick = onMedicalGuideClick, modifier = Modifier.weight(1f)) { Text("Guide") }
                // Profile button disabled for Guest
                Button(onClick = onEmergencyProfileClick, modifier = Modifier.weight(1f), enabled = !isGuest) { Text("Profile") }
            }

            Spacer(Modifier.height(12.dp))

            Button(onClick = onEmergencyCallClick, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("EMERGENCY ACTIONS", color = Color.White)
            }

            // --- HISTORY SECTION (Only for Users) ---
            if (!isGuest) {
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                ListItem(
                    headlineContent = { Text("View Previous Hikes") },
                    leadingContent = { Icon(Icons.Default.List, null) },
                    modifier = Modifier.clickable { onPreviousHikesClick() }
                )
            } else {
                Text("Log in to view hike history", modifier = Modifier.fillMaxWidth().padding(16.dp),
                    textAlign = TextAlign.Center, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}