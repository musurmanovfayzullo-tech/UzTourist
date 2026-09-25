package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdded
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DayItinerary
import com.example.model.Destination
import com.example.model.ItineraryStop
import com.example.model.PredefinedTripPlans
import com.example.model.StopType
import com.example.model.TripPlan
import com.example.ui.components.AdminAiActivationDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.UzbekStarEmblem
import com.example.util.UserSessionManager
import com.example.ui.theme.AccentEmerald
import com.example.ui.theme.GlassBackgroundDark
import com.example.ui.theme.GlassBackgroundLight
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldDark
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TurquoiseTile
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AiPlannerScreen(
    initialDestination: Destination? = null,
    onNavigateToAr: (Destination) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()

    var showAdminActivationDialog by remember { mutableStateOf(false) }
    val isAiActive = UserSessionManager.currentAiActivatedState
    val currentUserId = remember { UserSessionManager.getUserId(context) }

    var userPrompt by remember {
        mutableStateOf(
            if (initialDestination != null) "Plan a luxury VIP journey focusing on ${initialDestination.title} in ${initialDestination.city}"
            else "Plan a 3-day royal Silk Road trip across Samarkand & Bukhara"
        )
    }
    var isGenerating by remember { mutableStateOf(false) }
    var currentPlan by remember { mutableStateOf<TripPlan>(PredefinedTripPlans.defaultLuxury3Day) }
    var selectedDayIndex by remember { mutableIntStateOf(0) }
    var isPlanSaved by remember { mutableStateOf(false) }

    val handleGenerateAiPlan: () -> Unit = {
        if (!UserSessionManager.isAiActivated(context)) {
            showAdminActivationDialog = true
        } else {
            isGenerating = true
            scope.launch {
                delay(1000)
                isGenerating = false
            }
        }
    }

    val quickBubbles = listOf(
        "3 Days VIP Heritage",
        "Romantic Silk Road Honeymoon",
        "Architectural Photo Safari",
        "Afrasiyob Express Hop",
        "Gastronomic Plov & Wine Tour"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "AuraPulse")
    val bubblePulse by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header: Sitora AI Concierge
            item {
                PlannerHeaderSection(isDark = isDark)
            }

            // AI Activation Status Indicator Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isAiActive) Color(0x2210B981) else Color(0x33EF4444))
                        .border(
                            1.dp,
                            if (isAiActive) Color(0xFF10B981).copy(alpha = 0.6f) else Color(0xFFEF4444).copy(alpha = 0.6f),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable {
                            if (!isAiActive) {
                                showAdminActivationDialog = true
                            }
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = if (isAiActive) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                                contentDescription = null,
                                tint = if (isAiActive) Color(0xFF10B981) else Color(0xFFEF4444),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isAiActive) "🟢 AI FUNKSIYALARI FAOLLASHTIRILGAN" else "🔒 AI BLOKLANGAN: Admin ruxsati kerak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = if (isAiActive) Color(0xFF10B981) else Color(0xFFFF8A80)
                                )
                                Text(
                                    text = if (isAiActive) "Sizning ID: $currentUserId • Barcha AI xizmatlar ochiq" else "ID: $currentUserId • Faollashtirish uchun bosing",
                                    fontSize = 10.5.sp,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                                )
                            }
                        }

                        if (!isAiActive) {
                            Text(
                                text = "Aktivatsiya ➔",
                                fontWeight = FontWeight.Black,
                                fontSize = 11.sp,
                                color = NeonGold
                            )
                        }
                    }
                }
            }

            // Glowing Floating Prompt Bubbles
            item {
                Text(
                    text = "CURATED TRAVEL INTENTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) NeonGold else RegistanBlue
                    ),
                    modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(quickBubbles) { bubbleText ->
                        GlowingBubbleChip(
                            text = bubbleText,
                            isSelected = userPrompt.contains(bubbleText.take(10)),
                            onClick = {
                                userPrompt = "Curate an elite $bubbleText itinerary with VIP access & Afrasiyob rail"
                                handleGenerateAiPlan()
                            }
                        )
                    }
                }
            }

            // Conversational Input Bar (Glowing bubble style)
            item {
                Spacer(modifier = Modifier.height(16.dp))

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .scale(bubblePulse),
                    shape = RoundedCornerShape(26.dp),
                    borderWidth = 1.2.dp,
                    borderColor = if (isDark) NeonGold.copy(alpha = 0.6f) else SilkGold.copy(alpha = 0.7f),
                    elevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sitora AI • Silk Road Concierge",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) NeonGold else RegistanBlue
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = userPrompt,
                            onValueChange = { userPrompt = it },
                            placeholder = {
                                Text(
                                    text = "Ask Sitora: e.g., '3 days in Samarkand with luxury tea ceremonies & photo spots'",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isDark) Color(0xFF8E9BB0) else Color(0xFF94A3B8)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("ai_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    userPrompt = "Plan sunset tour at Registan and ancient Bukhara trading domes"
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Mic,
                                    contentDescription = "Voice Input",
                                    tint = SilkGold
                                )
                            }

                            if (isGenerating) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = NeonGold,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Crafting Itinerary...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = SilkGold
                                    )
                                }
                            } else {
                                GoldGradientButton(
                                    text = "Generate VIP Plan",
                                    icon = Icons.AutoMirrored.Filled.Send,
                                    onClick = handleGenerateAiPlan,
                                    height = 42.dp,
                                    testTag = "generate_plan_button"
                                )
                            }
                        }
                    }
                }
            }

            // Generated Trip Summary Card
            item {
                Spacer(modifier = Modifier.height(20.dp))

                TripSummaryOverviewCard(
                    plan = currentPlan,
                    isSaved = isPlanSaved,
                    onSaveToggle = { isPlanSaved = !isPlanSaved }
                )
            }

            // Day Selector Tabs
            item {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "DAILY TIMELINE BREAKDOWN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) NeonGold else RegistanBlue
                    ),
                    modifier = Modifier.padding(start = 20.dp, bottom = 10.dp)
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(currentPlan.days.indices.toList()) { index ->
                        val day = currentPlan.days[index]
                        val isSelected = selectedDayIndex == index

                        val bgCol = if (isSelected) {
                            if (isDark) NeonGold else RegistanBlue
                        } else {
                            if (isDark) GlassBackgroundDark else Color.White
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(bgCol)
                                .border(
                                    1.dp,
                                    if (isSelected) SilkGold else (if (isDark) GlassBorderDark else Color(0x33CBD5E1)),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedDayIndex = index }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                                .testTag("day_tab_$index"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "DAY ${day.dayNumber}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) (if (isDark) TextPrimaryLight else Color.White) else SilkGold
                                    )
                                )
                                Text(
                                    text = day.city,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) (if (isDark) TextPrimaryLight else Color.White) else MaterialTheme.colorScheme.onBackground
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Timeline Stops for the selected day
            item {
                Spacer(modifier = Modifier.height(14.dp))

                val activeDay = currentPlan.days.getOrNull(selectedDayIndex) ?: currentPlan.days.first()

                DayItineraryTimeline(
                    day = activeDay,
                    isDark = isDark
                )
            }

            // Concierge Insider Advice
            item {
                Spacer(modifier = Modifier.height(16.dp))

                ConciergeTipsSection(
                    tips = currentPlan.conciergeTips,
                    isDark = isDark
                )
            }
        }

        if (showAdminActivationDialog) {
            AdminAiActivationDialog(
                onDismiss = { showAdminActivationDialog = false },
                onActivatedSuccess = {
                    showAdminActivationDialog = false
                    handleGenerateAiPlan()
                }
            )
        }
    }
}

