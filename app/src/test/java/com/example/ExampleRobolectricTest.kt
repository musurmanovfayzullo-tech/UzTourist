package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SampleOffers
import com.example.model.ServiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Uz Tourist", appName)
  }

  @Test
  fun `verify offers data has taxi dining and lodging`() {
    assertTrue(SampleOffers.taxiList.isNotEmpty())
    assertTrue(SampleOffers.diningList.isNotEmpty())
    assertTrue(SampleOffers.lodgingList.isNotEmpty())
    assertEquals(4, ServiceType.values().size)
  }

  @Test
  fun `verify sos session manager 5 minute countdown and active state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.util.SosSessionManager.startSos(
      context = context,
      name = "Test Tourist",
      phone = "+998901234567",
      lat = 39.6548,
      lon = 66.9757,
      address = "Registon Maydoni",
      reason = "Test Emergency"
    )
    assertTrue(com.example.util.SosSessionManager.isSosActive)
    assertEquals(300, com.example.util.SosSessionManager.remainingSeconds)
    assertEquals("05:00", com.example.util.SosSessionManager.formattedRemainingTime())
    
    // Clean up
    com.example.util.SosSessionManager.endSos()
    assertTrue(!com.example.util.SosSessionManager.isSosActive)
  }

  @Test
  fun `verify safetour radar bearing and direction description`() {
    val bearingNorth = com.example.util.SafeTourRadarManager.calculateBearing(41.0, 69.0, 42.0, 69.0)
    assertEquals(0f, bearingNorth, 2f)
    assertEquals("Shimol tomon ⬆️", com.example.util.SafeTourRadarManager.getDirectionDescriptionUz(0f))
    assertEquals("Sharq tomon ➡️", com.example.util.SafeTourRadarManager.getDirectionDescriptionUz(90f))
    assertEquals("Janub tomon ⬇️", com.example.util.SafeTourRadarManager.getDirectionDescriptionUz(180f))
    assertEquals("G'arb tomon ⬅️", com.example.util.SafeTourRadarManager.getDirectionDescriptionUz(270f))
  }

  @Test
  fun `verify safetour geofence alert logging`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.util.SafeTourRadarManager.logAlertBreach(
      context = context,
      touristName = "Test Sayyoh",
      distanceMeters = 95,
      customStatus = "Xavfli uzoqlashish"
    )
    val logs = com.example.util.SafeTourRadarManager.getAlertLogs(context)
    assertTrue(logs.any { it.touristName == "Test Sayyoh" && it.distanceMeters == 95 })
  }

  @Test
  fun `verify sos emergency manager record and resolution`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val emergency = com.example.util.SosEmergencyRecord(
      id = "SOS-TEST-999",
      touristName = "Akmal Vohidov",
      touristPhone = "+998901112233",
      latitude = 41.3111,
      longitude = 69.2797,
      address = "Amir Temur Xiyoboni",
      reason = "Tez tibbiy yordam zarur",
      timestamp = "Bugun, 15:30",
      isResolved = false
    )
    com.example.util.SosEmergencyManager.recordEmergency(context, emergency)
    val records = com.example.util.SosEmergencyManager.getAllEmergencies(context)
    assertTrue(records.any { it.id == "SOS-TEST-999" && !it.isResolved })

    // Resolve
    com.example.util.SosEmergencyManager.resolveEmergency(context, "SOS-TEST-999")
    val updatedRecords = com.example.util.SosEmergencyManager.getAllEmergencies(context)
    val resolved = updatedRecords.firstOrNull { it.id == "SOS-TEST-999" }
    assertTrue(resolved != null && resolved.isResolved)
  }

  @Test
  fun `verify user activation check without demo users`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val userId = com.example.util.UserSessionManager.getUserId(context)
    assertTrue(userId.startsWith("UZ-TOUR-"))

    val allRecords = com.example.util.UserSessionManager.getAllUserRecords(context)
    // Only device user should be in records (no hardcoded sample users)
    assertTrue(allRecords.isNotEmpty())
    assertTrue(allRecords.all { it.userId == userId || it.isActivated })
  }

  @Test
  fun `verify tourist wallet default balance and calculations`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val userId = com.example.util.UserSessionManager.getUserId(context)
    val wallet = com.example.util.TouristWalletManager.getOrCreateWallet(context, userId, "Sayyoh")
    assertTrue(wallet.balanceUzs >= 0.0)
    assertTrue(wallet.balanceUsd >= 0.0)
    assertTrue(wallet.cardNumber.isNotBlank())
  }

  @Test
  fun `verify camera frame vision analyzer quality evaluation and multi-mode analysis`() {
    val bitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)
    val pixels = IntArray(100 * 100) { android.graphics.Color.rgb(0, 180, 216) }
    bitmap.setPixels(pixels, 0, 100, 0, 0, 100, 100)

    val quality = com.example.util.CameraFrameVisionAnalyzer.evaluateFrameQuality(bitmap)
    assertTrue(quality.brightnessPercent >= 0)

    // Epigraphy mode
    val epigraphyAnalysis = com.example.util.CameraFrameVisionAnalyzer.buildTruthfulLocalAnalysis(
      bitmap = bitmap,
      langCode = "uz",
      scanMode = "epigraphy"
    )
    assertEquals("epigraphy", epigraphyAnalysis.scanCategory)
    assertTrue(epigraphyAnalysis.monumentName.isNotBlank())

    // Crafts mode
    val craftsAnalysis = com.example.util.CameraFrameVisionAnalyzer.buildTruthfulLocalAnalysis(
      bitmap = bitmap,
      langCode = "uz",
      scanMode = "crafts"
    )
    assertEquals("crafts", craftsAnalysis.scanCategory)

    // Cuisine mode
    val cuisineAnalysis = com.example.util.CameraFrameVisionAnalyzer.buildTruthfulLocalAnalysis(
      bitmap = bitmap,
      langCode = "uz",
      scanMode = "cuisine"
    )
    assertEquals("cuisine", cuisineAnalysis.scanCategory)
  }

  @Test
  fun `verify tour guide whisper manager presets and role switching`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.util.TourGuideWhisperManager.initPreferences(context)

    // Verify Presets
    assertEquals(4, com.example.model.WhisperAudioPreset.values().size)
    com.example.util.TourGuideWhisperManager.selectPreset(context, com.example.model.WhisperAudioPreset.ULTRA_HD)
    assertEquals(com.example.model.WhisperAudioPreset.ULTRA_HD, com.example.util.TourGuideWhisperManager.selectedPreset)

    // Role switching
    com.example.util.TourGuideWhisperManager.startBroadcasting(context)
    assertEquals(com.example.model.WhisperUserRole.GUIDE_BROADCASTER, com.example.util.TourGuideWhisperManager.userRole)
    assertTrue(com.example.util.TourGuideWhisperManager.isBroadcasting)

    // Mute toggle
    val wasMuted = com.example.util.TourGuideWhisperManager.isMuted
    com.example.util.TourGuideWhisperManager.toggleMute()
    assertEquals(!wasMuted, com.example.util.TourGuideWhisperManager.isMuted)

    // Volume clamping
    com.example.util.TourGuideWhisperManager.setVolume(context, 3.5f)
    assertEquals(2.5f, com.example.util.TourGuideWhisperManager.volumeGain, 0.01f)

    com.example.util.TourGuideWhisperManager.setVolume(context, 0.2f)
    assertEquals(0.5f, com.example.util.TourGuideWhisperManager.volumeGain, 0.01f)

    // Proximity alert verification (Pierre Dubois at 34m triggers alert)
    com.example.util.TourGuideWhisperManager.checkProximityAlert(context)
    assertTrue(com.example.util.TourGuideWhisperManager.proximityAlertActive)

    // Hand raising & lowering
    com.example.util.TourGuideWhisperManager.raiseHand(context, "Elena Rostova")
    val elena = com.example.util.TourGuideWhisperManager.groupMembers.find { it.name == "Elena Rostova" }
    assertTrue(elena != null && elena.isHandRaised)

    com.example.util.TourGuideWhisperManager.lowerHand("mem_2")
    val elenaAfter = com.example.util.TourGuideWhisperManager.groupMembers.find { it.name == "Elena Rostova" }
    assertTrue(elenaAfter != null && !elenaAfter.isHandRaised)
  }
}
