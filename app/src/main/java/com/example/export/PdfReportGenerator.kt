package com.example.export

import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.data.model.CreditEntry
import com.example.data.model.DenominationItem
import com.example.data.model.ExpenseEntry
import com.example.data.model.FuelType
import com.example.data.model.NozzleReading
import com.example.data.model.ShiftConfig
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfReportGenerator {

    fun generateAndSharePdf(
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
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 in points
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint().apply {
            isAntiAlias = true
        }

        var y = 35f

        // Top Accent Header
        paint.color = AndroidColor.rgb(15, 23, 42) // Dark Navy
        canvas.drawRect(20f, 20f, 575f, 95f, paint)

        // Company Name & Symbol
        paint.color = AndroidColor.WHITE
        paint.textSize = 18f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("${config.company.brandName} (${config.company.shortCode})", 35f, 50f, paint)

        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        paint.color = AndroidColor.rgb(203, 213, 225)
        canvas.drawText("${config.pumpName} | RO Code: ${config.roCode} | ${config.location}", 35f, 68f, paint)
        canvas.drawText("Shift: ${config.shiftName} | Supervisor: ${config.supervisorName} | Date: ${SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.getDefault()).format(Date())}", 35f, 82f, paint)

        y = 115f

        // Fuel Rates Banner
        paint.color = AndroidColor.rgb(241, 245, 249)
        canvas.drawRoundRect(20f, y, 575f, y + 26f, 6f, 6f, paint)

        paint.color = AndroidColor.rgb(30, 41, 59)
        paint.textSize = 9f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        var rateX = 30f
        rates.forEach { (fuel, rate) ->
            canvas.drawText("${fuel.code}: ₹${String.format(Locale.US, "%.2f", rate)}/${fuel.unit}", rateX, y + 17f, paint)
            rateX += 135f
        }

        y += 42f

        // Table Header
        paint.color = AndroidColor.rgb(30, 41, 59)
        paint.textSize = 11f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("NOZZLE DISPENSER READINGS & SALES", 20f, y, paint)

        y += 8f
        paint.color = AndroidColor.rgb(226, 232, 240)
        canvas.drawRect(20f, y, 575f, y + 20f, paint)

        paint.color = AndroidColor.rgb(15, 23, 42)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("Nozzle", 26f, y + 13f, paint)
        canvas.drawText("Fuel", 110f, y + 13f, paint)
        canvas.drawText("Opening", 160f, y + 13f, paint)
        canvas.drawText("Closing", 240f, y + 13f, paint)
        canvas.drawText("Test(L)", 320f, y + 13f, paint)
        canvas.drawText("Net Litres", 380f, y + 13f, paint)
        canvas.drawText("Rate(₹)", 460f, y + 13f, paint)
        canvas.drawText("Total (₹)", 515f, y + 13f, paint)

        y += 20f

        var totalNetLitres = 0.0
        var totalFuelSale = 0.0

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        nozzles.forEachIndexed { index, nozzle ->
            val rate = rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
            val net = nozzle.netLitres
            val sale = nozzle.calculateTotalAmount(rate)
            totalNetLitres += net
            totalFuelSale += sale

            paint.color = if (index % 2 == 0) AndroidColor.WHITE else AndroidColor.rgb(248, 250, 252)
            canvas.drawRect(20f, y, 575f, y + 18f, paint)

            paint.color = AndroidColor.rgb(15, 23, 42)
            canvas.drawText(nozzle.nozzleName, 26f, y + 12f, paint)
            canvas.drawText(nozzle.fuelType.code, 110f, y + 12f, paint)
            canvas.drawText(String.format(Locale.US, "%.2f", nozzle.openingReading), 160f, y + 12f, paint)
            canvas.drawText(String.format(Locale.US, "%.2f", nozzle.closingReading), 240f, y + 12f, paint)
            canvas.drawText(String.format(Locale.US, "%.2f", nozzle.testingLitres), 320f, y + 12f, paint)
            canvas.drawText(String.format(Locale.US, "%.2f L", net), 380f, y + 12f, paint)
            canvas.drawText(String.format(Locale.US, "%.2f", rate), 460f, y + 12f, paint)
            canvas.drawText(String.format(Locale.US, "%,.2f", sale), 515f, y + 12f, paint)

            y += 18f
        }

        // Total Row
        paint.color = AndroidColor.rgb(226, 232, 240)
        canvas.drawRect(20f, y, 575f, y + 20f, paint)
        paint.color = AndroidColor.rgb(15, 23, 42)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("TOTAL DISPENSED:", 26f, y + 14f, paint)
        canvas.drawText("${String.format(Locale.US, "%.2f", totalNetLitres)} Litres", 380f, y + 14f, paint)
        canvas.drawText("₹${String.format(Locale.US, "%,.2f", totalFuelSale)}", 505f, y + 14f, paint)

        y += 35f

        // Left Box: Cash Denominations | Right Box: Payment Summary
        paint.color = AndroidColor.rgb(30, 41, 59)
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("CASH DENOMINATION TALLY", 20f, y, paint)
        canvas.drawText("COLLECTION RECONCILIATION", 310f, y, paint)

        y += 8f
        val boxTop = y

        // Denominations box
        paint.color = AndroidColor.rgb(248, 250, 252)
        canvas.drawRoundRect(20f, boxTop, 290f, boxTop + 140f, 6f, 6f, paint)

        var denomY = boxTop + 16f
        var totalCash = 0L
        paint.color = AndroidColor.rgb(51, 65, 85)
        paint.textSize = 8.5f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        denominations.filter { it.count > 0 }.take(6).forEach { d ->
            totalCash += d.totalAmount
            val label = if (d.value == 1) "Coins" else "₹${d.value}"
            canvas.drawText("$label  ×  ${d.count}", 32f, denomY, paint)
            canvas.drawText("=  ₹${d.totalAmount}", 210f, denomY, paint)
            denomY += 16f
        }

        // If totalCash calculation from all denominations:
        val calculatedCash = denominations.sumOf { it.totalAmount }
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = AndroidColor.rgb(15, 23, 42)
        canvas.drawText("Total Cash: ₹${calculatedCash}", 32f, boxTop + 128f, paint)

        // Payment Summary Box
        paint.color = AndroidColor.rgb(248, 250, 252)
        canvas.drawRoundRect(310f, boxTop, 575f, boxTop + 140f, 6f, 6f, paint)

        val totalCredit = creditEntries.filter { !it.isCollected }.sumOf { it.amount }
        val totalExp = expenses.sumOf { it.amount }
        val totalAccounted = calculatedCash + totalUpi + totalCard + totalCredit
        val difference = totalAccounted - totalFuelSale

        var payY = boxTop + 16f
        paint.color = AndroidColor.rgb(51, 65, 85)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        canvas.drawText("Target Fuel Sales:", 322f, payY, paint)
        canvas.drawText("₹${String.format(Locale.US, "%,.2f", totalFuelSale)}", 495f, payY, paint)
        payY += 16f

        canvas.drawText("Cash In Hand:", 322f, payY, paint)
        canvas.drawText("₹${String.format(Locale.US, "%,.2f", calculatedCash.toDouble())}", 495f, payY, paint)
        payY += 16f

        canvas.drawText("UPI / QR Payments:", 322f, payY, paint)
        canvas.drawText("₹${String.format(Locale.US, "%,.2f", totalUpi)}", 495f, payY, paint)
        payY += 16f

        canvas.drawText("Card / POS Swipes:", 322f, payY, paint)
        canvas.drawText("₹${String.format(Locale.US, "%,.2f", totalCard)}", 495f, payY, paint)
        payY += 16f

        canvas.drawText("Udhar (Credit Slips):", 322f, payY, paint)
        canvas.drawText("₹${String.format(Locale.US, "%,.2f", totalCredit)}", 495f, payY, paint)
        payY += 16f

        canvas.drawText("Shift Expenses:", 322f, payY, paint)
        canvas.drawText("-₹${String.format(Locale.US, "%,.2f", totalExp)}", 495f, payY, paint)
        payY += 20f

        // Difference highlight
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 9.5f
        if (Math.abs(difference) < 1.0) {
            paint.color = AndroidColor.rgb(16, 185, 129) // Emerald
            canvas.drawText("STATUS: BALANCED (₹0.00)", 322f, payY, paint)
        } else if (difference < 0) {
            paint.color = AndroidColor.rgb(239, 68, 68) // Red
            canvas.drawText("SHORTAGE: -₹${String.format(Locale.US, "%,.2f", Math.abs(difference))}", 322f, payY, paint)
        } else {
            paint.color = AndroidColor.rgb(14, 165, 233) // Blue
            canvas.drawText("EXCESS: +₹${String.format(Locale.US, "%,.2f", difference)}", 322f, payY, paint)
        }

        y = boxTop + 160f

        // Udhar Customer Quick Table
        if (creditEntries.isNotEmpty()) {
            paint.color = AndroidColor.rgb(30, 41, 59)
            paint.textSize = 10f
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText("UDHAR (CREDIT) SLIPS IN SHIFT", 20f, y, paint)
            y += 12f

            paint.color = AndroidColor.rgb(241, 245, 249)
            canvas.drawRect(20f, y, 575f, y + 16f, paint)

            paint.textSize = 8f
            paint.color = AndroidColor.rgb(15, 23, 42)
            canvas.drawText("Customer Name", 26f, y + 11f, paint)
            canvas.drawText("Vehicle Number", 180f, y + 11f, paint)
            canvas.drawText("Fuel", 330f, y + 11f, paint)
            canvas.drawText("Litres", 420f, y + 11f, paint)
            canvas.drawText("Amount (₹)", 500f, y + 11f, paint)
            y += 16f

            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            creditEntries.take(4).forEach { cr ->
                canvas.drawText(cr.customerName, 26f, y + 11f, paint)
                canvas.drawText(cr.vehicleNumber.ifEmpty { "—" }, 180f, y + 11f, paint)
                canvas.drawText(cr.fuelType.code, 330f, y + 11f, paint)
                canvas.drawText("${cr.litres} L", 420f, y + 11f, paint)
                canvas.drawText("₹${cr.amount}", 500f, y + 11f, paint)
                y += 14f
            }
        }

        // Signature Blocks at Bottom
        val signY = 790f
        paint.color = AndroidColor.rgb(148, 163, 184)
        paint.strokeWidth = 1f
        canvas.drawLine(35f, signY, 175f, signY, paint)
        canvas.drawLine(225f, signY, 365f, signY, paint)
        canvas.drawLine(415f, signY, 555f, signY, paint)

        paint.color = AndroidColor.rgb(100, 116, 139)
        paint.textSize = 8f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText("Salesman (DSM) Sign", 50f, signY + 14f, paint)
        canvas.drawText("Supervisor Sign", 255f, signY + 14f, paint)
        canvas.drawText("Manager / Owner Sign", 435f, signY + 14f, paint)

        document.finishPage(page)

        // Save to cache and share
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "PetrolPump_ShiftReport_${config.company.shortCode}_$timeStamp.pdf"
        val exportDir = File(context.cacheDir, "reports")
        if (!exportDir.exists()) exportDir.mkdirs()
        val file = File(exportDir, fileName)

        FileOutputStream(file).use { out ->
            document.writeTo(out)
        }
        document.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, file.name)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(intent, "Share PDF Hisab Sheet"))
    }
}
