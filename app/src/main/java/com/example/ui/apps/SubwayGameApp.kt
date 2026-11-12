package com.example.ui.apps

import androidx.compose.foundation.background
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
import com.example.ui.theme.HonorCyan
import com.example.ui.theme.HonorGold
import kotlinx.coroutines.delay

data class Obstacle(var lane: Int, var yProgress: Float, val isCoin: Boolean = false)

@Composable
fun SubwayGameApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isPlaying by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var playerLane by remember { mutableIntStateOf(1) } // 0 = Left, 1 = Center, 2 = Right
  var coins by remember { mutableIntStateOf(0) }
  var score by remember { mutableIntStateOf(0) }
  var highScore by remember { mutableIntStateOf(1450) }

  val obstacles = remember { mutableStateListOf<Obstacle>() }

  LaunchedEffect(isPlaying) {
    if (!isPlaying) return@LaunchedEffect
    obstacles.clear()
    score = 0
    coins = 0
    playerLane = 1
    isGameOver = false

    while (isPlaying && !isGameOver) {
      delay(30)
      score += 1
      if (score > highScore) highScore = score

      // Spawn new obstacles randomly
      if (Math.random() < 0.05) {
        val lane = (0..2).random()
        val isCoin = Math.random() < 0.5
        obstacles.add(Obstacle(lane, 0f, isCoin))
      }

      // Move obstacles
      for (i in obstacles.indices) {
        obstacles[i] = obstacles[i].copy(yProgress = obstacles[i].yProgress + 0.035f)

        // Collision or collection check
        if (obstacles[i].yProgress in 0.78f..0.92f && obstacles[i].lane == playerLane) {
          if (obstacles[i].isCoin) {
            coins += 1
            obstacles[i] = obstacles[i].copy(yProgress = 2f) // remove
          } else {
            isGameOver = true
            isPlaying = false
          }
        }
      }

      // Remove passed obstacles
      obstacles.removeAll { it.yProgress > 1.1f }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("subway_game_screen")
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
        IconButton(onClick = onBack, modifier = Modifier.testTag("subway_back_btn")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("Subway Runner 🏃", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.MonetizationOn, contentDescription = null, tint = HonorGold, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("$coins", color = HonorGold, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
      }

      // Score bar
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("Masofa: ${score}m", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text("Rekord: ${highScore}m", color = HonorCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 3-Lane Track Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF1E293B)),
        contentAlignment = Alignment.Center
      ) {
        // Track divider lines
        Row(modifier = Modifier.fillMaxSize()) {
          Box(modifier = Modifier.weight(1f).fillMaxHeight())
          Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color(0xFF334155)))
          Box(modifier = Modifier.weight(1f).fillMaxHeight())
          Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Color(0xFF334155)))
          Box(modifier = Modifier.weight(1f).fillMaxHeight())
        }

        // Obstacles & Coins
        obstacles.forEach { item ->
          val laneFraction = when (item.lane) {
            0 -> 0.16f
            1 -> 0.5f
            else -> 0.84f
          }
          Box(
            modifier = Modifier
              .align(Alignment.TopStart)
              .offset(
                x = (300 * laneFraction - 18).dp,
                y = (380 * item.yProgress).dp
              )
          ) {
            if (item.isCoin) {
              Box(
                modifier = Modifier
                  .size(24.dp)
                  .clip(CircleShape)
                  .background(HonorGold),
                contentAlignment = Alignment.Center
              ) {
                Text("🪙", fontSize = 14.sp)
              }
            } else {
              Box(
                modifier = Modifier
                  .width(40.dp)
                  .height(28.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFFEF4444)),
                contentAlignment = Alignment.Center
              ) {
                Text("🚧", fontSize = 14.sp)
              }
            }
          }
        }

        // Runner Character (Bottom)
        val playerXFraction = when (playerLane) {
          0 -> 0.16f
          1 -> 0.5f
          else -> 0.84f
        }
        Box(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .offset(x = (300 * playerXFraction - 18).dp, y = (-20).dp)
            .size(38.dp)
            .clip(CircleShape)
            .background(HonorCyan),
          contentAlignment = Alignment.Center
        ) {
          Text("🏃", fontSize = 20.sp)
        }

        // Start / Game Over overlay
        if (!isPlaying && !isGameOver) {
          Button(
            onClick = { isPlaying = true },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
            modifier = Modifier.testTag("subway_start_btn")
          ) {
            Text("Yugurishni Boshlash 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }

        if (isGameOver) {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
            modifier = Modifier.padding(20.dp)
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text("To'qnashuv! 💥", color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold)
              Spacer(modifier = Modifier.height(6.dp))
              Text("Yig'ilgan tangalar: $coins 🪙", color = HonorGold, fontSize = 14.sp)
              Text("Masofa: ${score}m", color = Color.White, fontSize = 14.sp)
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = { isPlaying = true },
                colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
                modifier = Modifier.testTag("subway_retry_btn")
              ) {
                Text("Qayta Boshlash", color = Color.Black, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Lane Switching Controls (Left, Jump, Right)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { if (playerLane > 0) playerLane -= 1 },
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .testTag("subway_btn_left")
        ) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Chap", tint = Color.White, modifier = Modifier.size(28.dp))
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(HonorCyan.copy(alpha = 0.2f))
            .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
          Text("Yo'lak: ${if (playerLane == 0) "Chap" else if (playerLane == 1) "O'rta" else "O'ng"}", color = HonorCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        IconButton(
          onClick = { if (playerLane < 2) playerLane += 1 },
          modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B))
            .testTag("subway_btn_right")
        ) {
          Icon(Icons.Default.ArrowForward, contentDescription = "O'ng", tint = Color.White, modifier = Modifier.size(28.dp))
        }
      }
    }
  }
}
