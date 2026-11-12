package com.example.ui.apps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.ui.theme.HonorCyan
import com.example.viewmodel.PhoneViewModel
import kotlinx.coroutines.delay

@Composable
fun CameraApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  var selectedZoom by remember { mutableStateOf("1x") }
  var selectedMode by remember { mutableStateOf("FOTO") }
  var isFlashOn by remember { mutableStateOf(false) }
  var showShutterFlash by remember { mutableStateOf(false) }

  val modes = listOf("TUNGI", "PORTRET", "FOTO", "VIDEO", "PRO 108MP")

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black)
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("camera_app")
  ) {
    Column(
      modifier = Modifier.fillMaxSize(),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      // Top Controls
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { viewModel.goHome() }) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        IconButton(onClick = { isFlashOn = !isFlashOn }) {
          Icon(
            if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
            contentDescription = "Flash",
            tint = if (isFlashOn) HonorCyan else Color.White
          )
        }
        Text("108MP AI Matrix", color = HonorCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = {}) {
          Icon(Icons.Default.HdrOn, contentDescription = "HDR", tint = Color.White)
        }
        IconButton(onClick = {}) {
          Icon(Icons.Default.Settings, contentDescription = "Sozlama", tint = Color.White)
        }
      }

      // Viewfinder Canvas Window
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 8.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF131B2E)),
        contentAlignment = Alignment.Center
      ) {
        // Grid lines
        Column(modifier = Modifier.fillMaxSize()) {
          Spacer(modifier = Modifier.weight(1f))
          Divider(color = Color.White.copy(alpha = 0.1f))
          Spacer(modifier = Modifier.weight(1f))
          Divider(color = Color.White.copy(alpha = 0.1f))
          Spacer(modifier = Modifier.weight(1f))
        }

        // Camera Center Focus Reticle
        Box(
          modifier = Modifier
            .size(72.dp)
            .border(1.5.dp, HonorCyan.copy(alpha = 0.8f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(HonorCyan))
        }

        // Zoom Switcher
        Row(
          modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("0.6x", "1x", "2x").forEach { zoom ->
            Text(
              text = zoom,
              color = if (selectedZoom == zoom) HonorCyan else Color.White,
              fontSize = 12.sp,
              fontWeight = if (selectedZoom == zoom) FontWeight.Bold else FontWeight.Normal,
              modifier = Modifier
                .clickable { selectedZoom = zoom }
                .padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }

        // Shutter White Flash animation
        if (showShutterFlash) {
          Box(modifier = Modifier.fillMaxSize().background(Color.White))
        }
      }

      // Mode Selector Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        modes.forEach { mode ->
          Text(
            text = mode,
            color = if (selectedMode == mode) HonorCyan else Color.Gray,
            fontSize = 12.sp,
            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.clickable { selectedMode = mode }
          )
        }
      }

      // Bottom Shutter Controls
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Gallery Shortcut
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .clickable { viewModel.openApp(AppId.GALLERY) }
            .testTag("camera_gallery_shortcut"),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Collections, contentDescription = "Galereya", tint = Color.White)
        }

        // Big Shutter Button
        Box(
          modifier = Modifier
            .size(76.dp)
            .clip(CircleShape)
            .border(4.dp, Color.White, CircleShape)
            .padding(6.dp)
            .clip(CircleShape)
            .background(Color.White)
            .clickable {
              viewModel.takePhoto(selectedMode)
              showShutterFlash = true
            }
            .testTag("camera_shutter_button")
        )

        // Switch Lens Button
        IconButton(
          onClick = {},
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
        ) {
          Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Kamerani almashtirish", tint = Color.White)
        }
      }
    }
  }

  if (showShutterFlash) {
    LaunchedEffect(Unit) {
      delay(150)
      showShutterFlash = false
    }
  }
}
