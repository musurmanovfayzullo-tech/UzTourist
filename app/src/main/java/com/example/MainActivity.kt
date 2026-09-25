package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.model.AppLanguage
import com.example.model.AppLanguageState
import com.example.model.Destination
import com.example.model.LocalAppLanguage
import com.example.model.SampleDestinations
import com.example.model.TransportType
import com.example.ui.components.AppPermissionsDialog
import com.example.ui.components.AppScreen
import com.example.ui.components.PermissionUtils
import com.example.ui.components.SilkRoadBottomNavBar
import com.example.ui.components.SosActiveFloatingBanner
import com.example.ui.components.SosEmergencyDialog
import com.example.ui.screens.AdminServerScreen
import com.example.ui.screens.AiPlannerScreen
import com.example.ui.screens.ArGuideScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.Map3DScreen
import com.example.ui.screens.OffersScreen
import com.example.ui.screens.RegistrationScreen
import com.example.ui.screens.ShowcaseScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.StaffBookingScreen
import com.example.ui.screens.TicketBookingScreen
import com.example.ui.screens.TourTariffScreen
import com.example.ui.screens.TouristExpenseScreen
import com.example.ui.screens.UserRegistrationProfile
import com.example.ui.theme.UzTouristTheme
import com.example.util.GpsLocationManager
import com.example.util.SosSessionManager
import com.example.util.UserSessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UzTouristApp()
        }
    }
}

