package com.example.myapplication.medical

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.session.SessionStateViewModel

/**
 * Displays a checklist of symptoms for the user to track during a session.
 *
 * Each symptom is represented with a checkbox that updates the state
 * in [SessionStateViewModel]. Useful for monitoring altitude sickness
 * or other health indicators during a hike.
 *
 * @param sessionStateViewModel The [SessionStateViewModel] that holds symptom states and provides methods to toggle them.
 * @param onBackClick Callback invoked when the back button in the top bar is pressed.
 */
@Composable
fun SymptomsChecklistScreen(
    sessionStateViewModel: SessionStateViewModel,
    onBackClick: () -> Unit
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // Back button row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                "Symptoms Checklist",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(Modifier.height(16.dp))

        sessionStateViewModel.symptoms.forEachIndexed { index, symptom ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(
                    checked = symptom.checked,
                    onCheckedChange = { sessionStateViewModel.toggleSymptom(index) }
                )
                Spacer(Modifier.width(8.dp))
                Text(symptom.name)
            }
        }
    }
}