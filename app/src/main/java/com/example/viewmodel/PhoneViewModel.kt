package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class PhoneUiState(
  val isLocked: Boolean = false,
  val currentApp: AppId? = null,
  val isControlCenterOpen: Boolean = false,
  val isNotificationShadeOpen: Boolean = false,
  val timeString: String = "12:45",
  val dateString: String = "Payshanba, 15-Oktabr",
  val batteryLevel: Int = 98,
  val isCharging: Boolean = false,
  val isWifiOn: Boolean = true,
  val isBluetoothOn: Boolean = true,
  val isFlashlightOn: Boolean = false,
  val isAirplaneMode: Boolean = false,
  val volumeLevel: Float = 0.8f,
  val brightnessLevel: Float = 0.9f,
  val wallpaperIndex: Int = 0,
  val apps: List<AppItem> = emptyList(),
  val notifications: List<PhoneNotification> = emptyList(),
  // Calling state & Call blocking ("Telefon qilib bo'lmaydigan qilish")
  val activeCallName: String? = null,
  val isCallActive: Boolean = false,
  val callSeconds: Int = 0,
  val isIncomingCall: Boolean = false,
  val isCallBlocked: Boolean = false,
  val callBlockReason: String? = null,
  // PIN Lock state ("Parol qiladigan bo'lsin")
  val isPinLockEnabled: Boolean = true,
  val pinCode: String = "1234",
  val enteredPin: String = "",
  val isPinError: Boolean = false,
  val showPinKeypadOnLock: Boolean = false,
  // Music state
  val currentTrack: MusicTrack? = null,
  val isMusicPlaying: Boolean = false,
  val musicProgressSec: Int = 0,
  // Dynamic island
  val dynamicIslandExpanded: Boolean = false,
  val dynamicIslandMessage: String? = null,
  // Shorts active index
  val activeShortsIndex: Int = 0
)

class PhoneViewModel : ViewModel() {

  private val _uiState = MutableStateFlow(PhoneUiState())
  val uiState: StateFlow<PhoneUiState> = _uiState.asStateFlow()

  // YouTube State
  private val _youtubeVideos = MutableStateFlow<List<YouTubeVideo>>(emptyList())
  val youtubeVideos: StateFlow<List<YouTubeVideo>> = _youtubeVideos.asStateFlow()

  private val _shortsVideos = MutableStateFlow<List<ShortsVideo>>(emptyList())
  val shortsVideos: StateFlow<List<ShortsVideo>> = _shortsVideos.asStateFlow()

  private val _activeVideo = MutableStateFlow<YouTubeVideo?>(null)
  val activeVideo: StateFlow<YouTubeVideo?> = _activeVideo.asStateFlow()

  private val _isVideoPlaying = MutableStateFlow(false)
  val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

  private val _videoProgress = MutableStateFlow(0f)
  val videoProgress: StateFlow<Float> = _videoProgress.asStateFlow()

  // Instagram State
  private val _instaPosts = MutableStateFlow<List<InstagramPost>>(emptyList())
  val instaPosts: StateFlow<List<InstagramPost>> = _instaPosts.asStateFlow()

  private val _instaStories = MutableStateFlow<List<InstagramStory>>(emptyList())
  val instaStories: StateFlow<List<InstagramStory>> = _instaStories.asStateFlow()

  // Messages State
  private val _conversations = MutableStateFlow<List<ChatConversation>>(emptyList())
  val conversations: StateFlow<List<ChatConversation>> = _conversations.asStateFlow()

  private val _selectedChatId = MutableStateFlow<String?>(null)
  val selectedChatId: StateFlow<String?> = _selectedChatId.asStateFlow()

  // Gallery Photos
  private val _photos = MutableStateFlow<List<GalleryPhoto>>(emptyList())
  val photos: StateFlow<List<GalleryPhoto>> = _photos.asStateFlow()

  // Music Playlist
  val playlist = listOf(
    MusicTrack("1", "Honor Theme - Pure Magic", "Honor Sound Studio", 195),
    MusicTrack("2", "Tashkent City Lights", "Uzbek Lo-Fi Chill", 160),
    MusicTrack("3", "Cyber Wave 2026", "SynthMaster", 210),
    MusicTrack("4", "Relaxing Sunset Glow", "Acoustic Vibes", 175)
  )

