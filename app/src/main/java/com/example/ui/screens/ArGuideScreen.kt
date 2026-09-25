package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Exposure
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.GridOff
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.AppLanguage
import com.example.model.Destination
import com.example.model.SampleArFacts
import com.example.network.GeminiArVisionService
import com.example.network.GeminiMonumentAnalysis
import com.example.util.MinuteDataRotationManager
import com.example.ui.components.AdminAiActivationDialog
import com.example.ui.components.ArTargetReticle
import com.example.ui.components.AudioVoiceSettingsDialog
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPillBadge
import com.example.ui.components.GoldGradientButton
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.theme.MidnightCanvas
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RegistanBlue
import com.example.ui.theme.RegistanBlueDark
import com.example.ui.theme.SilkGold
import com.example.ui.theme.SilkGoldLight
import com.example.ui.theme.TextPrimaryLight
import com.example.ui.theme.TurquoiseTile
import com.example.util.ArAudioNarratorManager
import com.example.util.ArSpeechRecognitionManager
import com.example.util.UserSessionManager
import kotlinx.coroutines.launch
import java.io.InputStream
import java.util.concurrent.Executors
import kotlin.math.sin

enum class ArVisionMode(val labelKey: String, val subtitle: String) {
    REALTIME_AI_CAMERA("ar_mode_realtime", "Jonli AI Kamera (CameraX)"),
    ANCIENT_RECONSTRUCTION("ar_mode_ancient", "Qadimgi XV-XVIII asr ko'rinishi"),
    TIME_SPLIT("ar_mode_split", "Hozirgi vs Qadimgi Solishtirish"),
    HOLOGRAM_HUD("ar_mode_hologram", "3D Arxitektura Qatlamlari"),
    MODERN_CAM("ar_mode_modern", "Bugungi Real Holat (4K)")
}

