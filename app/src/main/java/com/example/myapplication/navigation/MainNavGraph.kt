package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.profile.EmergencyProfileScreen
import com.example.myapplication.medical.MedicalGuideScreen
import com.example.myapplication.medical.SymptomsChecklistScreen
import com.example.myapplication.session.SessionStateViewModel

@Composable
fun MainNavGraph(
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
                onEmergencyProfileClick = { navController.navigate(MainRoute.EmergencyProfile.route) },
                altitudeViewModel = altitudeViewModel,
                authViewModel = authViewModel,
                loggerViewModel = loggerViewModel,
                onSymptomsClick = { navController.navigate(MainRoute.SymptomsChecklist.route) },
                onMedicalGuideClick = { navController.navigate(MainRoute.MedicalGuide.route) }
            )
        }

        composable(MainRoute.EmergencyProfile.route) {
            EmergencyProfileScreen(
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
            MedicalGuideScreen()
        }
    }
}