@Composable
private fun PlannerHeaderSection(isDark: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UzbekStarEmblem(
                    size = 36.dp,
                    primaryColor = if (isDark) NeonGold else SilkGold,
                    secondaryColor = TurquoiseTile
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "AI ITINERARY CONCIERGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.8.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (isDark) NeonGold else SilkGold
                    )
                    Text(
                        text = "Sitora AI Planner",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            GlassPillBadge(
                text = "Timurid VIP Mode",
                accentColor = NeonGold,
                textColor = if (isDark) NeonGold else SilkGoldDark
            )
        }
    }
}

@Composable
private fun GlowingBubbleChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    val bg = if (isSelected) {
        Brush.horizontalGradient(listOf(NeonGold, SilkGold))
    } else {
        Brush.horizontalGradient(
            if (isDark) listOf(Color(0x330047AB), Color(0x330A2463))
            else listOf(Color(0xFFE8F0FE), Color(0xFFF5F7FA))
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(
                1.dp,
                if (isSelected) SilkGoldLight else (if (isDark) GlassBorderDark else Color(0x44D4AF37)),
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .testTag("ai_bubble_${text.take(8).lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) TextPrimaryLight else MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TripSummaryOverviewCard(
    plan: TripPlan,
    isSaved: Boolean,
    onSaveToggle: () -> Unit
) {
    val isDark = isSystemInDarkTheme()

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassPillBadge(
                    text = plan.themeTag,
                    accentColor = TurquoiseTile,
                    textColor = TurquoiseTile
                )

                IconButton(
                    onClick = onSaveToggle,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isSaved) AccentEmerald.copy(alpha = 0.2f) else Color(0x1AD4AF37))
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Filled.BookmarkAdded else Icons.Filled.BookmarkAdded,
                        contentDescription = "Save Trip",
                        tint = if (isSaved) AccentEmerald else SilkGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = plan.title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = plan.summary,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 19.sp),
                color = if (isDark) Color(0xFFC0CCDC) else Color(0xFF4A5568)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Budget & Duration Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color(0x440047AB) else Color(0xFFF0F6FF))
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ESTIMATED BUDGET",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = SilkGold
                    )
                    Text(
                        text = "$${plan.estimatedBudgetUsd} USD • ${plan.estimatedBudgetUzs}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "PACE & DURATION",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
                        color = SilkGold
                    )
                    Text(
                        text = "${plan.durationDays} Days • ${plan.pace}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun DayItineraryTimeline(
    day: DayItinerary,
    isDark: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // City & Transport Highlight Card
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            backgroundColor = if (isDark) Color(0x3300A896) else Color(0xFFE8FAF6),
            borderColor = TurquoiseTile.copy(alpha = 0.5f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = day.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsTransit,
                        contentDescription = null,
                        tint = TurquoiseTile,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = day.transportHighlight,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stop by Stop timeline items
        day.stops.forEachIndexed { index, stop ->
            TimelineStopItem(
                stop = stop,
                isLast = index == day.stops.size - 1,
                isDark = isDark
            )
        }
    }
}

