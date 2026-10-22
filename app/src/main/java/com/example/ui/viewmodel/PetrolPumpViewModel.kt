package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.data.model.CreditEntry
import com.example.data.model.DenominationItem
import com.example.data.model.DensityRecord
import com.example.data.model.ExpenseEntry
import com.example.data.model.FuelRate
import com.example.data.model.FuelType
import com.example.data.model.NozzleReading
import com.example.data.model.OilCompany
import com.example.data.model.ShiftConfig
import com.example.data.model.UserRole
import com.example.export.ExcelReportGenerator
import com.example.export.PdfReportGenerator
import com.example.export.WhatsAppShareHelper
import com.example.util.DensityCalculatorHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PetrolPumpUiState(
    val currentRole: UserRole = UserRole.MANAGER,
    val config: ShiftConfig = ShiftConfig(),
    val rates: Map<FuelType, Double> = mapOf(
        FuelType.PETROL to 96.72,
        FuelType.DIESEL to 89.62,
        FuelType.XP95 to 104.50,
        FuelType.CNG to 76.50
    ),
    val nozzles: List<NozzleReading> = listOf(
        NozzleReading(
            nozzleName = "Nozzle 1 (MS)",
            fuelType = FuelType.PETROL,
            openingReading = 14250.00,
            closingReading = 14670.50,
            testingLitres = 5.0
        ),
        NozzleReading(
            nozzleName = "Nozzle 2 (MS)",
            fuelType = FuelType.PETROL,
            openingReading = 8920.00,
            closingReading = 9260.00,
            testingLitres = 5.0
        ),
        NozzleReading(
            nozzleName = "Nozzle 3 (HSD)",
            fuelType = FuelType.DIESEL,
            openingReading = 35100.00,
            closingReading = 36020.50,
            testingLitres = 10.0
        ),
        NozzleReading(
            nozzleName = "Nozzle 4 (HSD)",
            fuelType = FuelType.DIESEL,
            openingReading = 21400.00,
            closingReading = 22180.00,
            testingLitres = 10.0
        ),
        NozzleReading(
            nozzleName = "Nozzle 5 (Power)",
            fuelType = FuelType.XP95,
            openingReading = 4200.00,
            closingReading = 4360.00,
            testingLitres = 5.0
        )
    ),
    val denominations: List<DenominationItem> = listOf(
        DenominationItem(2000, 5),
        DenominationItem(500, 180),
        DenominationItem(200, 95),
        DenominationItem(100, 120),
        DenominationItem(50, 40),
        DenominationItem(20, 25),
        DenominationItem(10, 30),
        DenominationItem(1, 150) // Coins
    ),
    val upiAmount: Double = 42500.0,
    val cardAmount: Double = 18200.0,
    val creditEntries: List<CreditEntry> = listOf(
        CreditEntry(
            customerName = "Sharma Transport Co.",
            vehicleNumber = "RJ-14-GA-8821",
            mobileNumber = "9829012345",
            fuelType = FuelType.DIESEL,
            litres = 180.0,
            amount = 16131.60,
            isCollected = false
        ),
        CreditEntry(
            customerName = "Rawat Logistics",
            vehicleNumber = "DL-1L-AA-4091",
            mobileNumber = "9911023456",
            fuelType = FuelType.DIESEL,
            litres = 120.0,
            amount = 10754.40,
            isCollected = false
        ),
        CreditEntry(
            customerName = "Dr. Vikas Mehra",
            vehicleNumber = "HR-26-CB-1902",
            mobileNumber = "9810054321",
            fuelType = FuelType.PETROL,
            litres = 40.0,
            amount = 3868.80,
            isCollected = false
        )
    ),
    val udharCollectedToday: Double = 8500.0,
    val expenses: List<ExpenseEntry> = listOf(
        ExpenseEntry(title = "Generator (DG) Diesel", category = "Power Backup", amount = 1200.0),
        ExpenseEntry(title = "Staff Tea & Snacks", category = "Staff Welfare", amount = 350.0),
        ExpenseEntry(title = "Compressor Air Hose Repair", category = "Maintenance", amount = 600.0)
    ),
    val densityRecords: List<DensityRecord> = emptyList()
) {
    val totalDispensedLitres: Double
        get() = nozzles.sumOf { it.netLitres }

    val totalFuelSaleAmount: Double
        get() = nozzles.sumOf { nozzle ->
            val rate = rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
            nozzle.calculateTotalAmount(rate)
        }

    val totalCashAmount: Long
        get() = denominations.sumOf { it.totalAmount }

    val totalCreditGiven: Double
        get() = creditEntries.filter { !it.isCollected }.sumOf { it.amount }

    val totalExpenses: Double
        get() = expenses.sumOf { it.amount }

    val totalMoneyAccounted: Double
        get() = totalCashAmount.toDouble() + upiAmount + cardAmount + totalCreditGiven

    // Galla Milan Difference:
    // If Cash + UPI + Card + Udhar equals Total Fuel Sale Target: diff = 0.
    val reconciliationDifference: Double
        get() = totalMoneyAccounted - totalFuelSaleAmount
}

class PetrolPumpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PetrolPumpUiState())
    val uiState: StateFlow<PetrolPumpUiState> = _uiState.asStateFlow()

    fun setRole(role: UserRole) {
        _uiState.update { it.copy(currentRole = role) }
    }

    fun setCompany(company: OilCompany) {
        _uiState.update { it.copy(config = it.config.copy(company = company)) }
    }

    fun updateShiftConfig(
        pumpName: String,
        roCode: String,
        dealerName: String,
        supervisorName: String,
        shiftName: String,
        location: String
    ) {
        _uiState.update {
            it.copy(
                config = it.config.copy(
                    pumpName = pumpName,
                    roCode = roCode,
                    dealerName = dealerName,
                    supervisorName = supervisorName,
                    shiftName = shiftName,
                    location = location
                )
            )
        }
    }

    fun updateFuelRate(fuelType: FuelType, newRate: Double) {
        if (newRate <= 0.0) return
        _uiState.update { state ->
            val updated = state.rates.toMutableMap()
            updated[fuelType] = newRate
            state.copy(rates = updated)
        }
    }

    fun updateNozzle(
        id: String,
        opening: Double,
        closing: Double,
        testing: Double,
        name: String,
        fuelType: FuelType
    ) {
        _uiState.update { state ->
            val updated = state.nozzles.map {
                if (it.id == id) {
                    it.copy(
                        openingReading = opening,
                        closingReading = closing,
                        testingLitres = testing,
                        nozzleName = name,
                        fuelType = fuelType
                    )
                } else it
            }
            state.copy(nozzles = updated)
        }
    }

    fun addNozzle(name: String, fuelType: FuelType, opening: Double) {
        val newNozzle = NozzleReading(
            nozzleName = name,
            fuelType = fuelType,
            openingReading = opening,
            closingReading = opening,
            testingLitres = 0.0
        )
        _uiState.update { it.copy(nozzles = it.nozzles + newNozzle) }
    }

    fun deleteNozzle(id: String) {
        _uiState.update { it.copy(nozzles = it.nozzles.filterNot { n -> n.id == id }) }
    }

    fun updateDenomination(value: Int, count: Int) {
        _uiState.update { state ->
            val updated = state.denominations.map {
                if (it.value == value) it.copy(count = count.coerceAtLeast(0)) else it
            }
            state.copy(denominations = updated)
        }
    }

    fun clearDenominations() {
        _uiState.update { state ->
            val updated = state.denominations.map { it.copy(count = 0) }
            state.copy(denominations = updated)
        }
    }

    fun updateDigitalPayments(upi: Double, card: Double) {
        _uiState.update { it.copy(upiAmount = upi, cardAmount = card) }
    }

    fun addCreditEntry(
        customerName: String,
        vehicleNo: String,
        mobile: String,
        fuelType: FuelType,
        litres: Double,
        amount: Double,
        notes: String
    ) {
        val entry = CreditEntry(
            customerName = customerName,
            vehicleNumber = vehicleNo,
            mobileNumber = mobile,
            fuelType = fuelType,
            litres = litres,
            amount = amount,
            notes = notes
        )
        _uiState.update { it.copy(creditEntries = it.creditEntries + entry) }
    }

    fun markCreditCollected(id: String) {
        _uiState.update { state ->
            var recoveredAmount = 0.0
            val updated = state.creditEntries.map {
                if (it.id == id) {
                    recoveredAmount = it.amount
                    it.copy(isCollected = true, collectedAmount = it.amount)
                } else it
            }
            state.copy(
                creditEntries = updated,
                udharCollectedToday = state.udharCollectedToday + recoveredAmount
            )
        }
    }

    fun deleteCreditEntry(id: String) {
        _uiState.update { it.copy(creditEntries = it.creditEntries.filterNot { c -> c.id == id }) }
    }

    fun recordManualUdharCollection(amount: Double) {
        _uiState.update { it.copy(udharCollectedToday = it.udharCollectedToday + amount) }
    }

    fun addExpense(title: String, category: String, amount: Double) {
        val expense = ExpenseEntry(title = title, category = category, amount = amount)
        _uiState.update { it.copy(expenses = it.expenses + expense) }
    }

    fun deleteExpense(id: String) {
        _uiState.update { it.copy(expenses = it.expenses.filterNot { e -> e.id == id }) }
    }

    fun addDensityRecord(
        fuelType: FuelType,
        observedDensity: Double,
        observedTemp: Double,
        invoiceDensity: Double
    ) {
        val converted = DensityCalculatorHelper.calculateDensityAt15C(observedDensity, observedTemp, fuelType)
        val tolerance = DensityCalculatorHelper.checkTolerance(converted, invoiceDensity)
        val record = DensityRecord(
            fuelType = fuelType,
            observedDensity = observedDensity,
            observedTemperature = observedTemp,
            convertedDensity15C = converted,
            invoiceDensity = invoiceDensity,
            variation = tolerance.variation,
            isApproved = tolerance.isPermissible
        )
        _uiState.update { it.copy(densityRecords = listOf(record) + it.densityRecords) }
    }

    fun shareWhatsApp(context: Context) {
        val state = _uiState.value
        val text = WhatsAppShareHelper.generateWhatsAppText(
            config = state.config,
            rates = state.rates,
            nozzles = state.nozzles,
            totalCash = state.totalCashAmount.toDouble(),
            totalUpi = state.upiAmount,
            totalCard = state.cardAmount,
            creditEntries = state.creditEntries,
            expenses = state.expenses,
            udharCollected = state.udharCollectedToday
        )
        WhatsAppShareHelper.shareToWhatsApp(context, text)
    }

    fun exportExcel(context: Context) {
        val state = _uiState.value
        ExcelReportGenerator.generateAndShareExcelCsv(
            context = context,
            config = state.config,
            rates = state.rates,
            nozzles = state.nozzles,
            denominations = state.denominations,
            totalUpi = state.upiAmount,
            totalCard = state.cardAmount,
            creditEntries = state.creditEntries,
            expenses = state.expenses,
            udharCollected = state.udharCollectedToday
        )
    }

    fun exportPdf(context: Context) {
        val state = _uiState.value
        PdfReportGenerator.generateAndSharePdf(
            context = context,
            config = state.config,
            rates = state.rates,
            nozzles = state.nozzles,
            denominations = state.denominations,
            totalUpi = state.upiAmount,
            totalCard = state.cardAmount,
            creditEntries = state.creditEntries,
            expenses = state.expenses,
            udharCollected = state.udharCollectedToday
        )
    }

    fun startNewShift(newShiftName: String) {
        _uiState.update { state ->
            // Shift rollover: Opening readings become the current closing readings!
            val rolledNozzles = state.nozzles.map {
                it.copy(
                    openingReading = it.closingReading,
                    closingReading = it.closingReading,
                    testingLitres = 0.0
                )
            }
            val resetDenoms = state.denominations.map { it.copy(count = 0) }
            state.copy(
                nozzles = rolledNozzles,
                denominations = resetDenoms,
                upiAmount = 0.0,
                cardAmount = 0.0,
                udharCollectedToday = 0.0,
                expenses = emptyList(),
                config = state.config.copy(shiftName = newShiftName)
            )
        }
    }
}
