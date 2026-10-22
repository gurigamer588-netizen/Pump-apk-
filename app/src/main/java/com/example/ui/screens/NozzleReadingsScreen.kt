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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelType
import com.example.data.model.NozzleReading
import com.example.data.model.UserRole
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.theme.GlassBackgroundMid
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.viewmodel.PetrolPumpViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NozzleReadingsScreen(
    viewModel: PetrolPumpViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var editingNozzle by remember { mutableStateOf<NozzleReading?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        floatingActionButton = {
            if (state.currentRole != UserRole.STAFF) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = NeonCyan,
                    contentColor = Color.Black,
                    shape = CircleShape
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Nozzle")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header summary
            item {
                GlassCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "DISPENSER TOTALIZERS",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${state.nozzles.size} Active Nozzles",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Total Sale (₹)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "₹${String.format(Locale.US, "%,.2f", state.totalFuelSaleAmount)}",
                                color = NeonAmber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            // List of nozzles
            items(state.nozzles, key = { it.id }) { nozzle ->
                val rate = state.rates[nozzle.fuelType] ?: nozzle.fuelType.defaultRate
                val totalAmount = nozzle.calculateTotalAmount(rate)

                GlassCard(
                    borderColor = nozzle.fuelType.accentColor.copy(alpha = 0.4f),
                    glowColor = nozzle.fuelType.accentColor,
                    onClick = { editingNozzle = nozzle }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(nozzle.fuelType.accentColor.copy(alpha = 0.15f))
                                    .border(1.dp, nozzle.fuelType.accentColor.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = nozzle.fuelType.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = nozzle.nozzleName,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${nozzle.fuelType.displayName} @ ₹${String.format(Locale.US, "%.2f", rate)}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassBadge(
                                text = nozzle.fuelType.code,
                                accentColor = nozzle.fuelType.accentColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(onClick = { editingNozzle = nozzle }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    tint = NeonCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Reading Matrix
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x221E293B))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Opening Reading", color = TextTertiary, fontSize = 10.sp)
                            Text(
                                String.format(Locale.US, "%.2f", nozzle.openingReading),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column {
                            Text("Closing Reading", color = TextTertiary, fontSize = 10.sp)
                            Text(
                                String.format(Locale.US, "%.2f", nozzle.closingReading),
                                color = NeonCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column {
                            Text("Testing (Return)", color = TextTertiary, fontSize = 10.sp)
                            Text(
                                "-${String.format(Locale.US, "%.1f", nozzle.testingLitres)} L",
                                color = NeonCoral,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Calculation footer
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Net Dispensed: ${String.format(Locale.US, "%.2f", nozzle.netLitres)} L",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%,.2f", totalAmount)}",
                            color = NeonAmber,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    // Edit Nozzle Dialog
    editingNozzle?.let { nozzle ->
        var openInput by remember { mutableStateOf(nozzle.openingReading.toString()) }
        var closeInput by remember { mutableStateOf(nozzle.closingReading.toString()) }
        var testInput by remember { mutableStateOf(nozzle.testingLitres.toString()) }
        var nameInput by remember { mutableStateOf(nozzle.nozzleName) }
        var selectedFuel by remember { mutableStateOf(nozzle.fuelType) }

        AlertDialog(
            onDismissRequest = { editingNozzle = null },
            containerColor = GlassBackgroundMid,
            shape = RoundedCornerShape(24.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit ${nozzle.nozzleName}", color = TextPrimary, fontWeight = FontWeight.Bold)
                    if (state.currentRole == UserRole.MANAGER) {
                        IconButton(onClick = {
                            viewModel.deleteNozzle(nozzle.id)
                            editingNozzle = null
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonCoral)
                        }
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Nozzle Name / ID") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = openInput,
                        onValueChange = { openInput = it },
                        label = { Text("Opening Totalizer Reading") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = closeInput,
                        onValueChange = { closeInput = it },
                        label = { Text("Closing Totalizer Reading") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = testInput,
                        onValueChange = { testInput = it },
                        label = { Text("Testing Measure Litres (5L / 10L returned)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Quick preview
                    val openVal = openInput.toDoubleOrNull() ?: 0.0
                    val closeVal = closeInput.toDoubleOrNull() ?: 0.0
                    val testVal = testInput.toDoubleOrNull() ?: 0.0
                    val net = (closeVal - openVal - testVal).coerceAtLeast(0.0)
                    val rate = state.rates[selectedFuel] ?: selectedFuel.defaultRate
                    val estAmount = net * rate

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x221E293B))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Net: ${String.format(Locale.US, "%.2f", net)} L  |  Est: ₹${String.format(Locale.US, "%,.2f", estAmount)}",
                            color = NeonGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val openVal = openInput.toDoubleOrNull() ?: nozzle.openingReading
                        val closeVal = closeInput.toDoubleOrNull() ?: nozzle.closingReading
                        val testVal = testInput.toDoubleOrNull() ?: nozzle.testingLitres
                        viewModel.updateNozzle(
                            id = nozzle.id,
                            opening = openVal,
                            closing = closeVal,
                            testing = testVal,
                            name = nameInput,
                            fuelType = selectedFuel
                        )
                        editingNozzle = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Save Reading")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingNozzle = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Add Nozzle Dialog
    if (showAddDialog) {
        var newName by remember { mutableStateOf("Nozzle ${state.nozzles.size + 1}") }
        var newOpening by remember { mutableStateOf("0.0") }
        var newFuel by remember { mutableStateOf(FuelType.PETROL) }
        var expandedFuel by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = GlassBackgroundMid,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Add New Nozzle", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Nozzle Name (e.g. Pump 3 - MS)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenuBox(
                        expanded = expandedFuel,
                        onExpandedChange = { expandedFuel = !expandedFuel }
                    ) {
                        OutlinedTextField(
                            value = "${newFuel.displayName} (${newFuel.code})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fuel Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFuel) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = expandedFuel,
                            onDismissRequest = { expandedFuel = false }
                        ) {
                            FuelType.values().forEach { fuel ->
                                DropdownMenuItem(
                                    text = { Text("${fuel.displayName} (${fuel.code})") },
                                    onClick = {
                                        newFuel = fuel
                                        expandedFuel = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newOpening,
                        onValueChange = { newOpening = it },
                        label = { Text("Current Totalizer Opening Reading") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
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
                        val openingVal = newOpening.toDoubleOrNull() ?: 0.0
                        viewModel.addNozzle(newName, newFuel, openingVal)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
                ) {
                    Text("Add Nozzle")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
