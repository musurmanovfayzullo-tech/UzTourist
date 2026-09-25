package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.model.AppLanguage
import com.example.model.AppLanguageState
import com.example.model.LocalAppLanguage
import com.example.model.appString
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldDark
import com.example.ui.theme.TurquoiseTile
import com.example.util.TelegramBotManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

/**
 * User Profile Registration Data
 */
data class UserRegistrationProfile(
    val languageCode: String = "uz",
    val languageName: String = "O'zbekcha",
    val phoneNumber: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val touristId: String = "UZ-TOUR-7842",
    val registeredDate: String = "Bugun"
)

data class AppLanguageOption(
    val code: String,
    val name: String,
    val flag: String,
    val greeting: String
)

val AvailableLanguages = AppLanguage.values().map {
    AppLanguageOption(it.code, it.nativeName, it.flag, it.greeting)
}

enum class RegistrationStep {
    STEP_1_LANG_PHONE,
    STEP_2_NAME,
    STEP_3_EMAIL,
    STEP_4_SAVING_ANIMATION
}

@Composable
fun RegistrationScreen(
    onRegistrationSuccess: (UserRegistrationProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val focusManager = LocalFocusManager.current
    val currentLang = LocalAppLanguage.current

    var currentStep by remember { mutableStateOf(RegistrationStep.STEP_1_LANG_PHONE) }

    // Form inputs state
    var selectedLanguage by remember {
        val initial = AvailableLanguages.firstOrNull { it.code == AppLanguageState.currentLanguage.code } ?: AvailableLanguages[0]
        mutableStateOf(initial)
    }
    var phoneNumber by remember { mutableStateOf("+998 ") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var emailAddress by remember { mutableStateOf("") }

    // Errors
    var phoneErrorKey by remember { mutableStateOf<String?>(null) }
    var nameErrorKey by remember { mutableStateOf<String?>(null) }
    var emailErrorKey by remember { mutableStateOf<String?>(null) }

    // 10-Second Saving Progress state
    var savingSecondsLeft by remember { mutableIntStateOf(10) }
    var savingProgress by remember { mutableFloatStateOf(0f) }
    var savingStatusKey by remember { mutableStateOf("saving_msg_1") }
    var isTelegramDispatched by remember { mutableStateOf(false) }
    var hasDispatchedToTelegram by remember { mutableStateOf(false) }

    // 10-Second Timer Effect with Telegram Dispatch
    LaunchedEffect(currentStep) {
        if (currentStep == RegistrationStep.STEP_4_SAVING_ANIMATION) {
            savingSecondsLeft = 10
            savingProgress = 0f
            isTelegramDispatched = false
            hasDispatchedToTelegram = false

            val totalMillis = 10000L
            val interval = 100L
            var elapsed = 0L

            val generatedTouristId = "UZ-TOUR-${(1000..9999).random()}"
            val registeredTime = SimpleDateFormat("HH:mm:ss, dd.MM.yyyy", Locale.getDefault()).format(Date())

            val profileToSend = UserRegistrationProfile(
                languageCode = selectedLanguage.code,
                languageName = selectedLanguage.name,
                phoneNumber = phoneNumber.trim(),
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = emailAddress.trim(),
                touristId = generatedTouristId,
                registeredDate = registeredTime
            )

            while (elapsed < totalMillis) {
                delay(interval)
                elapsed += interval
                savingProgress = (elapsed.toFloat() / totalMillis.toFloat()).coerceIn(0f, 1f)
                savingSecondsLeft = ((totalMillis - elapsed + 999) / 1000).toInt().coerceAtLeast(0)

                // Dispatch to Telegram bot at 2 seconds into the saving countdown
                if (elapsed >= 2000L && !hasDispatchedToTelegram) {
                    hasDispatchedToTelegram = true
                    TelegramBotManager.sendNewUserRegistrationViaTelegramApi(context, profileToSend) { success, _ ->
                        isTelegramDispatched = success
                    }
                }

                savingStatusKey = when {
                    elapsed < 3000 -> "saving_msg_1"
                    elapsed < 6000 -> "saving_msg_2"
                    elapsed < 8500 -> "saving_msg_3"
                    else -> "saving_msg_4"
                }
            }

            delay(400)
            onRegistrationSuccess(profileToSend)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(MidnightCanvas, RegistanBlueDark, Color(0xFF020914))
                    } else {
                        listOf(Color(0xFFF8FAFC), Color(0xFFF1F5F9), Color(0xFFE2E8F0))
                    }
                )
            )
    ) {
        // Decorative background stars & ambient glows
        UzbekStarEmblem(
            size = 320.dp,
            primaryColor = NeonGold.copy(alpha = 0.04f),
            secondaryColor = TurquoiseTile.copy(alpha = 0.04f),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = 20.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .testTag("registration_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Header & Progress Stepper
            if (currentStep != RegistrationStep.STEP_4_SAVING_ANIMATION) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep != RegistrationStep.STEP_1_LANG_PHONE) {
                        IconButton(
                            onClick = {
                                when (currentStep) {
                                    RegistrationStep.STEP_2_NAME -> currentStep = RegistrationStep.STEP_1_LANG_PHONE
                                    RegistrationStep.STEP_3_EMAIL -> currentStep = RegistrationStep.STEP_2_NAME
                                    else -> {}
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x33FFFFFF) else Color(0x1A000000))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = appString("back_button"),
                                tint = if (isDark) Color.White else Color(0xFF0F172A)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(40.dp))
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SILK ROAD UZ",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontSize = 11.sp
                            ),
                            color = NeonGold
                        )
                        Text(
                            text = appString("reg_title"),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            ),
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    }

                    // Step Counter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x26F6C845))
                            .border(1.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        val stepNumber = when (currentStep) {
                            RegistrationStep.STEP_1_LANG_PHONE -> "1/3"
                            RegistrationStep.STEP_2_NAME -> "2/3"
                            RegistrationStep.STEP_3_EMAIL -> "3/3"
                            RegistrationStep.STEP_4_SAVING_ANIMATION -> "..."
                        }
                        Text(
                            text = stepNumber,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = NeonGold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Linear Step Progress indicator
                val stepProgress = when (currentStep) {
                    RegistrationStep.STEP_1_LANG_PHONE -> 0.33f
                    RegistrationStep.STEP_2_NAME -> 0.66f
                    RegistrationStep.STEP_3_EMAIL -> 1.0f
                    RegistrationStep.STEP_4_SAVING_ANIMATION -> 1.0f
                }
                val animatedProgress by animateFloatAsState(targetValue = stepProgress, label = "step_progress")

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonGold,
                    trackColor = if (isDark) Color(0x33FFFFFF) else Color(0x22000000),
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Screen Flow Content
            AnimatedContent(
                targetState = currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "RegistrationStepTransition",
                modifier = Modifier.weight(1f)
            ) { step ->
                when (step) {
                    // ==========================================
                    // 1-QADAM: ILOVA TILI VA TELEFON RAQAM
                    // ==========================================
                    RegistrationStep.STEP_1_LANG_PHONE -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = appString("reg_step_1_title"),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = appString("reg_step_1_sub"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            // Language Selection Row
                            item {
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(AvailableLanguages) { lang ->
                                        val isSelected = selectedLanguage.code == lang.code
                                        val cardBg by animateColorAsState(
                                            targetValue = if (isSelected) Color(0x33F6C845) else if (isDark) Color(0x220B2246) else Color.White,
                                            label = "langBg"
                                        )
                                        val borderCol by animateColorAsState(
                                            targetValue = if (isSelected) NeonGold else Color(0x22FFFFFF),
                                            label = "langBorder"
                                        )

                                        Box(
                                            modifier = Modifier
                                                .width(90.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(cardBg)
                                                .border(if (isSelected) 2.dp else 1.dp, borderCol, RoundedCornerShape(16.dp))
                                                .clickable {
                                                    selectedLanguage = lang
                                                    val appLang = AppLanguage.values().firstOrNull { it.code == lang.code } ?: AppLanguage.UZ
                                                    AppLanguage.currentLanguage = appLang
                                                    AppLanguageState.currentLanguage = appLang
                                                }
                                                .padding(vertical = 14.dp, horizontal = 6.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = lang.flag, fontSize = 24.sp)
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(
                                                    text = lang.name,
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                                        fontSize = 11.sp
                                                    ),
                                                    color = if (isSelected) NeonGold else if (isDark) Color.White else Color(0xFF1E293B)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Column {
                                    Text(
                                        text = appString("reg_step_1_phone_title"),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = appString("reg_step_1_phone_sub"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            // Phone input field
                            item {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(18.dp),
                                    backgroundColor = if (isDark) Color(0x330B2246) else Color.White,
                                    borderColor = if (phoneErrorKey != null) Color(0xFFFF5252) else NeonGold.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        OutlinedTextField(
                                            value = phoneNumber,
                                            onValueChange = { input ->
                                                phoneErrorKey = null
                                                // Keep format clean
                                                if (input.startsWith("+") || input.isEmpty()) {
                                                    phoneNumber = input
                                                } else {
                                                    phoneNumber = "+$input"
                                                }
                                            },
                                            label = { Text(appString("reg_phone_label")) },
                                            placeholder = { Text("+998 90 123 45 67") },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Phone,
                                                    contentDescription = null,
                                                    tint = NeonGold
                                                )
                                            },
                                            isError = phoneErrorKey != null,
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Phone,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("phone_input_field"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = NeonGold,
                                                unfocusedBorderColor = Color(0x44FFFFFF),
                                                focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                                                unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A)
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )

                                        if (phoneErrorKey != null) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = appString(phoneErrorKey!!),
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF5252), fontSize = 11.sp)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                GoldGradientButton(
                                    text = appString("reg_continue_btn"),
                                    onClick = {
                                        val cleanPhone = phoneNumber.replace(" ", "").replace("-", "")
                                        if (cleanPhone.length < 9) {
                                            phoneErrorKey = "err_enter_phone"
                                        } else {
                                            phoneErrorKey = null
                                            currentStep = RegistrationStep.STEP_2_NAME
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("continue_step_1_button")
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 2-QADAM: ISM VA FAMILYA
                    // ==========================================
                    RegistrationStep.STEP_2_NAME -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = appString("reg_step_2_title"),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        ),
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = appString("reg_step_2_sub"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            item {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    backgroundColor = if (isDark) Color(0x330B2246) else Color.White,
                                    borderColor = if (nameErrorKey != null) Color(0xFFFF5252) else NeonGold.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        // Ism (First name)
                                        OutlinedTextField(
                                            value = firstName,
                                            onValueChange = {
                                                nameErrorKey = null
                                                firstName = it
                                            },
                                            label = { Text(appString("reg_first_name")) },
                                            placeholder = { Text(appString("reg_first_name_hint")) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Person,
                                                    contentDescription = null,
                                                    tint = NeonGold
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("first_name_input"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = NeonGold,
                                                unfocusedBorderColor = Color(0x44FFFFFF),
                                                focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                                                unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A)
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Familya (Last name)
                                        OutlinedTextField(
                                            value = lastName,
                                            onValueChange = {
                                                nameErrorKey = null
                                                lastName = it
                                            },
                                            label = { Text(appString("reg_last_name")) },
                                            placeholder = { Text(appString("reg_last_name_hint")) },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Badge,
                                                    contentDescription = null,
                                                    tint = TurquoiseTile
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("last_name_input"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = NeonGold,
                                                unfocusedBorderColor = Color(0x44FFFFFF),
                                                focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                                                unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A)
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )

                                        if (nameErrorKey != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = appString(nameErrorKey!!),
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF5252), fontSize = 11.sp)
                                            )
                                        }
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                GoldGradientButton(
                                    text = appString("reg_continue_btn"),
                                    onClick = {
                                        if (firstName.trim().isBlank()) {
                                            nameErrorKey = "err_enter_firstname"
                                        } else if (lastName.trim().isBlank()) {
                                            nameErrorKey = "err_enter_lastname"
                                        } else {
                                            nameErrorKey = null
                                            currentStep = RegistrationStep.STEP_3_EMAIL
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("continue_step_2_button")
                                )
                            }
                        }
                    }

                    // ==========================================
                    // 3-QADAM: GMAIL / ELEKTRON POCHTA
                    // ==========================================
                    RegistrationStep.STEP_3_EMAIL -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Column {
                                    Text(
                                        text = appString("reg_step_3_title"),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 20.sp
                                        ),
                                        color = if (isDark) Color.White else Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = appString("reg_step_3_sub"),
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                        )
                                    )
                                }
                            }

                            item {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(20.dp),
                                    backgroundColor = if (isDark) Color(0x330B2246) else Color.White,
                                    borderColor = if (emailErrorKey != null) Color(0xFFFF5252) else NeonGold.copy(alpha = 0.5f)
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        OutlinedTextField(
                                            value = emailAddress,
                                            onValueChange = {
                                                emailErrorKey = null
                                                emailAddress = it
                                            },
                                            label = { Text(appString("reg_email_label")) },
                                            placeholder = { Text("namuna@gmail.com") },
                                            leadingIcon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Email,
                                                    contentDescription = null,
                                                    tint = NeonGold
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Email,
                                                imeAction = ImeAction.Done
                                            ),
                                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("email_input_field"),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = NeonGold,
                                                unfocusedBorderColor = Color(0x44FFFFFF),
                                                focusedTextColor = if (isDark) Color.White else Color(0xFF0F172A),
                                                unfocusedTextColor = if (isDark) Color.White else Color(0xFF0F172A)
                                            ),
                                            shape = RoundedCornerShape(14.dp)
                                        )

                                        if (emailErrorKey != null) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = appString(emailErrorKey!!),
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFFF5252), fontSize = 11.sp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Quick @gmail.com suggestions
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("@gmail.com", "@mail.ru", "@icloud.com").forEach { domain ->
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0x260047AB))
                                                        .clickable {
                                                            val clean = emailAddress.substringBefore("@")
                                                            emailAddress = if (clean.isNotBlank()) "$clean$domain" else domain
                                                        }
                                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                                ) {
                                                    Text(
                                                        text = domain,
                                                        style = MaterialTheme.typography.labelSmall.copy(
                                                            fontSize = 11.sp,
                                                            color = TurquoiseTile
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Summary Preview Card
                            item {
                                GlassCard(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(16.dp),
                                    backgroundColor = Color(0x18F6C845),
                                    borderColor = NeonGold.copy(alpha = 0.3f)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = appString("reg_summary_title"),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                fontSize = 10.sp,
                                                letterSpacing = 1.sp,
                                                color = NeonGold
                                            )
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = "👤 $firstName $lastName",
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = if (isDark) Color.White else Color(0xFF0F172A)
                                        )
                                        Text(
                                            text = "📞 $phoneNumber",
                                            style = MaterialTheme.typography.bodySmall.copy(color = TurquoiseTile)
                                        )
                                        Text(
                                            text = "🌐 ${appString("reg_lang_label")}: ${selectedLanguage.flag} ${selectedLanguage.name}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B))
                                        )
                                    }
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                GoldGradientButton(
                                    text = appString("reg_finish_btn"),
                                    onClick = {
                                        if (emailAddress.trim().isBlank() || !emailAddress.contains("@")) {
                                            emailErrorKey = "err_enter_email"
                                        } else {
                                            emailErrorKey = null
                                            currentStep = RegistrationStep.STEP_4_SAVING_ANIMATION
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("complete_registration_button")
                                )
                            }
                        }
                    }

                    // =========================================================================
                    // 4-QADAM: 10 SONIYALIK "MA'LUMOTLARINGIZNI SAQLAYAPMIZ..." ANIMATSIYASI
                    // =========================================================================
                    RegistrationStep.STEP_4_SAVING_ANIMATION -> {
                        val infiniteTransition = rememberInfiniteTransition(label = "pulse_rotation")
                        val rotationAngle by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 3000, easing = LinearEasing),
                                repeatMode = RepeatMode.Restart
                            ),
                            label = "star_rotation"
                        )
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 0.95f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "pulse_scale"
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            // Luxury Animated Center Spinner
                            Box(
                                modifier = Modifier
                                    .size(160.dp)
                                    .scale(pulseScale),
                                contentAlignment = Alignment.Center
                            ) {
                                // Background rotating ornament star
                                UzbekStarEmblem(
                                    size = 150.dp,
                                    primaryColor = NeonGold.copy(alpha = 0.25f),
                                    secondaryColor = TurquoiseTile.copy(alpha = 0.25f),
                                    modifier = Modifier.rotate(rotationAngle)
                                )

                                // Circular Progress Gauge
                                CircularProgressIndicator(
                                    progress = { savingProgress },
                                    modifier = Modifier.size(130.dp),
                                    color = NeonGold,
                                    trackColor = if (isDark) Color(0x330047AB) else Color(0x220047AB),
                                    strokeWidth = 6.dp,
                                    strokeCap = StrokeCap.Round
                                )

                                // Seconds Counter Display in Center
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "$savingSecondsLeft",
                                        style = MaterialTheme.typography.displaySmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 36.sp
                                        ),
                                        color = NeonGold
                                    )
                                    Text(
                                        text = appString("reg_seconds"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = TurquoiseTile
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))

                            // Main Required Headline
                            Text(
                                text = appString("reg_saving_title"),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 21.sp
                                ),
                                color = if (isDark) Color.White else Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Step-by-step Status Message
                            Text(
                                text = appString(savingStatusKey),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = TurquoiseTile,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            // Linear Progress Bar with percentage
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                LinearProgressIndicator(
                                    progress = { savingProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = NeonGold,
                                    trackColor = if (isDark) Color(0x26FFFFFF) else Color(0x26000000),
                                    strokeCap = StrokeCap.Round
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = appString("reg_loading"),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = if (isDark) Color(0xFFA0B2C6) else Color(0xFF64748B)
                                        )
                                    )
                                    Text(
                                        text = "${(savingProgress * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = NeonGold
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Telegram Dispatch Live Confirmation Pill
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isTelegramDispatched) Color(0x3310B981) else Color(0x220047AB),
                                    border = BorderStroke(1.dp, if (isTelegramDispatched) Color(0xFF10B981) else Color(0x4400E5FF)),
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = if (isTelegramDispatched) Icons.Filled.CheckCircle else Icons.Filled.Send,
                                            contentDescription = null,
                                            tint = if (isTelegramDispatched) Color(0xFF10B981) else TurquoiseTile,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isTelegramDispatched) 
                                                "Telegram dispetcheriga yuborildi (ID: 6089586932) ✓" 
                                            else 
                                                "Telegram dispetcherlik tizimiga uzatilmoqda...",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = if (isTelegramDispatched) Color(0xFF10B981) else TurquoiseTile
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
