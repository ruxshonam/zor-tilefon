package com.example.ui.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HonorCyan
import com.example.viewmodel.PhoneUiState
import com.example.viewmodel.PhoneViewModel

@Composable
fun ControlCenterOverlay(
  uiState: PhoneUiState,
  viewModel: PhoneViewModel,
  onDismiss: () -> Unit
) {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.65f))
      .clickable { onDismiss() }
      .testTag("control_center_overlay")
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable(enabled = false) {}, // prevent dismiss on card tap
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF131B2E).copy(alpha = 0.95f))
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Boshqaruv Markazi (MagicOS)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_control_center")) {
            Icon(Icons.Default.Close, contentDescription = "Yopish", tint = Color.White)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main 2x2 Quick toggles
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickTile(
            icon = Icons.Default.Wifi,
            label = "Wi-Fi",
            subLabel = if (uiState.isWifiOn) "5G Tarmoq" else "O'chiq",
            isActive = uiState.isWifiOn,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.toggleWifi() }
          )
          QuickTile(
            icon = Icons.Default.Bluetooth,
            label = "Bluetooth",
            subLabel = if (uiState.isBluetoothOn) "Ulangan" else "O'chiq",
            isActive = uiState.isBluetoothOn,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.toggleBluetooth() }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          QuickTile(
            icon = Icons.Default.FlashlightOn,
            label = "Fonar",
            subLabel = if (uiState.isFlashlightOn) "Yonmoqda" else "O'chiq",
            isActive = uiState.isFlashlightOn,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.toggleFlashlight() }
          )
          QuickTile(
            icon = Icons.Default.AirplanemodeActive,
            label = "Parvoz rejimi",
            subLabel = if (uiState.isAirplaneMode) "Yoqilgan" else "O'chiq",
            isActive = uiState.isAirplaneMode,
            modifier = Modifier.weight(1f),
            onClick = { viewModel.toggleAirplane() }
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Brightness & Volume Sliders
        Text("Yorug'lik (${(uiState.brightnessLevel * 100).toInt()}%)", color = Color.LightGray, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.BrightnessLow, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(20.dp))
          Slider(
            value = uiState.brightnessLevel,
            onValueChange = { viewModel.setBrightness(it) },
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
              .testTag("brightness_slider"),
            colors = SliderDefaults.colors(thumbColor = HonorCyan, activeTrackColor = HonorCyan)
          )
          Icon(Icons.Default.BrightnessHigh, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text("Ovoz balandligi (${(uiState.volumeLevel * 100).toInt()}%)", color = Color.LightGray, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.VolumeMute, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(20.dp))
          Slider(
            value = uiState.volumeLevel,
            onValueChange = { viewModel.setVolume(it) },
            modifier = Modifier
              .weight(1f)
              .padding(horizontal = 8.dp)
              .testTag("volume_slider"),
            colors = SliderDefaults.colors(thumbColor = HonorCyan, activeTrackColor = HonorCyan)
          )
          Icon(Icons.Default.VolumeUp, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(20.dp))
        }
      }
    }
  }
}

@Composable
fun QuickTile(
  icon: ImageVector,
  label: String,
  subLabel: String,
  isActive: Boolean,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Card(
    modifier = modifier
      .clickable { onClick() }
      .testTag("quick_tile_${label.lowercase()}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isActive) HonorCyan.copy(alpha = 0.25f) else Color(0xFF1E293B)
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(36.dp)
          .clip(CircleShape)
          .background(if (isActive) HonorCyan else Color(0xFF334155)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = label,
          tint = if (isActive) Color.Black else Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(text = label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(text = subLabel, color = Color.LightGray, fontSize = 10.sp)
      }
    }
  }
}
