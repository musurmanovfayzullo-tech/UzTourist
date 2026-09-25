package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.IkatPatternDivider
import com.example.ui.components.UzbekStarEmblem
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TurquoiseTile
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onExploreClicked: () -> Unit,
    onOpenMapClicked: () -> Unit = onExploreClicked,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val heroScale = remember { Animatable(0.85f) }
    val heroAlpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val loadingAlpha = remember { Animatable(0f) }
    val buttonAlpha = remember { Animatable(0f) }

    // Dynamic Loading Progress State
    var progressPercent by remember { mutableFloatStateOf(0.12f) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var isPreparationComplete by remember { mutableStateOf(false) }

    val loadingSteps = remember {
        listOf(
            "Samarqand Registon obidalari tekshirilmoqda...",
            "Eng qulay sayohat marshrutlari saralanmoqda...",
            "Tarixiy AI Audio Gid yuklanmoqda...",
            "Sayohat paketi tayyor!"
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "SplashAnimations")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SpinAngle"
    )

    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerX"
    )

    LaunchedEffect(Unit) {
        heroScale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        )
        heroAlpha.animateTo(1.0f, animationSpec = tween(500))
        delay(100)
        textAlpha.animateTo(1.0f, animationSpec = tween(500))
        delay(100)
        loadingAlpha.animateTo(1.0f, animationSpec = tween(500))

        // Progress simulation animation
        delay(400)
        progressPercent = 0.35f
        currentStepIndex = 1
        delay(700)
        progressPercent = 0.72f
        currentStepIndex = 2
        delay(700)
        progressPercent = 1.0f
        currentStepIndex = 3
        isPreparationComplete = true
        buttonAlpha.animateTo(1.0f, animationSpec = tween(400))

        // Auto-advance after smooth delay or user can tap anytime
        delay(1200)
        onExploreClicked()
    }

    val bgBrush = Brush.verticalGradient(
        colors = if (isDark) {
            listOf(MidnightCanvas, RegistanBlueDark, Color(0xFF030814))
        } else {
            listOf(Color(0xFF092058), RegistanBlue, Color(0xFF020E2C))
        }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgBrush)
    ) {
        // Decorative radial aura
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .size(420.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            TurquoiseTile.copy(alpha = 0.18f),
                            RegistanBlue.copy(alpha = 0.10f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // App Brand Header with Official UzTurist Logo
            Row(
                modifier = Modifier
                    .alpha(textAlpha.value)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_app_brand_logo),
                    contentDescription = "UzTurist Logo",
                    modifier = Modifier
                        .size(56.dp)
                        .shadow(8.dp, CircleShape, spotColor = NeonGold)
                        .clip(CircleShape)
                        .border(2.dp, Brush.linearGradient(listOf(NeonGold, TurquoiseTile)), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Uz",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            text = "TURIST",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 28.sp,
                                letterSpacing = 1.sp
                            ),
                            color = NeonGold
                        )
                    }
                    Text(
                        text = "SAYOHATGA CHIQING",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = TurquoiseTile
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Hero Registan Square Photo (Samarqand)
            Box(
                modifier = Modifier
                    .scale(heroScale.value)
                    .alpha(heroAlpha.value)
                    .fillMaxWidth()
                    .height(210.dp)
                    .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = NeonGold.copy(alpha = 0.5f))
                    .clip(RoundedCornerShape(26.dp))
                    .border(2.dp, Brush.linearGradient(listOf(NeonGold, SilkGold, TurquoiseTile)), RoundedCornerShape(26.dp))
                    .testTag("splash_registan_image_container")
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_registan_1787819168326),
                    contentDescription = "Registon Maydoni - Samarkand",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark elegant gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x22000000),
                                    Color(0x44000C1F),
                                    Color(0xEE020914)
                                )
                            )
                        )
                )

                // Top Tag: UNESCO World Heritage
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC000000))
                        .border(1.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = NeonGold,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "REGISTON MAYDONI • SAMARQAND",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    )
                }

                // Bottom Overlay Title
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "O'zbekiston Gavhari",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SilkGoldLight
                        )
                    )
                    Text(
                        text = "Registon Majmuasi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // AI PREPARATION & LOADING CARD (Requested: "Sayohatni boshlashingiz uchun eng yaxshi variantlarni topyapmiz" & "Tayyorlanmoqda...")
            GlassCard(
                modifier = Modifier
                    .alpha(loadingAlpha.value)
                    .fillMaxWidth()
                    .testTag("splash_loading_card"),
                shape = RoundedCornerShape(24.dp),
                backgroundColor = Color(0x330047AB),
                borderColor = NeonGold.copy(alpha = 0.7f),
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // AI Sparkle Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x33F6C845))
                            .border(1.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI TOUR VIP DISPATCH",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp,
                                color = NeonGold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Main Text Requested by User
                    Text(
                        text = "Sayohatni boshlashingiz uchun eng yaxshi variantlarni topyapmiz",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 23.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Animated Progress Bar with Silk Road Gold Gradient
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0x44001A3D))
                    ) {
                        // Fill bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressPercent)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(TurquoiseTile, NeonGold, SilkGold)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Status: "Tayyorlanmoqda..." with Animated Loading Spinner
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (!isPreparationComplete) {
                            // Spinning Gold Loader
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .rotate(spinAngle),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.fillMaxSize(),
                                    color = NeonGold,
                                    strokeWidth = 2.5.dp,
                                    trackColor = Color(0x33F6C845)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = TurquoiseTile,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = if (!isPreparationComplete) "Tayyorlanmoqda..." else "Tayyor bo'ldi!",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = if (!isPreparationComplete) NeonGold else TurquoiseTile
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Current step detail subtitle
                    Text(
                        text = loadingSteps[currentStepIndex],
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            color = Color(0xFFA0B2C6)
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Start Journey Button & Action Area
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                GoldGradientButton(
                    text = "Sayohatni Boshlash",
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    onClick = onExploreClicked,
                    modifier = Modifier.fillMaxWidth(),
                    height = 52.dp,
                    testTag = "splash_explore_button"
                )

                Spacer(modifier = Modifier.height(12.dp))

                IkatPatternDivider(
                    modifier = Modifier.width(160.dp),
                    color = SilkGold.copy(alpha = 0.5f),
                    height = 6.dp
                )
            }
        }
    }
}

