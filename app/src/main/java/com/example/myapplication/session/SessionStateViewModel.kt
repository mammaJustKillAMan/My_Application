package com.example.myapplication.session

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication.altitude.Symptom
import com.example.myapplication.altitude.RiskLevel

class SessionStateViewModel : ViewModel() {

    // Symptoms shared across the session
    var symptoms = mutableStateListOf(
        Symptom("Headache", 2.0),
        Symptom("Fatigue", 1.5),
        Symptom("Dizziness", 2.0),
        Symptom("Nausea", 3.0),
        Symptom("Vomiting", 10.0)
    )

    val riskLevel: RiskLevel
        get() {
            val totalScore = symptoms.filter { it.checked }.sumOf { it.weight }
            return when {
                totalScore >= 10.0 -> RiskLevel.HIGH
                totalScore >= 3.0 -> RiskLevel.MODERATE
                else -> RiskLevel.LOW
            }
        }


    // Toggle a symptom
    fun toggleSymptom(index: Int) {
        if (index in symptoms.indices) {
            symptoms[index] = symptoms[index].copy(checked = !symptoms[index].checked)
        }
    }

    fun resetSymptoms() {
        symptoms.forEachIndexed { index, symptom ->
            symptoms[index] = symptom.copy(checked = false)
        }
    }
}