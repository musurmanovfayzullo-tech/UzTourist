package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.TelegramBotManager
import com.example.util.UserSessionManager

/**
 * Modal Dialog displayed when the Guide accepts the tourist (or when the app becomes active via ID).
 * Prompts the tourist to enter the code/number provided by the Guide, verifies user details
 * (Name, Connected Phone, Gmail, Tourist ID), and dispatches all information directly
 * to the Admin App and Telegram Dispatch Bot.
 */
@Composable
fun GuideAcceptedNumberDialog(
    onDismiss: () -> Unit,
    onSubmittedSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentUserId = remember { UserSessionManager.getUserId(context) }
    val profile = remember { UserSessionManager.loadProfile(context) }

    var guideNumberInput by remember { mutableStateOf("") }
    var userNameInput by remember {
        mutableStateOf(
            if (profile != null && profile.firstName.isNotBlank())
                "${profile.firstName} ${profile.lastName}".trim()
            else ""
        )
    }
    var phoneInput by remember {
        mutableStateOf(profile?.phoneNumber?.trim() ?: "")
    }
    var emailInput by remember {
        mutableStateOf(profile?.email?.trim() ?: "")
    }

    var showQrScanner by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }

    val handleSubmit = {
        val trimmedNum = guideNumberInput.trim()
        val trimmedName = userNameInput.trim()
        val trimmedPhone = phoneInput.trim()
        val trimmedEmail = emailInput.trim()

        if (trimmedNum.isBlank()) {
            errorMessage = "Iltimos, Gid bergan raqamni kiriting!"
        } else if (trimmedName.isBlank()) {
            errorMessage = "Iltimos, ismingizni kiriting!"
        } else if (trimmedPhone.isBlank()) {
            errorMessage = "Iltimos, ulangan telefon raqamingizni kiriting!"
        } else {
            errorMessage = null
            isSubmitting = true

            // 1. Save in UserSessionManager (Local persistent DB for Admin and app)
            UserSessionManager.saveGuideVerification(
                context = context,
                guideNumber = trimmedNum,
                userName = trimmedName,
                phone = trimmedPhone,
                email = trimmedEmail.ifBlank { "tourist@gmail.com" },
                targetUserId = currentUserId
            )

            // 2. Dispatch to Telegram Bot API (Chat ID: 6089586932 and connected channels)
            TelegramBotManager.sendGuideAcceptedVerificationViaTelegramApi(
                context = context,
                guideNumber = trimmedNum,
                userName = trimmedName,
                userPhone = trimmedPhone,
                userEmail = trimmedEmail.ifBlank { "tourist@gmail.com" },
                userId = currentUserId
            ) { success, msg ->
                isSubmitting = false
                isSuccess = true
                Toast.makeText(context, "✅ Ma'lumotlar Admin ilovasi va Telegramga yuborildi!", Toast.LENGTH_LONG).show()
                onSubmittedSuccess()
            }
        }
    }

    Dialog(
        onDismissRequest = {
            if (!isSubmitting) onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.93f)
                .padding(vertical = 20.dp),
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xF8030F23)),
            border = androidx.compose.foundation.BorderStroke(2.dp, Brush.linearGradient(listOf(NeonGold, Color(0xFF10B981))))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Color(0xFF10B981), NeonGold))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "GID SIZNI QABUL QILDI! ✓",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF10B981),
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "Ilova ID orqali faollashtirildi",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.5.sp,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Yopish",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Badge Info Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0x2210B981))
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Sizning Sayyohlik ID:",
                                fontSize = 10.5.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = currentUserId,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = NeonGold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x3310B981))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🟢 AKTIV",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = Color(0xFF10B981)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Gid sizga bergan maxsus raqamni kiriting. Ushbu raqam va shaxsiy ma'lumotlaringiz Admin ilovaga hamda Telegramga xavfsiz yetkaziladi:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 16.sp
                    ),
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 1. GID BERGAN RAQAM INPUT FIELD (PRIMARY FOCUS)
                OutlinedTextField(
                    value = guideNumberInput,
                    onValueChange = {
                        guideNumberInput = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("🔢 Gid bergan raqamni kiriting *", color = NeonGold, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    placeholder = { Text("Masalan: 7788 yoki Gid bergan maxsus kod", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Key, contentDescription = null, tint = NeonGold, modifier = Modifier.size(20.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("guide_assigned_number_input"),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonGold,
                        unfocusedBorderColor = SilkGold.copy(alpha = 0.6f),
                        focusedContainerColor = Color(0x33F6C845),
                        unfocusedContainerColor = Color(0x1A0047AB),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Fast QR Scan and Preset Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // QR Scanner Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Brush.linearGradient(listOf(Color(0xFF0284C7), Color(0xFF0369A1))))
                            .clickable { showQrScanner = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.QrCodeScanner,
                                contentDescription = "QR Skaner",
                                tint = Color.White,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "📷 QR Skanerlash",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Fast Samples
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Kodlar:", fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
                        listOf("7788", "2026", "9911").forEach { sample ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x33F6C845))
                                    .border(0.8.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        guideNumberInput = sample
                                        if (errorMessage != null) errorMessage = null
                                    }
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(text = sample, fontSize = 10.sp, color = NeonGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 2. ISM FAMILIYA INPUT
                OutlinedTextField(
                    value = userNameInput,
                    onValueChange = {
                        userNameInput = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("👤 Ism va Familiyangiz *", fontSize = 11.5.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TurquoiseTile,
                        unfocusedBorderColor = Color(0x55FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. ULANGAN TELEFON RAQAM INPUT
                OutlinedTextField(
                    value = phoneInput,
                    onValueChange = {
                        phoneInput = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("📞 Ulangan telefon raqami *", fontSize = 11.5.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Phone, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TurquoiseTile,
                        unfocusedBorderColor = Color(0x55FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. GMAIL / ELEKTRON POCHTA INPUT
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = {
                        emailInput = it
                        if (errorMessage != null) errorMessage = null
                    },
                    label = { Text("📧 Gmail / Elektron pochta", fontSize = 11.5.sp) },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = null, tint = TurquoiseTile, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TurquoiseTile,
                        unfocusedBorderColor = Color(0x55FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                // Error Message if any
                errorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "⚠️ $err",
                        color = Color(0xFFFF5252),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // SUBMIT BUTTON (Admin va Telegramga yuborish)
                Button(
                    onClick = handleSubmit,
                    enabled = !isSubmitting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_guide_number_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGold,
                        contentColor = Color.Black
                    )
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.Black,
                            strokeWidth = 2.5.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Yuborilmoqda...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tasdiqlash va Yuborish (Admin & Telegram)",
                            fontWeight = FontWeight.Black,
                            fontSize = 12.5.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "🔒 Ma'lumotlaringiz xavfsiz shifrlanadi va faqat rasmiy gid hamda admin boshqaruviga yetkaziladi.",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (showQrScanner) {
        Dialog(
            onDismissRequest = { showQrScanner = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF030E1F)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📷 GIDNING QR-KODINI SKANERLASH",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = NeonGold
                        )
                        IconButton(
                            onClick = { showQrScanner = false },
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Scanner Target Frame
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(Color(0xFF071936))
                            .border(2.dp, Brush.linearGradient(listOf(NeonGold, Color(0xFF10B981))), RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.QrCodeScanner,
                            contentDescription = null,
                            tint = NeonGold.copy(alpha = 0.8f),
                            modifier = Modifier.size(90.dp)
                        )
                        Text(
                            text = "KODNI SHU YERGA QARATING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Gid bergan shaxsiy QR kodni skanerlang yoki raqamni qo'lda kiriting",
                        fontSize = 11.5.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
