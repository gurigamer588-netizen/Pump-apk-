package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BpclBlue
import com.example.ui.theme.BpclGold
import com.example.ui.theme.EssarOrange
import com.example.ui.theme.EssarSlate
import com.example.ui.theme.HpclBlue
import com.example.ui.theme.HpclRed
import com.example.ui.theme.IoclBlue
import com.example.ui.theme.IoclOrange
import com.example.ui.theme.JioBpBlue
import com.example.ui.theme.JioBpGreen
import com.example.ui.theme.NayaraRed
import com.example.ui.theme.NayaraTeal
import com.example.ui.theme.ShellRed
import com.example.ui.theme.ShellYellow
import com.example.ui.theme.TotalBlue
import com.example.ui.theme.TotalRed

enum class OilCompany(
    val brandName: String,
    val shortCode: String,
    val tagline: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val brandSymbol: String
) {
    IOCL(
        brandName = "Indian Oil Corporation",
        shortCode = "IOCL",
        tagline = "The Energy of India",
        primaryColor = IoclOrange,
        secondaryColor = IoclBlue,
        brandSymbol = "IOCL"
    ),
    BPCL(
        brandName = "Bharat Petroleum",
        shortCode = "BPCL",
        tagline = "Energising Lives",
        primaryColor = BpclBlue,
        secondaryColor = BpclGold,
        brandSymbol = "BPCL"
    ),
    HPCL(
        brandName = "Hindustan Petroleum",
        shortCode = "HPCL",
        tagline = "Delivering Happiness",
        primaryColor = HpclBlue,
        secondaryColor = HpclRed,
        brandSymbol = "HPCL"
    ),
    JIO_BP(
        brandName = "Jio-bp (Reliance BP)",
        shortCode = "Jio-bp",
        tagline = "Experience the Future of Fuel",
        primaryColor = JioBpBlue,
        secondaryColor = JioBpGreen,
        brandSymbol = "JIO-BP"
    ),
    NAYARA(
        brandName = "Nayara Energy",
        shortCode = "NAYARA",
        tagline = "Powering Tomorrow (formerly Essar)",
        primaryColor = NayaraTeal,
        secondaryColor = NayaraRed,
        brandSymbol = "NAYARA"
    ),
    SHELL(
        brandName = "Shell India",
        shortCode = "SHELL",
        tagline = "Go Well, Go Shell",
        primaryColor = ShellYellow,
        secondaryColor = ShellRed,
        brandSymbol = "SHELL"
    ),
    ESSAR(
        brandName = "Essar Oil",
        shortCode = "ESSAR",
        tagline = "Fuelling Growth",
        primaryColor = EssarOrange,
        secondaryColor = EssarSlate,
        brandSymbol = "ESSAR"
    ),
    TOTAL_ENERGIES(
        brandName = "TotalEnergies",
        shortCode = "TOTAL",
        tagline = "Committed to Better Energy",
        primaryColor = TotalRed,
        secondaryColor = TotalBlue,
        brandSymbol = "TOTAL"
    ),
    GULF(
        brandName = "Gulf Oil",
        shortCode = "GULF",
        tagline = "Fuel The Drive",
        primaryColor = Color(0xFFFF6D00),
        secondaryColor = Color(0xFF0038A8),
        brandSymbol = "GULF"
    )
}
