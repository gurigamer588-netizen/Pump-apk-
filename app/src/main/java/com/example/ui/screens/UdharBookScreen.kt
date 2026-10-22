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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CreditEntry
import com.example.data.model.FuelType
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
fun UdharBookScreen(
    viewModel: PetrolPumpViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    var filterMode by remember { mutableStateOf("ALL") } // ALL, PENDING, COLLECTED
    var showAddDialog by remember { mutableStateOf(false) }
    var showManualRecoveryDialog by remember { mutableStateOf(false) }

    val filteredList = when (filterMode) {
        "PENDING" -> state.creditEntries.filter { !it.isCollected }
        "COLLECTED" -> state.creditEntries.filter { it.isCollected }
        else -> state.creditEntries
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Hero Summary Card
        item {
            GlassCard(
                borderColor = NeonAmber.copy(alpha = 0.5f),
                glowColor = NeonAmber
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "TOTAL UDHAR GIVEN",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%,.2f", state.totalCreditGiven)}",
                            color = NeonAmber,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "${state.creditEntries.count { !it.isCollected }} Pending Customers",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "UDHAR RECOVERED",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${String.format(Locale.US, "%,.2f", state.udharCollectedToday)}",
                            color = NeonGreen,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Cash Received Today",
                            color = TextTertiary,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Udhar Slip", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { showManualRecoveryDialog = true },
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Udhar Collect", color = NeonGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("ALL" to "All Slips (${state.creditEntries.size})", "PENDING" to "Pending (${state.creditEntries.count { !it.isCollected }})", "COLLECTED" to "Collected").forEach { (key, label) ->
                    val isSelected = filterMode == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) NeonCyan.copy(alpha = 0.2f) else Color(0x221E293B)
                            )
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else GlassBorder,
                                RoundedCornerShape(20.dp)
                            )
                            .clickable { filterMode = key }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) NeonCyan else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // List of Udhar Records
        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = TextTertiary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No credit records found", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }
        } else {
            items(filteredList, key = { it.id }) { credit ->
                GlassCard(
                    borderColor = if (credit.isCollected) NeonGreen.copy(alpha = 0.35f) else NeonAmber.copy(alpha = 0.35f),
                    glowColor = if (credit.isCollected) NeonGreen else NeonAmber
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
                                    .background(Color(0x221E293B)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = NeonAmber,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = credit.customerName,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (credit.vehicleNumber.isNotBlank()) {
                                        Text(
                                            text = credit.vehicleNumber,
                                            color = NeonCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    if (credit.mobileNumber.isNotBlank()) {
                                        Text(
                                            text = "• 📞 ${credit.mobileNumber}",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${String.format(Locale.US, "%,.2f", credit.amount)}",
                                color = if (credit.isCollected) NeonGreen else NeonAmber,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            GlassBadge(
                                text = if (credit.isCollected) "PAID" else "PENDING",
                                accentColor = if (credit.isCollected) NeonGreen else NeonCoral
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "${credit.fuelType.displayName} • ${credit.litres} L",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (!credit.isCollected) {
                                OutlinedButton(
                                    onClick = { viewModel.markCreditCollected(credit.id) },
                                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Collect ₹", color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            IconButton(
                                onClick = { viewModel.deleteCreditEntry(credit.id) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextTertiary, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(50.dp))
        }
    }

    // Add New Udhar Slip Dialog
    if (showAddDialog) {
        var custName by remember { mutableStateOf("") }
        var vehNo by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }
        var fuel by remember { mutableStateOf(FuelType.DIESEL) }
        var litresInput by remember { mutableStateOf("") }
        var amountInput by remember { mutableStateOf("") }
        var notesInput by remember { mutableStateOf("") }
        var fuelExpanded by remember { mutableStateOf(false) }

        // Auto calculate amount from litres or vice versa
        val currentRate = state.rates[fuel] ?: fuel.defaultRate

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = GlassBackgroundMid,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("New Credit / Udhar Slip", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = custName,
                        onValueChange = { custName = it },
                        label = { Text("Customer / Party Name *") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonAmber,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = vehNo,
                            onValueChange = { vehNo = it.uppercase() },
                            label = { Text("Vehicle No.") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = mobile,
                            onValueChange = { mobile = it },
                            label = { Text("Mobile") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Fuel selection
                    ExposedDropdownMenuBox(
                        expanded = fuelExpanded,
                        onExpandedChange = { fuelExpanded = !fuelExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${fuel.displayName} (@ ₹$currentRate)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fuel") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelExpanded) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = fuelExpanded,
                            onDismissRequest = { fuelExpanded = false }
                        ) {
                            FuelType.values().forEach { f ->
                                DropdownMenuItem(
                                    text = { Text("${f.displayName} - ₹${state.rates[f] ?: f.defaultRate}") },
                                    onClick = {
                                        fuel = f
                                        val l = litresInput.toDoubleOrNull()
                                        if (l != null && l > 0) {
                                            val r = state.rates[f] ?: f.defaultRate
                                            amountInput = String.format(Locale.US, "%.2f", l * r)
                                        }
                                        fuelExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = litresInput,
                            onValueChange = {
                                litresInput = it
                                val l = it.toDoubleOrNull()
                                if (l != null) {
                                    amountInput = String.format(Locale.US, "%.2f", l * currentRate)
                                }
                            },
                            label = { Text("Litres") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = amountInput,
                            onValueChange = {
                                amountInput = it
                                val a = it.toDoubleOrNull()
                                if (a != null && currentRate > 0) {
                                    litresInput = String.format(Locale.US, "%.2f", a / currentRate)
                                }
                            },
                            label = { Text("Total ₹") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonAmber,
                                unfocusedBorderColor = GlassBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = notesInput,
                        onValueChange = { notesInput = it },
                        label = { Text("Driver / Slip / Note (Optional)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonAmber,
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
                        val litres = litresInput.toDoubleOrNull() ?: 0.0
                        val amount = amountInput.toDoubleOrNull() ?: (litres * currentRate)
                        if (custName.isNotBlank() && amount > 0) {
                            viewModel.addCreditEntry(
                                customerName = custName,
                                vehicleNo = vehNo,
                                mobile = mobile,
                                fuelType = fuel,
                                litres = litres,
                                amount = amount,
                                notes = notesInput
                            )
                        }
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonAmber, contentColor = Color.Black)
                ) {
                    Text("Save Slip", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // Record Manual Udhar Recovery Dialog
    if (showManualRecoveryDialog) {
        var recAmount by remember { mutableStateOf("") }
        var recCustomer by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showManualRecoveryDialog = false },
            containerColor = GlassBackgroundMid,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text("Record Udhar Collected (Cash In)", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Collect previous balance payment from customer into current shift galla.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    OutlinedTextField(
                        value = recCustomer,
                        onValueChange = { recCustomer = it },
                        label = { Text("Customer Name / Ledger Account") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = recAmount,
                        onValueChange = { recAmount = it },
                        label = { Text("Amount Collected (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonGreen,
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
                        val amt = recAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.recordManualUdharCollection(amt)
                        }
                        showManualRecoveryDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen, contentColor = Color.Black)
                ) {
                    Text("Add to Galla", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showManualRecoveryDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
