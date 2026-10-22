package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FuelType
import com.example.ui.theme.GlassBackgroundMid
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun RateChangeDialog(
    currentRates: Map<FuelType, Double>,
    onSaveRates: (Map<FuelType, Double>) -> Unit,
    onDismiss: () -> Unit
) {
    val editedRates = remember {
        mutableStateMapOf<FuelType, String>().apply {
            currentRates.forEach { (fuel, rate) ->
                put(fuel, String.format(java.util.Locale.US, "%.2f", rate))
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GlassBackgroundMid,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.PriceChange,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Update Fuel Rates (₹)",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily 06:00 AM OMC Revised Prices",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FuelType.values().forEach { fuel ->
                    val textVal = editedRates[fuel] ?: ""
                    OutlinedTextField(
                        value = textVal,
                        onValueChange = { newVal ->
                            // Allow numbers and decimal point
                            if (newVal.isEmpty() || newVal.matches(Regex("^\\d*\\.?\\d{0,2}\$"))) {
                                editedRates[fuel] = newVal
                            }
                        },
                        label = { Text("${fuel.displayName} (${fuel.code})") },
                        prefix = {
                            Icon(
                                imageVector = Icons.Default.CurrencyRupee,
                                contentDescription = null,
                                tint = fuel.accentColor,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        suffix = { Text("/ ${fuel.unit}", color = TextSecondary, fontSize = 12.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = fuel.accentColor,
                            unfocusedBorderColor = GlassBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedLabelColor = fuel.accentColor,
                            unfocusedLabelColor = TextSecondary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val map = mutableMapOf<FuelType, Double>()
                    editedRates.forEach { (fuel, str) ->
                        val parsed = str.toDoubleOrNull() ?: currentRates[fuel] ?: fuel.defaultRate
                        map[fuel] = parsed
                    }
                    onSaveRates(map)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonAmber,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Save Rates", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
