package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Refresh
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
import com.example.ui.theme.HonorCyan
import kotlinx.coroutines.delay

@Composable
fun ReflexTapGameApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isPlaying by remember { mutableStateOf(false) }
  var timeLeft by remember { mutableIntStateOf(20) }
  var score by remember { mutableIntStateOf(0) }
  var highScore by remember { mutableIntStateOf(0) }
  var activeTileIndex by remember { mutableIntStateOf((0..15).random()) }

  LaunchedEffect(isPlaying) {
    if (isPlaying) {
      timeLeft = 20
      score = 0
      while (timeLeft > 0 && isPlaying) {
        delay(1000)
        timeLeft -= 1
      }
      isPlaying = false
      if (score > highScore) highScore = score
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("reflex_game_screen")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("reflex_back_btn")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("Tezkor Bosish ⚡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Rekord: $highScore", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Stats Bar: Time & Score
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Vaqt", color = Color.Gray, fontSize = 12.sp)
            Text("${timeLeft}s", color = if (timeLeft <= 5) Color.Red else Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
          }
        }
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ball", color = Color.Gray, fontSize = 12.sp)
            Text("$score", color = HonorCyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      // 4x4 Reflex Grid
      Column(
        modifier = Modifier
          .size(310.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF131B2E))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (row in 0..3) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (col in 0..3) {
              val index = row * 4 + col
              val isActive = isPlaying && (index == activeTileIndex)
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isActive) Color(0xFFFF3366) else Color(0xFF1E293B))
                  .clickable(enabled = isPlaying && isActive) {
                    score += 1
                    activeTileIndex = (0..15).random()
                  }
                  .testTag("reflex_tile_$index"),
                contentAlignment = Alignment.Center
              ) {
                if (isActive) {
                  Icon(
                    Icons.Default.Bolt,
                    contentDescription = "Target",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      // Start / Play Again Button
      if (!isPlaying) {
        Button(
          onClick = { isPlaying = true },
          colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
          modifier = Modifier.testTag("reflex_start_btn"),
          shape = RoundedCornerShape(20.dp),
          contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
        ) {
          Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.Black)
          Spacer(modifier = Modifier.width(8.dp))
          Text(if (timeLeft == 0) "Qaytadan O'ynash" else "O'yinni Boshlash ⚡", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
      }
    }
  }
}
