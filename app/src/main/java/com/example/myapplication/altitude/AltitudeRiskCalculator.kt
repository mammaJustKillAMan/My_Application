package com.example.myapplication.altitude

data class Symptom(
    val name: String,
    val weight: Double, // higher = increases risk faster
    var checked: Boolean = false
)

object AltitudeRiskCalculator {

    /**
     * Ascent rate considered safe with negligible AMS risk.
     */
    private const val SAFE_ASCENT = 3.0      // m/min
    /**
     * Ascent rate where AMS risk begins to increase noticeably.
     */
    private const val WARNING_ASCENT = 8.0   // m/min
    /**
     * Ascent rate considered dangerous and strongly associated with AMS.
     */
    private const val DANGEROUS_ASCENT = 15.0 // m/min

    /**
     * Calculates the altitude sickness risk level.
     *
     * The risk score is computed from:
     * - Vertical ascent rate (meters per minute)
     * - Presence and severity of reported symptoms
     *
     * Descent provides a slow risk reduction but does not immediately
     * negate symptom-based risk. Certain severe symptoms override the
     * calculated score.
     *
     * @param ascentRate Current ascent rate in meters per minute.
     * Positive values indicate ascent, negative values indicate descent.
     * @param symptoms List of symptoms contributing to risk evaluation.
     *
     * @return The computed [RiskLevel].
     */
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
        if (symptoms.any { it.checked && it.name.equals("vomiting", true) }) {
            return RiskLevel.HIGH
        }

        return when {
            riskScore < 4 -> RiskLevel.LOW
            riskScore < 8 -> RiskLevel.MODERATE
            else -> RiskLevel.HIGH
        }
    }
}

/**
 * Discrete altitude sickness risk levels.
 *
 * Used to communicate the severity of Acute Mountain Sickness (AMS)
 * risk to the user.
 */
enum class RiskLevel {
    LOW, MODERATE, HIGH
}