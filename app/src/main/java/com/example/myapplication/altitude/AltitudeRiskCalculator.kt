package com.example.myapplication.altitude

data class Symptom(
    val name: String,
    val weight: Double, // higher = increases risk faster
    var checked: Boolean = false
)

object AltitudeRiskCalculator {

    fun calculateRisk(ascentRate: Double, symptoms: List<Symptom>): RiskLevel {
        var riskScore = ascentRate / 100.0 // baseline from ascent rate

        // Add weighted symptoms
        for (symptom in symptoms) {
            if (symptom.checked) {
                riskScore += symptom.weight
            }
        }

        // Vomiting overrides everything
        if (symptoms.any { it.checked && it.name.lowercase() == "vomiting" }) {
            return RiskLevel.HIGH
        }

        return when {
            riskScore < 6 -> RiskLevel.LOW
            riskScore < 12 -> RiskLevel.MODERATE
            else -> RiskLevel.HIGH
        }
    }
}

enum class RiskLevel {
    LOW, MODERATE, HIGH
}