  private var callTimerJob: Job? = null
  private var musicTimerJob: Job? = null
  private var videoTimerJob: Job? = null

  init {
    initializeApps()
    initializeYouTube()
    initializeShorts()
    initializeInstagram()
    initializeMessages()
    initializeGallery()
    updateTime()

    _uiState.value = _uiState.value.copy(
      currentTrack = playlist[0],
      notifications = listOf(
        PhoneNotification("1", "YouTube", "Yangi Shorts video chiqdi! ⚡", "MrBeast: Honor 400 Lite sinov...", "2 daqiqa oldin", AppId.YOUTUBE),
        PhoneNotification("2", "Instagram", "Sardor sizning rasmingizga layk bosdi ❤️", "12 ta yangi bildirishnoma", "5 daqiqa oldin", AppId.INSTAGRAM),
        PhoneNotification("3", "Xabarlar", "Onajonim ❤️: O'g'lim, yangi telefon qalay?", "Javob yozish...", "10 daqiqa oldin", AppId.MESSAGES)
      )
    )
  }

  private fun updateTime() {
    val sdfTime = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdfDate = SimpleDateFormat("EEEE, d-MMMM", Locale("uz"))
    val now = Date()
    _uiState.value = _uiState.value.copy(
      timeString = sdfTime.format(now),
      dateString = sdfDate.format(now)
    )
  }

  private fun initializeApps() {
    val defaultApps = listOf(
      AppItem(AppId.PHONE, "Telefon", "Aloqa", isDockApp = true),
      AppItem(AppId.MESSAGES, "Xabarlar", "Aloqa", isDockApp = true, badgeCount = 2),
      AppItem(AppId.CAMERA, "Kamera", "Media", isDockApp = true),
      AppItem(AppId.GALLERY, "Galereya", "Media", isDockApp = true),
      AppItem(AppId.YOUTUBE, "YouTube", "Video", isInstalled = true),
      AppItem(AppId.INSTAGRAM, "Instagram", "Ijtimoiy", isInstalled = true, badgeCount = 3),
      AppItem(AppId.PLAY_STORE, "Play Market", "Do'kon", isInstalled = true),
      AppItem(AppId.TIKTOK, "TikTok", "Video", isInstalled = true),
      AppItem(AppId.GAME_FLAPPY, "Uchar Qushcha", "O'yin", isInstalled = true),
      AppItem(AppId.GAME_TICTACTOE, "X-O O'yini", "O'yin", isInstalled = true),
      AppItem(AppId.GAME_REFLEX, "Tezkor Bosish", "O'yin", isInstalled = true),
      AppItem(AppId.GAME_2048, "2048 Puzzle", "O'yin", isInstalled = true),
      AppItem(AppId.GAME_SUBWAY, "Subway Runner", "O'yin", isInstalled = false, sizeMb = 54),
      AppItem(AppId.WEATHER, "Ob-havo Pro", "Vositalar", isInstalled = false, sizeMb = 18),
      AppItem(AppId.COMPASS, "Kompas", "Vositalar", isInstalled = false, sizeMb = 12),
      AppItem(AppId.TELEGRAM, "Telegram", "Aloqa", isInstalled = false, sizeMb = 35),
      AppItem(AppId.MUSIC, "Musiqa", "Media", isInstalled = true),
      AppItem(AppId.CALCULATOR, "Kalkulyator", "Vositalar", isInstalled = true),
      AppItem(AppId.NOTES, "Qaydlar", "Vositalar", isInstalled = true),
      AppItem(AppId.SETTINGS, "Sozlamalar", "Tizim", isInstalled = true)
    )
    _uiState.value = _uiState.value.copy(apps = defaultApps)
  }

