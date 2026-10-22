package com.example.export

import android.content.Context
import android.content.Intent
import com.example.data.model.CreditEntry
import com.example.data.model.ExpenseEntry
import com.example.data.model.FuelType
import com.example.data.model.NozzleReading
import com.example.data.model.ShiftConfig
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object WhatsAppShareHelper {

    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    fun generateWhatsAppText(
        config: ShiftConfig,
        rates: Map<FuelType, Double>,
        nozzles: List<NozzleReading>,
        totalCash: Double,
        totalUpi: Double,
        totalCard: Double,
        creditEntries: List<CreditEntry>,
        expenses: List<ExpenseEntry>,
        udharCollected: Double
    ): String {
        val totalLitres = nozzles.sumOf { it.netLitres }
        val totalFuelSale = nozzles.sumOf { nozzle ->
            val rate = rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
            nozzle.calculateTotalAmount(rate)
        }

        val totalCreditGiven = creditEntries.filter { !it.isCollected }.sumOf { it.amount }
        val totalExpenses = expenses.sumOf { it.amount }
        val totalRecovered = totalCash + totalUpi + totalCard + totalCreditGiven
        val netDifference = totalRecovered - totalFuelSale

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault())
        val dateStr = dateFormat.format(Date())

        val sb = StringBuilder()
        sb.append("⛽ *DAILY PETROL PUMP HISAB REPORT*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🏢 *Company:* ${config.company.brandName} (${config.company.shortCode})\n")
        sb.append("📍 *Station:* ${config.pumpName}\n")
        sb.append("🔢 *RO Code:* ${config.roCode}\n")
        sb.append("👤 *Supervisor:* ${config.supervisorName}\n")
        sb.append("⏱️ *Shift:* ${config.shiftName}\n")
        sb.append("📅 *Date:* $dateStr\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")

        sb.append("\n📈 *CURRENT FUEL RATES:*\n")
        rates.forEach { (fuel, rate) ->
            sb.append("• ${fuel.displayName}: ₹${String.format(Locale.US, "%.2f", rate)}/${fuel.unit}\n")
        }

        sb.append("\n📊 *NOZZLE SALES BREAKDOWN:*\n")
        nozzles.forEach { nozzle ->
            val rate = rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
            val amount = nozzle.calculateTotalAmount(rate)
            sb.append("• *${nozzle.nozzleName}* (${nozzle.fuelType.code})\n")
            sb.append("   Closing: ${String.format(Locale.US, "%.2f", nozzle.closingReading)} | Opening: ${String.format(Locale.US, "%.2f", nozzle.openingReading)}\n")
            if (nozzle.testingLitres > 0) {
                sb.append("   Testing: -${String.format(Locale.US, "%.2f", nozzle.testingLitres)} L\n")
            }
            sb.append("   Net Sold: *${String.format(Locale.US, "%.2f", nozzle.netLitres)} L* = ₹${String.format(Locale.US, "%,.2f", amount)}\n")
        }

        sb.append("────────────────────────\n")
        sb.append("⚡ *TOTAL FUEL SALE:* ₹${String.format(Locale.US, "%,.2f", totalFuelSale)}\n")
        sb.append("📦 *Total Volume:* ${String.format(Locale.US, "%,.2f", totalLitres)} Litres\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")

        sb.append("\n💰 *PAYMENTS & COLLECTIONS:*\n")
        sb.append("💵 Cash Tally: ₹${String.format(Locale.US, "%,.2f", totalCash)}\n")
        sb.append("📱 UPI / QR: ₹${String.format(Locale.US, "%,.2f", totalUpi)}\n")
        sb.append("💳 Card Swipes: ₹${String.format(Locale.US, "%,.2f", totalCard)}\n")
        sb.append("📝 Udhar (Credit Given): ₹${String.format(Locale.US, "%,.2f", totalCreditGiven)}\n")
        if (udharCollected > 0) {
            sb.append("📥 Old Udhar Recovered: ₹${String.format(Locale.US, "%,.2f", udharCollected)}\n")
        }
        if (totalExpenses > 0) {
            sb.append("🧾 Shift Expenses: ₹${String.format(Locale.US, "%,.2f", totalExpenses)}\n")
        }

        if (creditEntries.isNotEmpty()) {
            sb.append("\n📋 *UDHAR (CREDIT) SLIPS:*\n")
            creditEntries.take(5).forEach { credit ->
                sb.append("• ${credit.customerName} (${credit.vehicleNumber}): ₹${String.format(Locale.US, "%.2f", credit.amount)} [${credit.fuelType.code}]\n")
            }
            if (creditEntries.size > 5) {
                sb.append("  ...and ${creditEntries.size - 5} more customers\n")
            }
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("⚖️ *HISAB RECONCILIATION:*\n")
        sb.append("Target Fuel Sale: ₹${String.format(Locale.US, "%,.2f", totalFuelSale)}\n")
        sb.append("Total Accounted: ₹${String.format(Locale.US, "%,.2f", totalRecovered)}\n")

        val diffAbs = Math.abs(netDifference)
        if (diffAbs < 1.0) {
            sb.append("Status: 🟢 *EXACT MATCH (₹0.00)* - Perfect Tally! ✨\n")
        } else if (netDifference < 0) {
            sb.append("Status: 🔴 *SHORTAGE OF ₹${String.format(Locale.US, "%,.2f", diffAbs)}*\n")
        } else {
            sb.append("Status: 🔵 *EXCESS CASH OF +₹${String.format(Locale.US, "%,.2f", diffAbs)}*\n")
        }
        sb.append("━━━━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("Generated with *PetrolPump Pro* (Smart Fuel Manager)")

        return sb.toString()
    }

    fun shareToWhatsApp(context: Context, text: String) {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
            // Attempt to target WhatsApp if installed, otherwise show standard chooser
            setPackage("com.whatsapp")
        }

        try {
            context.startActivity(sendIntent)
        } catch (e: Exception) {
            // Fallback to normal chooser if WhatsApp isn't installed
            val chooserIntent = Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            }, "Share Petrol Pump Hisab via")
            context.startActivity(chooserIntent)
        }
    }
}
