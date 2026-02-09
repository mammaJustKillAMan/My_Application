package com.example.myapplication.logger

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.altitude.AltitudeRepository
import com.example.myapplication.altitude.AltitudeViewModel
import com.example.myapplication.auth.AuthRepository
import com.example.myapplication.auth.AuthViewModel
import com.example.myapplication.session.SessionStateViewModel
import com.example.myapplication.tracking.TrackingViewModel

/**
 * Factory class responsible for creating all application-level ViewModels.
 *
 * Provides proper dependency injection for ViewModels such as [AltitudeViewModel],
 * [AuthViewModel], [SessionLoggerViewModel], [SessionStateViewModel], and [TrackingViewModel].
 *
 * @property application The [Application] instance, used for ViewModels that require context.
 */
class AppViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    // Repositories created once and shared
    private val authRepo = AuthRepository()
    private val altRepo = AltitudeRepository(application)

    /**
     * Creates an instance of the requested ViewModel class with necessary dependencies.
     *
     * Supports multiple ViewModel types and throws an exception for unknown classes.
     *
     * @param T The type of ViewModel to create.
     * @param modelClass The [Class] object corresponding to the ViewModel.
     * @return An instance of the requested ViewModel.
     * @throws IllegalArgumentException If the [modelClass] is not supported.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AltitudeViewModel::class.java) ->
                AltitudeViewModel(application) as T

            modelClass.isAssignableFrom(AuthViewModel::class.java) ->
                AuthViewModel(authRepo) as T

            modelClass.isAssignableFrom(SessionLoggerViewModel::class.java) ->
                SessionLoggerViewModel(application, altRepo) as T // Fixed injection

            modelClass.isAssignableFrom(SessionStateViewModel::class.java) ->
                SessionStateViewModel() as T

            modelClass.isAssignableFrom(TrackingViewModel::class.java) ->
                TrackingViewModel(application) as T

            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}