package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBackgroundDark
import com.example.ui.theme.GlassBackgroundLight
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.model.AppLanguage
import com.example.model.AppStrings
import com.example.model.appString
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldDark
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TextPrimaryLight

import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.outlined.SupervisorAccount

enum class AppScreen(val title: String) {
    HOME("Discover"),
    MAP_3D("Yandex Map"),
    BOOKING("Zakaz Berish"),
    TARIFFS("Tariflar"),
    OFFERS("Takliflarimiz"),
    TICKETS("Chiptalar"),
    AI_PLANNER("Xarajatlar"),
    AR_GUIDE("AR Vision"),
    SHOWCASE("Editorial"),
    ADMIN("Admin Server")
}

@Composable
fun SilkRoadBottomNavBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()

    val navItems = listOf(
        NavItemData(
            screen = AppScreen.HOME,
            label = appString("nav_discover"),
            selectedIcon = Icons.Filled.Explore,
            unselectedIcon = Icons.Outlined.Explore
        ),
        NavItemData(
            screen = AppScreen.MAP_3D,
            label = appString("nav_map"),
            selectedIcon = Icons.Filled.Map,
            unselectedIcon = Icons.Outlined.Map
        ),
        NavItemData(
            screen = AppScreen.AI_PLANNER,
            label = appString("nav_expenses"),
            selectedIcon = Icons.Filled.AutoAwesome,
            unselectedIcon = Icons.Outlined.AutoAwesome,
            isCenterHero = true
        ),
        NavItemData(
            screen = AppScreen.AR_GUIDE,
            label = appString("nav_ar"),
            selectedIcon = Icons.Filled.CameraAlt,
            unselectedIcon = Icons.Outlined.CameraAlt
        ),
        NavItemData(
            screen = AppScreen.OFFERS,
            label = appString("nav_offers"),
            selectedIcon = Icons.Filled.LocalOffer,
            unselectedIcon = Icons.Outlined.LocalOffer
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating Frosted Glass Capsule with Luxury Gold Glow
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            elevation = 16.dp,
            backgroundColor = if (isDark) Color(0xF2071228) else Color(0xF7FFFFFF),
            borderColor = if (isDark) NeonGold.copy(alpha = 0.45f) else SilkGold.copy(alpha = 0.4f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                navItems.forEach { item ->
                    val isSelected = currentScreen == item.screen
                    if (item.isCenterHero) {
                        CenterAiNavButton(
                            isSelected = isSelected,
                            onClick = { onScreenSelected(item.screen) }
                        )
                    } else {
                        StandardNavItem(
                            item = item,
                            isSelected = isSelected,
                            onClick = { onScreenSelected(item.screen) }
                        )
                    }
                }
            }
        }
    }
}

private data class NavItemData(
    val screen: AppScreen,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val isCenterHero: Boolean = false
)

@Composable
private fun StandardNavItem(
    item: NavItemData,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.08f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "NavScale"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isSelected) {
            if (isDark) NeonGold else RegistanBlue
        } else {
            if (isDark) Color(0xFF8E9BB0) else Color(0xFF64748B)
        },
        label = "NavIconColor"
    )

    Column(
        modifier = Modifier
            .scale(scale)
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("nav_item_${item.screen.name.lowercase()}"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(26.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = iconColor
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(14.dp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(if (isDark) NeonGold else SilkGold)
            )
        } else {
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
private fun CenterAiNavButton(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.12f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f),
        label = "AiButtonScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 12.dp else 6.dp,
        label = "AiElevation"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .padding(bottom = 4.dp)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                ambientColor = SilkGold.copy(alpha = 0.5f),
                spotColor = NeonGold.copy(alpha = 0.8f)
            )
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = if (isSelected) {
                        listOf(NeonGold, SilkGold, SilkGoldDark)
                    } else {
                        listOf(RegistanBlue, RegistanBlueDark)
                    }
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(10.dp)
            .testTag("nav_item_ai_planner"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.AutoAwesome,
            contentDescription = "AI Planner",
            tint = if (isSelected) TextPrimaryLight else SilkGoldLight,
            modifier = Modifier.size(24.dp)
        )
    }
}
