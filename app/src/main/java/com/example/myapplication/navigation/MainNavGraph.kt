package com.example.myapplication.navigation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.myapplication.altitude.AltitudeRiskCalculator
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthState
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.history.PreviousHikesScreen
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.medical.EmergencyCallScreen
import com.example.myapplication.medical.MedicalGuideScreen
import com.example.myapplication.medical.SymptomsChecklistScreen
import com.example.myapplication.profile.EmergencyProfileScreen
import com.example.myapplication.session.SessionStateViewModel
import com.example.myapplication.tracking.TrackingViewModel

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
    navigation(
        startDestination = MainRoute.Home.route,
        route = RootScreen.Main.route
    ) {
        composable(MainRoute.Home.route) {
            val trackingViewModel: TrackingViewModel = viewModel()

            MainScreen(
                onEmergencyProfileClick = { navController.navigate(MainRoute.EmergencyProfile.route) },
                onSymptomsClick = { navController.navigate(MainRoute.SymptomsChecklist.route) },
                onMedicalGuideClick = { navController.navigate(MainRoute.MedicalGuide.route) },
                onEmergencyCallClick = { navController.navigate(MainRoute.EmergencyCall.route) },
                onPreviousHikesClick = { navController.navigate(MainRoute.PreviousHikes.route) },
                altitudeViewModel = altitudeViewModel,
                authViewModel = authViewModel,
                loggerViewModel = loggerViewModel,
                sessionStateViewModel = sessionStateViewModel,
                navController = navController,
                trackingViewModel = trackingViewModel
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
            val altitudeState by altitudeViewModel.state.collectAsState()
            val currentRiskLevel = remember(altitudeState.ascentRate, sessionStateViewModel.symptoms.toList()) {
                AltitudeRiskCalculator.calculateRisk(
                    ascentRate = altitudeState.ascentRate,
                    symptoms = sessionStateViewModel.symptoms
                )
            }
            MedicalGuideScreen(
                riskLevel = currentRiskLevel,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(MainRoute.EmergencyCall.route) {
            val authState by authViewModel.authState.collectAsState()
            var emergencyPhone by remember { mutableStateOf<String?>(null) }
            LaunchedEffect(authState) {
                if (authState is AuthState.Authenticated) {
                    authViewModel.loadEmergencyContact { _, phone -> emergencyPhone = phone }
                }
            }
            EmergencyCallScreen(
                isLoggedIn = authState is AuthState.Authenticated,
                emergencyContact = emergencyPhone,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(MainRoute.PreviousHikes.route) {
            PreviousHikesScreen(
                onBackClick = { navController.popBackStack() },
                onHikeClick = { hikeId ->
                    navController.navigate(MainRoute.HikeDetail.createRoute(hikeId))
                }
            )
        }

        composable(
            route = "hike_detail/{hikeId}",
            arguments = listOf(navArgument("hikeId") { type = NavType.LongType })
        ) { backStackEntry ->
            // 1. Get the ID from the URL
            val hikeId = backStackEntry.arguments?.getLong("hikeId") ?: 0L

            // 2. Pass ONLY the ID to the screen
            HikeDetailScreen(
                navController = navController,
                hikeId = hikeId
            )
        }
    }
}