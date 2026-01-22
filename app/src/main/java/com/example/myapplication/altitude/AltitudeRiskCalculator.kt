package com.example.myapplication.altitude

data class Symptom(
    val name: String,
    val weight: Double, // higher = increases risk faster
    var checked: Boolean = false
)

object AltitudeRiskCalculator {

    private const val SAFE_ASCENT = 3.0      // m/min
    private const val WARNING_ASCENT = 8.0   // m/min
    private const val DANGEROUS_ASCENT = 15.0 // m/min

    fun calculateRisk(ascentRate: Double, symptoms: List<Symptom>): RiskLevel {
        var riskScore = 0.0

        // ASCENT CONTRIBUTION (real AMS logic)
        when {
            ascentRate <= SAFE_ASCENT -> riskScore += 0.0
            ascentRate <= WARNING_ASCENT -> riskScore += 2.0
            ascentRate <= DANGEROUS_ASCENT -> riskScore += 5.0
            else -> riskScore += 8.0 // very rapid ascent
        }

        // DESCENT BENEFIT (slow relief, not instant)
        if (ascentRate < 0) {
            riskScore += ascentRate / 300.0 // gentle reduction
        }

        // SYMPTOMS
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