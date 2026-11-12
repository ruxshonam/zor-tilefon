package com.example.ui.apps

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MusicTrack
import com.example.ui.theme.HonorCyan
import com.example.viewmodel.PhoneViewModel

@Composable
fun MusicApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  val playlist = viewModel.playlist

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("music_app")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { viewModel.goHome() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
          }
          Text("Honor Musiqa 🎵", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          Text("Magic Sound", color = HonorCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Playlist
        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(playlist) { track ->
            val isCurrent = uiState.currentTrack?.id == track.id
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.playTrack(track) }
                .testTag("music_track_${track.id}"),
              colors = CardDefaults.cardColors(
                containerColor = if (isCurrent) HonorCyan.copy(alpha = 0.2f) else Color(0xFF1E293B)
              ),
              shape = RoundedCornerShape(16.dp)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isCurrent) HonorCyan else Color(0xFF334155)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = if (isCurrent) Color.Black else Color.White,
                    modifier = Modifier.size(24.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(track.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                  Text(track.artist, color = Color.Gray, fontSize = 12.sp)
                }
                if (isCurrent && uiState.isMusicPlaying) {
                  Text("Ijroda...", color = HonorCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }

      // Bottom Mini Player Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                uiState.currentTrack?.title ?: "Musiqa tanlanmagan",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(uiState.currentTrack?.artist ?: "", color = Color.Gray, fontSize = 11.sp)
            }
            Text(
              "${uiState.musicProgressSec / 60}:${String.format("%02d", uiState.musicProgressSec % 60)}",
              color = HonorCyan,
              fontSize = 12.sp
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          LinearProgressIndicator(
            progress = {
              val maxSec = (uiState.currentTrack?.durationSec ?: 180).toFloat()
              (uiState.musicProgressSec / maxSec).coerceIn(0f, 1f)
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp),
            color = HonorCyan
          )

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = { viewModel.previousTrack() }) {
              Icon(Icons.Default.SkipPrevious, contentDescription = "Oldingi", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(
              onClick = { viewModel.toggleMusicPlay() },
              modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(HonorCyan)
                .testTag("music_play_toggle")
            ) {
              Icon(
                if (uiState.isMusicPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Ijro",
                tint = Color.Black,
                modifier = Modifier.size(28.dp)
              )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { viewModel.nextTrack() }) {
              Icon(Icons.Default.SkipNext, contentDescription = "Keyingi", tint = Color.White, modifier = Modifier.size(28.dp))
            }
          }
        }
      }
    }
  }
}