@Composable
private fun TimelineStopItem(
    stop: ItineraryStop,
    isLast: Boolean,
    isDark: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        // Time & Icon Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(54.dp)
        ) {
            Text(
                text = stop.time,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = if (stop.isKeyHighlight) NeonGold else SilkGold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (stop.isKeyHighlight) {
                            Brush.linearGradient(listOf(NeonGold, SilkGold))
                        } else {
                            Brush.linearGradient(
                                if (isDark) listOf(RegistanBlue, RegistanBlueDark)
                                else listOf(Color(0xFFE8F0FE), Color(0xFFD6E4FC))
                            )
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (stop.type) {
                        StopType.MONUMENT -> Icons.Filled.AccountBalance
                        StopType.GASTRONOMY -> Icons.Filled.Restaurant
                        StopType.TRAIN -> Icons.Filled.DirectionsTransit
                        StopType.CRAFT -> Icons.Filled.Brush
                        StopType.REST -> Icons.Filled.Hotel
                        StopType.SUNSET -> Icons.Filled.WbTwilight
                    },
                    contentDescription = null,
                    tint = if (stop.isKeyHighlight) TextPrimaryLight else (if (isDark) SilkGoldLight else RegistanBlue),
                    modifier = Modifier.size(16.dp)
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(50.dp)
                        .background(SilkGold.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Content Card
        GlassCard(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 8.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = if (stop.isKeyHighlight) 4.dp else 2.dp,
            borderColor = if (stop.isKeyHighlight) NeonGold.copy(alpha = 0.5f) else null
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stop.title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = stop.duration,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = SilkGold
                    )
                }

                Text(
                    text = stop.location,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = TurquoiseTile
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stop.note,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 16.sp),
                    color = if (isDark) Color(0xFFB0C0D4) else Color(0xFF5A6B80)
                )
            }
        }
    }
}

@Composable
private fun ConciergeTipsSection(
    tips: List<String>,
    isDark: Boolean
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        backgroundColor = if (isDark) Color(0x33D4AF37) else Color(0xFFFFFBEA),
        borderColor = SilkGold.copy(alpha = 0.6f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = SilkGoldDark,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sitora Concierge Silk Road Insights",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SilkGoldDark
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            tips.forEach { tip ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = SilkGold,
                        modifier = Modifier
                            .size(16.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = tip,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
