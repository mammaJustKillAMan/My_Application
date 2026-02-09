package com.example.myapplication.navigation

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.Hike
import com.example.myapplication.data.RoutePoint
import kotlinx.coroutines.launch

// 1. Define the possible states of the screen
/**
 * Represents the UI state for the Hike Detail screen.
 *
 * - [Loading]: Data is being fetched from the database.
 * - [Error]: The requested hike could not be found.
 * - [Success]: Data successfully loaded, containing the [Hike] and its list of [RoutePoint]s.
 */
sealed class HikeDetailState {
    object Loading : HikeDetailState()
    object Error : HikeDetailState()
    data class Success(val hike: Hike, val points: List<RoutePoint>) : HikeDetailState()
}

// 2. The ViewModel fetches data
/**
 * ViewModel responsible for fetching and exposing details of a specific hike.
 *
 * Provides [uiState] to observe loading, error, or success states for a given hike ID.
 *
 * @param application The [Application] context used to access the database.
 */
class HikeDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).hikeDao()

    var uiState by mutableStateOf<HikeDetailState>(HikeDetailState.Loading)
        private set

    /**
     * Loads a hike and its route points from the database.
     *
     * Updates [uiState] to [HikeDetailState.Success] if found, otherwise [HikeDetailState.Error].
     *
     * @param hikeId The ID of the hike to load.
     */
    fun loadHike(hikeId: Long) {
        viewModelScope.launch {
            val hike = dao.getHikeById(hikeId)
            if (hike != null) {
                val points = dao.getPointsForHike(hikeId)
                uiState = HikeDetailState.Success(hike, points)
            } else {
                uiState = HikeDetailState.Error
            }
        }
    }
}