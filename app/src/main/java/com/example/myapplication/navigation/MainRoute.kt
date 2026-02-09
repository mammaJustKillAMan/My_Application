/**
 * Represents the main navigation routes within the authenticated portion of the app.
 *
 * Each object corresponds to a specific screen in the main app flow, including
 * dashboard, emergency profile, symptoms checklist, medical guide, emergency calls,
 * previous hikes, and hike detail screens.
 *
 * @property route The string route associated with each destination.
 */
sealed class MainRoute(val route: String) {
    object Home : MainRoute("home")                       // Dashboard
    object EmergencyProfile : MainRoute("emergency_profile")
    object SymptomsChecklist : MainRoute("symptoms_checklist")
    object MedicalGuide : MainRoute("medical_guide")
    object EmergencyCall : MainRoute("emergency_call")
    object PreviousHikes : MainRoute("previous_hikes")
    object HikeDetail : MainRoute("hike_detail/{hikeId}") {
        fun createRoute(hikeId: Long) = "hike_detail/$hikeId"}
}