class SplitLeftShape(private val splitRatio: Float) : Shape {
    override fun createOutline(
        size: androidx.compose.ui.geometry.Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width * splitRatio, 0f)
            lineTo(size.width * splitRatio, size.height)
            lineTo(0f, size.height)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun ArGuideScreen(
    destination: Destination? = null,
    onClose: (() -> Unit)? = null,
    onSwitchDestination: ((Destination) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    var currentLanguage by remember { mutableStateOf(AppLanguage.currentLanguage) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(false) }

    // Camera & Audio Permission State
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(
                context,
                "AI Kamera ishlashi uchun kamera ruxsati kerak",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        if (!hasAudioPermission) {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Camera Controls State
    var lensFacing by remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }
    var isTorchOn by remember { mutableStateOf(false) }
    var cameraControlRef by remember { mutableStateOf<CameraControl?>(null) }
    var imageCaptureRef by remember { mutableStateOf<ImageCapture?>(null) }
    var previewViewInstance by remember { mutableStateOf<PreviewView?>(null) }
    var historicBlendAlpha by remember { mutableFloatStateOf(0.0f) }

    // AI & Scanning States
    var isAiScanning by remember { mutableStateOf(false) }
    var geminiAnalysis by remember { mutableStateOf<GeminiMonumentAnalysis?>(null) }
    var capturedFrameBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var scanErrorMessage by remember { mutableStateOf<String?>(null) }
    var isCardExpanded by remember { mutableStateOf(true) }

    // TTS Audio Manager
    val audioNarrator = remember { ArAudioNarratorManager(context) }
    DisposableEffect(Unit) {
        onDispose {
            audioNarrator.shutdown()
        }
    }

    // Landmark selection
    val initialIndex = remember(destination) {
        if (destination != null) {
            val match = SampleArFacts.items.indexOfFirst { it.city.equals(destination.city, ignoreCase = true) }
            if (match >= 0) match else 0
        } else {
            0
        }
    }
    var selectedFactIndex by remember(destination) { mutableIntStateOf(initialIndex) }
    var currentArMode by remember { mutableStateOf(ArVisionMode.REALTIME_AI_CAMERA) }
    var autoRotateEveryMinute by remember { mutableStateOf(false) }

    // Live Minute State
    val minuteState by MinuteDataRotationManager.state.collectAsStateWithLifecycle()

    // Automatically update the active fact only if explicitly enabled and NOT in live camera mode
    LaunchedEffect(minuteState.minuteCount) {
        if (destination == null && autoRotateEveryMinute && currentArMode != ArVisionMode.REALTIME_AI_CAMERA) {
            selectedFactIndex = minuteState.currentArFactIndex.coerceIn(0, SampleArFacts.items.lastIndex)
            geminiAnalysis = null
        }
    }

    // Audio Playback States
    var isPlayingAudio by remember { mutableStateOf(false) }
    var speechProgress by remember { mutableFloatStateOf(0f) }
    var speechSpeed by remember { mutableFloatStateOf(0.88f) }
    var showSubtitles by remember { mutableStateOf(true) }
    var showVoiceSettingsDialog by remember { mutableStateOf(false) }
    var activeSpokenScript by remember { mutableStateOf("") }

    // Enhanced AI Camera States
    var selectedScanMode by remember { mutableStateOf("landmark") } // landmark, epigraphy, crafts, cuisine, translate
    var selectedZoomPreset by remember { mutableStateOf("1x") }
    var isGridOverlayVisible by remember { mutableStateOf(false) }
    var exposureCompensationIndex by remember { mutableIntStateOf(0) }
    var showQuestionDialog by remember { mutableStateOf(false) }
    var customQuestionInput by remember { mutableStateOf("") }
    var isQuestionAnswering by remember { mutableStateOf(false) }
    var answeredQuestionText by remember { mutableStateOf<String?>(null) }
    var showPostcardDialog by remember { mutableStateOf(false) }

    // Hands-Free Continuous Voice Assistant & Central Ancient Reconstruction States
    var isAlwaysListeningMicEnabled by remember { mutableStateOf(true) }
    var isMicListeningActive by remember { mutableStateOf(false) }
    var micRmsLevel by remember { mutableFloatStateOf(0f) }
    var liveSpokenQuery by remember { mutableStateOf("") }
    var isVoiceAnswering by remember { mutableStateOf(false) }
    var lastVoiceQuestion by remember { mutableStateOf("") }
    var showCenterAncientButton by remember { mutableStateOf(false) }
    var showAncientReconstructModal by remember { mutableStateOf(false) }
    var hasAutoSpokenInitial by remember { mutableStateOf(false) }
    var showAdminActivationDialog by remember { mutableStateOf(false) }
    var pendingAiFeatureName by remember { mutableStateOf("Gemini AI Vision & Ovozli Gid") }

    var speechRecognizerManager by remember { mutableStateOf<ArSpeechRecognitionManager?>(null) }

    var liveFrameQuality by remember {
        mutableStateOf(
            com.example.util.CameraFrameVisionAnalyzer.FrameQuality(
                brightnessPercent = 75,
                sharpnessPercent = 65,
                isOptimal = true,
                statusText = "⚡ Kadr tiniq & barqaror",
                adviceText = "AI tahlil uchun optimal masofa",
                dominantHues = listOf("Sharqona me'morchilik")
            )
        )
    }

    LaunchedEffect(hasCameraPermission, currentArMode) {
        if (hasCameraPermission && currentArMode == ArVisionMode.REALTIME_AI_CAMERA) {
            while (true) {
                kotlinx.coroutines.delay(2000)
                try {
                    val bmp = previewViewInstance?.bitmap
                    if (bmp != null) {
                        liveFrameQuality = com.example.util.CameraFrameVisionAnalyzer.evaluateFrameQuality(bmp)
                    }
                } catch (ignored: Exception) {}
            }
        }
    }

    fun applyZoomPreset(preset: String) {
        selectedZoomPreset = preset
        val linear = when (preset) {
            "0.5x" -> 0.0f
            "1x" -> 0.0f
            "2x" -> 0.25f
            "3x" -> 0.48f
            "5x" -> 0.75f
            "10x" -> 1.0f
            else -> 0.0f
        }
        cameraControlRef?.setLinearZoom(linear)
    }

    fun cycleExposure() {
        val nextExp = when (exposureCompensationIndex) {
            0 -> 2
            2 -> -2
            else -> 0
        }
        exposureCompensationIndex = nextExp
        cameraControlRef?.setExposureCompensationIndex(nextExp)
    }

    // Active Fact / Analysis resolution
    val activeFact = SampleArFacts.items[selectedFactIndex.coerceIn(0, SampleArFacts.items.lastIndex)]
    val activeMonumentName = geminiAnalysis?.monumentName ?: activeFact.monumentName
    val activeCity = geminiAnalysis?.city ?: activeFact.city
    val activeAncientEra = geminiAnalysis?.ancientEra ?: activeFact.ancientEraLabel
    val activeBuilder = geminiAnalysis?.builder ?: activeFact.builder
    val activeArchStyle = geminiAnalysis?.architectureStyle ?: activeFact.architecturalStyle
    val activeHeight = geminiAnalysis?.heightMeters ?: activeFact.heightMeters
    val localizedDescription = remember(activeFact, currentLanguage) {
        activeFact.getLocalizedDescription(currentLanguage)
    }
    val activeDescription = geminiAnalysis?.historicalDescription ?: localizedDescription
    val activeAncientReconstruction = geminiAnalysis?.ancientReconstructionDescription
        ?: "Qadimda ushbu obida moviy feruza koshinlar, tillarang naqshlar, baland minoralar va gavjum karvonsaroylar bilan o'ralgan bo'lgan."
    val localizedAudioScript = remember(activeFact, currentLanguage) {
        activeFact.getAudioScript(currentLanguage)
    }
    val activeAudioScript = geminiAnalysis?.audioGuideScript ?: localizedAudioScript
    val activeKeyFacts = geminiAnalysis?.keyFeatures ?: activeFact.keyFacts

    fun triggerAudioPlay(speak: Boolean, explicitText: String? = null) {
        if (speak) {
            showCenterAncientButton = false
            speechRecognizerManager?.setTtsSpeaking(true)
            val textToSpeak = explicitText ?: (if (activeSpokenScript.isNotBlank()) activeSpokenScript else activeAudioScript)
            activeSpokenScript = textToSpeak
            isPlayingAudio = true
            speechProgress = 0f

            audioNarrator.onWordPositionListener = { _, _, progress ->
                speechProgress = progress
            }

            audioNarrator.speak(
                text = textToSpeak,
                lang = currentLanguage,
                speechRate = speechSpeed
            ) { speaking, progress ->
                isPlayingAudio = speaking
                speechProgress = progress
                speechRecognizerManager?.setTtsSpeaking(speaking)
                // Ovozli ma'lumot tugaganidan so'ng ekran o'rtasida tugma chiqadi!
                if (!speaking && progress >= 0.90f) {
                    showCenterAncientButton = true
                }
            }
        } else {
            isPlayingAudio = false
            audioNarrator.stop()
            speechRecognizerManager?.setTtsSpeaking(false)
        }
    }

    // Hands-Free Continuous Voice Assistant Lifecycle
    DisposableEffect(hasAudioPermission, isAlwaysListeningMicEnabled, activeMonumentName) {
        val manager = ArSpeechRecognitionManager(
            context = context,
            onQuestionDetected = { question ->
                liveSpokenQuery = ""
                lastVoiceQuestion = question
                if (!UserSessionManager.isAiActivated(context)) {
                    val msg = "AI ovozli tahlil funksiyasi admin tomonidan ID faollashtirilgandan so'ng to'liq ishlaydi."
                    answeredQuestionText = msg
                    triggerAudioPlay(true, explicitText = msg)
                    pendingAiFeatureName = "Doimiy Mikrafon va AI Ovozli Javob"
                    showAdminActivationDialog = true
                    return@ArSpeechRecognitionManager
                }
                isVoiceAnswering = true
                coroutineScope.launch {
                    val currentBitmap = capturedFrameBitmap ?: previewViewInstance?.bitmap
                    val result = GeminiArVisionService.askFollowUpQuestion(
                        bitmap = currentBitmap,
                        monumentName = activeMonumentName,
                        question = question,
                        languageCode = currentLanguage.code,
                        context = context
                    )
                    isVoiceAnswering = false
                    val answer = result.getOrElse {
                        "Kechirasiz, ushbu obida bo'yicha savolingizga javob olishda xatolik yuz berdi: ${it.message}"
                    }
                    answeredQuestionText = answer
                    // Foydalanuvchi so'ragan savolga javob darhol OVOZDA ham aytib beriladi!
                    triggerAudioPlay(true, explicitText = answer)
                }
            },
            onPartialSpeech = { partial ->
                liveSpokenQuery = partial
            },
            onRmsChanged = { rms ->
                micRmsLevel = rms
            },
            onListeningStateChanged = { listening ->
                isMicListeningActive = listening
            }
        )
        speechRecognizerManager = manager
        if (hasAudioPermission && isAlwaysListeningMicEnabled) {
            manager.startListening()
        }
        onDispose {
            manager.destroy()
            speechRecognizerManager = null
        }
    }

    // Obida tanlanganda yoki yuklanganda avtomatik ovozli ma'lumot aytish
    LaunchedEffect(activeAudioScript) {
        activeSpokenScript = activeAudioScript
        showCenterAncientButton = false
        triggerAudioPlay(true, explicitText = activeAudioScript)
    }

    LaunchedEffect(selectedFactIndex) {
        showCenterAncientButton = false
        val script = activeFact.getAudioScript(currentLanguage)
        activeSpokenScript = script
        triggerAudioPlay(true, explicitText = script)
    }

    LaunchedEffect(currentLanguage) {
        val newScript = geminiAnalysis?.audioGuideScript ?: activeFact.getAudioScript(currentLanguage)
        activeSpokenScript = newScript
        if (isPlayingAudio) {
            triggerAudioPlay(true, explicitText = newScript)
        }
    }

    // Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (!UserSessionManager.isAiActivated(context)) {
                pendingAiFeatureName = "Galereya Rasmiga AI Tahlil O'tkazish"
                showAdminActivationDialog = true
                return@rememberLauncherForActivityResult
            }
            try {
                val stream: InputStream? = context.contentResolver.openInputStream(uri)
                val loadedBitmap = BitmapFactory.decodeStream(stream)
                stream?.close()
                if (loadedBitmap != null) {
                    capturedFrameBitmap = loadedBitmap
                    // Run real visual scan on this selected image without any forced assumptions
                    isAiScanning = true
                    scanErrorMessage = null
                    coroutineScope.launch {
                        val res = GeminiArVisionService.analyzeMonument(
                            bitmap = loadedBitmap,
                            languageCode = currentLanguage.code,
                            landmarkHint = null, // Strictly unbiased analysis of actual photo
                            scanMode = selectedScanMode,
                            context = context
                        )
                        isAiScanning = false
                        res.onSuccess { analysis ->
                            geminiAnalysis = analysis
                            isCardExpanded = true
                            triggerAudioPlay(true, explicitText = analysis.audioGuideScript)
                        }.onFailure { err ->
                            scanErrorMessage = "Xatolik: ${err.message}"
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Rasmni yuklashda xatolik yuz berdi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Execute real vision scan with real Camera frame (zero guessing, true visual inspection)
    val onPerformAiScan: () -> Unit = performAiScan@{
        if (!UserSessionManager.isAiActivated(context)) {
            pendingAiFeatureName = "Gemini AI Jonli Obida Tahlili"
            showAdminActivationDialog = true
            return@performAiScan
        }
        isAiScanning = true
        scanErrorMessage = null

        // 1. First priority: instant PreviewView live frame bitmap (100% reliable, zero latency, exact user view)
        val previewBmp = previewViewInstance?.bitmap
        if (previewBmp != null) {
            coroutineScope.launch {
                capturedFrameBitmap = previewBmp
                val res = GeminiArVisionService.analyzeMonument(
                    bitmap = previewBmp,
                    languageCode = currentLanguage.code,
                    landmarkHint = null, // Strictly unbiased visual recognition
                    scanMode = selectedScanMode,
                    context = context
                )
                isAiScanning = false
                res.onSuccess { analysis ->
                    geminiAnalysis = analysis
                    isCardExpanded = true
                    triggerAudioPlay(true, explicitText = analysis.audioGuideScript)
                }.onFailure { err ->
                    scanErrorMessage = "Xatolik: ${err.message}"
                }
            }
        } else {
            // 2. Secondary fallback: ImageCapture use-case
            val capture = imageCaptureRef
            if (capture != null) {
                val executor = Executors.newSingleThreadExecutor()
                capture.takePicture(
                    executor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val rotation = image.imageInfo.rotationDegrees
                            val bmp = image.toBitmap()
                            image.close()

                            val rotatedBmp = if (rotation != 0) {
                                val matrix = android.graphics.Matrix().apply { postRotate(rotation.toFloat()) }
                                Bitmap.createBitmap(bmp, 0, 0, bmp.width, bmp.height, matrix, true)
                            } else bmp

                            coroutineScope.launch {
                                capturedFrameBitmap = rotatedBmp
                                val res = GeminiArVisionService.analyzeMonument(
                                    bitmap = rotatedBmp,
                                    languageCode = currentLanguage.code,
                                    landmarkHint = null, // Strictly unbiased visual recognition
                                    scanMode = selectedScanMode,
                                    context = context
                                )
                                isAiScanning = false
                                res.onSuccess { analysis ->
                                    geminiAnalysis = analysis
                                    isCardExpanded = true
                                    triggerAudioPlay(true, explicitText = analysis.audioGuideScript)
                                }.onFailure { err ->
                                    scanErrorMessage = "Xatolik: ${err.message}"
                                }
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            coroutineScope.launch {
                                isAiScanning = false
                                scanErrorMessage = "Kameradan kadr olishda xatolik: ${exception.message}"
                            }
                        }
                    }
                )
            } else {
                isAiScanning = false
                scanErrorMessage = "Kamera kadri topilmadi. Kamera ruxsatini tekshiring."
            }
        }
    }

    // Scanner & Target Reticle Animations
    val infiniteTransition = rememberInfiniteTransition(label = "ArScanning")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ArScanLoop"
    )

    val reticlePulse by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ReticlePulse"
    )

    val eqWave by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "EqWave"
    )

    var splitSliderPosition by remember { mutableFloatStateOf(0.5f) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightCanvas)
    ) {
        val screenWidth = maxWidth

        // 1. CAMERA & HISTORICAL VIEWPORT LAYER
        when (currentArMode) {
            ArVisionMode.REALTIME_AI_CAMERA -> {
                if (hasCameraPermission) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Live CameraX Preview View
                        AndroidView(
                            factory = { ctx ->
                                val previewView = PreviewView(ctx).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    scaleType = PreviewView.ScaleType.FILL_CENTER
                                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                }
                                previewViewInstance = previewView

                                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                cameraProviderFuture.addListener({
                                    try {
                                        val cameraProvider = cameraProviderFuture.get()
                                        val preview = Preview.Builder().build().also {
                                            it.surfaceProvider = previewView.surfaceProvider
                                        }

                                        val imageCapture = ImageCapture.Builder()
                                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                            .build()
                                        imageCaptureRef = imageCapture

                                        cameraProvider.unbindAll()
                                        val camera: Camera = cameraProvider.bindToLifecycle(
                                            lifecycleOwner,
                                            lensFacing,
                                            preview,
                                            imageCapture
                                        )
                                        cameraControlRef = camera.cameraControl
                                    } catch (exc: Exception) {
                                        exc.printStackTrace()
                                    }
                                }, ContextCompat.getMainExecutor(ctx))

                                previewView
                            },
                            update = { _ ->
                                // Re-bind if lensFacing changed
                                try {
                                    val cameraProvider = ProcessCameraProvider.getInstance(context).get()
                                    val preview = Preview.Builder().build().also {
                                        it.surfaceProvider = previewViewInstance?.surfaceProvider
                                    }
                                    val imageCapture = ImageCapture.Builder()
                                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                        .build()
                                    imageCaptureRef = imageCapture
                                    cameraProvider.unbindAll()
                                    val camera = cameraProvider.bindToLifecycle(
                                        lifecycleOwner,
                                        lensFacing,
                                        preview,
                                        imageCapture
                                    )
                                    cameraControlRef = camera.cameraControl
                                } catch (ignored: Exception) {}
                            },
                            modifier = Modifier.fillMaxSize(),
                            onRelease = { view ->
                                previewViewInstance = null
                                try {
                                    val cameraProvider = ProcessCameraProvider.getInstance(view.context).get()
                                    cameraProvider.unbindAll()
                                } catch (ignored: Exception) {}
                            }
                        )

                        // Display selected photo from gallery if loaded
                        if (capturedFrameBitmap != null && geminiAnalysis != null) {
                            Image(
                                bitmap = capturedFrameBitmap!!.asImageBitmap(),
                                contentDescription = "Captured Frame",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alpha = 0.95f
                            )
                        }

                        // Architectural Rule-of-Thirds Grid Overlay
                        if (isGridOverlayVisible) {
                            CameraCompositionGrid(modifier = Modifier.fillMaxSize())
                        }

                        // Real-Time AR Historic Superimposition Layer (Blended with Live Camera)
                        if (historicBlendAlpha > 0.05f) {
                            Image(
                                painter = painterResource(id = activeFact.ancientImageRes),
                                contentDescription = "AR Historic Reconstruction",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop,
                                alpha = historicBlendAlpha
                            )
                        }

                        // Sci-Fi Live Camera Grid Overlay
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val bracketLen = 36.dp.toPx()
                            val bracketStroke = 2.5.dp.toPx()
                            val color = NeonGold.copy(alpha = 0.85f)

                            // Top Left
                            drawLine(color, Offset(24f, 190f), Offset(24f + bracketLen, 190f), bracketStroke)
                            drawLine(color, Offset(24f, 190f), Offset(24f, 190f + bracketLen), bracketStroke)

                            // Top Right
                            drawLine(color, Offset(w - 24f, 190f), Offset(w - 24f - bracketLen, 190f), bracketStroke)
                            drawLine(color, Offset(w - 24f, 190f), Offset(w - 24f, 190f + bracketLen), bracketStroke)

                            // Bottom Left
                            drawLine(color, Offset(24f, h - 220f), Offset(24f + bracketLen, h - 220f), bracketStroke)
                            drawLine(color, Offset(24f, h - 220f), Offset(24f, h - 220f - bracketLen), bracketStroke)

                            // Bottom Right
                            drawLine(color, Offset(w - 24f, h - 220f), Offset(w - 24f - bracketLen, h - 220f), bracketStroke)
                            drawLine(color, Offset(w - 24f, h - 220f), Offset(w - 24f, h - 220f - bracketLen), bracketStroke)
                        }
                    }
                } else {
                    // Camera Permission Request Card
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MidnightCanvas)
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            backgroundColor = Color(0xF206132D),
                            borderColor = NeonGold,
                            elevation = 16.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonGold, SilkGold))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Videocam,
                                        contentDescription = "Camera",
                                        tint = RegistanBlueDark,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                    text = "Jonli AI Kamera & AR Gid",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = NeonGold,
                                        fontSize = 18.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "O'zbekistonning qadimiy obidalarini real vaqtda jonli kamera orqali tanib olish, XV asr ko'rinishini ustiga joylashtirish va audio gid tinglash uchun kamera ruxsatini bering.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFFD6E2F0),
                                        lineHeight = 18.sp
                                    ),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                GoldGradientButton(
                                    text = "KAMERANI YOQISH ➔",
                                    onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            ArVisionMode.ANCIENT_RECONSTRUCTION -> {
                Image(
                    painter = painterResource(id = activeFact.ancientImageRes),
                    contentDescription = "Ancient Historical View",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(0x66001A3D),
                                    Color(0x33D4AF37),
                                    Color(0x55002B66),
                                    Color(0xDD07122C)
                                )
                            )
                        )
                )
            }

            ArVisionMode.TIME_SPLIT -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = activeFact.modernImageRes),
                        contentDescription = "Modern View",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Image(
                        painter = painterResource(id = activeFact.ancientImageRes),
                        contentDescription = "Ancient View",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(SplitLeftShape(splitSliderPosition)),
                        contentScale = ContentScale.Crop
                    )

                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val newPos = (splitSliderPosition + dragAmount.x / size.width).coerceIn(0.05f, 0.95f)
                                    splitSliderPosition = newPos
                                }
                            }
                    ) {
                        val splitX = size.width * splitSliderPosition
                        drawLine(
                            color = NeonGold,
                            start = Offset(splitX, 0f),
                            end = Offset(splitX, size.height),
                            strokeWidth = 4.dp.toPx()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = (screenWidth * splitSliderPosition) - 24.dp)
                            .size(48.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonGold, SilkGold)))
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Compare,
                            contentDescription = "Time Split Handle",
                            tint = RegistanBlueDark,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            ArVisionMode.HOLOGRAM_HUD -> {
                Image(
                    painter = painterResource(id = activeFact.ancientImageRes),
                    contentDescription = "Hologram View",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x77002A66))
                )
            }

            ArVisionMode.MODERN_CAM -> {
                Image(
                    painter = painterResource(id = activeFact.modernImageRes),
                    contentDescription = "Modern View",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // 2. TOP TELEMETRY HUD BAR
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // Row 1: Back, Status Badge, API Key, Language
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onClose?.invoke() },
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC07122C))
                        .border(1.dp, NeonGold.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close AR",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                GlassCard(
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xDD07122C),
                    borderColor = NeonGold,
                    elevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = NeonGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "AI VISION • ${activeFact.city.uppercase()} (1m ⟳ ${minuteState.lastUpdatedTimestamp})",
                            color = NeonGold,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    // Admin AI Activation Status / Dialog Button
                    IconButton(
                        onClick = {
                            pendingAiFeatureName = "Gemini AI & AR Gid Funksiyalari"
                            showAdminActivationDialog = true
                        },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (UserSessionManager.isAiActivated(context)) Color(0x3310B981) else Color(0x55EF4444))
                            .border(1.dp, if (UserSessionManager.isAiActivated(context)) Color(0xFF10B981) else Color(0xFFFF8A80), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (UserSessionManager.isAiActivated(context)) Icons.Filled.CheckCircle else Icons.Filled.Lock,
                            contentDescription = "Admin AI Aktivatsiya Holati",
                            tint = if (UserSessionManager.isAiActivated(context)) Color(0xFF10B981) else Color(0xFFFF8A80),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // API Key Settings Button
                    IconButton(
                        onClick = { showApiKeyDialog = true },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xDD07122C))
                            .border(1.dp, NeonGold.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Key,
                            contentDescription = "Gemini API Key",
                            tint = NeonGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Language Selector
                    GlassCard(
                        modifier = Modifier.clickable { showLanguageDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        backgroundColor = Color(0xDD07122C),
                        borderColor = TurquoiseTile.copy(alpha = 0.8f),
                        elevation = 6.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = currentLanguage.flag, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.code.uppercase(),
                                color = Color.White,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // AR Mode Selector Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(ArVisionMode.entries) { mode ->
                    val isSelected = currentArMode == mode
                    val title = when (mode) {
                        ArVisionMode.REALTIME_AI_CAMERA -> "📹 JONLI AI KAMERA"
                        ArVisionMode.ANCIENT_RECONSTRUCTION -> "⏳ QADIMGI (1400 AD)"
                        ArVisionMode.TIME_SPLIT -> "⚖️ VAQT SOLISHTIRISH"
                        ArVisionMode.HOLOGRAM_HUD -> "✨ HOLOGRAMMA"
                        ArVisionMode.MODERN_CAM -> "📸 BUGUNGI KUN"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) Brush.horizontalGradient(listOf(NeonGold, SilkGold))
                                else Brush.horizontalGradient(listOf(Color(0xCC001A3D), Color(0xCC002B66)))
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color.White else Color(0x44D4AF37),
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { currentArMode = mode }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("ar_mode_${mode.name.lowercase()}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            color = if (isSelected) TextPrimaryLight else Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }

            // AI Scan Category Selector Pills (Only in Realtime AI Camera mode)
            if (currentArMode == ArVisionMode.REALTIME_AI_CAMERA) {
                Spacer(modifier = Modifier.height(6.dp))
                val scanCategories = listOf(
                    Triple("landmark", "🏛️ Obida", "Me'moriy yodgorlik"),
                    Triple("epigraphy", "📜 Bitik & Xattotlik", "Arabcha/forscha yozuv"),
                    Triple("crafts", "🏺 Hunarmandchilik", "Koshin va amaliy san'at"),
                    Triple("cuisine", "🍲 Milliy Taomlar", "Milliy oshxona"),
                    Triple("relics", "🪙 Qadimiy Tangalar", "Numizmatika & Osori-atiqa"),
                    Triple("translate", "🌐 Tarjimon", "Lavha va matnlar"),
                    Triple("nature", "🌿 Tarixiy Tabiat", "Bog' va Chashmalar")
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(scanCategories) { (modeKey, title, _) ->
                        val isCatSelected = selectedScanMode == modeKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isCatSelected) Brush.horizontalGradient(listOf(TurquoiseTile, Color(0xFF007A87)))
                                    else Brush.horizontalGradient(listOf(Color(0xCC07142E), Color(0xCC0B1E40)))
                                )
                                .border(
                                    1.dp,
                                    if (isCatSelected) Color.White else Color(0x4400E5FF),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedScanMode = modeKey
                                    geminiAnalysis = null
                                    capturedFrameBitmap = null
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = title,
                                color = if (isCatSelected) Color.White else Color(0xFFB0D7E8),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isCatSelected) FontWeight.Black else FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // Real-time Live Neural Telemetry HUD Bar
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xCC07142E))
                        .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (liveFrameQuality.isOptimal) Color(0xFF00E676) else Color(0xFFFFB300))
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "✨ AI HUD: ${liveFrameQuality.statusText}",
                                color = if (liveFrameQuality.isOptimal) Color(0xFFE0F7FA) else Color(0xFFFFE082),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "☀️ ${liveFrameQuality.brightnessPercent}%",
                                color = SilkGoldLight,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.5.sp
                                )
                            )
                            Text(
                                text = "📐 ${liveFrameQuality.sharpnessPercent}%",
                                color = TurquoiseTile,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 9.5.sp
                                )
                            )
                            if (liveFrameQuality.brightnessPercent < 22) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x66FFD600))
                                        .clickable { isTorchOn = !isTorchOn; cameraControlRef?.enableTorch(isTorchOn) }
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "💡 Chiroq",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Landmark Focus Presets Strip + Auto-Minute Switcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(SampleArFacts.items.size) { index ->
                        val fact = SampleArFacts.items[index]
                        val isSelected = selectedFactIndex == index
                        val icon = when (index) {
                            0 -> "🏛️ Registon"
                            1 -> "🕌 Minorai Kalon"
                            2 -> "💎 Kalta Minor"
                            3 -> "👑 Go'ri Amir"
                            4 -> "🏰 Ark Qal'asi"
                            else -> "📍 ${fact.monumentName}"
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Brush.horizontalGradient(listOf(RegistanBlue, Color(0xFF003380)))
                                    else Brush.horizontalGradient(listOf(Color(0xCC000C1F), Color(0xCC00183B)))
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) NeonGold else Color(0x33D4AF37),
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedFactIndex = index
                                    geminiAnalysis = null
                                    capturedFrameBitmap = null
                                    val factScript = SampleArFacts.items[index].getAudioScript(currentLanguage)
                                    activeSpokenScript = factScript
                                    if (isPlayingAudio) {
                                        triggerAudioPlay(true, explicitText = factScript)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$icon (${fact.city})",
                                color = if (isSelected) NeonGold else Color.White.copy(alpha = 0.85f),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Auto rotate live countdown indicator (100% automatic)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x3300E676))
                        .border(1.dp, Color(0xFF00E676), RoundedCornerShape(10.dp))
                        .padding(horizontal = 7.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E676))
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "AVTO ${minuteState.secondsRemaining}s",
                            color = Color(0xFF00E676),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }
        }

        // 3A. FLOATING ZOOM CONTROLS (LEFT SIDE)
        if (currentArMode == ArVisionMode.REALTIME_AI_CAMERA && hasCameraPermission) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xD907142E))
                    .border(1.2.dp, Color(0x44D4AF37), RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.ZoomIn,
                    contentDescription = null,
                    tint = SilkGoldLight,
                    modifier = Modifier.size(16.dp)
                )
                listOf("0.5x", "1x", "2x", "3x", "5x", "10x").forEach { preset ->
                    val isSelected = selectedZoomPreset == preset
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) NeonGold else Color.Transparent
                            )
                            .clickable { applyZoomPreset(preset) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            color = if (isSelected) RegistanBlueDark else Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // 3B. FLOATING CAMERA ACTIONS (RIGHT SIDE)
        if (currentArMode == ArVisionMode.REALTIME_AI_CAMERA && hasCameraPermission) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Rule-of-Thirds Composition Grid Toggle
                IconButton(
                    onClick = { isGridOverlayVisible = !isGridOverlayVisible },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC07122C))
                        .border(1.2.dp, if (isGridOverlayVisible) NeonGold else Color(0x66FFFFFF), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isGridOverlayVisible) Icons.Filled.GridOn else Icons.Filled.GridOff,
                        contentDescription = "To'r chiziqlari (Grid)",
                        tint = if (isGridOverlayVisible) NeonGold else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Exposure Compensation Button
                IconButton(
                    onClick = { cycleExposure() },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC07122C))
                        .border(1.2.dp, if (exposureCompensationIndex != 0) TurquoiseTile else Color(0x66FFFFFF), CircleShape)
                ) {
                    Text(
                        text = if (exposureCompensationIndex > 0) "+${exposureCompensationIndex}" else if (exposureCompensationIndex < 0) "${exposureCompensationIndex}" else "EV",
                        color = if (exposureCompensationIndex != 0) TurquoiseTile else Color.White,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 10.sp)
                    )
                }

                // Photo Gallery Picker (Select existing monument photo)
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC07122C))
                        .border(1.2.dp, TurquoiseTile, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = "Pick Photo",
                        tint = TurquoiseTile,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Camera Switch (Front/Back)
                IconButton(
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else {
                            CameraSelector.DEFAULT_BACK_CAMERA
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC07122C))
                        .border(1.2.dp, NeonGold.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Cameraswitch,
                        contentDescription = "Switch Camera",
                        tint = NeonGold,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Torch Toggle
                IconButton(
                    onClick = {
                        isTorchOn = !isTorchOn
                        cameraControlRef?.enableTorch(isTorchOn)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC07122C))
                        .border(1.2.dp, if (isTorchOn) NeonGold else Color.White.copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isTorchOn) Icons.Filled.FlashOn else Icons.Filled.FlashOff,
                        contentDescription = "Torch",
                        tint = if (isTorchOn) NeonGold else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Audio Tour Floating Quick Toggle
                IconButton(
                    onClick = { triggerAudioPlay(!isPlayingAudio) },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            if (isPlayingAudio) Brush.linearGradient(listOf(NeonGold, SilkGold))
                            else Brush.linearGradient(listOf(Color(0xCC07122C), Color(0xCC07122C)))
                        )
                        .border(1.2.dp, NeonGold, CircleShape)
                ) {
                    Icon(
                        imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Audio Guide",
                        tint = if (isPlayingAudio) RegistanBlueDark else NeonGold,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 4. CENTER AR TARGET RETICLE
        if (currentArMode != ArVisionMode.TIME_SPLIT && capturedFrameBitmap == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(175.dp)
                    .scale(reticlePulse)
            ) {
                ArTargetReticle(
                    modifier = Modifier.fillMaxSize(),
                    color = if (geminiAnalysis != null) TurquoiseTile else NeonGold,
                    scanProgress = scanProgress
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xE607122C))
                        .border(1.dp, if (geminiAnalysis != null) TurquoiseTile else NeonGold, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (geminiAnalysis != null) {
                            "✓ REAL TAHLIL: ${activeMonumentName.uppercase()}"
                        } else {
                            "📷 KAMERANI OBIDAGA QARATING VA BOSING"
                        },
                        color = if (geminiAnalysis != null) TurquoiseTile else NeonGold,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // 5. PRIMARY PROMINENT AI SCANNER SHUTTER BUTTON (VISIBLE ABOVE BOTTOM BAR)
        if (currentArMode == ArVisionMode.REALTIME_AI_CAMERA && hasCameraPermission && !isAiScanning && geminiAnalysis == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 90.dp)
            ) {
                Button(
                    onClick = { onPerformAiScan() },
                    modifier = Modifier
                        .shadow(20.dp, RoundedCornerShape(32.dp), spotColor = NeonGold)
                        .height(58.dp)
                        .testTag("ai_camera_scan_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.horizontalGradient(listOf(NeonGold, SilkGold, TurquoiseTile)))
                            .border(2.dp, Color.White, RoundedCornerShape(32.dp))
                            .padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = RegistanBlueDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "✨ JONLI KADRNI TAHLIL QILISH",
                                color = RegistanBlueDark,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 6. FULLSCREEN SCANNING HUD ANIMATION
        AnimatedVisibility(
            visible = isAiScanning,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xEE07122C)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(90.dp),
                            color = NeonGold,
                            trackColor = RegistanBlue,
                            strokeWidth = 4.dp
                        )
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = TurquoiseTile,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "GEMINI AI OBIDANI TAHLIL QILMOQDA...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = NeonGold,
                            fontSize = 16.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Kamera oldidagi me'moriy qatlamlar, koshinlar, tarixiy asr va qadimgi ko'rinish tahlil qilinmoqda",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFD6E2F0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        // 7. BOTTOM AR HISTORICAL FACT & AUDIO GUIDE CARD (AFTER SCAN OR ON SELECTION)
        if (geminiAnalysis != null) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp, bottom = 85.dp)
            ) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ar_fact_card"),
                    shape = RoundedCornerShape(22.dp),
                    backgroundColor = Color(0xF406132D),
                    borderColor = if (geminiAnalysis?.isLiveAi == true) TurquoiseTile else NeonGold.copy(alpha = 0.9f),
                    elevation = 14.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(14.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Informative truth banner if the camera is pointing at an indoor room or everyday object
                        val isNotMonument = geminiAnalysis?.monumentName?.contains("Xona", ignoreCase = true) == true ||
                                geminiAnalysis?.monumentName?.contains("Ish stoli", ignoreCase = true) == true ||
                                geminiAnalysis?.monumentName?.contains("Aniqlanmadi", ignoreCase = true) == true ||
                                geminiAnalysis?.monumentName?.contains("Buyum", ignoreCase = true) == true ||
                                geminiAnalysis?.city?.contains("Kamera", ignoreCase = true) == true

                        if (isNotMonument) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 6.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x33FFB300))
                                    .border(1.dp, Color(0xFFFFB300), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFFFFB300),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Haqiqiy kamera tahlili: Kadrda tarixiy obida emas, xona/buyum aniqlandi. Ilova taxmin qilmaydi. Tarixiy obidani ko'rish uchun kamerani obidaga yoki uning fotosuratiga qarating.",
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp, lineHeight = 13.sp)
                                    )
                                }
                            }
                        }

                        // Header Row: Title, City, Era, Collapse Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(
                                                if (geminiAnalysis?.isLiveAi == true) TurquoiseTile.copy(alpha = 0.25f)
                                                else NeonGold.copy(alpha = 0.25f)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (geminiAnalysis?.isLiveAi == true) "✓ GEMINI AI VISION" else "✓ AR VISION GID",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Black,
                                                color = if (geminiAnalysis?.isLiveAi == true) TurquoiseTile else NeonGold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                    geminiAnalysis?.aiConfidenceScore?.let { score ->
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0x3300E676))
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "${score}% Ishonchlilik",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF00E676),
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = activeMonumentName,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        color = Color.White,
                                        fontSize = 17.sp
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlassPillBadge(
                                    text = if (activeHeight > 0) "H: ${activeHeight}m" else activeCity,
                                    accentColor = TurquoiseTile,
                                    textColor = TurquoiseTile
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { isCardExpanded = !isCardExpanded },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isCardExpanded) Icons.Filled.ExpandMore else Icons.Filled.ExpandLess,
                                        contentDescription = "Toggle Details",
                                        tint = Color.White
                                    )
                                }
                            }
                        }

                        if (isCardExpanded) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Davr: $activeAncientEra • $activeBuilder",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 11.sp,
                                    color = SilkGoldLight
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (currentArMode == ArVisionMode.ANCIENT_RECONSTRUCTION) activeAncientReconstruction else activeDescription,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    lineHeight = 15.sp,
                                    color = Color(0xFFD6E2F0),
                                    fontSize = 11.sp
                                ),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )

                            // Ancient Epigraphy / Calligraphy translation if detected
                            geminiAnalysis?.translationOrEpigraphy?.let { epigraphy ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x33D4AF37))
                                        .border(1.dp, NeonGold, RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.AutoAwesome,
                                                contentDescription = null,
                                                tint = NeonGold,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "📜 QADIMIY BITIK / XATTOTLIK TARJIMASI",
                                                color = NeonGold,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                        Spacer(Modifier.height(3.dp))
                                        Text(
                                            text = epigraphy,
                                            color = Color(0xFFFFF9E6),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Cultural significance details
                            geminiAnalysis?.culturalSignificance?.let { significance ->
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0x3300B4D8))
                                        .border(1.dp, TurquoiseTile, RoundedCornerShape(10.dp))
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.AutoAwesome,
                                                contentDescription = null,
                                                tint = TurquoiseTile,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = "🏺 MADANIY VA AMALIY SAN'AT AHAMIYATI",
                                                color = TurquoiseTile,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 9.sp
                                                )
                                            )
                                        }
                                        Spacer(Modifier.height(3.dp))
                                        Text(
                                            text = significance,
                                            color = Color(0xFFE8F7FF),
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.sp
                                            )
                                        )
                                    }
                                }
                            }

                            // Status note if fallback
                            geminiAnalysis?.statusNote?.let { note ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ℹ️ $note",
                                    color = TurquoiseTile,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick Tourist Question Suggestions
                            val quickTouristQuestions = listOf(
                                "Ichiga kirsa bo'ladimi?",
                                "Qanday afsonasi bor?",
                                "Eng zo'r rasm nuqtasi?"
                            )
                            androidx.compose.foundation.lazy.LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(quickTouristQuestions.size) { qIdx ->
                                    val qText = quickTouristQuestions[qIdx]
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0x3300E5FF))
                                            .border(0.8.dp, TurquoiseTile.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .clickable {
                                                customQuestionInput = qText
                                                showQuestionDialog = true
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "💡 $qText",
                                            color = Color(0xFFE0F7FA),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Interactive AI Visual Q&A, Postcard, and Tourist Passport Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Button(
                                    onClick = { showQuestionDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xDD0B3B60)),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.QuestionAnswer,
                                        contentDescription = null,
                                        tint = TurquoiseTile,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "SAVOL",
                                        color = TurquoiseTile,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }

                                Button(
                                    onClick = { showPostcardDialog = true },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xDD3A2A05)),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = null,
                                        tint = NeonGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "OTKRITKA",
                                        color = NeonGold,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        android.widget.Toast.makeText(
                                            context,
                                            "🌟 '$activeMonumentName' sizning Sayohat Pasportingizga muvaffaqiyatli saqlandi!",
                                            android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .weight(1.1f)
                                        .height(36.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xDD005F73)),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color(0xFF80ED99),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(Modifier.width(3.dp))
                                    Text(
                                        text = "PASSPORT",
                                        color = Color(0xFF80ED99),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action Buttons Row: Ancient Reconstruction & Reset Scan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        currentArMode = if (currentArMode == ArVisionMode.ANCIENT_RECONSTRUCTION) {
                                            ArVisionMode.REALTIME_AI_CAMERA
                                        } else {
                                            ArVisionMode.ANCIENT_RECONSTRUCTION
                                        }
                                    },
                                    modifier = Modifier
                                        .weight(1.3f)
                                        .height(38.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (currentArMode == ArVisionMode.ANCIENT_RECONSTRUCTION) NeonGold else Color(0xDD002A66)
                                    ),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.HistoryEdu,
                                        contentDescription = null,
                                        tint = if (currentArMode == ArVisionMode.ANCIENT_RECONSTRUCTION) RegistanBlueDark else NeonGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = if (currentArMode == ArVisionMode.ANCIENT_RECONSTRUCTION) "📹 JONLI KAMERA" else "⏳ QADIMGI ASL QIYOFASI",
                                        color = if (currentArMode == ArVisionMode.ANCIENT_RECONSTRUCTION) RegistanBlueDark else Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Button(
                                    onClick = {
                                        geminiAnalysis = null
                                        capturedFrameBitmap = null
                                        triggerAudioPlay(false)
                                        currentArMode = ArVisionMode.REALTIME_AI_CAMERA
                                    },
                                    modifier = Modifier
                                        .weight(0.9f)
                                        .height(38.dp)
                                        .testTag("reset_ai_scan_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = "YANGI SKAN",
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Audio Tour Bar
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        if (isPlayingAudio) Brush.horizontalGradient(listOf(Color(0x99003380), Color(0x99001A40)))
                                        else Brush.horizontalGradient(listOf(Color(0x66002A66), Color(0x6600183B)))
                                    )
                                    .border(
                                        1.dp,
                                        if (isPlayingAudio) NeonGold else Color(0x33D4AF37),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = { triggerAudioPlay(!isPlayingAudio) },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(NeonGold, SilkGold)))
                                ) {
                                    Icon(
                                        imageVector = if (isPlayingAudio) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                        contentDescription = "Audio Guide",
                                        tint = RegistanBlueDark,
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
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                                contentDescription = null,
                                                tint = if (isPlayingAudio) NeonGold else Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = "${currentLanguage.flag} ${currentLanguage.displayName} Audio Gid",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
                                                color = if (isPlayingAudio) NeonGold else Color.White
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0x33D4AF37))
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "MAX OVOZ 🔊",
                                                    color = NeonGold,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Black)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0x44FFFFFF))
                                                    .clickable {
                                                        speechSpeed = if (speechSpeed == 0.95f) 1.15f else 0.95f
                                                        if (isPlayingAudio) triggerAudioPlay(true)
                                                    }
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "${speechSpeed}x",
                                                    color = Color.White,
                                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(6.dp))

                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0x3300E676))
                                                    .clickable { showVoiceSettingsDialog = true }
                                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Filled.Tune,
                                                        contentDescription = null,
                                                        tint = Color(0xFF00E676),
                                                        modifier = Modifier.size(11.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(2.dp))
                                                    Text(
                                                        text = "Ovoz",
                                                        color = Color(0xFF00E676),
                                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    if (isPlayingAudio) {
                                        Canvas(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(14.dp)
                                                .padding(vertical = 2.dp)
                                        ) {
                                            val barCount = 28
                                            val barWidth = size.width / barCount
                                            for (i in 0 until barCount) {
                                                val wave = (sin(eqWave + i * 0.4f) + 1f) / 2f
                                                val barHeight = (4.dp.toPx() + wave * (size.height - 4.dp.toPx()))
                                                val startX = i * barWidth + barWidth * 0.2f
                                                val topY = (size.height - barHeight) / 2f
                                                drawLine(
                                                    color = if (i % 2 == 0) NeonGold else TurquoiseTile,
                                                    start = Offset(startX, topY),
                                                    end = Offset(startX, topY + barHeight),
                                                    strokeWidth = barWidth * 0.6f,
                                                    cap = StrokeCap.Round
                                                )
                                            }
                                        }
                                    } else {
                                        Text(
                                            text = "Jonli ekskursiya tinglash uchun ijroni bosing",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 9.sp,
                                                color = Color(0xFFA0B0C4)
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Live Translucent Animated Subtitle Teleprompter inside Card
                            val currentScriptText = if (activeSpokenScript.isNotBlank()) activeSpokenScript else activeAudioScript
                            if (showSubtitles && currentScriptText.isNotBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                TranslucentLiveAudioPrompter(
                                    text = currentScriptText,
                                    speechProgress = speechProgress,
                                    isPlaying = isPlayingAudio,
                                    speechSpeed = speechSpeed,
                                    onTogglePlay = { triggerAudioPlay(!isPlayingAudio) },
                                    onReplay = { triggerAudioPlay(true, explicitText = currentScriptText) },
                                    onToggleSpeed = {
                                        speechSpeed = if (speechSpeed <= 0.90f) 1.05f else 0.88f
                                        if (isPlayingAudio) triggerAudioPlay(true)
                                    },
                                    isCompact = false
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating translucent live subtitle teleprompter in AR Viewport when card is collapsed or before scan
        if ((geminiAnalysis == null || !isCardExpanded) && isPlayingAudio) {
            val floatingScriptText = if (activeSpokenScript.isNotBlank()) activeSpokenScript else activeAudioScript
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(start = 14.dp, end = 14.dp, bottom = 90.dp)
            ) {
                TranslucentLiveAudioPrompter(
                    text = floatingScriptText,
                    speechProgress = speechProgress,
                    isPlaying = isPlayingAudio,
                    speechSpeed = speechSpeed,
                    onTogglePlay = { triggerAudioPlay(!isPlayingAudio) },
                    onReplay = { triggerAudioPlay(true, explicitText = floatingScriptText) },
                    onToggleSpeed = {
                        speechSpeed = if (speechSpeed <= 0.90f) 1.05f else 0.88f
                        if (isPlayingAudio) triggerAudioPlay(true)
                    },
                    onClose = { triggerAudioPlay(false) },
                    isCompact = true
                )
            }
        }

        // 8. GEMINI API KEY SETUP DIALOG
        if (showApiKeyDialog) {
            var tempKey by remember { mutableStateOf(UserSessionManager.getCustomApiKey(context)) }
            AlertDialog(
                onDismissRequest = { showApiKeyDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Key, contentDescription = null, tint = NeonGold)
                        Spacer(Modifier.width(8.dp))
                        Text("Gemini AI API Kaliti", color = NeonGold, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        val currentActive = GeminiArVisionService.getApiKey(context)
                        Text(
                            text = if (currentActive.isNotBlank()) "✅ Holat: Kalit o'rnatilgan (Gemini AI Vision faol)"
                            else "ℹ️ Holat: Kalit o'rnatilmagan (Mahalliy AR tahlil rejimi ishlamoqda)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (currentActive.isNotBlank()) TurquoiseTile else Color(0xFFE2E8F0),
                                fontSize = 12.sp
                            )
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = tempKey,
                            onValueChange = { tempKey = it },
                            label = { Text("Gemini API Key (AIzaSy...)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                focusedLabelColor = NeonGold,
                                unfocusedBorderColor = Color.Gray
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "O'z shaxsiy Gemini API kalitingizni ai.google.dev orqali bepul olishingiz mumkin.",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.Gray,
                                fontSize = 10.sp
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            UserSessionManager.saveCustomApiKey(context, tempKey)
                            showApiKeyDialog = false
                            Toast.makeText(context, "API Kalit saqlandi!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold)
                    ) {
                        Text("Saqlash", color = RegistanBlueDark, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showApiKeyDialog = false }) {
                        Text("Bekor qilish", color = Color.White)
                    }
                },
                containerColor = Color(0xF207122C)
            )
        }

        // 9. LANGUAGE SELECTION DIALOG
        if (showLanguageDialog) {
            LanguageSelectorDialog(
                currentLanguage = currentLanguage,
                onLanguageSelected = { selectedLang ->
                    currentLanguage = selectedLang
                    AppLanguage.currentLanguage = selectedLang
                    showLanguageDialog = false
                    if (isPlayingAudio) {
                        triggerAudioPlay(true)
                    }
                },
                onDismiss = { showLanguageDialog = false }
            )
        }

        // 10. VOICE PERSONA & AMBIENT SETTINGS DIALOG
        if (showVoiceSettingsDialog) {
            AudioVoiceSettingsDialog(
                narratorManager = audioNarrator,
                onDismiss = {
                    showVoiceSettingsDialog = false
                    if (isPlayingAudio) {
                        triggerAudioPlay(true)
                    }
                },
                isDark = true
            )
        }

        // 11. INTERACTIVE AI VISUAL QUESTION DIALOG
        if (showQuestionDialog) {
            AlertDialog(
                onDismissRequest = { showQuestionDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.QuestionAnswer,
                            contentDescription = null,
                            tint = TurquoiseTile,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "AI Tarixchiga Savol Berish",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        )
                    }
                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Obida: $activeMonumentName",
                            color = SilkGoldLight,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(Modifier.height(8.dp))

                        if (answeredQuestionText != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x2200E5FF))
                                    .border(1.dp, TurquoiseTile, RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "Javob:",
                                        color = TurquoiseTile,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black)
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = answeredQuestionText!!,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 18.sp)
                                    )
                                }
                            }
                            Spacer(Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        triggerAudioPlay(true, explicitText = answeredQuestionText)
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Filled.PlayArrow, null, tint = RegistanBlueDark, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("Ovozda tinglash", color = RegistanBlueDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        answeredQuestionText = null
                                        customQuestionInput = ""
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33FFFFFF)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Yana savol", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        } else {
                            Text(
                                text = "Kamera ko'rib turgan obida yuzasidan xohlagan savolingizni bering:",
                                color = Color(0xFFCBD5E1),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(8.dp))

                            // Quick suggestion chips
                            val suggestions = listOf(
                                "Qurilishida qanday sirlar bor?",
                                "Devordagi bitiklarda nima yozilgan?",
                                "Nega moviy koshinlar ishlatilgan?"
                            )
                            suggestions.forEach { prompt ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0x33003B6F))
                                        .clickable { customQuestionInput = prompt }
                                        .padding(horizontal = 8.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "💡 $prompt",
                                        color = Color(0xFF90CAF9),
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                                    )
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customQuestionInput,
                                onValueChange = { customQuestionInput = it },
                                placeholder = { Text("Savolingizni yozing...", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TurquoiseTile,
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                        }
                    }
                },
                confirmButton = {
                    if (answeredQuestionText == null) {
                        Button(
                            onClick = {
                                if (customQuestionInput.isNotBlank()) {
                                    if (!UserSessionManager.isAiActivated(context)) {
                                        pendingAiFeatureName = "Obida haqida AI ga Savol Berish"
                                        showAdminActivationDialog = true
                                        return@Button
                                    }
                                    isQuestionAnswering = true
                                    coroutineScope.launch {
                                        val result = GeminiArVisionService.askFollowUpQuestion(
                                            bitmap = capturedFrameBitmap ?: previewViewInstance?.bitmap,
                                            monumentName = activeMonumentName,
                                            question = customQuestionInput,
                                            languageCode = currentLanguage.code,
                                            context = context
                                        )
                                        isQuestionAnswering = false
                                        answeredQuestionText = result.getOrElse { "Kechirasiz, savolga javob olishda xatolik yuz berdi: ${it.message}" }
                                    }
                                }
                            },
                            enabled = customQuestionInput.isNotBlank() && !isQuestionAnswering,
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseTile)
                        ) {
                            if (isQuestionAnswering) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = RegistanBlueDark, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Filled.Send, null, tint = RegistanBlueDark, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("So'rash", color = RegistanBlueDark, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showQuestionDialog = false }) {
                        Text("Yopish", color = Color.White)
                    }
                },
                containerColor = Color(0xF607142E)
            )
        }

        // 12. AR SOUVENIR POSTCARD DIALOG (E-Otkritka)
        if (showPostcardDialog) {
            AlertDialog(
                onDismissRequest = { showPostcardDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Share, null, tint = NeonGold, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "AR Sayyohlik E-Otkritkasi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = NeonGold
                            )
                        )
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0C1F42), Color(0xFF040A17))
                                )
                            )
                            .border(1.5.dp, NeonGold, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Stamp Badge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "O'ZBEKISTON BO'YLAB SAYOHAT",
                                color = SilkGoldLight,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    letterSpacing = 1.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonGold)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "AR PASSPORT",
                                    color = RegistanBlueDark,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 8.sp)
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Preview Image (Captured photo or fallback historic art)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0x66D4AF37), RoundedCornerShape(10.dp))
                        ) {
                            if (capturedFrameBitmap != null) {
                                Image(
                                    bitmap = capturedFrameBitmap!!.asImageBitmap(),
                                    contentDescription = "Postcard Photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = activeFact.ancientImageRes),
                                    contentDescription = "Postcard Art",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = activeMonumentName,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                        Text(
                            text = "📍 $activeCity • $activeAncientEra",
                            color = TurquoiseTile,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "\"${activeDescription.take(130)}...\"",
                            color = Color(0xFFD6E2F0),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 10.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(
                                    Intent.EXTRA_SUBJECT,
                                    "O'zbekiston AR Sayohati: $activeMonumentName"
                                )
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "Men hozirgina O'zbekiston AR Sayohat ilovasi orqali $activeMonumentName ($activeCity) obidasini kashf etdim!\n\nDavr: $activeAncientEra\n\n$activeDescription\n\n#UzbekistanTravel #ARGuide #Culture"
                                )
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "E-Otkritkani ulashish"))
                            showPostcardDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Share, null, tint = RegistanBlueDark, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Ulashish", color = RegistanBlueDark, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPostcardDialog = false }) {
                        Text("Yopish", color = Color.White)
                    }
                },
                containerColor = Color(0xF607142E)
            )
        }

        // 13. OVOZLI MA'LUMOT TUGAGANIDAN SO'NG EKRAN O'RTASIDA CHIQUVCHI MARKAZIY TUGMA
        AnimatedVisibility(
            visible = showCenterAncientButton,
            enter = fadeIn(tween(400)) + scaleIn(initialScale = 0.82f),
            exit = fadeOut(tween(300)) + scaleOut(targetScale = 0.82f)
        ) {
            CenterAncientReconstructionButton(
                monumentName = activeMonumentName,
                onViewAncient = {
                    if (!UserSessionManager.isAiActivated(context)) {
                        pendingAiFeatureName = "Qadimgi Butun Holatini Qayta Tiklash"
                        showAdminActivationDialog = true
                    } else {
                        showCenterAncientButton = false
                        showAncientReconstructModal = true
                    }
                },
                onDismiss = {
                    showCenterAncientButton = false
                }
            )
        }

        // 14. QADIMGI ASL BUTUN HOLATINI (BUZILMASDAN OLDINGI DAVRINI) KO'RSATUVCHI MODAL
        if (showAncientReconstructModal) {
            AncientReconstructionViewerModal(
                monumentName = activeMonumentName,
                city = activeCity,
                ancientEra = activeAncientEra,
                ancientImageRes = activeFact.ancientImageRes,
                modernImageRes = activeFact.modernImageRes,
                ancientDescription = activeAncientReconstruction,
                onClose = { showAncientReconstructModal = false },
                onSwitchToCameraAr = {
                    showAncientReconstructModal = false
                    currentArMode = ArVisionMode.ANCIENT_RECONSTRUCTION
                },
                onPlayAudio = {
                    triggerAudioPlay(true, explicitText = activeAncientReconstruction)
                }
            )
        }

        // 15. HAR DOIM YONIQ MIKROFON & JONLI OVOZLI SAVOL-JAVOB HUD
        AlwaysOnVoiceGuideOverlay(
            isMicListening = isMicListeningActive,
            micRms = micRmsLevel,
            isEnabled = isAlwaysListeningMicEnabled,
            liveSpokenQuery = liveSpokenQuery,
            isAnswering = isVoiceAnswering,
            lastQuestion = lastVoiceQuestion,
            answeredText = answeredQuestionText,
            onToggleEnabled = {
                val newState = !isAlwaysListeningMicEnabled
                isAlwaysListeningMicEnabled = newState
                speechRecognizerManager?.setEnabled(newState)
            },
            onDismissAnswer = {
                answeredQuestionText = null
            },
            onReplayAnswer = {
                answeredQuestionText?.let { text ->
                    triggerAudioPlay(true, explicitText = text)
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 44.dp, start = 12.dp, end = 12.dp)
        )

        // 16. ADMIN AI AKTIVATSIYA TALAB QILUVCHI MODAL DIALOG
        if (showAdminActivationDialog) {
            AdminAiActivationDialog(
                onDismiss = { showAdminActivationDialog = false },
                onActivatedSuccess = {
                    showAdminActivationDialog = false
                    Toast.makeText(context, "✅ AI muvaffaqiyatli faollashtirildi!", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

/**
 * Architectural Rule-of-Thirds Composition Grid Overlay with Corner Guides.
 */
@Composable
fun CameraCompositionGrid(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val gridColor = Color(0x33FFFFFF)
        val cornerColor = NeonGold.copy(alpha = 0.7f)
        val stroke = 1.dp.toPx()

        // 1/3 and 2/3 Vertical lines
        drawLine(gridColor, Offset(w / 3f, 0f), Offset(w / 3f, h), strokeWidth = stroke)
        drawLine(gridColor, Offset(2f * w / 3f, 0f), Offset(2f * w / 3f, h), strokeWidth = stroke)

        // 1/3 and 2/3 Horizontal lines
        drawLine(gridColor, Offset(0f, h / 3f), Offset(w, h / 3f), strokeWidth = stroke)
        drawLine(gridColor, Offset(0f, 2f * h / 3f), Offset(w, 2f * h / 3f), strokeWidth = stroke)

        // Center cross intersection marks
        val intersections = listOf(
            Offset(w / 3f, h / 3f),
            Offset(2f * w / 3f, h / 3f),
            Offset(w / 3f, 2f * h / 3f),
            Offset(2f * w / 3f, 2f * h / 3f)
        )
        val crossLen = 8.dp.toPx()
        intersections.forEach { center ->
            drawLine(cornerColor, Offset(center.x - crossLen, center.y), Offset(center.x + crossLen, center.y), strokeWidth = 1.5.dp.toPx())
            drawLine(cornerColor, Offset(center.x, center.y - crossLen), Offset(center.x, center.y + crossLen), strokeWidth = 1.5.dp.toPx())
        }
    }
}

/**
 * Translucent live moving teleprompter / subtitle overlay that smoothly moves in sync with the audio guide narration.
 * Highlights words in glowing gold as they are spoken, keeps spoken words in crisp white, and unread words translucent.
 */
@Composable
fun TranslucentLiveAudioPrompter(
    text: String,
    speechProgress: Float,
    isPlaying: Boolean,
    speechSpeed: Float,
    onTogglePlay: () -> Unit,
    onReplay: () -> Unit,
    onToggleSpeed: () -> Unit,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    if (text.isBlank()) return

    val words = remember(text) { text.split(Regex("\\s+")).filter { it.isNotBlank() } }
    val totalWords = words.size.coerceAtLeast(1)
    val currentWordIndex = (speechProgress * totalWords).toInt().coerceIn(0, totalWords - 1)

    // Calculate time metrics based on speech rate
    val totalSec = remember(words.size, speechSpeed) { ((words.size / (2.6f * speechSpeed)).toInt()).coerceAtLeast(4) }
    val elapsedSec = (speechProgress * totalSec).toInt().coerceIn(0, totalSec)
    val timeLabel = String.format(java.util.Locale.ROOT, "%02d:%02d / %02d:%02d", elapsedSec / 60, elapsedSec % 60, totalSec / 60, totalSec % 60)

    val scrollState = rememberScrollState()

    // Smooth auto-scroll as speech advances
    LaunchedEffect(currentWordIndex) {
        if (totalWords > 0 && scrollState.maxValue > 0) {
            val targetScroll = (scrollState.maxValue * (currentWordIndex.toFloat() / totalWords)).toInt()
            scrollState.animateScrollTo(targetScroll)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xD9040F28))
            .border(
                width = 1.2.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        NeonGold.copy(alpha = if (isPlaying) 0.9f else 0.5f),
                        TurquoiseTile.copy(alpha = if (isPlaying) 0.9f else 0.5f)
                    )
                ),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Column {
            // Header: Live Indicator, Title, Time, and Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isPlaying) Color(0xFF00E676) else Color(0xFF94A3B8))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "🎙️ JONLI AUDIO GID" else "AUDIO MATN",
                        color = if (isPlaying) NeonGold else Color(0xFFCBD5E1),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = timeLabel,
                        color = TurquoiseTile,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Speed button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x33FFFFFF))
                            .clickable { onToggleSpeed() }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${speechSpeed}x",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Replay button
                    IconButton(
                        onClick = onReplay,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Replay,
                            contentDescription = "Qaytadan eshitish",
                            tint = NeonGold,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Play / Pause button
                    IconButton(
                        onClick = onTogglePlay,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "To'xtatish" else "Boshlash",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    if (onClose != null) {
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier.size(22.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Yopish",
                                tint = Color(0x99FFFFFF),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle text area with progressive word highlighting
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isCompact) 68.dp else 98.dp)
                    .verticalScroll(scrollState)
            ) {
                val annotatedText = buildAnnotatedString {
                    words.forEachIndexed { index, word ->
                        when {
                            index < currentWordIndex -> {
                                // Spoken words: crisp clear white
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xFFF1F5F9),
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                ) {
                                    append(word)
                                    append(" ")
                                }
                            }
                            index == currentWordIndex -> {
                                // Current active spoken word: vivid glowing gold with background chip
                                withStyle(
                                    style = SpanStyle(
                                        color = NeonGold,
                                        background = Color(0x55D4AF37),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp
                                    )
                                ) {
                                    append(word)
                                    append(" ")
                                }
                            }
                            else -> {
                                // Upcoming unspoken words: translucent soft silver
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0x66CBD5E1),
                                        fontWeight = FontWeight.Light,
                                        fontSize = 13.sp
                                    )
                                ) {
                                    append(word)
                                    append(" ")
                                }
                            }
                        }
                    }
                }

                Text(
                    text = annotatedText,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 19.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Smooth timeline progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0x33FFFFFF))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(speechProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(Brush.horizontalGradient(listOf(NeonGold, TurquoiseTile)))
                )
            }
        }
    }
}

