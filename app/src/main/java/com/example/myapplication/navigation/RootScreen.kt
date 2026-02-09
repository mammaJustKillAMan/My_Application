package com.example.myapplication.navigation

/**
 * Represents the root-level navigation destinations in the app.
 *
 * Used to determine whether the user should see authentication screens
 * or the main application screens.
 *
 * @property route The string route associated with each root destination.
 */
sealed class RootScreen(val route: String) {
    object Auth : RootScreen("auth")
    object Main : RootScreen("main")
}