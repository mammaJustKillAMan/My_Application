package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.auth.authNavGraph
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.session.SessionStateViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    altitudeViewModel: AltitudeViewModel,
    sessionStateViewModel: SessionStateViewModel,
    loggerViewModel: SessionLoggerViewModel
) {
    NavHost(
        navController = navController,
        startDestination = RootScreen.Auth.route
    ) {

        composable(RootScreen.Auth.route) {
            AuthNavGraph(
                navController = navController,
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(RootScreen.Main.route){
                        popUpTo(RootScreen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(RootScreen.Main.route) {
            MainNavGraph(
                navController = navController,
                authViewModel = authViewModel,
                altitudeViewModel = altitudeViewModel,
                sessionStateViewModel = sessionStateViewModel,
                loggerViewModel = loggerViewModel
            )
        }

    }
}