/**
 * Centered Action Button that appears in the center of the screen when audio narration completes.
 * Invites the user to explore the monument's pristine ancient reconstruction (before destruction).
 */
@Composable
fun CenterAncientReconstructionButton(
    monumentName: String,
    onViewAncient: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CenterPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "GlowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0x7A000000))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onViewAncient
                )
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xF5081938),
                                Color(0xF5000C24)
                            )
                        )
                    )
                    .border(
                        width = 2.5.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                NeonGold.copy(alpha = glowAlpha),
                                TurquoiseTile.copy(alpha = glowAlpha),
                                NeonGold.copy(alpha = glowAlpha)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(horizontal = 22.dp, vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Top glowing badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x33D4AF37))
                            .border(1.dp, NeonGold, RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "✨ OVOZLI GID TUGADI",
                            color = NeonGold,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp,
                                letterSpacing = 1.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Center Emblem
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(NeonGold, SilkGold, Color(0xFF8A6D1C))
                                )
                            )
                            .border(2.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HistoryEdu,
                            contentDescription = null,
                            tint = RegistanBlueDark,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "QADIMGI ASL BUTUN HOLATINI KO'RISH",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 16.sp
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "$monumentName ning qadimda (qurilgan vaqtida) buzilmagan butun holatini va me'moriy hashamatini ko'ring",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFD6E2F0),
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = onViewAncient,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Compare,
                                contentDescription = null,
                                tint = RegistanBlueDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "BUTUN QIYOFASINI OCHISH ➔",
                                color = RegistanBlueDark,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Full-screen Interactive Modal showing the original ancient intact state of the monument
 * prior to destruction, earthquake damage, or decay, with interactive comparison.
 */
