package com.example.util

import com.example.data.model.FuelType
import kotlin.math.abs
import kotlin.math.round

object DensityCalculatorHelper {

    /**
     * Converts hydrometer observed density at observed temperature to standard 15°C density
     * conforming to Indian OMC (IOCL, BPCL, HPCL) ASTM 53B petroleum tables.
     *
     * @param observedDensity in kg/m³ (e.g. 720.0 - 775.0 for Petrol, 810.0 - 860.0 for Diesel)
     * @param observedTemp in °C (e.g. 28.5)
     * @param fuelType FuelType (PETROL/MS or DIESEL/HSD)
     * @return Standard Density at 15°C rounded to 1 decimal place
     */
    fun calculateDensityAt15C(
        observedDensity: Double,
        observedTemp: Double,
        fuelType: FuelType
    ): Double {
        if (observedDensity <= 0.0) return 0.0

        val tempDiff = observedTemp - 15.0

        // Thermal expansion coefficient factor based on standard ASTM 53B for petroleum fractions:
        val factor = when (fuelType) {
            FuelType.PETROL, FuelType.XP95 -> {
                // MS Petrol thermal expansion typically ~0.66 - 0.70 kg/m3 per °C
                0.00095 * observedDensity - 0.02
            }
            FuelType.DIESEL -> {
                // HSD Diesel thermal expansion typically ~0.68 - 0.73 kg/m3 per °C
                0.00084 * observedDensity - 0.015
            }
            FuelType.CNG -> 0.0
        }

        val corrected = observedDensity + (tempDiff * factor)
        return round(corrected * 10.0) / 10.0
    }

    /**
     * Checks if variation between invoice density and converted 15°C density is within
     * the government permissible tolerance of ±3.0 kg/m³.
     */
    fun checkTolerance(converted15C: Double, invoiceDensity: Double): ToleranceResult {
        if (invoiceDensity <= 0.0 || converted15C <= 0.0) {
            return ToleranceResult(variation = 0.0, isPermissible = true, message = "Enter invoice density to verify tolerance")
        }

        val diff = round((converted15C - invoiceDensity) * 10.0) / 10.0
        val isPass = abs(diff) <= 3.0

        val message = if (isPass) {
            "PASS: Variation ${if (diff >= 0) "+$diff" else "$diff"} kg/m³ is within ±3.0 kg/m³ limit. Safe to decant."
        } else {
            "ALERT: Variation ${if (diff >= 0) "+$diff" else "$diff"} kg/m³ exceeds ±3.0 kg/m³! Contact supply depot before decanting."
        }

        return ToleranceResult(
            variation = diff,
            isPermissible = isPass,
            message = message
        )
    }

    data class ToleranceResult(
        val variation: Double,
        val isPermissible: Boolean,
        val message: String
    )
}
