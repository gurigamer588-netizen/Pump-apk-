package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple

enum class FuelType(
    val displayName: String,
    val code: String,
    val unit: String,
    val defaultRate: Double,
    val accentColor: Color
) {
    PETROL("Motor Spirit (Petrol)", "MS", "L", 96.72, NeonAmber),
    DIESEL("High Speed Diesel", "HSD", "L", 89.62, NeonCyan),
    XP95("Speed / XP95 / Power", "XP", "L", 104.50, NeonPurple),
    CNG("Compressed Natural Gas", "CNG", "Kg", 76.50, NeonGreen)
}

data class FuelRate(
    val fuelType: FuelType,
    val currentRate: Double,
    val lastUpdated: String
)