@Composable
fun AncientReconstructionViewerModal(
    monumentName: String,
    city: String,
    ancientEra: String,
    ancientImageRes: Int,
    modernImageRes: Int,
    ancientDescription: String,
    onClose: () -> Unit,
    onSwitchToCameraAr: () -> Unit,
    onPlayAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var comparisonSplitRatio by remember { mutableFloatStateOf(0.85f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xF6040C1A))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {}
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(NeonGold, SilkGold))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.HistoryEdu,
                            contentDescription = null,
                            tint = RegistanBlueDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "ASL QADIMGI BUTUN HOLATI",
                            color = NeonGold,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = "$monumentName • $city ($ancientEra)",
                            color = Color(0xFFC2D9ED),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                        )
                    }
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0x33FFFFFF))
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Yopish",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Interactive Image Comparison View
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .border(2.dp, NeonGold, RoundedCornerShape(20.dp))
            ) {
                // Background: Modern View
                Image(
                    painter = painterResource(id = modernImageRes),
                    contentDescription = "Modern View",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Foreground: Ancient Pristine Reconstruction with Split Clip
                Image(
                    painter = painterResource(id = ancientImageRes),
                    contentDescription = "Ancient Pristine View",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(SplitLeftShape(comparisonSplitRatio)),
                    contentScale = ContentScale.Crop
                )

                // Split Divider Line
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val dividerX = maxWidth * comparisonSplitRatio
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight()
                            .padding(start = dividerX - 1.5.dp)
                            .background(Color.White)
                    )
                }

                // Badges on Left & Right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xDD001C3D))
                        .border(1.dp, NeonGold, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏛️ QURILGAN DAVRI (BUTUN)",
                        color = NeonGold,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp)
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xDD2A1400))
                        .border(1.dp, Color(0xFFFFB74D), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "🏢 HOZIRGI HOLAT",
                        color = Color(0xFFFFD54F),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Comparison Slider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x4400193D))
                    .border(1.dp, Color(0x33D4AF37), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "⏪ Hozirgi holati",
                        color = Color(0xFFC0D8EB),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                    )
                    Text(
                        text = "Vaqt Solishtiruvi (${(comparisonSplitRatio * 100).toInt()}%)",
                        color = NeonGold,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.5.sp)
                    )
                    Text(
                        text = "Qadimgi butun holati ⏩",
                        color = NeonGold,
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
                    )
                }
                Slider(
                    value = comparisonSplitRatio,
                    onValueChange = { comparisonSplitRatio = it },
                    valueRange = 0.05f..0.95f,
                    colors = SliderDefaults.colors(
                        thumbColor = NeonGold,
                        activeTrackColor = NeonGold,
                        inactiveTrackColor = Color(0x55FFFFFF)
                    )
                )
            }

            Spacer(Modifier.height(10.dp))

            // Historical Architectural Context & Destruction Facts Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xD9061633))
                    .border(1.dp, TurquoiseTile.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = TurquoiseTile,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "ME'MORIY QAYTA TIKLANISH VA BUZILISH TARIXI",
                            color = TurquoiseTile,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = ancientDescription,
                        color = Color(0xFFE9F4FF),
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            // Action Buttons: Realtime AR Overlay & Audio Explain
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onSwitchToCameraAr,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGold)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Videocam,
                        contentDescription = null,
                        tint = RegistanBlueDark,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "KAMERADA KO'RISH",
                        color = RegistanBlueDark,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    )
                }

                Button(
                    onClick = onPlayAudio,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x4400E5FF))
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null,
                        tint = TurquoiseTile,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "OVOZDA ESHITISH",
                        color = TurquoiseTile,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Hands-Free Always-On Microphone Overlay:
 * Continuously listens to user voice queries regarding the monument in view,
 * renders live partial speech, query processing status, and AI responses.
 */
