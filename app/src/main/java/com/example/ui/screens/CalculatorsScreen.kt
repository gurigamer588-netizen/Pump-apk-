package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelType
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.GlassBackgroundMid
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
import com.example.util.DensityCalculatorHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CalculatorsScreen(
    viewModel: PetrolPumpViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Tab Switcher
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color.Transparent,
            contentColor = NeonCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonCyan,
                    height = 3.dp
                )
            },
            divider = {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0x22FFFFFF))
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Litre ⇄ Rupee Calc", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("15°C Density Calc", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (selectedTab == 0) {
            LitrePetrolCalculatorView(rates = state.rates)
        } else {
            DensityCalculatorView(
                rates = state.rates,
                densityHistory = state.densityRecords,
                onSaveTest = { fuel, obsDensity, obsTemp, invDensity ->
                    viewModel.addDensityRecord(fuel, obsDensity, obsTemp, invDensity)
                }
            )
        }
    }
}

@Composable
fun LitrePetrolCalculatorView(
    rates: Map<FuelType, Double>
) {
    var isRupeeToLitre by remember { mutableStateOf(true) }
    var selectedFuel by remember { mutableStateOf(FuelType.PETROL) }
    var inputVal by remember { mutableStateOf("500") }

    val currentRate = rates[selectedFuel] ?: selectedFuel.defaultRate
    val parsed = inputVal.toDoubleOrNull() ?: 0.0

    val calculatedResult = if (isRupeeToLitre) {
        if (currentRate > 0) parsed / currentRate else 0.0
    } else {
        parsed * currentRate
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Mode toggle
        item {
            GlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isRupeeToLitre) NeonCyan.copy(alpha = 0.2f) else Color(0x221E293B)
                            )
                            .border(
                                1.dp,
                                if (isRupeeToLitre) NeonCyan else GlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                isRupeeToLitre = true
                                if (inputVal.toDoubleOrNull() == null || inputVal.toDoubleOrNull()!! < 50) {
                                    inputVal = "500"
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "₹ to Litres (Amount ➔ L)",
                            color = if (isRupeeToLitre) NeonCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (!isRupeeToLitre) NeonAmber.copy(alpha = 0.2f) else Color(0x221E293B)
                            )
                            .border(
                                1.dp,
                                if (!isRupeeToLitre) NeonAmber else GlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                isRupeeToLitre = false
                                if (inputVal.toDoubleOrNull() == null || inputVal.toDoubleOrNull()!! > 100) {
                                    inputVal = "10.0"
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Litres to ₹ (L ➔ Amount)",
                            color = if (!isRupeeToLitre) NeonAmber else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Fuel selector chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FuelType.values().forEach { fuel ->
                    val isSelected = fuel == selectedFuel
                    val rate = rates[fuel] ?: fuel.defaultRate

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) fuel.accentColor.copy(alpha = 0.2f) else Color(0x221E293B)
                            )
                            .border(
                                1.dp,
                                if (isSelected) fuel.accentColor else GlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedFuel = fuel }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = fuel.code,
                                color = if (isSelected) fuel.accentColor else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "₹${String.format(Locale.US, "%.1f", rate)}",
                                color = TextPrimary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Input Box
        item {
            GlassCard(
                borderColor = if (isRupeeToLitre) NeonCyan.copy(alpha = 0.4f) else NeonAmber.copy(alpha = 0.4f),
                glowColor = if (isRupeeToLitre) NeonCyan else NeonAmber
            ) {
                Text(
                    text = if (isRupeeToLitre) "ENTER AMOUNT IN RUPEES (₹)" else "ENTER VOLUME IN LITRES (L)",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inputVal,
                    onValueChange = { inputVal = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    prefix = {
                        Text(
                            text = if (isRupeeToLitre) "₹ " else "L ",
                            color = if (isRupeeToLitre) NeonCyan else NeonAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isRupeeToLitre) NeonCyan else NeonAmber,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick presets
                Text(
                    text = "Quick Presets:",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                val presets = if (isRupeeToLitre) {
                    listOf("100", "200", "500", "1000", "2000", "3000")
                } else {
                    listOf("1", "2", "5", "10", "15", "25", "40")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    presets.forEach { preset ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x221E293B))
                                .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                                .clickable { inputVal = preset }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isRupeeToLitre) "₹$preset" else "${preset}L",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Hero Glowing Result Readout
        item {
            val accentColor = if (isRupeeToLitre) NeonCyan else NeonAmber

            GlassCard(
                borderColor = accentColor,
                glowColor = accentColor
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = if (isRupeeToLitre) "EXACT FUEL QUANTITY" else "TOTAL FUEL AMOUNT",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isRupeeToLitre) {
                            "${String.format(Locale.US, "%.2f", calculatedResult)} Litres"
                        } else {
                            "₹${String.format(Locale.US, "%,.2f", calculatedResult)}"
                        },
                        color = accentColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Fuel: ${selectedFuel.displayName} @ ₹${String.format(Locale.US, "%.2f", currentRate)}/${selectedFuel.unit}",
                        color = TextTertiary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun DensityCalculatorView(
    rates: Map<FuelType, Double>,
    densityHistory: List<com.example.data.model.DensityRecord>,
    onSaveTest: (FuelType, Double, Double, Double) -> Unit
) {
    var fuelType by remember { mutableStateOf(FuelType.PETROL) }
    var obsDensityInput by remember { mutableStateOf("735.0") }
    var obsTempInput by remember { mutableStateOf("29.5") }
    var invDensityInput by remember { mutableStateOf("744.0") }
    var savedSuccessMessage by remember { mutableStateOf(false) }

    val obsDensity = obsDensityInput.toDoubleOrNull() ?: 0.0
    val obsTemp = obsTempInput.toDoubleOrNull() ?: 15.0
    val invDensity = invDensityInput.toDoubleOrNull() ?: 0.0

    val convertedDensity = DensityCalculatorHelper.calculateDensityAt15C(obsDensity, obsTemp, fuelType)
    val tolerance = DensityCalculatorHelper.checkTolerance(convertedDensity, invDensity)

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Explanatory Banner
        item {
            GlassCard(
                borderColor = NeonCyan.copy(alpha = 0.4f),
                glowColor = NeonCyan
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Science, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ASTM 53B Standard Petroleum Density",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "OMC standard: Permissible tolerance is ±3.0 kg/m³ vs Tanker Invoice.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Fuel Type Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(FuelType.PETROL, FuelType.DIESEL).forEach { fuel ->
                    val isSelected = fuel == fuelType
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) fuel.accentColor.copy(alpha = 0.2f) else Color(0x221E293B)
                            )
                            .border(
                                1.dp,
                                if (isSelected) fuel.accentColor else GlassBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                fuelType = fuel
                                if (fuel == FuelType.PETROL) {
                                    obsDensityInput = "735.0"
                                    invDensityInput = "744.0"
                                } else {
                                    obsDensityInput = "828.0"
                                    invDensityInput = "838.0"
                                }
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = fuel.displayName,
                            color = if (isSelected) fuel.accentColor else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Inputs
        item {
            GlassCard {
                Text(
                    text = "TANKER / NOZZLE HYDROMETER READINGS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = obsDensityInput,
                    onValueChange = { obsDensityInput = it },
                    label = { Text("Observed Hydrometer Reading (kg/m³)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = obsTempInput,
                    onValueChange = { obsTempInput = it },
                    label = { Text("Observed Temperature (°C)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = invDensityInput,
                    onValueChange = { invDensityInput = it },
                    label = { Text("Tanker Challan / Invoice Density (kg/m³)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Converted Density & Permissible Variation Result
        item {
            val statusColor = if (tolerance.isPermissible) NeonGreen else NeonCoral

            GlassCard(
                borderColor = statusColor,
                glowColor = statusColor
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "CONVERTED DENSITY AT 15°C",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$convertedDensity kg/m³",
                        color = statusColor,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = if (tolerance.isPermissible) Icons.Default.CheckCircle else Icons.Default.Warning,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (tolerance.isPermissible) "PASS (±3.0 kg/m³ Limit)" else "OUT OF LIMIT - ALERT!",
                            color = statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = tolerance.message,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onSaveTest(fuelType, obsDensity, obsTemp, invDensity)
                        savedSuccessMessage = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = statusColor, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save Test to Daily Shift Log", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Density History Log
        if (densityHistory.isNotEmpty()) {
            item {
                Text(
                    text = "SHIFT DENSITY TEST LOGS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            items(densityHistory) { rec ->
                GlassCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlassBadge(text = rec.fuelType.code, accentColor = rec.fuelType.accentColor)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${rec.convertedDensity15C} kg/m³ @ 15°C",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Observed: ${rec.observedDensity} kg/m³ at ${rec.observedTemperature}°C | Invoice: ${rec.invoiceDensity}",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        GlassBadge(
                            text = if (rec.isApproved) "PASS" else "FAIL",
                            accentColor = if (rec.isApproved) NeonGreen else NeonCoral
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}
