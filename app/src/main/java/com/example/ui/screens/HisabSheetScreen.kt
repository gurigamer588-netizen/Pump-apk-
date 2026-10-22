package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelType
import com.example.data.model.UserRole
import com.example.ui.components.CompanySelectorDialog
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassStatCard
import com.example.ui.components.RateChangeDialog
import com.example.ui.components.RoleSwitchDialog
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.PetrolPumpViewModel
import java.util.Locale
import kotlin.math.abs

@Composable
fun HisabSheetScreen(
    viewModel: PetrolPumpViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var showCompanyDialog by remember { mutableStateOf(false) }
    var showRateDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var showNewShiftDialog by remember { mutableStateOf(false) }
    var newShiftInput by remember { mutableStateOf("Evening Shift (14:00 - 22:00)") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Station & Company Banner
        item {
            GlassCard(
                borderColor = state.config.company.primaryColor.copy(alpha = 0.5f),
                glowColor = state.config.company.primaryColor,
                onClick = {
                    if (state.currentRole != UserRole.STAFF) {
                        showCompanyDialog = true
                    }
                }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(state.config.company.primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.config.company.shortCode.take(4),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.config.company.brandName,
                                color = state.config.company.primaryColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            GlassBadge(
                                text = state.config.roCode,
                                accentColor = NeonCyan
                            )
                        }
                        Text(
                            text = state.config.pumpName,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${state.config.shiftName} • Supv: ${state.config.supervisorName}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // 4 Summary Hero Stats
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassStatCard(
                        title = "Fuel Sales (Target)",
                        value = "₹${String.format(Locale.US, "%,.0f", state.totalFuelSaleAmount)}",
                        subtitle = "${String.format(Locale.US, "%.1f", state.totalDispensedLitres)} L Total",
                        icon = Icons.Default.LocalGasStation,
                        accentColor = NeonAmber,
                        modifier = Modifier.weight(1f)
                    )

                    GlassStatCard(
                        title = "Total Accounted",
                        value = "₹${String.format(Locale.US, "%,.0f", state.totalMoneyAccounted)}",
                        subtitle = "Cash+UPI+Card+Udhar",
                        icon = Icons.Default.AccountBalanceWallet,
                        accentColor = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Galla Milan / Reconciliation Card
                val diff = state.reconciliationDifference
                val isBalanced = abs(diff) < 1.0
                val isShortage = diff < -1.0
                val statusColor = when {
                    isBalanced -> NeonGreen
                    isShortage -> NeonCoral
                    else -> NeonCyan
                }
                val statusTitle = when {
                    isBalanced -> "Galla Milan: Perfect Match (₹0.00)"
                    isShortage -> "Galla Milan: Shortage of ₹${String.format(Locale.US, "%,.2f", abs(diff))}"
                    else -> "Galla Milan: Excess Cash +₹${String.format(Locale.US, "%,.2f", diff)}"
                }

                GlassCard(
                    borderColor = statusColor.copy(alpha = 0.6f),
                    glowColor = statusColor
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(statusColor.copy(alpha = 0.15f))
                                .border(1.dp, statusColor.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isBalanced -> Icons.Default.CheckCircle
                                    isShortage -> Icons.Default.TrendingDown
                                    else -> Icons.Default.TrendingUp
                                },
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = statusTitle,
                                color = statusColor,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fuel Dispensed: ₹${String.format(Locale.US, "%,.2f", state.totalFuelSaleAmount)} | Recovered: ₹${String.format(Locale.US, "%,.2f", state.totalMoneyAccounted)}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Action Toolbar (WhatsApp, Excel, PDF, Rates)
        item {
            GlassCard {
                Text(
                    text = "QUICK SHIFT ACTIONS & EXPORTS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // WhatsApp Share
                    Button(
                        onClick = { viewModel.shareWhatsApp(context) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Excel CSV
                    OutlinedButton(
                        onClick = { viewModel.exportExcel(context) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = NeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Excel", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // PDF Sheet
                    OutlinedButton(
                        onClick = { viewModel.exportPdf(context) },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCoral),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = NeonCoral,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("PDF", color = NeonCoral, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showRateDialog = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriceChange,
                            contentDescription = null,
                            tint = NeonAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Change Rates", color = NeonAmber, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (state.currentRole != UserRole.STAFF) {
                        OutlinedButton(
                            onClick = { showNewShiftDialog = true },
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.RestartAlt,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Shift", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Live Rates Row
        item {
            GlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "TODAY'S FUEL RATES (₹)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Edit ✎",
                        color = NeonAmber,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { showRateDialog = true }
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FuelType.values().forEach { fuel ->
                        val rate = state.rates[fuel] ?: fuel.defaultRate
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(fuel.accentColor.copy(alpha = 0.1f))
                                .padding(vertical = 6.dp)
                        ) {
                            Text(text = fuel.code, color = fuel.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(text = "₹${String.format(Locale.US, "%.2f", rate)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                            Text(text = "/${fuel.unit}", color = TextSecondary, fontSize = 9.sp)
                        }
                        if (fuel != FuelType.values().last()) {
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                    }
                }
            }
        }

        // Nozzles Summary Table
        item {
            GlassCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "NOZZLE DISPENSERS (${state.nozzles.size})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${String.format(Locale.US, "%.2f", state.totalDispensedLitres)} Litres",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                state.nozzles.forEachIndexed { index, nozzle ->
                    val rate = state.rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
                    val amount = nozzle.calculateTotalAmount(rate)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlassBadge(
                                    text = nozzle.fuelType.code,
                                    accentColor = nozzle.fuelType.accentColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = nozzle.nozzleName,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = "₹${String.format(Locale.US, "%,.2f", amount)}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp)
                        ) {
                            Text(
                                text = "Close: ${String.format(Locale.US, "%.1f", nozzle.closingReading)} | Open: ${String.format(Locale.US, "%.1f", nozzle.openingReading)}",
                                color = TextTertiary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.2f", nozzle.netLitres)} L sold",
                                color = NeonAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (index < state.nozzles.size - 1) {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0x1AFFFFFF))
                        )
                    }
                }
            }
        }

        // Payments Breakdown Details
        item {
            GlassCard {
                Text(
                    text = "COLLECTIONS & PAYMENTS BREAKDOWN",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                PaymentBreakdownRow(
                    label = "Cash Collected (Galla Tally)",
                    amount = "₹${String.format(Locale.US, "%,.2f", state.totalCashAmount.toDouble())}",
                    icon = Icons.Default.Payments,
                    color = NeonGreen
                )

                PaymentBreakdownRow(
                    label = "UPI / Online QR",
                    amount = "₹${String.format(Locale.US, "%,.2f", state.upiAmount)}",
                    icon = Icons.Default.QrCode2,
                    color = NeonCyan
                )

                PaymentBreakdownRow(
                    label = "Card / POS EDC Swipes",
                    amount = "₹${String.format(Locale.US, "%,.2f", state.cardAmount)}",
                    icon = Icons.Default.CreditCard,
                    color = NeonPurple
                )

                PaymentBreakdownRow(
                    label = "Credit Sales (Udhar Given)",
                    amount = "₹${String.format(Locale.US, "%,.2f", state.totalCreditGiven)}",
                    icon = Icons.Default.Description,
                    color = NeonAmber
                )

                if (state.udharCollectedToday > 0) {
                    PaymentBreakdownRow(
                        label = "Old Udhar Recovered (Cash In)",
                        amount = "+₹${String.format(Locale.US, "%,.2f", state.udharCollectedToday)}",
                        icon = Icons.Default.TrendingUp,
                        color = NeonGreen
                    )
                }

                if (state.totalExpenses > 0) {
                    PaymentBreakdownRow(
                        label = "Shift Expenses (Parchi / Payout)",
                        amount = "-₹${String.format(Locale.US, "%,.2f", state.totalExpenses)}",
                        icon = Icons.Default.TrendingDown,
                        color = NeonCoral
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showCompanyDialog) {
        CompanySelectorDialog(
            selectedCompany = state.config.company,
            onCompanySelected = { viewModel.setCompany(it) },
            onDismiss = { showCompanyDialog = false }
        )
    }

    if (showRateDialog) {
        RateChangeDialog(
            currentRates = state.rates,
            onSaveRates = { newRates ->
                newRates.forEach { (fuel, rate) ->
                    viewModel.updateFuelRate(fuel, rate)
                }
            },
            onDismiss = { showRateDialog = false }
        )
    }

    if (showNewShiftDialog) {
        AlertDialog(
            onDismissRequest = { showNewShiftDialog = false },
            containerColor = com.example.ui.theme.GlassBackgroundMid,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Start New Shift", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text(
                        "Closing readings will become opening readings for the new shift. Cash counts and daily payouts will reset.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newShiftInput,
                        onValueChange = { newShiftInput = it },
                        label = { Text("Shift Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.startNewShift(newShiftInput)
                        showNewShiftDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Confirm & Rollover")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewShiftDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun PaymentBreakdownRow(
    label: String,
    amount: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 13.sp
            )
        }
        Text(
            text = amount,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
