package com.example.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas as AndroidCanvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DirectionsTransit
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.Destination
import com.example.model.SampleDestinations
import com.example.ui.components.GlassCard
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.SilkGold
import com.example.ui.theme.TurquoiseTile
import com.example.util.GoogleMapHelper
import com.example.util.GpsLocationManager
import com.example.util.YandexMapHelper
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

data class NativeMapPlace(
    val id: String,
    val name: String,
    val city: String,
    val desc: String,
    val category: String,
    val lat: Double,
    val lon: Double,
    val icon: String = "📍"
)

enum class NativeTileStyle(val title: String, val icon: String) {
    STANDARD("Standart Ko'chalar", "🗺️"),
    TOPO("Tog'lar va Relyef", "⛰️"),
    WIKIMEDIA("Wikimedia", "🏛️")
}

// Functions to create modern high-DPI vector pins and GPS dot
private fun createModernPinDrawable(
    context: Context,
    iconEmoji: String,
    category: String,
    isSelected: Boolean
): Drawable {
    val size = if (isSelected) 104 else 84
    val totalHeight = size + 18
    val bitmap = Bitmap.createBitmap(size, totalHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    val primaryColor = when (category) {
        "monument" -> AndroidColor.parseColor("#0047AB")
        "hotel" -> AndroidColor.parseColor("#D4AF37")
        "shopping" -> AndroidColor.parseColor("#7C3AED")
        "dining" -> AndroidColor.parseColor("#E11D48")
        "nature" -> AndroidColor.parseColor("#059669")
        "transport" -> AndroidColor.parseColor("#0284C7")
        "entertainment" -> AndroidColor.parseColor("#D97706")
        else -> AndroidColor.parseColor("#2563EB")
    }

    // Shadow
    paint.color = AndroidColor.argb(70, 0, 0, 0)
    paint.style = Paint.Style.FILL
    canvas.drawCircle(size / 2f, size + 8f, size * 0.22f, paint)

    // Pin Body
    val path = android.graphics.Path().apply {
        val r = size * 0.40f
        val cx = size / 2f
        val cy = r + 4f
        addCircle(cx, cy, r, android.graphics.Path.Direction.CW)
        moveTo(cx - r * 0.72f, cy + r * 0.52f)
        lineTo(cx, size.toFloat() + 4f)
        lineTo(cx + r * 0.72f, cy + r * 0.52f)
        close()
    }

    // Outer border if selected
    if (isSelected) {
        paint.color = AndroidColor.parseColor("#FFD700")
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 7f
        canvas.drawPath(path, paint)
    }

    // Fill pin
    paint.style = Paint.Style.FILL
    paint.color = if (isSelected) AndroidColor.parseColor("#FFB800") else primaryColor
    canvas.drawPath(path, paint)

    // Inner White Disc
    paint.color = AndroidColor.WHITE
    val innerR = size * 0.28f
    val innerCy = size * 0.40f + 4f
    canvas.drawCircle(size / 2f, innerCy, innerR, paint)

    // Emoji icon
    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = size * 0.30f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(iconEmoji, size / 2f, innerCy + (textPaint.textSize / 3f), textPaint)

    return BitmapDrawable(context.resources, bitmap)
}

private fun createModernUserGpsDrawable(context: Context): Drawable {
    val size = 68
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Pulsing halo
    paint.color = AndroidColor.argb(55, 0, 132, 255)
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 2f, paint)

    // White disc
    paint.color = AndroidColor.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 3.2f, paint)

    // Blue core
    paint.color = AndroidColor.parseColor("#0084FF")
    canvas.drawCircle(size / 2f, size / 2f, size / 4.5f, paint)

    return BitmapDrawable(context.resources, bitmap)
}

