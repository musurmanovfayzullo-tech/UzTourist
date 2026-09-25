package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile

data class PermissionItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val permissions: List<String>
)

object PermissionUtils {
    fun getRequiredPermissionsList(): Array<String> {
        val list = mutableListOf(
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return list.toTypedArray()
    }

    fun hasAllPermissions(context: Context): Boolean {
        return getRequiredPermissionsList().all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
fun AppPermissionsDialog(
    onAllGranted: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var permissionsState by remember {
        mutableStateOf(
            PermissionUtils.getRequiredPermissionsList().associateWith {
                PermissionUtils.hasPermission(context, it)
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionsState = result
        if (result.values.all { it }) {
            onAllGranted()
        }
    }

    val permissionItems = remember {
        val list = mutableListOf(
            PermissionItem(
                title = "AI Kamera & AR Gid",
                description = "Tarixiy obidalarni skanerlash, jonli kamera orqali tanib olish va XV asr AR qatlamini ko'rish uchun",
                icon = Icons.Filled.CameraAlt,
                permissions = listOf(Manifest.permission.CAMERA)
            ),
            PermissionItem(
                title = "Ipak Yo'li GPS Joylashuvi",
                description = "Xaritada sizning o'rningizni ko'rsatish va eng yaqin maqbara, madrasa va kafelargacha masofani aniqlash uchun",
                icon = Icons.Filled.MyLocation,
                permissions = listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            ),
            PermissionItem(
                title = "Ovozli AI & Audio Gid",
                description = "Ovoz orqali AI yordamchiga savol berish va audiogid funksiyalaridan to'liq foydalanish uchun",
                icon = Icons.Filled.Mic,
                permissions = listOf(Manifest.permission.RECORD_AUDIO)
            )
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(
                PermissionItem(
                    title = "Ekskursiya & Bron Xabarnomalari",
                    description = "Gid buyurtmalari, sayohat rejalari va muhim eslatmalarni o'z vaqtida qabul qilish uchun",
                    icon = Icons.Filled.Notifications,
                    permissions = listOf(Manifest.permission.POST_NOTIFICATIONS)
                )
            )
        }
        list
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        GlassCard(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            backgroundColor = Color(0xF206132D),
            borderColor = NeonGold,
            elevation = 18.dp
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonGold, SilkGold))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = "Permissions",
                        tint = RegistanBlueDark,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Ilova Ruxsatnomalari",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Black,
                        color = NeonGold,
                        fontSize = 20.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Uzbekistan Silk Road VIP ilovasining barcha imkoniyatlari (AR Kamera, 3D Xarita, Ovozli Gid) to'liq ishlashi uchun quyidagi ruxsatlar talab qilinadi:",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFFD6E2F0),
                        lineHeight = 17.sp,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Permission List
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    permissionItems.forEach { item ->
                        val isGranted = item.permissions.all { permissionsState[it] == true }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isGranted) Color(0x3300C853) else Color(0x3300224D)
                                )
                                .border(
                                    1.dp,
                                    if (isGranted) Color(0xFF00E676) else Color(0x44D4AF37),
                                    RoundedCornerShape(14.dp)
                                )
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isGranted) Color(0xFF00E676).copy(alpha = 0.2f)
                                        else RegistanBlue.copy(alpha = 0.5f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isGranted) Icons.Filled.CheckCircle else item.icon,
                                    contentDescription = null,
                                    tint = if (isGranted) Color(0xFF00E676) else NeonGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isGranted) Color(0xFF00E676) else Color.White
                                        )
                                    )
                                    if (isGranted) {
                                        Text(
                                            text = "RUXSAT ETILDI ✓",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color(0xFF00E676)
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = item.description,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 10.sp,
                                        color = Color(0xFFAEC4DD),
                                        lineHeight = 13.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                GoldGradientButton(
                    text = "BARCHA RUXSATLARNI BERISH",
                    onClick = {
                        permissionLauncher.launch(PermissionUtils.getRequiredPermissionsList())
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", context.packageName, null)
                            }
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            text = "Sozlamalardan ochish",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TurquoiseTile,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    TextButton(onClick = onDismiss) {
                        Text(
                            text = "Keyinroq",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