@Composable
fun UzTouristApp() {
    val context = LocalContext.current
    val systemDark = isSystemInDarkTheme()
    var isDarkMode by remember { mutableStateOf(systemDark) }
    var showSplash by remember { mutableStateOf(true) }
    var isRegistered by remember { mutableStateOf(UserSessionManager.isRegistered(context)) }
    var userProfile by remember { mutableStateOf(UserSessionManager.loadProfile(context)) }
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var initialTicketType by remember { mutableStateOf(TransportType.FLIGHT) }
    var arDestination by remember { mutableStateOf<Destination>(SampleDestinations.items[0]) }
    var plannerInitialDestination by remember { mutableStateOf<Destination?>(null) }
    var showPermissionsDialog by remember { mutableStateOf(false) }
    var showGuideNumberDialog by remember { mutableStateOf(false) }

    // Reactively detect when Guide accepts the tourist (app becomes active by ID)
    // and automatically display the prompt for the guide-assigned number
    LaunchedEffect(UserSessionManager.currentAiActivatedState, isRegistered) {
        if (isRegistered && UserSessionManager.shouldPromptForGuideNumber(context)) {
            showGuideNumberDialog = true
        }
    }

    // Periodic check (every 3.5 seconds) to catch real-time activation from Guide or Admin
    LaunchedEffect(isRegistered) {
        if (isRegistered) {
            while (true) {
                kotlinx.coroutines.delay(3500)
                if (UserSessionManager.shouldPromptForGuideNumber(context) && !showGuideNumberDialog) {
                    showGuideNumberDialog = true
                }
            }
        }
    }

    // Multi-Permission Request Launcher
    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (!allGranted) {
            // If any permission is denied, keep dialog option ready
            showPermissionsDialog = false
        }
        if (GpsLocationManager.hasLocationPermission(context)) {
            GpsLocationManager.startLocationUpdates(context.applicationContext)
        }
    }

    // Automatically request all required permissions and start GPS when app opens
    LaunchedEffect(Unit) {
        val savedLang = UserSessionManager.getSavedLanguage(context)
        AppLanguageState.currentLanguage = savedLang
        AppLanguage.currentLanguage = savedLang

        if (GpsLocationManager.hasLocationPermission(context)) {
            GpsLocationManager.startLocationUpdates(context.applicationContext)
        }
        if (!PermissionUtils.hasAllPermissions(context)) {
            multiplePermissionLauncher.launch(PermissionUtils.getRequiredPermissionsList())
        }
        // Initialize Realtime WebSocket connection to Supabase
        com.example.network.SupabaseRealtimeManager.init(context)
    }

    val currentAppLanguage = AppLanguageState.currentLanguage
    key(currentAppLanguage) {
        CompositionLocalProvider(LocalAppLanguage provides currentAppLanguage) {
            UzTouristTheme(darkTheme = isDarkMode) {
                Crossfade(
                targetState = when {
                    showSplash -> "SPLASH"
                    !isRegistered -> "REGISTRATION"
                    else -> "MAIN_APP"
                },
                label = "AppNavigationStateTransition"
            ) { appState ->
            when (appState) {
                "SPLASH" -> {
                    SplashScreen(
                        onExploreClicked = { showSplash = false },
                        onOpenMapClicked = {
                            currentScreen = AppScreen.MAP_3D
                            showSplash = false
                        }
                    )
                }

                "REGISTRATION" -> {
                    RegistrationScreen(
                        onRegistrationSuccess = { profile ->
                            UserSessionManager.saveProfile(context, profile)
                            userProfile = profile
                            isRegistered = true
                            currentScreen = AppScreen.HOME
                        }
                    )
                }

                "MAIN_APP" -> {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            SilkRoadBottomNavBar(
                                currentScreen = currentScreen,
                                onScreenSelected = { screen ->
                                    currentScreen = screen
                                }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            AnimatedContent(
                                targetState = currentScreen,
                                transitionSpec = {
                                    fadeIn() togetherWith fadeOut()
                                },
                                label = "ScreenSwitchTransition"
                            ) { targetScreen ->
                                when (targetScreen) {
                                    AppScreen.HOME -> {
                                        HomeScreen(
                                            onNavigateToAr = { dest ->
                                                arDestination = dest
                                                currentScreen = AppScreen.AR_GUIDE
                                            },
                                            onNavigateToPlanner = { dest ->
                                                plannerInitialDestination = dest
                                                currentScreen = AppScreen.AI_PLANNER
                                            },
                                            onNavigateTo3DMap = { _ ->
                                                currentScreen = AppScreen.MAP_3D
                                            },
                                            onNavigateToBooking = {
                                                currentScreen = AppScreen.BOOKING
                                            },
                                            onNavigateToTariffs = {
                                                currentScreen = AppScreen.TARIFFS
                                            },
                                            onNavigateToAdmin = {
                                                currentScreen = AppScreen.ADMIN
                                            },
                                            onNavigateToTickets = { type ->
                                                initialTicketType = type
                                                currentScreen = AppScreen.TICKETS
                                            },
                                            isDarkMode = isDarkMode,
                                            onToggleDarkMode = { isDarkMode = !isDarkMode }
                                        )
                                    }

                                    AppScreen.ADMIN -> {
                                        AdminServerScreen(
                                            onBack = {
                                                currentScreen = AppScreen.HOME
                                            }
                                        )
                                    }

                                    AppScreen.TARIFFS -> {
                                        TourTariffScreen(
                                            onNavigateBack = {
                                                currentScreen = AppScreen.HOME
                                            }
                                        )
                                    }

                                    AppScreen.TICKETS -> {
                                        TicketBookingScreen(
                                            initialType = initialTicketType,
                                            onNavigateBack = {
                                                currentScreen = AppScreen.HOME
                                            }
                                        )
                                    }

                                    AppScreen.MAP_3D -> {
                                        Map3DScreen(
                                            onNavigateToAr = { dest ->
                                                arDestination = dest
                                                currentScreen = AppScreen.AR_GUIDE
                                            },
                                            onNavigateToPlanner = { dest ->
                                                plannerInitialDestination = dest
                                                currentScreen = AppScreen.AI_PLANNER
                                            }
                                        )
                                    }

                                    AppScreen.BOOKING -> {
                                        StaffBookingScreen(
                                            onNavigateBack = {
                                                currentScreen = AppScreen.HOME
                                            }
                                        )
                                    }

                                    AppScreen.OFFERS -> {
                                        OffersScreen(
                                            onNavigateToMap = {
                                                currentScreen = AppScreen.MAP_3D
                                            }
                                        )
                                    }

                                    AppScreen.AI_PLANNER -> {
                                        TouristExpenseScreen(
                                            onNavigateBack = {
                                                currentScreen = AppScreen.HOME
                                            }
                                        )
                                    }

                                    AppScreen.AR_GUIDE -> {
                                        ArGuideScreen(
                                            destination = arDestination,
                                            onClose = {
                                                currentScreen = AppScreen.HOME
                                            },
                                            onSwitchDestination = { newDest ->
                                                arDestination = newDest
                                            }
                                        )
                                    }

                                    AppScreen.SHOWCASE -> {
                                        ShowcaseScreen(
                                            onNavigateToDestination = { dest ->
                                                arDestination = dest
                                                currentScreen = AppScreen.HOME
                                            },
                                            onNavigateToAr = { dest ->
                                                arDestination = dest
                                                currentScreen = AppScreen.AR_GUIDE
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showPermissionsDialog) {
                AppPermissionsDialog(
                    onAllGranted = { showPermissionsDialog = false },
                    onDismiss = { showPermissionsDialog = false }
                )
            }

            // Pinned in-app countdown banner when SOS is active and minimized
            if (SosSessionManager.isSosActive && !SosSessionManager.showActiveDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp)
                ) {
                    SosActiveFloatingBanner(
                        onReopen = { SosSessionManager.reopenDialog() },
                        onCancel = { SosSessionManager.endSos() }
                    )
                }
            }

            // Global active SOS countdown dialog
            if (SosSessionManager.showActiveDialog) {
                val currentGps = GpsLocationManager.currentLocation.value
                SosEmergencyDialog(
                    currentLat = currentGps?.latitude ?: 39.6548,
                    currentLon = currentGps?.longitude ?: 66.9757,
                    nearestLandmark = currentGps?.addressEstimate ?: "Registon Maydoni, Samarqand",
                    onDismiss = {
                        SosSessionManager.showActiveDialog = false
                    }
                )
            }

            // Prompt user for Guide-assigned number when Guide accepts the tourist
            if (showGuideNumberDialog) {
                com.example.ui.components.GuideAcceptedNumberDialog(
                    onDismiss = { showGuideNumberDialog = false },
                    onSubmittedSuccess = { showGuideNumberDialog = false }
                )
            }
        }
    }
}
}
}
