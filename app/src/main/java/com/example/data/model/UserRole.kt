package com.example.data.model

enum class UserRole(
    val title: String,
    val hindiTitle: String,
    val description: String
) {
    MANAGER(
        title = "Manager / Owner",
        hindiTitle = "मैनेजर / मालिक",
        description = "Full pump control: rates update, shift approvals, report export, credit limits"
    ),
    SUPERVISOR(
        title = "Shift Supervisor",
        hindiTitle = "सुपरवाइजर",
        description = "Nozzle readings entry, cash reconciliation, density test, WhatsApp hisab"
    ),
    STAFF(
        title = "Staff / DSM",
        hindiTitle = "स्टाफ / सेल्समैन",
        description = "Quick nozzle reading entry, quick calculator, credit slips logger"
    )
}
