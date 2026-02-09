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

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    val sessionStateViewModel: SessionStateViewModel by viewModels()
    private val loggerViewModel: SessionLoggerViewModel by viewModels()

    private lateinit var altitudeViewModel: AltitudeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //initialize AltitudeViewModel
        altitudeViewModel = AltitudeViewModel(AltitudeRepository(this))

        setContent {
            MyApplicationTheme {

                //state to track if location permission is granted
                var hasLocationPermission by remember {
                    mutableStateOf(
                        ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    )
                }

                //launcher for runtime permission request
                val locationPermissionLauncher =
                    rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { granted ->
                        hasLocationPermission = granted
                    }

                //request permission if not granted
                LaunchedEffect(Unit) {
                    if (!hasLocationPermission) {
                        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                }

                //scaffold for basic layout
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    val navController = rememberNavController()

                    if (hasLocationPermission) {
                        RootNavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            altitudeViewModel = altitudeViewModel,
                            sessionStateViewModel = sessionStateViewModel,
                            loggerViewModel = loggerViewModel
                        )
                    } else {
                        //permission not granted screen
                        PermissionRequiredScreen {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    }
                }
            }
        }
    }
}