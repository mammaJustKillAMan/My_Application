package com.example.myapplication.session

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.myapplication.altitude.Symptom

class SessionStateViewModel : ViewModel() {

    // Symptoms shared across the session
    var symptoms = mutableStateListOf(
        Symptom("Headache", 2.0),
        Symptom("Fatigue", 1.5),
        Symptom("Dizziness", 2.0),
        Symptom("Nausea", 3.0),
        Symptom("Vomiting", 10.0)
    )

    // Toggle a symptom
    /**
     * Toggles the checked state of a symptom at the given index.
     *
     * @param index The position of the symptom in [symptoms] to toggle.
     */
    fun toggleSymptom(index: Int) {
        if (index in symptoms.indices) {
            symptoms[index] = symptoms[index].copy(checked = !symptoms[index].checked)
        }
    }
}