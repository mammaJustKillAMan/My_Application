package com.example.myapplication.medical

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.altitude.RiskLevel

@Composable
fun MedicalGuideScreen(
    sessionStateViewModel: SessionStateViewModel,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var expandedIndex by remember { mutableStateOf(-1) }
    val riskLevel = sessionStateViewModel.riskLevel

    val riskColor = when (riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.tertiaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.secondaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.errorContainer
    }

    val riskTextColor = when (riskLevel) {
        RiskLevel.LOW -> MaterialTheme.colorScheme.onTertiaryContainer
        RiskLevel.MODERATE -> MaterialTheme.colorScheme.onSecondaryContainer
        RiskLevel.HIGH -> MaterialTheme.colorScheme.onErrorContainer
    }

    val sections = listOf(
        "What is AMS" to "Acute Mountain Sickness (AMS) is a condition caused by ascending too quickly to high altitudes. It can affect anyone and may lead to serious complications if ignored.",
        "Early signs you should monitor" to "Common early signs include headache, nausea, fatigue, dizziness, shortness of breath, and difficulty sleeping. Monitor yourself and fellow climbers closely.",
        "When you must descend" to "Immediate descent is necessary if symptoms worsen, especially if severe headache, persistent vomiting, shortness of breath at rest, or confusion appear.",
        "What to do if symptoms worsen" to "Rest, hydrate, avoid further ascent, and consider medication if available. Keep monitoring symptoms; descending to lower altitude is the safest solution.",
        "When to call emergency rescue" to "Call emergency services if severe symptoms appear, including loss of consciousness, confusion, extreme shortness of breath, or vomiting with dehydration."
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {

        // Top row: back button + title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.width(8.dp))

            Text(
                "Altitude Sickness Guide",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(Modifier.height(16.dp))

        // Risk highlight box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(riskColor, shape = RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Current Risk: ${riskLevel.name}",
                style = MaterialTheme.typography.titleMedium,
                color = riskTextColor
            )
        }

        Spacer(Modifier.height(16.dp))

        // Expandable sections
        sections.forEachIndexed { index, section ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expandedIndex = if (expandedIndex == index) -1 else index }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        section.first,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    Icon(
                        imageVector = if (expandedIndex == index) Icons.Default.KeyboardArrowUp
                        else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }

                if (expandedIndex == index) {
                    Text(
                        section.second,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}