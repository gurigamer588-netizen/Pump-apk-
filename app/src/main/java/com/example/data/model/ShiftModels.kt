package com.example.data.model

import java.util.UUID

data class NozzleReading(
    val id: String = UUID.randomUUID().toString(),
    val nozzleName: String,
    val fuelType: FuelType,
    val openingReading: Double = 0.0,
    val closingReading: Double = 0.0,
    val testingLitres: Double = 0.0 // Pump calibration measure (e.g. 5L/10L) returned to tank
) {
    val grossLitres: Double
        get() = if (closingReading >= openingReading) closingReading - openingReading else 0.0

    val netLitres: Double
        get() = (grossLitres - testingLitres).coerceAtLeast(0.0)

    fun calculateTotalAmount(rate: Double): Double {
        return netLitres * rate
    }
}

data class DenominationItem(
    val value: Int,
    val count: Int = 0
) {
    val totalAmount: Long
        get() = value.toLong() * count
}

data class CreditEntry(
    val id: String = UUID.randomUUID().toString(),
    val customerName: String,
    val vehicleNumber: String = "",
    val mobileNumber: String = "",
    val fuelType: FuelType,
    val litres: Double,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val isCollected: Boolean = false,
    val collectedAmount: Double = 0.0,
    val notes: String = ""
)

data class ExpenseEntry(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String = "General",
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis()
)

data class DensityRecord(
    val id: String = UUID.randomUUID().toString(),
    val fuelType: FuelType,
    val observedDensity: Double, // in kg/m3 (e.g. 732.5 for MS, 828.0 for HSD)
    val observedTemperature: Double, // in Celsius (e.g. 29.5 C)
    val convertedDensity15C: Double, // calculated at 15 C
    val invoiceDensity: Double, // from TT tanker invoice challan
    val variation: Double, // converted - invoice
    val isApproved: Boolean, // within +/- 3.0 kg/m3
    val timestamp: Long = System.currentTimeMillis()
)

data class ShiftConfig(
    val pumpName: String = "Shree Ganesh Fuel Center",
    val roCode: String = "RO-548912",
    val dealerName: String = "Rajesh Verma",
    val location: String = "Highway Bypass, Mile 14",
    val shiftName: String = "Morning Shift (06:00 - 14:00)",
    val supervisorName: String = "Anil Sharma",
    val company: OilCompany = OilCompany.IOCL
)
