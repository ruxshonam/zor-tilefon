package com.example.ui.phone

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppId
import com.example.ui.theme.HonorCyan
import com.example.viewmodel.PhoneUiState
import com.example.viewmodel.PhoneViewModel

@Composable
fun LockScreen(
  uiState: PhoneUiState,
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF070B14),
            Color(0xFF0F172A),
            Color(0xFF1E1B4B)
          )
        )
      )
      .testTag("lock_screen"),
    contentAlignment = Alignment.Center
  ) {
    if (uiState.showPinKeypadOnLock) {
      // PIN Keypad Screen ("Parol qiladigan bo'lsin")
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.height(20.dp))
          Icon(Icons.Default.Lock, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(28.dp))
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "Parolni kiriting",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (uiState.isPinError) "Noto'g'ri PIN! Qayta urinib ko'ring" else "Boshlang'ich parol: ${uiState.pinCode}",
            color = if (uiState.isPinError) Color.Red else Color.LightGray,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(20.dp))

          // PIN Dots Indicator (4 dots)
          Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            for (i in 0..3) {
              val isFilled = i < uiState.enteredPin.length
              Box(
                modifier = Modifier
                  .size(16.dp)
                  .clip(CircleShape)
                  .background(
                    if (uiState.isPinError) Color.Red
                    else if (isFilled) HonorCyan
                    else Color(0xFF334155)
                  )
              )
            }
          }
        }

        // 0-9 Keypad
        val keys = listOf(
          listOf("1", "2", "3"),
          listOf("4", "5", "6"),
          listOf("7", "8", "9"),
          listOf("Cancel", "0", "Del")
        )

        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          for (row in keys) {
            Row(
              horizontalArrangement = Arrangement.spacedBy(24.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              for (key in row) {
                if (key == "Cancel") {
                  Box(
                    modifier = Modifier
                      .size(62.dp)
                      .clip(CircleShape)
                      .clickable { viewModel.showPinPad(false) },
                    contentAlignment = Alignment.Center
                  ) {
                    Text("Bekor", color = Color.Gray, fontSize = 12.sp)
                  }
                } else if (key == "Del") {
                  Box(
                    modifier = Modifier
                      .size(62.dp)
                      .clip(CircleShape)
                      .clickable { viewModel.deletePinDigit() }
                      .testTag("pin_key_delete"),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Backspace, contentDescription = "O'chirish", tint = Color.White)
                  }
                } else {
                  Box(
                    modifier = Modifier
                      .size(62.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF1E293B))
                      .clickable { viewModel.enterPinDigit(key) }
                      .testTag("pin_key_$key"),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(key, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // Fingerprint fallback button
        IconButton(
          onClick = { viewModel.unlockPhone() },
          modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(HonorCyan.copy(alpha = 0.15f))
        ) {
          Icon(Icons.Default.Fingerprint, contentDescription = "Barmoq izi", tint = HonorCyan)
        }
      }
    } else {
      // Main Lock Screen Clock & Notifications
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        // Top Area: Lock Icon and Big Clock
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.height(24.dp))
          Icon(
            Icons.Default.Lock,
            contentDescription = "Qulflangan",
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = uiState.timeString,
            color = Color.White,
            fontSize = 68.sp,
            fontWeight = FontWeight.Light,
            letterSpacing = (-2).sp
          )
          Text(
            text = uiState.dateString,
            color = HonorCyan,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
          )
        }

        // Middle Area: Notifications Cards
        LazyColumn(
          modifier = Modifier
            .weight(1f)
            .padding(vertical = 16.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(uiState.notifications) { notification ->
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  if (uiState.isPinLockEnabled) {
                    viewModel.showPinPad(true)
                  } else {
                    viewModel.unlockPhone()
                    notification.targetApp?.let { viewModel.openApp(it) }
                  }
                }
                .testTag("lock_notification_${notification.id}"),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1E293B).copy(alpha = 0.85f)
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
                    .background(HonorCyan.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Notifications,
                    contentDescription = null,
                    tint = HonorCyan,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Text(
                      text = notification.appName,
                      color = HonorCyan,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                    Text(text = notification.time, color = Color.Gray, fontSize = 10.sp)
                  }
                  Text(
                    text = notification.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                  )
                  Text(
                    text = notification.message,
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    maxLines = 1
                  )
                }
              }
            }
          }
        }

        // Bottom Area: Unlock with Password or Fingerprint
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Password PIN button
            IconButton(
              onClick = { viewModel.showPinPad(true) },
              modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .testTag("lock_pin_pad_button")
            ) {
              Icon(
                Icons.Default.Pin,
                contentDescription = "PIN bilan ochish",
                tint = HonorCyan,
                modifier = Modifier.size(30.dp)
              )
            }

            // Fingerprint Instant Unlock Button
            IconButton(
              onClick = { viewModel.unlockPhone() },
              modifier = Modifier
                .size(68.dp)
                .clip(CircleShape)
                .background(HonorCyan.copy(alpha = 0.2f))
                .testTag("fingerprint_unlock_button")
            ) {
              Icon(
                Icons.Default.Fingerprint,
                contentDescription = "Barmoq izi bilan ochish",
                tint = HonorCyan,
                modifier = Modifier.size(38.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Parol yoki barmoq izi bilan oching",
            color = Color.LightGray,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Quick bottom shortcuts (Flashlight & Camera)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = { viewModel.toggleFlashlight() },
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(if (uiState.isFlashlightOn) HonorCyan else Color.White.copy(alpha = 0.15f))
                .testTag("lock_flashlight_btn")
            ) {
              Icon(
                Icons.Default.FlashlightOn,
                contentDescription = "Fonar",
                tint = if (uiState.isFlashlightOn) Color.Black else Color.White
              )
            }

            Text(
              text = "HONOR 400 LITE",
              color = Color.White.copy(alpha = 0.4f),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )

            IconButton(
              onClick = {
                viewModel.unlockPhone()
                viewModel.openApp(AppId.CAMERA)
              },
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f))
                .testTag("lock_camera_btn")
            ) {
              Icon(Icons.Default.PhotoCamera, contentDescription = "Kamera", tint = Color.White)
            }
          }
        }
      }
    }
  }
}
