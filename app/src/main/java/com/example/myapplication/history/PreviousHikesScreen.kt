package com.example.myapplication.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.AppDatabase
import java.text.SimpleDateFormat
import java.util.*

/**
 * Displays a list of all previously recorded hikes.
 *
 * Fetches hikes from the local [AppDatabase] and displays them in a scrollable list.
 * Each item is clickable to view hike details.
 *
 * @param onBackClick Callback invoked when the back button is pressed.
 * @param onHikeClick Callback invoked when a hike is clicked, passing the hike's ID.
 */
@Composable
fun PreviousHikesScreen(
    onBackClick: () -> Unit,
    onHikeClick: (Long) -> Unit
) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getDatabase(context) }

    val hikesFlow = remember { db.hikeDao().getAllHikes() }
    val hikes by hikesFlow.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text("Previous Hikes", style = MaterialTheme.typography.headlineSmall)
        }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(hikes) { hike ->
                HikeItem(hike, onClick = { onHikeClick(hike.id) })
            }
        }
    }
}

/**
 * Represents a single hike item in the previous hikes list.
 *
 * Shows the date of the hike and the highest altitude reached.
 * The card is clickable and triggers [onClick] when tapped.
 *
 * @param hike The [Hike] object containing date and max altitude information.
 * @param onClick Callback triggered when the card is clicked.
 */
@Composable
fun HikeItem(hike: com.example.myapplication.data.Hike, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = dateFormat.format(Date(hike.date)),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Highest Point: ${hike.maxAltitude.toInt()} m",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}