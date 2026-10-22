package com.example.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.data.model.CreditEntry
import com.example.data.model.DenominationItem
import com.example.data.model.ExpenseEntry
import com.example.data.model.FuelType
import com.example.data.model.NozzleReading
import com.example.data.model.ShiftConfig
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ExcelReportGenerator {

    fun generateAndShareExcelCsv(
        context: Context,
        config: ShiftConfig,
        rates: Map<FuelType, Double>,
        nozzles: List<NozzleReading>,
        denominations: List<DenominationItem>,
        totalUpi: Double,
        totalCard: Double,
        creditEntries: List<CreditEntry>,
        expenses: List<ExpenseEntry>,
        udharCollected: Double
    ) {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "PetrolPump_Hisab_${config.company.shortCode}_$timeStamp.csv"
        val exportDir = File(context.cacheDir, "reports")
        if (!exportDir.exists()) {
            exportDir.mkdirs()
        }
        val file = File(exportDir, fileName)

        FileWriter(file).use { writer ->
            // Company & Pump Header
            writer.append("PETROL PUMP DAILY SHIFT HISAB SHEET\n")
            writer.append("Company,\"${config.company.brandName} (${config.company.shortCode})\"\n")
            writer.append("Pump Station,\"${config.pumpName}\"\n")
            writer.append("RO Code,\"${config.roCode}\"\n")
            writer.append("Dealer Name,\"${config.dealerName}\"\n")
            writer.append("Supervisor,\"${config.supervisorName}\"\n")
            writer.append("Shift,\"${config.shiftName}\"\n")
            writer.append("Generated Date,\"${SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())}\"\n\n")

            // Fuel Rates
            writer.append("CURRENT FUEL RATES\n")
            writer.append("Fuel Type,Code,Rate (INR/Unit)\n")
            rates.forEach { (fuel, rate) ->
                writer.append("\"${fuel.displayName}\",${fuel.code},${String.format(Locale.US, "%.2f", rate)}\n")
            }
            writer.append("\n")

            // Nozzle Readings Table
            writer.append("NOZZLE DISPENSER READINGS & SALES\n")
            writer.append("Nozzle ID,Fuel,Opening Reading,Closing Reading,Gross Litres,Testing Litres,Net Sold Litres,Rate (INR),Total Sale (INR)\n")
            var totalVolume = 0.0
            var totalFuelSaleAmount = 0.0

            nozzles.forEach { nozzle ->
                val rate = rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
                val net = nozzle.netLitres
                val sale = nozzle.calculateTotalAmount(rate)
                totalVolume += net
                totalFuelSaleAmount += sale

                writer.append("\"${nozzle.nozzleName}\",${nozzle.fuelType.code},${nozzle.openingReading},${nozzle.closingReading},${nozzle.grossLitres},${nozzle.testingLitres},${String.format(Locale.US, "%.2f", net)},${String.format(Locale.US, "%.2f", rate)},${String.format(Locale.US, "%.2f", sale)}\n")
            }
            writer.append("TOTALS,,,,,,\"${String.format(Locale.US, "%.2f", totalVolume)} L\",,\"INR ${String.format(Locale.US, "%.2f", totalFuelSaleAmount)}\"\n\n")

            // Cash Denomination Table
            val totalCash = denominations.sumOf { it.totalAmount }
            writer.append("CASH DENOMINATION BREAKDOWN\n")
            writer.append("Denomination,Count,Total Amount (INR)\n")
            denominations.forEach { denom ->
                if (denom.count > 0) {
                    val label = if (denom.value == 1) "Coins" else "INR ${denom.value}"
                    writer.append("$label,${denom.count},${denom.totalAmount}\n")
                }
            }
            writer.append("TOTAL CASH COLLECTED,,INR $totalCash\n\n")

            // Digital & Udhar Summary
            val totalCreditGiven = creditEntries.filter { !it.isCollected }.sumOf { it.amount }
            val totalExpenses = expenses.sumOf { it.amount }
            val totalAccounted = totalCash + totalUpi + totalCard + totalCreditGiven
            val shortageOrExcess = totalAccounted - totalFuelSaleAmount

            writer.append("COLLECTION SUMMARY & RECONCILIATION\n")
            writer.append("Particulars,Amount (INR)\n")
            writer.append("Total Fuel Sales Target,${String.format(Locale.US, "%.2f", totalFuelSaleAmount)}\n")
            writer.append("Cash Collections,$totalCash\n")
            writer.append("UPI / QR Payments,${String.format(Locale.US, "%.2f", totalUpi)}\n")
            writer.append("Card / POS Swipes,${String.format(Locale.US, "%.2f", totalCard)}\n")
            writer.append("Credit Sales (Udhar Given),${String.format(Locale.US, "%.2f", totalCreditGiven)}\n")
            writer.append("Old Udhar Recovered (Cash In),${String.format(Locale.US, "%.2f", udharCollected)}\n")
            writer.append("Shift Expenses (Cash Out),${String.format(Locale.US, "%.2f", totalExpenses)}\n")
            writer.append("Total Accounted Money,${String.format(Locale.US, "%.2f", totalAccounted)}\n")
            writer.append("Difference (Balance),${String.format(Locale.US, "%.2f", shortageOrExcess)}\n")
            writer.append("Status,\"${if (Math.abs(shortageOrExcess) < 1.0) "BALANCED (OK)" else if (shortageOrExcess < 0) "SHORTAGE" else "EXCESS"}\"\n\n")

            // Udhar Customer Details
            if (creditEntries.isNotEmpty()) {
                writer.append("UDHAR (CREDIT) KHATA LEDGER\n")
                writer.append("Customer Name,Vehicle No,Mobile No,Fuel Type,Litres,Amount (INR),Status\n")
                creditEntries.forEach { credit ->
                    val status = if (credit.isCollected) "COLLECTED" else "PENDING"
                    writer.append("\"${credit.customerName}\",\"${credit.vehicleNumber}\",\"${credit.mobileNumber}\",${credit.fuelType.code},${credit.litres},${credit.amount},$status\n")
                }
                writer.append("\n")
            }

            // Expenses Detail
            if (expenses.isNotEmpty()) {
                writer.append("SHIFT EXPENSES DETAILS\n")
                writer.append("Expense Title,Category,Amount (INR)\n")
                expenses.forEach { exp ->
                    writer.append("\"${exp.title}\",\"${exp.category}\",${exp.amount}\n")
                }
            }
        }

        shareFile(context, file, "application/vnd.ms-excel", "Share Excel Hisab Sheet")
    }

    private fun shareFile(context: Context, file: File, mimeType: String, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, title))
    }
}
