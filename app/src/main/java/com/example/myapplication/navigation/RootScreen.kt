package com.example.myapplication.navigation

sealed class RootScreen(val route: String) {
    object Auth : RootScreen("auth")
    object Main : RootScreen("main")
}