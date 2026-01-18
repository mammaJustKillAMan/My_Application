sealed class MainRoute(val route: String) {
    object Home : MainRoute("home")                       // Dashboard
    object EmergencyProfile : MainRoute("emergency_profile")
    object SymptomsChecklist : MainRoute("symptoms_checklist")
    object MedicalGuide : MainRoute("medical_guide")
    object EmergencyCall : MainRoute("emergency_call")
}