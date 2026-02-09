package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.navigation.RootNavGraph
import com.example.myapplication.session.SessionStateViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.logger.AppViewModelFactory

/**
 * The main entry point of the application.
 *
 * Responsibilities:
 *  - Initializes all core [ViewModel]s using [AppViewModelFactory].
 *  - Handles runtime permission requests for location (altitude tracking) and notifications.
 *  - Sets up the Compose content and theme.
 *  - Launches the root navigation graph ([RootNavGraph]) if permissions are granted.
 *  - Displays [PermissionRequiredScreen] if location permission is missing.
 */
class MainActivity : ComponentActivity() {

    // 1. Initialize the Factory cleanly
    private val appViewModelFactory by lazy { AppViewModelFactory(application) }

    // 2. Initialize dependent ViewModels using the factory
    private val authViewModel: AuthViewModel by viewModels { appViewModelFactory }
    private val loggerViewModel: SessionLoggerViewModel by viewModels { appViewModelFactory }
    private val altitudeViewModel: AltitudeViewModel by viewModels { appViewModelFactory }
    private val sessionStateViewModel: SessionStateViewModel by viewModels { appViewModelFactory }

    // 3. Define the request
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted
        } else {
            // Explain to user that notifications won't work
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // State to track permission status
                    var hasLocationPermission by remember {
                        mutableStateOf(
                            ContextCompat.checkSelfPermission(
                                this, Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        )
                    }

                    // Permission Launcher
                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted -> hasLocationPermission = isGranted }

                    // Initial check on startup
                    LaunchedEffect(Unit) {
                        if (!hasLocationPermission) {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }

                    if (hasLocationPermission) {
                        RootNavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            altitudeViewModel = altitudeViewModel,
                            sessionStateViewModel = sessionStateViewModel,
                            loggerViewModel = loggerViewModel
                        )
                    } else {
                        PermissionRequiredScreen {
                            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            }
        }
    }
}