  private fun initializeYouTube() {
    _youtubeVideos.value = listOf(
      YouTubeVideo(
        id = "yt_1",
        title = "HONOR 400 Lite Rasmiy Obzor & Test (120Hz AMOLED, 108MP Kamera!)",
        channel = "TexnoUzbek Official",
        views = "245K ko'rishlar",
        timeAgo = "1 kun oldin",
        duration = "14:20",
        likes = 12400,
        comments = listOf(
          CommentItem("c1", "Jasur_Pro", "Dizayni juda chiroyli ekan, kamerasi gap yo'q!", 84, "2 soat oldin"),
          CommentItem("c2", "Azizbek", "Qayerdan sotib olsa bo'ladi? Narxi qancha?", 32, "5 soat oldin")
        ),
        videoGradient = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
      ),
      YouTubeVideo(
        id = "yt_2",
        title = "MrBeast: $1,000,000 Dollarlik Yashirin O'yin O'zbekistonda!",
        channel = "MrBeast Uzbek",
        views = "1.8M ko'rishlar",
        timeAgo = "3 kun oldin",
        duration = "22:45",
        likes = 98500,
        comments = listOf(
          CommentItem("c3", "Shoxrux", "Zo'r video bo'libdi! Yana shunaqa qilinglar!", 156, "1 kun oldin"),
          CommentItem("c4", "Kamronbek", "Eng zo'r Yutuber!", 95, "1 kun oldin")
        ),
        videoGradient = listOf(0xFF2E0854, 0xFF5B147D, 0xFF8E2DE2)
      ),
      YouTubeVideo(
        id = "yt_3",
        title = "Top 4 Ta Android O'yinlari 2026 (Honor 400 Lite da sinov!)",
        channel = "Gamer O'zbekiston",
        views = "89K ko'rishlar",
        timeAgo = "5 soat oldin",
        duration = "11:15",
        likes = 8900,
        comments = listOf(
          CommentItem("c5", "Diyorbek", "Flappy va 2048 zo'r ketarkan!", 24, "3 soat oldin")
        ),
        videoGradient = listOf(0xFF134E5E, 0xFF71B280)
      ),
      YouTubeVideo(
        id = "yt_4",
        title = "Yangi Xit Qo'shiqlar To'plami 2026 (Bass Bosted HD)",
        channel = "Music Wave UZ",
        views = "510K ko'rishlar",
        timeAgo = "1 hafta oldin",
        duration = "35:10",
        likes = 34000,
        comments = emptyList(),
        videoGradient = listOf(0xFFB24592, 0xFFF15F79)
      )
    )
  }

