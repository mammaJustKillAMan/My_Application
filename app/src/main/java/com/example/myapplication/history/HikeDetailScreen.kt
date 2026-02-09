package com.example.myapplication.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.Hike
import com.example.myapplication.data.RoutePoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.Marker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- 1. The Entry Point ---
/**
 * Displays the details of a single hike, including a map of the route
 * and summary statistics.
 *
 * This is the entry point composable for the Hike Detail screen.
 * It observes the [HikeDetailViewModel] for loading state and shows
 * a loading indicator, error message, or the hike content accordingly.
 *
 * @param navController Navigation controller used to handle back navigation.
 * @param hikeId ID of the hike to display.
 * @param viewModel The [HikeDetailViewModel] instance for loading hike data.
 */
@Composable
fun HikeDetailScreen(
    navController: NavController,
    hikeId: Long,
    viewModel: HikeDetailViewModel = viewModel()
) {
    // Trigger the load when the screen opens
    LaunchedEffect(hikeId) {
        viewModel.loadHike(hikeId)
    }

    // Switch based on data state
    when (val state = viewModel.uiState) {
        is HikeDetailState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HikeDetailState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: Hike not found")
            }
        }
        is HikeDetailState.Success -> {
            // Pass the loaded data to the UI
            HikeDetailContent(navController, state.hike, state.points)
        }
    }
}

// --- 2. The Actual UI (Map + Stats) ---
/**
 * Renders the main content of the Hike Detail screen.
 *
 * Shows a map with the hike route, start and end markers, and summary statistics.
 *
 * @param navController Navigation controller for back navigation.
 * @param hike The [Hike] object containing summary statistics.
 * @param routePoints List of [RoutePoint] objects representing the hike path.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HikeDetailContent(
    navController: NavController,
    hike: Hike,
    routePoints: List<RoutePoint>
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val mapView = remember { MapView(context)}

    DisposableEffect(Unit) {
        onDispose {
            mapView.onDetach() // Critical for OSMDroid
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hike Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .background(Color.Gray)
            ) {
                if (routePoints.isNotEmpty()) {
                    AndroidView(
                        factory = { context ->
                            // 1. Initialize Configuration
                            Configuration.getInstance().load(
                                context,
                                androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
                            )
                            // 2. Create and return the MapView
                            MapView(context).apply {
                                setTileSource(TileSourceFactory.MAPNIK)
                                setMultiTouchControls(true)
                            }
                        },
                        update = { mapView ->
                            // Use the instance provided by 'update' (mapView)
                            mapView.overlays.clear()

                            val geoPoints = routePoints.map { GeoPoint(it.latitude, it.longitude) }

                            // Path Line
                            val line = Polyline().apply {
                                setPoints(geoPoints)
                                color = android.graphics.Color.RED
                                width = 10f
                            }
                            mapView.overlays.add(line)

                            // Start/End Markers
                            val startMarker = Marker(mapView).apply {
                                position = geoPoints.first()
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "Start"
                            }
                            mapView.overlays.add(startMarker)

                            val endMarker = Marker(mapView).apply {
                                position = geoPoints.last()
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = "End"
                            }
                            mapView.overlays.add(endMarker)

                            // Zoom to fit
                            if (geoPoints.isNotEmpty()) {
                                mapView.post {
                                    val boundingBox = org.osmdroid.util.BoundingBox.fromGeoPoints(geoPoints)
                                    mapView.zoomToBoundingBox(boundingBox, true, 100)
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No GPS data recorded.")
                    }
                }
            }

            // --- STATS SECTION ---
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Summary", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))

                StatRow(Icons.Default.DateRange, "Date", SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(hike.date)))
                StatRow(Icons.Default.Info, "Duration", formatDuration(hike.durationSeconds))
                StatRow(Icons.Default.LocationOn, "Max Altitude", "%.1f m".format(hike.maxAltitude))
            }
        }
    }
}

// --- Helper Composable for Rows ---
/**
 * A single row displaying a statistic with an icon, label, and value.
 *
 * Used to display hike summary data such as date, duration, and max altitude.
 *
 * @param icon The [ImageVector] to show as the row icon.
 * @param label The label describing the statistic.
 * @param value The value of the statistic.
 */
@Composable
fun StatRow(icon: ImageVector, label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium)
                Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Formats a duration in seconds into a human-readable string.
 *
 * - If the duration is more than one hour, returns "Hh MMm".
 * - Otherwise, returns "MMm SSs".
 *
 * @param seconds Duration in seconds.
 * @return Formatted string representing the duration.
 */
fun formatDuration(seconds: Long): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return if (hours > 0) "%dh %02dm".format(hours, minutes) else "%02dm %02ds".format(minutes, seconds % 60)
}