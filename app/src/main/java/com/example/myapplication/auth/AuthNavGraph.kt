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
 * @param navController The NavHostController managing navigation.
 * @param viewModel Shared AuthViewModel instance for login/register logic.
 * @param onAuthSuccess Callback executed when the user successfully logs in
 * or registers.
 */
@Composable
fun AuthNavGraph(
    navController: NavHostController,
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = AuthScreen.Login.route
    ) {
        composable(AuthScreen.Login.route) {
            LoginScreen(
                viewModel = viewModel,
                onRegisterClick = {
                    navController.navigate(AuthScreen.Register.route)
                },
                onLoginSuccess = onAuthSuccess
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
