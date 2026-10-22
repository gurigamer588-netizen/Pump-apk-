package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.CompanySelectorDialog
import com.example.ui.components.RoleSwitchDialog
import com.example.ui.screens.CalculatorsScreen
import com.example.ui.screens.HisabSheetScreen
import com.example.ui.screens.NozzleReadingsScreen
import com.example.ui.screens.PaymentCashScreen
import com.example.ui.screens.UdharBookScreen
import com.example.ui.theme.GlassBackgroundEnd
import com.example.ui.theme.GlassBackgroundMid
import com.example.ui.theme.GlassBackgroundStart
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PetrolPumpViewModel

enum class NavigationTab(
    val title: String,
    val icon: ImageVector
) {
    HISAB_SHEET("Hisab Sheet", Icons.Default.ReceiptLong),
    NOZZLES("Nozzles", Icons.Default.Speed),
    PAYMENTS("Cash & Pay", Icons.Default.Payments),
    UDHAR_BOOK("Udhar Book", Icons.Default.MenuBook),
    CALCULATORS("Calculators", Icons.Default.Calculate)
}

@Composable
fun PetrolPumpApp(
    viewModel: PetrolPumpViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var currentTab by remember { mutableStateOf(NavigationTab.HISAB_SHEET) }

    var showCompanySelector by remember { mutableStateOf(false) }
    var showRoleSwitcher by remember { mutableStateOf(false) }

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            GlassBackgroundStart,
            GlassBackgroundMid,
            GlassBackgroundEnd
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                GlassTopAppBar(
                    companyName = state.config.company.brandName,
                    companyCode = state.config.company.shortCode,
                    companyColor = state.config.company.primaryColor,
                    userRole = state.currentRole,
                    onCompanyClick = {
                        if (state.currentRole != UserRole.STAFF) {
                            showCompanySelector = true
                        }
                    },
                    onRoleClick = { showRoleSwitcher = true }
                )
            },
            bottomBar = {
                GlassBottomNavigationBar(
                    currentTab = currentTab,
                    onTabSelected = { currentTab = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    NavigationTab.HISAB_SHEET -> HisabSheetScreen(viewModel = viewModel)
                    NavigationTab.NOZZLES -> NozzleReadingsScreen(viewModel = viewModel)
                    NavigationTab.PAYMENTS -> PaymentCashScreen(viewModel = viewModel)
                    NavigationTab.UDHAR_BOOK -> UdharBookScreen(viewModel = viewModel)
                    NavigationTab.CALCULATORS -> CalculatorsScreen(viewModel = viewModel)
                }
            }
        }
    }

    if (showCompanySelector) {
        CompanySelectorDialog(
            selectedCompany = state.config.company,
            onCompanySelected = { viewModel.setCompany(it) },
            onDismiss = { showCompanySelector = false }
        )
    }

    if (showRoleSwitcher) {
        RoleSwitchDialog(
            currentRole = state.currentRole,
            onRoleSelected = { viewModel.setRole(it) },
            onDismiss = { showRoleSwitcher = false }
        )
    }
}

@Composable
fun GlassTopAppBar(
    companyName: String,
    companyCode: String,
    companyColor: Color,
    userRole: UserRole,
    onCompanyClick: () -> Unit,
    onRoleClick: () -> Unit
) {
    val roleColor = when (userRole) {
        UserRole.MANAGER -> NeonAmber
        UserRole.SUPERVISOR -> NeonCyan
        UserRole.STAFF -> NeonGreen
    }
    val roleIcon = when (userRole) {
        UserRole.MANAGER -> Icons.Default.AdminPanelSettings
        UserRole.SUPERVISOR -> Icons.Default.SupervisorAccount
        UserRole.STAFF -> Icons.Default.Engineering
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x2E1E293B))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            // App Branding & Company Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onCompanyClick)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(companyColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = companyCode.take(3),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "PetrolPump Pro",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.LocalGasStation,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Text(
                        text = "$companyName ▾",
                        color = companyColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // User Role Switcher Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(roleColor.copy(alpha = 0.15f))
                    .border(1.dp, roleColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .clickable(onClick = onRoleClick)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = roleIcon,
                        contentDescription = null,
                        tint = roleColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = userRole.title.substringBefore(" /"),
                        color = roleColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun GlassBottomNavigationBar(
    currentTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Color(0xE60F172A))
            .border(1.dp, Color(0x3338BDF8), RoundedCornerShape(22.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationTab.values().forEach { tab ->
                val isSelected = tab == currentTab
                val itemColor = if (isSelected) NeonCyan else TextSecondary

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) NeonCyan.copy(alpha = 0.15f) else Color.Transparent)
                        .clickable { onTabSelected(tab) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.title,
                        tint = itemColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tab.title,
                        color = itemColor,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}
