package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.auth.authNavGraph
import com.example.myapplication.logger.SessionLoggerViewModel
import com.example.myapplication.session.SessionStateViewModel

/**
 * The root navigation graph of the application.
 *
 * Hosts all top-level navigation destinations and sub-graphs,
 * including the authentication flow and the main application flow.
 * This is the only [NavHost] in the app and determines which
 * screens the user sees based on authentication state.
 *
 * @param navController The [NavHostController] used to navigate between screens.
 * @param authViewModel The [AuthViewModel] used for authentication flows.
 * @param altitudeViewModel The [AltitudeViewModel] used for altitude-related functionality.
 * @param sessionStateViewModel The [SessionStateViewModel] that tracks session-related state.
 * @param loggerViewModel The [SessionLoggerViewModel] used for logging session data.
 */
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