@Composable
fun InteractiveMapView(
    onNavigateToAr: (Destination) -> Unit,
    onNavigateToPlanner: (Destination?) -> Unit,
    onSwitchTo3DCanvas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()

    // Real-Time GPS Location Stream
    val userLocation by GpsLocationManager.currentLocation.collectAsStateWithLifecycle()

    var mapViewRef by remember { mutableStateOf<MapView?>(null) }
    var selectedPlace by remember { mutableStateOf<NativeMapPlace?>(null) }
    var selectedCategory by remember { mutableStateOf("all") }
    var currentTileStyle by remember { mutableStateOf(NativeTileStyle.STANDARD) }
    var showStyleMenu by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var activeRoutePolyline by remember { mutableStateOf<Polyline?>(null) }
    var userGpsMarker by remember { mutableStateOf<Marker?>(null) }

    val destinations = remember {
        listOf(
            NativeMapPlace("tashkent_city", "Tashkent City Mall", "Toshkent", "Zamonaviy biznes, savdo va dam olish markazi", "shopping", 41.3145, 69.2492, "🛍️"),
            NativeMapPlace("magic_city", "Magic City Park", "Toshkent", "Yevropa uslubidagi ko'ngilochar oilaviy istirohat bog'i", "entertainment", 41.3025, 69.2486, "🏰"),
            NativeMapPlace("besh_qozon", "Besh Qozon (Osh Markazi)", "Toshkent", "Toshkent teleminorasi etagidagi eng mashhur milliy osh markazi", "dining", 41.3482, 69.2842, "🍲"),
            NativeMapPlace("chorsu", "Chorsu Bozori", "Toshkent", "Sharqona mashhur milliy bozor va hunarmandchilik", "shopping", 41.3275, 69.2345, "🥐"),
            NativeMapPlace("amirsoy", "Amirsoy Mountain Resort", "Toshkent vil.", "Zamonaviy xalqaro tog'-chang'i va dor yo'li kurorti", "nature", 41.5360, 70.0230, "⛷️"),
            NativeMapPlace("registan", "Registon Maydoni", "Samarqand", "Sharq gavhari — Ulug'bek, Sherdor va Tillakori madrasalari", "monument", 39.6547, 66.9758, "🕌"),
            NativeMapPlace("silk_road_smq", "Silk Road Samarkand", "Samarqand", "Boqiy Shahar etnografik majmuasi va premium mehmonxonalar", "hotel", 39.6580, 67.0650, "🌟"),
            NativeMapPlace("bukhara_ark", "Ark Qal'asi", "Buxoro", "Qadimiy Buxoro amirligi mustahkam ark qal'asi", "monument", 39.7778, 64.4111, "🏛️"),
            NativeMapPlace("minorai_kalon", "Minorai Kalon", "Buxoro", "XII asr me'moriy mo'jizasi va Poi Kalon ansambli", "monument", 39.7758, 64.4158, "🕌"),
            NativeMapPlace("khiva_ichan", "Ichan Qal'a & Kalta Minor", "Xiva", "Ochiq osmon ostidagi tarixiy muzey-shahar", "monument", 41.3783, 60.3639, "👑"),
            NativeMapPlace("airport_tas", "Toshkent Xalqaro Aeroporti", "Toshkent", "Islom Karimov nomidagi xalqaro aeroport", "transport", 41.2579, 69.2812, "✈️"),
            NativeMapPlace("zomin", "Zomin Milliy Bog'i", "Jizzax", "O'zbekiston Shveytsariyasi — archazorlar va osma ko'prik", "nature", 39.6300, 68.4800, "🌲"),
            NativeMapPlace("shahrisabz", "Oqsaroy Majmuasi", "Shahrisabz", "Amir Temur qurdirgan muazzam saroy qoldiqlari", "monument", 39.0560, 66.8300, "🏛️")
        )
    }

    val categories = listOf(
        Pair("all", "Barchasi 🌟"),
        Pair("shopping", "Savdo & Park 🛍️"),
        Pair("dining", "Milliy Oshxona 🍲"),
        Pair("hotel", "Mehmonxonalar 🏨"),
        Pair("transport", "Aeroport / Vokzal ✈️"),
        Pair("nature", "Tog' & Tabiat 🏔️"),
        Pair("monument", "Tarixiy Obidalar 🕌")
    )

    // Configure OsmDroid User-Agent on entry
    DisposableEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        GpsLocationManager.startLocationUpdates(context)
        onDispose {
            GpsLocationManager.stopLocationUpdates()
            mapViewRef?.onDetach()
        }
    }

    // Function to draw route on native map
    fun drawRouteOnMap(startLat: Double, startLon: Double, destLat: Double, destLon: Double) {
        val map = mapViewRef ?: return

        // Remove old route polyline
        activeRoutePolyline?.let { map.overlays.remove(it) }

        val routeLine = Polyline(map).apply {
            outlinePaint.color = AndroidColor.parseColor("#0084FF")
            outlinePaint.strokeWidth = 14f
            outlinePaint.strokeCap = Paint.Cap.ROUND
            outlinePaint.strokeJoin = Paint.Join.ROUND

            val points = ArrayList<GeoPoint>()
            points.add(GeoPoint(startLat, startLon))

            // Add intermediate bezier curve point for visual beauty
            val midLat = (startLat + destLat) / 2.0 + (if (startLat != destLat) 0.005 else 0.0)
            val midLon = (startLon + destLon) / 2.0 + (if (startLon != destLon) 0.005 else 0.0)
            points.add(GeoPoint(midLat, midLon))
            points.add(GeoPoint(destLat, destLon))

            setPoints(points)
        }

        map.overlays.add(routeLine)
        activeRoutePolyline = routeLine
        map.invalidate()
    }

    // Update GPS Marker when location updates
    LaunchedEffect(userLocation, mapViewRef) {
        val map = mapViewRef ?: return@LaunchedEffect
        userLocation?.let { loc ->
            val userGeoPoint = GeoPoint(loc.latitude, loc.longitude)
            if (userGpsMarker == null) {
                userGpsMarker = Marker(map).apply {
                    position = userGeoPoint
                    title = "Siz turgan joy: ${loc.addressEstimate}"
                    icon = createModernUserGpsDrawable(context)
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    map.overlays.add(this)
                }
            } else {
                userGpsMarker?.position = userGeoPoint
                userGpsMarker?.title = "Siz turgan joy: ${loc.addressEstimate}"
                userGpsMarker?.icon = createModernUserGpsDrawable(context)
            }
            map.invalidate()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) Color(0xFF030A18) else Color(0xFFEAF1FB))
    ) {
        // Native OsmDroid MapView Component (100% Native Android Canvas, Zero Webview, No API Key needed)
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .testTag("native_osmdroid_map"),
            factory = { ctx ->
                MapView(ctx).apply {
                    mapViewRef = this
                    setMultiTouchControls(true)
                    zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                    setTileSource(TileSourceFactory.MAPNIK)

                    // Default camera centered on Tashkent / Uzbekistan
                    val initialLat = userLocation?.latitude ?: 41.3111
                    val initialLon = userLocation?.longitude ?: 69.2797
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(initialLat, initialLon))

                    // Add markers for all locations with modern vector pins
                    destinations.forEach { place ->
                        val marker = Marker(this).apply {
                            position = GeoPoint(place.lat, place.lon)
                            title = "${place.icon} ${place.name}"
                            snippet = "${place.city} • ${place.desc}"
                            icon = createModernPinDrawable(ctx, place.icon, place.category, isSelected = false)
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            setOnMarkerClickListener { _, _ ->
                                selectedPlace = place
                                searchQuery = place.name
                                controller.animateTo(GeoPoint(place.lat, place.lon), 15.0, 600L)
                                true
                            }
                        }
                        overlays.add(marker)
                    }
                }
            },
            update = { map ->
                // Apply Tile Source changes
                when (currentTileStyle) {
                    NativeTileStyle.STANDARD -> map.setTileSource(TileSourceFactory.MAPNIK)
                    NativeTileStyle.TOPO -> map.setTileSource(TileSourceFactory.OpenTopo)
                    NativeTileStyle.WIKIMEDIA -> map.setTileSource(TileSourceFactory.WIKIMEDIA)
                }

                // Update marker pins to reflect selection state
                map.overlays.filterIsInstance<Marker>().forEach { m ->
                    if (m != userGpsMarker) {
                        val place = destinations.find { m.title?.contains(it.name) == true }
                        if (place != null) {
                            val isSelected = selectedPlace?.id == place.id
                            m.icon = createModernPinDrawable(context, place.icon, place.category, isSelected)
                        }
                    }
                }
            }
        )

        // Top Search Bar & Header Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Live GPS Status & Fast Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Real-Time GPS Location Indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0xEE051329) else Color(0xF0FFFFFF))
                        .border(1.dp, TurquoiseTile.copy(alpha = 0.7f), RoundedCornerShape(20.dp))
                        .clickable {
                            val uLat = userLocation?.latitude ?: 41.3111
                            val uLon = userLocation?.longitude ?: 69.2797
                            mapViewRef?.controller?.animateTo(GeoPoint(uLat, uLon), 16.0, 700L)
                            Toast.makeText(
                                context,
                                "Siz turgan joy: ${userLocation?.addressEstimate ?: "Toshkent shahri"}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (userLocation != null) Color(0xFF00E676) else TurquoiseTile)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = userLocation?.addressEstimate ?: "📍 Jonli GPS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isDark) NeonGold else RegistanBlue
                    )
                }

                // Fast Action Buttons: 3D Rejim
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) Color(0xCC0A1B3B) else Color(0xEEFFFFFF))
                            .border(1.dp, TurquoiseTile.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .clickable { onSwitchTo3DCanvas() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("switch_to_3d_canvas")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.ViewInAr,
                                contentDescription = "3D Topografiya",
                                tint = TurquoiseTile,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "3D Rejim",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                ),
                                color = if (isDark) Color.White else RegistanBlue
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { query ->
                    searchQuery = query
                    val matched = destinations.find {
                        it.name.contains(query, ignoreCase = true) ||
                                it.city.contains(query, ignoreCase = true)
                    }
                    if (matched != null) {
                        selectedPlace = matched
                        mapViewRef?.controller?.animateTo(GeoPoint(matched.lat, matched.lon), 15.0, 600L)
                    }
                },
                placeholder = {
                    Text(
                        text = "Qayerga borish kerak? (Tashkent City, Samarqand, Amirsoy...)",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Izlash",
                        tint = NeonGold,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(30.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = "Tozalash",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = if (isDark) Color(0xEE071838) else Color(0xF5FFFFFF),
                    unfocusedContainerColor = if (isDark) Color(0xDD051329) else Color(0xEDFFFFFF),
                    focusedBorderColor = NeonGold,
                    unfocusedBorderColor = if (isDark) Color(0x33D4AF37) else Color(0x220047AB)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { (catKey, catLabel) ->
                    val isSelected = selectedCategory == catKey
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    if (isDark) NeonGold else RegistanBlue
                                } else {
                                    if (isDark) Color(0xCC051329) else Color(0xDDFFFFFF)
                                }
                            )
                            .border(
                                1.dp,
                                if (isSelected) SilkGold else Color(0x22888888),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                selectedCategory = catKey
                                if (catKey != "all") {
                                    val firstMatch = destinations.find { it.category == catKey }
                                    firstMatch?.let {
                                        selectedPlace = it
                                        mapViewRef?.controller?.animateTo(GeoPoint(it.lat, it.lon), 14.0, 600L)
                                    }
                                }
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = catLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (isSelected) {
                                if (isDark) Color.Black else Color.White
                            } else {
                                MaterialTheme.colorScheme.onBackground
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Buttons on Right Side
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.End
        ) {
            // Layer Style Switcher
            IconButton(
                onClick = { showStyleMenu = !showStyleMenu },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD071838) else Color(0xF0FFFFFF))
                    .border(1.5.dp, NeonGold, CircleShape)
                    .shadow(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Layers,
                    contentDescription = "Xarita uslubi",
                    tint = NeonGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Layer Selection Dropdown
            AnimatedVisibility(
                visible = showStyleMenu,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDark) Color(0xF00A1B3B) else Color(0xF5FFFFFF))
                        .border(1.dp, NeonGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NativeTileStyle.values().forEach { style ->
                        val isSelected = currentTileStyle == style
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TurquoiseTile.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable {
                                    currentTileStyle = style
                                    showStyleMenu = false
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = style.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = style.title,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) NeonGold else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }

            // "Mening Joylashuvim" (My GPS Location Button)
            IconButton(
                onClick = {
                    GpsLocationManager.startLocationUpdates(context)
                    val uLat = userLocation?.latitude ?: 41.3111
                    val uLon = userLocation?.longitude ?: 69.2797
                    mapViewRef?.controller?.animateTo(GeoPoint(uLat, uLon), 16.0, 700L)
                    Toast.makeText(
                        context,
                        "Mening joylashuvim: ${userLocation?.addressEstimate ?: "Toshkent shahri"}",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD071838) else Color(0xF0FFFFFF))
                    .border(2.dp, Color(0xFF0084FF), CircleShape)
                    .shadow(8.dp)
                    .testTag("my_gps_location_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.MyLocation,
                    contentDescription = "Mening joylashuvim",
                    tint = Color(0xFF0084FF),
                    modifier = Modifier.size(22.dp)
                )
            }

            // Clear Route Button if route is drawn
            if (activeRoutePolyline != null) {
                IconButton(
                    onClick = {
                        activeRoutePolyline?.let { mapViewRef?.overlays?.remove(it) }
                        activeRoutePolyline = null
                        mapViewRef?.invalidate()
                        Toast.makeText(context, "Marshrut tozalandi", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE53935))
                        .shadow(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "Marshrutni tozalash",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Zoom In Button
            IconButton(
                onClick = { mapViewRef?.controller?.zoomIn() },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD071838) else Color(0xF0FFFFFF))
                    .border(1.dp, if (isDark) Color(0x44D4AF37) else Color(0x330047AB), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = "Kattalashtirish",
                    tint = if (isDark) NeonGold else RegistanBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Zoom Out Button
            IconButton(
                onClick = { mapViewRef?.controller?.zoomOut() },
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xDD071838) else Color(0xF0FFFFFF))
                    .border(1.dp, if (isDark) Color(0x44D4AF37) else Color(0x330047AB), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomOut,
                    contentDescription = "Kichiklashtirish",
                    tint = if (isDark) NeonGold else RegistanBlue,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Bottom Selected Place & Navigation Card
        AnimatedVisibility(
            visible = selectedPlace != null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 85.dp, start = 12.dp, end = 12.dp)
        ) {
            val place = selectedPlace
            if (place != null) {
                val uLat = userLocation?.latitude ?: 41.3111
                val uLon = userLocation?.longitude ?: 69.2797
                val distanceKm = GpsLocationManager.calculateDistanceKm(uLat, uLon, place.lat, place.lon)
                val distanceText = GpsLocationManager.formatDistance(distanceKm)
                val carTime = GpsLocationManager.estimateDuration(distanceKm, "car")
                val trainTime = GpsLocationManager.estimateDuration(distanceKm, "train")
                val walkTime = GpsLocationManager.estimateDuration(distanceKm, "walk")

                val matchedDestination = SampleDestinations.items.find {
                    it.title.contains(place.name, ignoreCase = true) ||
                            place.name.contains(it.title, ignoreCase = true) ||
                            it.city.contains(place.city, ignoreCase = true)
                }

                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(18.dp, RoundedCornerShape(24.dp))
                        .testTag("map_place_bottom_sheet"),
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = if (isDark) Color(0xF8071838) else Color(0xFAFFFFFF),
                    borderColor = NeonGold,
                    elevation = 12.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Photo Banner (if destination has photo)
                        if (matchedDestination != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            ) {
                                Image(
                                    painter = painterResource(id = matchedDestination.imageRes),
                                    contentDescription = matchedDestination.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color.Transparent, Color(0xB0000000))
                                            )
                                        )
                                )
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xDD000000))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Star,
                                                contentDescription = null,
                                                tint = NeonGold,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "${matchedDestination.rating}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                    if (matchedDestination.unescoYear != null) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xDDA16207))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "UNESCO ${matchedDestination.unescoYear}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Title Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(NeonGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = place.icon, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = place.name,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 16.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                    Text(
                                        text = "${place.city} • Masofa: $distanceText",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TurquoiseTile
                                        )
                                    )
                                }
                            }

                            IconButton(
                                onClick = { selectedPlace = null },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Yopish",
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Travel Times (Car, Train, Walk)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x3300C2FF) else Color(0x150047AB))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Car Time
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.DirectionsCar,
                                    contentDescription = null,
                                    tint = NeonGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = carTime,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            // Train Time (if long distance)
                            if (distanceKm > 40) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.DirectionsTransit,
                                        contentDescription = null,
                                        tint = TurquoiseTile,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = trainTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            } else {
                                // Walk Time
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.DirectionsWalk,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = walkTime,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = place.desc,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Real Navigation Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // 1. Marshrut Chizish (Draw Route directly on Native Map)
                            Box(
                                modifier = Modifier
                                    .weight(1.1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0084FF), Color(0xFF00C2FF))
                                        )
                                    )
                                    .clickable {
                                        drawRouteOnMap(uLat, uLon, place.lat, place.lon)
                                        mapViewRef?.controller?.animateTo(GeoPoint((uLat + place.lat)/2.0, (uLon + place.lon)/2.0), 12.0, 700L)
                                        Toast.makeText(
                                            context,
                                            "Sizdan ${place.name}gacha marshrut chizildi ($distanceText)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.NearMe,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Marshrut",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            // 2. AR Kamera (Launch 3D AR Guide)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(listOf(Color(0xFF059669), Color(0xFF10B981)))
                                    )
                                    .clickable {
                                        val target = matchedDestination ?: SampleDestinations.items[0]
                                        onNavigateToAr(target)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.CameraAlt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "AR Ko'rish",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    )
                                }
                            }

                            // 3. Yandex Go Taxi / Navigator
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)))
                                    )
                                    .clickable {
                                        YandexMapHelper.openInYandexMaps(context, place.lat, place.lon, place.name)
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.DirectionsCar,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Yandex Go",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 11.sp,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }

                            // 4. AI Planner shortcut
                            if (matchedDestination != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x33FFD700) else Color(0xFFFFF3CD))
                                        .border(1.dp, NeonGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                        .clickable {
                                            onNavigateToPlanner(matchedDestination)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = "AI Planner",
                                        tint = if (isDark) NeonGold else SilkGold,
                                        modifier = Modifier.size(16.dp)
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
