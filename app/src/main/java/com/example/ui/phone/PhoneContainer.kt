package com.example.ui.phone

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppId
import com.example.ui.apps.*
import com.example.ui.theme.HonorCyan
import com.example.ui.theme.PhoneGreen
import com.example.ui.theme.PhoneRed
import com.example.viewmodel.PhoneUiState
import com.example.viewmodel.PhoneViewModel

@Composable
fun PhoneContainer(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()

  // Handle hardware / system Back Button
  BackHandler(enabled = uiState.currentApp != null || uiState.isControlCenterOpen) {
    if (uiState.isControlCenterOpen) {
      viewModel.closeControlCenter()
    } else if (uiState.currentApp != null) {
      viewModel.goHome()
    }
  }

  // Outer phone casing frame (Honor 400 Lite with premium slim bezel)
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF030712))
      .padding(4.dp),
    contentAlignment = Alignment.Center
  ) {
    // Phone Screen Chassis
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clip(RoundedCornerShape(32.dp))
        .border(2.dp, Color(0xFF1E293B), RoundedCornerShape(32.dp))
        .background(Color(0xFF0A0F1D))
    ) {
      // Main Screen Content (App or Home or Lock)
      Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLocked) {
          LockScreen(uiState = uiState, viewModel = viewModel)
        } else {
          Crossfade(targetState = uiState.currentApp, label = "app_switch") { appId ->
            when (appId) {
              null -> HomeScreen(uiState = uiState, viewModel = viewModel)
              AppId.YOUTUBE -> YouTubeApp(viewModel = viewModel)
              AppId.INSTAGRAM -> InstagramApp(viewModel = viewModel)
              AppId.PLAY_STORE -> AppStoreApp(viewModel = viewModel)
              AppId.GAME_FLAPPY -> FlappyGameApp(onBack = { viewModel.goHome() })
              AppId.GAME_TICTACTOE -> TicTacToeGameApp(onBack = { viewModel.goHome() })
              AppId.GAME_REFLEX -> ReflexTapGameApp(onBack = { viewModel.goHome() })
              AppId.GAME_2048 -> Game2048App(onBack = { viewModel.goHome() })
              AppId.MESSAGES -> MessagesApp(viewModel = viewModel)
              AppId.PHONE -> PhoneDialerApp(viewModel = viewModel)
              AppId.CAMERA -> CameraApp(viewModel = viewModel)
              AppId.GALLERY -> GalleryApp(viewModel = viewModel)
              AppId.MUSIC -> MusicApp(viewModel = viewModel)
              AppId.CALCULATOR -> CalculatorApp(onBack = { viewModel.goHome() })
              AppId.NOTES -> NotesApp(onBack = { viewModel.goHome() })
              AppId.TIKTOK -> TikTokApp(onBack = { viewModel.goHome() })
              AppId.GAME_SUBWAY -> SubwayGameApp(onBack = { viewModel.goHome() })
              AppId.WEATHER -> WeatherApp(onBack = { viewModel.goHome() })
              AppId.COMPASS -> CompassApp(onBack = { viewModel.goHome() })
              AppId.TELEGRAM -> MessagesApp(viewModel = viewModel)
              AppId.SETTINGS -> SettingsApp(uiState = uiState, viewModel = viewModel)
            }
          }
        }
      }

      // Top Status Bar (Always Visible on Phone Display)
      PhoneStatusBar(
        uiState = uiState,
        onPullDown = { viewModel.toggleControlCenter() },
        modifier = Modifier
          .align(Alignment.TopCenter)
          .fillMaxWidth()
      )

      // Dynamic Island / Magic Capsule
      DynamicIslandCapsule(
        uiState = uiState,
        viewModel = viewModel,
        modifier = Modifier.align(Alignment.TopCenter)
      )

      // Bottom Navigation Bar & Home Gesture Pill
      if (!uiState.isLocked) {
        PhoneBottomNav(
          isInApp = uiState.currentApp != null,
          onBack = {
            if (uiState.currentApp != null) viewModel.goHome()
          },
          onHome = { viewModel.goHome() },
          onLock = { viewModel.lockPhone() },
          modifier = Modifier.align(Alignment.BottomCenter)
        )
      }

      // Control Center Overlay (Quick Settings)
      AnimatedVisibility(
        visible = uiState.isControlCenterOpen,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
      ) {
        ControlCenterOverlay(
          uiState = uiState,
          viewModel = viewModel,
          onDismiss = { viewModel.closeControlCenter() }
        )
      }

      // Incoming Call Simulation Dialog
      if (uiState.isIncomingCall) {
        IncomingCallBanner(
          callerName = uiState.activeCallName ?: "Qo'ng'iroq",
          onAccept = { viewModel.acceptIncomingCall() },
          onDecline = { viewModel.endCall() },
          modifier = Modifier.align(Alignment.TopCenter)
        )
      }
    }
  }
}

