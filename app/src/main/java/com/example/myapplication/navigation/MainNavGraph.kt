package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthState
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.medical.EmergencyCallScreen
import com.example.myapplication.profile.EmergencyProfileScreen
import com.example.myapplication.medical.MedicalGuideScreen
import com.example.myapplication.medical.SymptomsChecklistScreen
import com.example.myapplication.session.SessionStateViewModel

/**
 * Defines the main navigation graph for authenticated users.
 *
 * Includes all primary screens within the app after login, such as:
 * - Home / Dashboard ([MainScreen])
 * - Emergency Profile ([EmergencyProfileScreen])
 * - Symptoms Checklist ([SymptomsChecklistScreen])
 * - Medical Guide ([MedicalGuideScreen])
 * - Emergency Calls ([EmergencyCallScreen])
 * - Previous Hikes ([PreviousHikesScreen])
 * - Hike Detail ([HikeDetailScreen])
 *
 * This graph is a sub-graph under [RootScreen.Main.route].
 *
 * @param navController The [NavHostController] used for navigation between screens.
 * @param authViewModel The [AuthViewModel] providing authentication state.
 * @param sessionStateViewModel The [SessionStateViewModel] managing symptoms and session state.
 * @param altitudeViewModel The [AltitudeViewModel] providing altitude and risk data.
 * @param loggerViewModel The [SessionLoggerViewModel] used for logging hiking sessions.
 */
fun NavGraphBuilder.mainNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    sessionStateViewModel: SessionStateViewModel,
    altitudeViewModel: AltitudeViewModel,
    loggerViewModel: SessionLoggerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = MainRoute.Home.route,
        route = RootScreen.Main.route
    ) {
        composable(MainRoute.Home.route) {
            MainScreen(
                onEmergencyProfileClick = {
                    navController.navigate(MainRoute.EmergencyProfile.route)
                },
                onSymptomsClick = {
                    navController.navigate(MainRoute.SymptomsChecklist.route)
                },
                onMedicalGuideClick = {
                    navController.navigate(MainRoute.MedicalGuide.route)
                },
                onEmergencyCallClick = {
                    navController.navigate(MainRoute.EmergencyCall.route)
                },
                altitudeViewModel = altitudeViewModel,
                authViewModel = authViewModel,
                loggerViewModel = loggerViewModel,
                sessionStateViewModel = sessionStateViewModel,
                navController = navController
            )
        }

        composable(MainRoute.EmergencyProfile.route) {
            EmergencyProfileScreen(
                authViewModel = authViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(MainRoute.SymptomsChecklist.route) {
            SymptomsChecklistScreen(
                sessionStateViewModel = sessionStateViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(MainRoute.MedicalGuide.route) {
            MedicalGuideScreen(
                riskLevel = altitudeViewModel.state.collectAsState().value.riskLevel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(MainRoute.EmergencyCall.route) {
            val authState by authViewModel.authState.collectAsState()
            var emergencyPhone by remember { mutableStateOf<String?>(null) }

            // Load emergency contact from Firebase
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    authViewModel.loadEmergencyContact { _, phone ->
                        emergencyPhone = phone
                    }
                }
            }

            EmergencyCallScreen(
                isLoggedIn = authState is AuthState.Authenticated,
                emergencyContact = emergencyPhone,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}