  private fun initializeShorts() {
    _shortsVideos.value = listOf(
      ShortsVideo(
        id = "s_1",
        title = "Honor 400 Lite telefonini balandlikdan tashladik! 😱 Natija...",
        channel = "TexnoUzbek Official",
        likes = 94200,
        commentsCount = 1850,
        musicTitle = "Original Sound • Honor Tech Challenge",
        gradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)
      ),
      ShortsVideo(
        id = "s_2",
        title = "MrBeast $1,000,000 lik qutini ochish! 💥",
        channel = "MrBeast Uzbek",
        likes = 210000,
        commentsCount = 4200,
        musicTitle = "MrBeast Fast Action Beat",
        gradient = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
      ),
      ShortsVideo(
        id = "s_3",
        title = "Subway Surfersda yangi cheksiz tanga kodi! 🪙⚡",
        channel = "Gamer O'zbekiston",
        likes = 86000,
        commentsCount = 940,
        musicTitle = "Subway Surfers Remix Beat",
        gradient = listOf(0xFF11998E, 0xFF38EF7D)
      ),
      ShortsVideo(
        id = "s_4",
        title = "Cristiano Ronaldo jarima to'pidan fantastik gol! ⚽🔥",
        channel = "CR7 King",
        likes = 450000,
        commentsCount = 8900,
        musicTitle = "Champions League Epic Theme",
        gradient = listOf(0xFF2E0854, 0xFF5B147D, 0xFF8E2DE2)
      )
    )
  }

  private fun initializeInstagram() {
    _instaStories.value = listOf(
      InstagramStory("s1", "Sizning tarixingiz", "Tarix qo'shish", false),
      InstagramStory("s2", "cristiano", "Cristiano Ronaldo", false),
      InstagramStory("s3", "honor_uzbekistan", "Honor Official UZ", false),
      InstagramStory("s4", "sardor_gamer", "Sardor Do'stim", false),
      InstagramStory("s5", "mrbeast", "MrBeast Channel", false)
    )

    _instaPosts.value = listOf(
      InstagramPost(
        id = "p1",
        username = "honor_uzbekistan",
        location = "Toshkent, O'zbekiston",
        caption = "Yangi HONOR 400 Lite endi sotuvda! 🔥 Ultra yupqa korpus, super 108MP kamera va uzoqqa yetadigan batareya! Sizga qaysi rangi ko'proq yoqdi? 👇",
        likes = 3450,
        comments = listOf(
          CommentItem("cp1", "bobur_99", "Moviy rangi daxshat ko'rinar ekan!", 42, "1 soat oldin"),
          CommentItem("cp2", "malika_art", "Kamerasi juda tiniq!", 19, "2 soat oldin")
        ),
        timestamp = "3 SOAT OLDIN",
        postColorHex = 0xFF0D2538
      ),
      InstagramPost(
        id = "p2",
        username = "sardor_gamer",
        location = "Samarqand Registon",
        caption = "Honor 400 Lite telefonida o'yinlar umuman qotmasdan ishlayapti! 60 FPS stabil ⚡🎮 Kim birga o'ynaydi?",
        likes = 890,
        comments = listOf(
          CommentItem("cp3", "anvar_pro", "Menga ham tortib ber!", 11, "4 soat oldin")
        ),
        timestamp = "6 SOAT OLDIN",
        postColorHex = 0xFF2A1B4E
      ),
      InstagramPost(
        id = "p3",
        username = "cristiano",
        location = "Riyadh, Saudi Arabia",
        caption = "Focus on the goal. Hard work always pays off! ⚽💪 #NeverGiveUp",
        likes = 452000,
        comments = listOf(
          CommentItem("cp4", "uzbek_fan", "GOAT Cristiano! 🐐", 1890, "8 soat oldin")
        ),
        timestamp = "12 SOAT OLDIN",
        postColorHex = 0xFF1E3A1E
      )
    )
  }

  private fun initializeMessages() {
    _conversations.value = listOf(
      ChatConversation(
        id = "conv_mom",
        name = "Onajonim ❤️",
        avatarText = "❤️",
        status = "onlayn",
        messages = listOf(
          ChatMessage("m1", "Assalomu alaykum o'g'lim!", false, "12:30"),
          ChatMessage("m2", "Yangi Honor 400 Lite telefoning qutlug' bo'lsin!", false, "12:31"),
          ChatMessage("m3", "Rahmat onajon! Juda zo'r ishlayapti, kamerasi ham ajoyib!", true, "12:35"),
          ChatMessage("m4", "Kechga ovqatga nima pishiray?", false, "12:40")
        ),
        unreadCount = 1
      ),
      ChatConversation(
        id = "conv_friend",
        name = "Sardor Do'stim 🎮",
        avatarText = "🎮",
        status = "yaqinda bo'lgan",
        messages = listOf(
          ChatMessage("f1", "Salom jo'ra! Yutuberdagi yangi videoni ko'rdingmi?", false, "11:15"),
          ChatMessage("f2", "Ha, ko'rdim! Yaxshi video bo'libdi.", true, "11:20"),
          ChatMessage("f3", "Honor 400 Lite dagi o'yinlarni o'ynab ko'rdingmi? Uchar qushcha bilan 2048 zo'r ekan!", false, "11:22")
        ),
        unreadCount = 1
      ),
      ChatConversation(
        id = "conv_honor",
        name = "Honor MagicOS Yordamchi",
        avatarText = "🤖",
        status = "bot",
        messages = listOf(
          ChatMessage("h1", "Honor 400 Lite smartfoniga xush kelibsiz!", false, "09:00"),
          ChatMessage("h2", "Telefoningiz MagicOS 8.0 da eng yuqori unumdorlikda ishlamoqda. 12GB RAM + 512GB Xotira tayyor.", false, "09:01")
        ),
        unreadCount = 0
      )
    )
  }

  private fun initializeGallery() {
    _photos.value = listOf(
      GalleryPhoto("ph1", System.currentTimeMillis() - 86400000, "Tog' manzarasi (108MP)", "Tabiat", listOf(0xFF134E5E, 0xFF71B280)),
      GalleryPhoto("ph2", System.currentTimeMillis() - 43200000, "Honor 400 Lite Portret", "Portret", listOf(0xFF2C3E50, 0xFF3498DB)),
      GalleryPhoto("ph3", System.currentTimeMillis() - 10000000, "Tungi Toshkent Chiroqlari", "Tungi", listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364))
    )
  }

  // Navigation & Phone Controls
  fun openApp(appId: AppId) {
    _uiState.value = _uiState.value.copy(
      currentApp = appId,
      isControlCenterOpen = false,
      isNotificationShadeOpen = false
    )
  }

  fun goHome() {
    _uiState.value = _uiState.value.copy(
      currentApp = null,
      isControlCenterOpen = false,
      isNotificationShadeOpen = false
    )
    _activeVideo.value = null
    _isVideoPlaying.value = false
    _selectedChatId.value = null
  }

  fun lockPhone() {
    _uiState.value = _uiState.value.copy(
      isLocked = true,
      currentApp = null,
      isControlCenterOpen = false,
      enteredPin = "",
      isPinError = false,
      showPinKeypadOnLock = false
    )
  }

  fun unlockPhone() {
    _uiState.value = _uiState.value.copy(
      isLocked = false,
      enteredPin = "",
      isPinError = false,
      showPinKeypadOnLock = false
    )
  }

  // PIN / Parol boshqaruvi ("Parol qiladigan bo'lsin")
  fun showPinPad(show: Boolean) {
    _uiState.value = _uiState.value.copy(showPinKeypadOnLock = show, enteredPin = "")
  }

  fun enterPinDigit(digit: String) {
    if (_uiState.value.enteredPin.length < 4) {
      val next = _uiState.value.enteredPin + digit
      _uiState.value = _uiState.value.copy(enteredPin = next, isPinError = false)
      if (next.length == 4) {
        if (next == _uiState.value.pinCode) {
          // Kod to'g'ri - qulfdan ochish!
          _uiState.value = _uiState.value.copy(
            isLocked = false,
            enteredPin = "",
            isPinError = false,
            showPinKeypadOnLock = false
          )
        } else {
          // Kod noto'g'ri
          _uiState.value = _uiState.value.copy(isPinError = true)
          viewModelScope.launch {
            delay(500)
            _uiState.value = _uiState.value.copy(enteredPin = "", isPinError = false)
          }
        }
      }
    }
  }

  fun deletePinDigit() {
    if (_uiState.value.enteredPin.isNotEmpty()) {
      _uiState.value = _uiState.value.copy(enteredPin = _uiState.value.enteredPin.dropLast(1))
    }
  }

  fun setPinCode(newPin: String) {
    if (newPin.length == 4) {
      _uiState.value = _uiState.value.copy(pinCode = newPin, isPinLockEnabled = true)
    }
  }

  fun togglePinLockEnabled(enabled: Boolean) {
    _uiState.value = _uiState.value.copy(isPinLockEnabled = enabled)
  }

  fun toggleControlCenter() {
    _uiState.value = _uiState.value.copy(
      isControlCenterOpen = !_uiState.value.isControlCenterOpen
    )
  }

  fun closeControlCenter() {
    _uiState.value = _uiState.value.copy(isControlCenterOpen = false)
  }

  fun toggleNotificationShade() {
    _uiState.value = _uiState.value.copy(
      isNotificationShadeOpen = !_uiState.value.isNotificationShadeOpen
    )
  }

  fun clearNotification(id: String) {
    _uiState.value = _uiState.value.copy(
      notifications = _uiState.value.notifications.filter { it.id != id }
    )
  }

  // Toggles
  fun toggleWifi() {
    _uiState.value = _uiState.value.copy(isWifiOn = !_uiState.value.isWifiOn)
  }

  fun toggleBluetooth() {
    _uiState.value = _uiState.value.copy(isBluetoothOn = !_uiState.value.isBluetoothOn)
  }

  fun toggleFlashlight() {
    _uiState.value = _uiState.value.copy(isFlashlightOn = !_uiState.value.isFlashlightOn)
  }

  fun toggleAirplane() {
    val newAirplane = !_uiState.value.isAirplaneMode
    _uiState.value = _uiState.value.copy(
      isAirplaneMode = newAirplane,
      isCallBlocked = if (newAirplane) true else _uiState.value.isCallBlocked
    )
  }

  // "Telefon qilib bo'lmaydigan qilish" - Qo'ng'iroqlarni bloklash rejimi
  fun toggleCallBlock() {
    val newBlocked = !_uiState.value.isCallBlocked
    _uiState.value = _uiState.value.copy(
      isCallBlocked = newBlocked,
      callBlockReason = if (newBlocked) "Qo'ng'iroqlar bloklangan!" else null
    )
  }

  fun setVolume(volume: Float) {
    _uiState.value = _uiState.value.copy(volumeLevel = volume)
  }

  fun setBrightness(brightness: Float) {
    _uiState.value = _uiState.value.copy(brightnessLevel = brightness)
  }

  fun setWallpaper(index: Int) {
    _uiState.value = _uiState.value.copy(wallpaperIndex = index)
  }

  // Play Store download simulation
  fun downloadApp(appId: AppId) {
    viewModelScope.launch {
      val app = _uiState.value.apps.find { it.id == appId } ?: return@launch
      if (app.isInstalled) return@launch

      _uiState.value = _uiState.value.copy(
        apps = _uiState.value.apps.map {
          if (it.id == appId) it.copy(isDownloading = true, downloadProgress = 0.15f) else it
        }
      )

      for (progress in listOf(0.35f, 0.65f, 0.9f, 1.0f)) {
        delay(400)
        _uiState.value = _uiState.value.copy(
          apps = _uiState.value.apps.map {
            if (it.id == appId) it.copy(downloadProgress = progress) else it
          }
        )
      }

      delay(200)
      _uiState.value = _uiState.value.copy(
        apps = _uiState.value.apps.map {
          if (it.id == appId) it.copy(isInstalled = true, isDownloading = false, downloadProgress = 1f) else it
        },
        notifications = listOf(
          PhoneNotification(
            UUID.randomUUID().toString(),
            "Play Market",
            "${app.name} o'rnatildi! 🎉",
            "Ilovani ochish uchun bosing.",
            "Hozirgina",
            appId
          )
        ) + _uiState.value.notifications
      )
    }
  }

  fun uninstallApp(appId: AppId) {
    _uiState.value = _uiState.value.copy(
      apps = _uiState.value.apps.map {
        if (it.id == appId) it.copy(isInstalled = false, downloadProgress = 0f) else it
      }
    )
  }

  // Shorts Navigation & Actions ("Shorti video ko'rinmayapti tez ishlasin")
  fun nextShorts() {
    val total = _shortsVideos.value.size
    if (total > 0) {
      _uiState.value = _uiState.value.copy(
        activeShortsIndex = (_uiState.value.activeShortsIndex + 1) % total
      )
    }
  }

  fun prevShorts() {
    val total = _shortsVideos.value.size
    if (total > 0) {
      val prev = if (_uiState.value.activeShortsIndex - 1 < 0) total - 1 else _uiState.value.activeShortsIndex - 1
      _uiState.value = _uiState.value.copy(activeShortsIndex = prev)
    }
  }

  fun toggleLikeShorts(id: String) {
    _shortsVideos.value = _shortsVideos.value.map { s ->
      if (s.id == id) {
        val newLiked = !s.isLiked
        s.copy(isLiked = newLiked, likes = if (newLiked) s.likes + 1 else s.likes - 1)
      } else s
    }
  }

  // YouTube Functions
  fun selectVideo(video: YouTubeVideo) {
    _activeVideo.value = video
    _isVideoPlaying.value = true
    _videoProgress.value = 0.05f
    startVideoPlayback()
  }

  fun toggleVideoPlay() {
    _isVideoPlaying.value = !_isVideoPlaying.value
    if (_isVideoPlaying.value) startVideoPlayback() else videoTimerJob?.cancel()
  }

  private fun startVideoPlayback() {
    videoTimerJob?.cancel()
    videoTimerJob = viewModelScope.launch {
      while (_isVideoPlaying.value && _videoProgress.value < 1.0f) {
        delay(1000)
        _videoProgress.value = (_videoProgress.value + 0.02f).coerceAtMost(1f)
      }
    }
  }

  fun closeActiveVideo() {
    videoTimerJob?.cancel()
    _activeVideo.value = null
    _isVideoPlaying.value = false
  }

  fun toggleLikeVideo(videoId: String) {
    _youtubeVideos.value = _youtubeVideos.value.map { v ->
      if (v.id == videoId) {
        val newLiked = !v.isLiked
        v.copy(isLiked = newLiked, likes = if (newLiked) v.likes + 1 else v.likes - 1)
      } else v
    }
    _activeVideo.value = _youtubeVideos.value.find { it.id == videoId }
  }

  fun toggleSubscribe(videoId: String) {
    _youtubeVideos.value = _youtubeVideos.value.map { v ->
      if (v.id == videoId) v.copy(isSubscribed = !v.isSubscribed) else v
    }
    _activeVideo.value = _youtubeVideos.value.find { it.id == videoId }
  }

  fun addCommentToVideo(videoId: String, commentText: String) {
    if (commentText.isBlank()) return
    val newComment = CommentItem(UUID.randomUUID().toString(), "Siz (Honor 400 Lite)", commentText, 1, "Hozirgina")
    _youtubeVideos.value = _youtubeVideos.value.map { v ->
      if (v.id == videoId) v.copy(comments = listOf(newComment) + v.comments) else v
    }
    _activeVideo.value = _youtubeVideos.value.find { it.id == videoId }
  }

  fun uploadYouTubeVideo(title: String) {
    if (title.isBlank()) return
    val newVid = YouTubeVideo(
      id = "yt_user_${System.currentTimeMillis()}",
      title = title,
      channel = "Mening Kanalim (Honor 400 Lite)",
      views = "1.2K ko'rishlar",
      timeAgo = "Hozirgina",
      duration = "08:30",
      likes = 120,
      comments = listOf(
        CommentItem("c_new", "Ali_Gamer", "Zo'r video chiqibdi! Omad!", 14, "Hozirgina")
      ),
      videoGradient = listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)
    )
    _youtubeVideos.value = listOf(newVid) + _youtubeVideos.value
  }

  // Instagram Functions
  fun toggleLikePost(postId: String) {
    _instaPosts.value = _instaPosts.value.map { p ->
      if (p.id == postId) {
        val newLiked = !p.isLiked
        p.copy(isLiked = newLiked, likes = if (newLiked) p.likes + 1 else p.likes - 1)
      } else p
    }
  }

  fun toggleSavePost(postId: String) {
    _instaPosts.value = _instaPosts.value.map { p ->
      if (p.id == postId) p.copy(isSaved = !p.isSaved) else p
    }
  }

  fun addCommentToPost(postId: String, commentText: String) {
    if (commentText.isBlank()) return
    val comment = CommentItem(UUID.randomUUID().toString(), "honor_user_uz", commentText, 1, "1 soniya oldin")
    _instaPosts.value = _instaPosts.value.map { p ->
      if (p.id == postId) p.copy(comments = p.comments + comment) else p
    }
  }

  fun addNewInstagramPost(caption: String) {
    if (caption.isBlank()) return
    val newPost = InstagramPost(
      id = "p_user_${System.currentTimeMillis()}",
      username = "honor_user_uz",
      location = "Honor 400 Lite AI Studio",
      caption = caption,
      likes = 54,
      comments = emptyList(),
      timestamp = "HOZIRGINA",
      postColorHex = 0xFF0D5C75
    )
    _instaPosts.value = listOf(newPost) + _instaPosts.value
  }

  // Messages Functions
  fun selectChat(chatId: String) {
    _selectedChatId.value = chatId
    _conversations.value = _conversations.value.map {
      if (it.id == chatId) it.copy(unreadCount = 0) else it
    }
  }

  fun backToChatList() {
    _selectedChatId.value = null
  }

  fun sendMessage(chatId: String, text: String) {
    if (text.isBlank()) return
    val userMsg = ChatMessage(UUID.randomUUID().toString(), text, true, _uiState.value.timeString)
    _conversations.value = _conversations.value.map { c ->
      if (c.id == chatId) c.copy(messages = c.messages + userMsg) else c
    }

    viewModelScope.launch {
      delay(1000)
      val replyText = when (chatId) {
        "conv_mom" -> "Rahmat bolam! Sendan xabar olganimdan xursandman. Telefoningga ehtiyot bo'l ❤️"
        "conv_friend" -> "Gap yo'q og'ayni! Honor 400 Lite da Shortilar va o'yinlar daxshat ishlayapti! 🚀"
        else -> "Xabaringiz qabul qilindi. MagicOS 8.0 yordamchisi doim xizmatingizda!"
      }
      val replyMsg = ChatMessage(UUID.randomUUID().toString(), replyText, false, _uiState.value.timeString)
      _conversations.value = _conversations.value.map { c ->
        if (c.id == chatId) c.copy(messages = c.messages + replyMsg) else c
      }
    }
  }

  // Calling Functions ("Telefon qilib bo'lmaydigan qilish")
  fun startCall(name: String) {
    if (_uiState.value.isCallBlocked || _uiState.value.isAirplaneMode) {
      _uiState.value = _uiState.value.copy(
        callBlockReason = "Qo'ng'iroq taqiqlangan! Qo'ng'iroqlar bloklangan yoki Parvoz rejimi faol."
      )
      viewModelScope.launch {
        delay(3000)
        _uiState.value = _uiState.value.copy(callBlockReason = null)
      }
      return
    }

    _uiState.value = _uiState.value.copy(
      activeCallName = name,
      isCallActive = true,
      isIncomingCall = false,
      callSeconds = 0,
      currentApp = AppId.PHONE
    )
    startCallTimer()
  }

  fun simulateIncomingCall() {
    if (_uiState.value.isCallBlocked || _uiState.value.isAirplaneMode) {
      _uiState.value = _uiState.value.copy(
        notifications = listOf(
          PhoneNotification(
            UUID.randomUUID().toString(),
            "Telefon",
            "Qo'ng'iroq bloklandi 🚫",
            "Onajonim ❤️ raqamidan qo'ng'iroq rad etildi (Qo'ng'iroqlar taqiqlangan)",
            "Hozirgina",
            AppId.PHONE
          )
        ) + _uiState.value.notifications
      )
      return
    }

    _uiState.value = _uiState.value.copy(
      activeCallName = "Onajonim ❤️",
      isIncomingCall = true,
      isCallActive = false,
      callSeconds = 0
    )
  }

  fun acceptIncomingCall() {
    _uiState.value = _uiState.value.copy(
      isIncomingCall = false,
      isCallActive = true,
      callSeconds = 0,
      currentApp = AppId.PHONE
    )
    startCallTimer()
  }

  fun endCall() {
    callTimerJob?.cancel()
    _uiState.value = _uiState.value.copy(
      activeCallName = null,
      isCallActive = false,
      isIncomingCall = false,
      callSeconds = 0
    )
  }

  private fun startCallTimer() {
    callTimerJob?.cancel()
    callTimerJob = viewModelScope.launch {
      while (_uiState.value.isCallActive) {
        delay(1000)
        _uiState.value = _uiState.value.copy(callSeconds = _uiState.value.callSeconds + 1)
      }
    }
  }

  // Camera & Gallery
  fun takePhoto(filterTag: String) {
    val gradients = listOf(
      listOf(0xFF1F4037, 0xFF99F2C8),
      listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364),
      listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121),
      listOf(0xFF141E30, 0xFF243B55)
    )
    val chosenGrad = gradients.random()
    val newPhoto = GalleryPhoto(
      id = "ph_${System.currentTimeMillis()}",
      timestamp = System.currentTimeMillis(),
      label = "Honor 400 Lite Foto (${filterTag})",
      filterTag = filterTag,
      colorGradient = chosenGrad
    )
    _photos.value = listOf(newPhoto) + _photos.value
  }

  fun deletePhoto(photoId: String) {
    _photos.value = _photos.value.filter { it.id != photoId }
  }

  // Music Player
  fun playTrack(track: MusicTrack) {
    _uiState.value = _uiState.value.copy(
      currentTrack = track,
      isMusicPlaying = true,
      musicProgressSec = 0
    )
    startMusicTimer()
  }

  fun toggleMusicPlay() {
    val newPlay = !_uiState.value.isMusicPlaying
    _uiState.value = _uiState.value.copy(isMusicPlaying = newPlay)
    if (newPlay) startMusicTimer() else musicTimerJob?.cancel()
  }

  fun nextTrack() {
    val current = _uiState.value.currentTrack ?: playlist.first()
    val currentIndex = playlist.indexOfFirst { it.id == current.id }
    val nextIndex = (currentIndex + 1) % playlist.size
    playTrack(playlist[nextIndex])
  }

  fun previousTrack() {
    val current = _uiState.value.currentTrack ?: playlist.first()
    val currentIndex = playlist.indexOfFirst { it.id == current.id }
    val prevIndex = if (currentIndex - 1 < 0) playlist.size - 1 else currentIndex - 1
    playTrack(playlist[prevIndex])
  }

  private fun startMusicTimer() {
    musicTimerJob?.cancel()
    musicTimerJob = viewModelScope.launch {
      while (_uiState.value.isMusicPlaying) {
        delay(1000)
        val maxDuration = _uiState.value.currentTrack?.durationSec ?: 180
        val nextSec = _uiState.value.musicProgressSec + 1
        if (nextSec >= maxDuration) {
          nextTrack()
        } else {
          _uiState.value = _uiState.value.copy(musicProgressSec = nextSec)
        }
      }
    }
  }
}
