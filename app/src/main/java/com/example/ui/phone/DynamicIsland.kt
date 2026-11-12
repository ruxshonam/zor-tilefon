package com.example.ui.phone

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import com.example.ui.theme.PhoneGreen
import com.example.viewmodel.PhoneUiState
import com.example.viewmodel.PhoneViewModel

@Composable
fun DynamicIslandCapsule(
  uiState: PhoneUiState,
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  var isExpanded by remember { mutableStateOf(false) }

  val hasActiveCall = uiState.isCallActive || uiState.isIncomingCall
  val isMusicPlaying = uiState.isMusicPlaying

  Box(
    modifier = modifier
      .fillMaxWidth()
      .padding(top = 6.dp),
    contentAlignment = Alignment.TopCenter
  ) {
    Box(
      modifier = Modifier
        .testTag("dynamic_island_capsule")
        .clip(RoundedCornerShape(24.dp))
        .background(Color(0xFF000000))
        .animateContentSize()
        .clickable {
          if (hasActiveCall || isMusicPlaying) {
            isExpanded = !isExpanded
          }
        }
        .padding(horizontal = if (isExpanded) 16.dp else 12.dp, vertical = if (isExpanded) 12.dp else 6.dp)
        .widthIn(min = if (isExpanded) 300.dp else if (hasActiveCall || isMusicPlaying) 190.dp else 120.dp),
      contentAlignment = Alignment.Center
    ) {
      if (isExpanded) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (hasActiveCall) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PhoneGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Call, contentDescription = "Qo'ng'iroq", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(uiState.activeCallName ?: "Qo'ng'iroq", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  val min = uiState.callSeconds / 60
                  val sec = uiState.callSeconds % 60
                  Text(String.format("%02d:%02d", min, sec), color = Color.Gray, fontSize = 12.sp)
                }
              }
              IconButton(onClick = { viewModel.openApp(AppId.PHONE) }) {
                Text("Ochish", color = HonorCyan, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            }
          } else if (isMusicPlaying) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HonorCyan),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.MusicNote, contentDescription = "Musiqa", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(uiState.currentTrack?.title ?: "Musiqa", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                  Text(uiState.currentTrack?.artist ?: "", color = Color.Gray, fontSize = 11.sp, maxLines = 1)
                }
              }
              IconButton(onClick = { viewModel.toggleMusicPlay() }) {
                Icon(
                  if (uiState.isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                  contentDescription = "Ijro",
                  tint = HonorCyan
                )
              }
            }
          }
        }
      } else {
        // Compact pill mode
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          // Camera punch hole
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(Color(0xFF1E293B))
          )
          Spacer(modifier = Modifier.width(8.dp))

          if (hasActiveCall) {
            Icon(Icons.Default.Call, contentDescription = null, tint = PhoneGreen, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            val min = uiState.callSeconds / 60
            val sec = uiState.callSeconds % 60
            Text(String.format("%02d:%02d", min, sec), color = PhoneGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          } else if (isMusicPlaying) {
            Icon(Icons.Default.MusicNote, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Honor Sound", color = HonorCyan, fontSize = 11.sp, fontWeight = FontWeight.Medium)
          } else {
            // Magic Capsule subtle status
            Text("HONOR", color = Color(0xFF64748B), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
          }
        }
      }
    }
  }
}