@Composable
fun AlwaysOnVoiceGuideOverlay(
    isMicListening: Boolean,
    micRms: Float,
    isEnabled: Boolean,
    liveSpokenQuery: String,
    isAnswering: Boolean,
    lastQuestion: String,
    answeredText: String?,
    onToggleEnabled: () -> Unit,
    onDismissAnswer: () -> Unit,
    onReplayAnswer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MicWave")
    val waveAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveAnim"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Status Capsule Indicator
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xE6071838))
                .border(
                    1.2.dp,
                    if (isEnabled) {
                        if (isMicListening) NeonGold else TurquoiseTile
                    } else Color(0x55FFFFFF),
                    RoundedCornerShape(20.dp)
                )
                .clickable(onClick = onToggleEnabled)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isEnabled) Icons.Filled.Mic else Icons.Filled.MicOff,
                contentDescription = "Microphone",
                tint = if (isEnabled) {
                    if (isMicListening) NeonGold else TurquoiseTile
                } else Color.LightGray,
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = if (isEnabled) {
                    if (liveSpokenQuery.isNotBlank()) "Eshitilmoqda..."
                    else if (isAnswering) "Javob tayyorlanmoqda..."
                    else "🎙️ MIKROFON YONIQ (Istalgan savolni bering)"
                } else "Mikrofon o'chiq",
                color = if (isEnabled) Color.White else Color.LightGray,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp
                )
            )

            if (isEnabled) {
                Spacer(Modifier.width(6.dp))
                // Animated live sound wave equalizer
                Canvas(modifier = Modifier.size(width = 24.dp, height = 12.dp)) {
                    val barWidth = size.width / 4f
                    for (i in 0 until 4) {
                        val factor = (sin(waveAnim + i * 1.2f) + 1f) / 2f
                        val barH = (3.dp.toPx() + factor * (size.height - 3.dp.toPx()))
                        val x = i * barWidth + barWidth * 0.2f
                        val y = (size.height - barH) / 2f
                        drawLine(
                            color = if (i % 2 == 0) NeonGold else TurquoiseTile,
                            start = Offset(x, y),
                            end = Offset(x, y + barH),
                            strokeWidth = barWidth * 0.6f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // Live Spoken Query Transcript Banner
        if (liveSpokenQuery.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xF000214D))
                    .border(1.5.dp, NeonGold, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = NeonGold,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "«$liveSpokenQuery»",
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }
        }

        // Processing Query Banner
        if (isAnswering && liveSpokenQuery.isBlank()) {
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xF0081C3D))
                    .border(1.2.dp, TurquoiseTile, RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        color = TurquoiseTile,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "AI javob tayyorlamoqda: «$lastQuestion»",
                        color = TurquoiseTile,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // Answer Card (Spoken automatically via TTS)
        if (answeredText != null && !isAnswering && liveSpokenQuery.isBlank()) {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xF5061633))
                    .border(1.5.dp, NeonGold, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.AutoAwesome,
                                contentDescription = null,
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "AI GID JAVOBI (Ovozda eshittirilmoqda)",
                                color = NeonGold,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        IconButton(
                            onClick = onDismissAnswer,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Yopish",
                                tint = Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = answeredText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onReplayAnswer,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = TurquoiseTile,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Qayta tinglash",
                                color = TurquoiseTile,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

