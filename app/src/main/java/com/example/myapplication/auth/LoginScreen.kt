package com.example.myapplication.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp


/**
 * Login screen allowing users to authenticate using email and password.
 *
 * Displays input fields for credentials, handles login actions,
 * provides navigation to registration, supports password reset,
 * and reacts to authentication state changes emitted by [AuthViewModel].
 *
 * @param onRegisterClick Callback triggered when the user chooses
 * to navigate to the registration screen.
 * @param onLoginSuccess Callback invoked when authentication succeeds.
 * @param viewModel ViewModel providing authentication logic and state.
 * @param onGuestClick Callback triggered when the user chooses
 * to continue without authentication.
 */
@Composable
fun LoginScreen(
    onRegisterClick: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel,
    onGuestClick: () -> Unit
){
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val state by viewModel.authState.collectAsState()

    LaunchedEffect(state) {
        if (state is AuthState.Authenticated) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log In")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (email.isNotBlank()) {
                    viewModel.resetPassword(email)
                }
            }
        ) {
            Text("Forgot password?")
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = onRegisterClick){
            Text("Create an account")
        }

        if (state is AuthState.Error){
            Text(
                text = (state as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (state is AuthState.Loading){
            CircularProgressIndicator()
        }
    }
}