@Composable
fun PhoneStatusBar(
  uiState: PhoneUiState,
  onPullDown: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable { onPullDown() }
      .testTag("phone_status_bar"),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Time on left
    Text(
      text = uiState.timeString,
      color = Color.White,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold
    )

    // Hardware Telemetry Icons on right (Honor 5G, Wi-Fi, Battery)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = "5G",
        color = HonorCyan,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(end = 4.dp)
      )

      if (uiState.isWifiOn) {
        Icon(
          Icons.Default.Wifi,
          contentDescription = "Wi-Fi",
          tint = Color.White,
          modifier = Modifier
            .size(14.dp)
            .padding(end = 4.dp)
        )
      }

      if (uiState.isBluetoothOn) {
        Icon(
          Icons.Default.Bluetooth,
          contentDescription = "Bluetooth",
          tint = Color.White,
          modifier = Modifier
            .size(14.dp)
            .padding(end = 4.dp)
        )
      }

      // Battery
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(start = 2.dp)
      ) {
        Text(
          text = "${uiState.batteryLevel}%",
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(2.dp))
        Icon(
          Icons.Default.BatteryChargingFull,
          contentDescription = "Batareya",
          tint = if (uiState.batteryLevel > 20) PhoneGreen else PhoneRed,
          modifier = Modifier.size(16.dp)
        )
      }
    }
  }
}

@Composable
fun PhoneBottomNav(
  isInApp: Boolean,
  onBack: () -> Unit,
  onHome: () -> Unit,
  onLock: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(bottom = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(
      modifier = Modifier
        .width(220.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color.Black.copy(alpha = 0.65f))
        .padding(horizontal = 14.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Back Button
      IconButton(
        onClick = onBack,
        enabled = isInApp,
        modifier = Modifier
          .size(36.dp)
          .testTag("nav_back_button")
      ) {
        Icon(
          Icons.Default.ArrowBack,
          contentDescription = "Orqaga",
          tint = if (isInApp) Color.White else Color.Gray.copy(alpha = 0.4f),
          modifier = Modifier.size(20.dp)
        )
      }

      // Home Pill
      Box(
        modifier = Modifier
          .width(54.dp)
          .height(6.dp)
          .clip(RoundedCornerShape(3.dp))
          .background(Color.White)
          .clickable { onHome() }
          .testTag("nav_home_pill")
      )

      // Lock Screen button
      IconButton(
        onClick = onLock,
        modifier = Modifier
          .size(36.dp)
          .testTag("nav_lock_button")
      ) {
        Icon(
          Icons.Default.PowerSettingsNew,
          contentDescription = "Qulflash",
          tint = Color.LightGray,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun IncomingCallBanner(
  callerName: String,
  onAccept: () -> Unit,
  onDecline: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 40.dp, start = 16.dp, end = 16.dp)
      .testTag("incoming_call_banner"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(HonorCyan.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Person, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(24.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(text = callerName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text(text = "Kiruvchi qo'ng'iroq...", color = Color.LightGray, fontSize = 12.sp)
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        IconButton(
          onClick = onDecline,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PhoneRed)
            .testTag("decline_call_btn")
        ) {
          Icon(Icons.Default.CallEnd, contentDescription = "Rad etish", tint = Color.White)
        }

        IconButton(
          onClick = onAccept,
          modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(PhoneGreen)
            .testTag("accept_call_btn")
        ) {
          Icon(Icons.Default.Call, contentDescription = "Qabul qilish", tint = Color.White)
        }
      }
    }
  }
}
