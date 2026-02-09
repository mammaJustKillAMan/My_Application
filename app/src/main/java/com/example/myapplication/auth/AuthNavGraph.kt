package com.example.myapplication.auth

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Represents the available authentication-related screens with their
 * corresponding navigation routes.
 *
 * @property route The identifier used by the navigation graph.
 */
sealed class AuthScreen(val route: String) {
    /** Login screen route. */
    object Login : AuthScreen("login")
    /** Registration screen route. */
    object Register : AuthScreen("register")
}

/**
 * Defines the navigation graph for authentication flow, including login
 * and registration screens. Handles transitions between the screens and
 * provides the ViewModel to each screen.
 *
 * Defines the login and registration screens and handles navigation
 * between them. This graph is intended to be nested inside a higher-level
 * NavHost and emits a callback when authentication succeeds.
 *
 * @receiver NavGraphBuilder Used to construct a nested navigation graph.
 * @param navController NavController used for in-graph navigation actions.
 * @param viewModel Shared [AuthViewModel] used by authentication screens.
 * @param onAuthSuccess Callback invoked when authentication completes
 * successfully (login, registration, or guest access).
 */
fun NavGraphBuilder.authNavGraph(
    navController: NavController,
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    navigation(
        startDestination = AuthScreen.Login.route,
        route = "auth_graph_route" // Unique ID for this graph
    ) {
        composable(AuthScreen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onRegisterClick = {
                    navController.navigate(AuthScreen.Register.route)
                },
                onLoginSuccess = onAuthSuccess,
                onGuestClick = onAuthSuccess
            )
        }

        composable(AuthScreen.Register.route) {
            RegisterScreen(
                viewModel = viewModel,
                onLoginClick = {
                    navController.popBackStack()
                },
                onRegisterSuccess = onAuthSuccess
            )
        }
    }
}
