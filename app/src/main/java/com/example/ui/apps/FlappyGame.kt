package com.example.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HonorCyan
import kotlinx.coroutines.delay

data class Pipe(var x: Float, val topHeight: Float, val gap: Float = 220f, var scored: Boolean = false)

@Composable
fun FlappyGameApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isPlaying by remember { mutableStateOf(false) }
  var isGameOver by remember { mutableStateOf(false) }
  var score by remember { mutableIntStateOf(0) }
  var highScore by remember { mutableIntStateOf(0) }

  var birdY by remember { mutableFloatStateOf(400f) }
  var birdVelocity by remember { mutableFloatStateOf(0f) }
  val gravity = 0.8f
  val jumpStrength = -12f

  val pipes = remember { mutableStateListOf<Pipe>() }

  // Game Loop
  LaunchedEffect(isPlaying) {
    if (!isPlaying) return@LaunchedEffect

    // Reset game state
    birdY = 400f
    birdVelocity = 0f
    score = 0
    pipes.clear()
    pipes.add(Pipe(800f, 250f))
    pipes.add(Pipe(1200f, 320f))

    while (isPlaying && !isGameOver) {
      delay(20)
      birdVelocity += gravity
      birdY += birdVelocity

      // Move pipes
      for (i in pipes.indices) {
        pipes[i] = pipes[i].copy(x = pipes[i].x - 6f)

        // Score increment
        if (!pipes[i].scored && pipes[i].x < 200f) {
          pipes[i] = pipes[i].copy(scored = true)
          score += 1
          if (score > highScore) highScore = score
        }
      }

      // Recycle pipe
      if (pipes.isNotEmpty() && pipes[0].x < -100f) {
        pipes.removeAt(0)
        val randomTop = (150..450).random().toFloat()
        val lastX = if (pipes.isNotEmpty()) pipes.last().x else 500f
        pipes.add(Pipe(lastX + 400f, randomTop))
      }

      // Check collision
      if (birdY < 0 || birdY > 900) {
        isGameOver = true
        isPlaying = false
      }

      pipes.forEach { pipe ->
        if (pipe.x in 150f..250f) {
          if (birdY < pipe.topHeight || birdY > (pipe.topHeight + pipe.gap)) {
            isGameOver = true
            isPlaying = false
          }
        }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .pointerInput(Unit) {
        detectTapGestures(
          onTap = {
            if (isPlaying) {
              birdVelocity = jumpStrength
            } else if (!isGameOver) {
              isPlaying = true
            }
          }
        )
      }
      .testTag("flappy_game_screen")
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("flappy_back_btn")) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
      }
      Text("Uchar Qushcha 🦅 (Honor 400)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Text("Rekord: $highScore", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }

    // Game Canvas
    Canvas(
      modifier = Modifier
        .fillMaxWidth()
        .height(550.dp)
        .align(Alignment.Center)
    ) {
      // Draw background sky stars
      drawCircle(Color(0xFF334155), radius = 3f, center = Offset(100f, 120f))
      drawCircle(Color(0xFF334155), radius = 4f, center = Offset(300f, 200f))
      drawCircle(Color(0xFF334155), radius = 2f, center = Offset(500f, 80f))

      // Draw Pipes
      pipes.forEach { pipe ->
        // Top pipe
        drawRect(
          color = Color(0xFF10B981),
          topLeft = Offset(pipe.x, 0f),
          size = Size(70f, pipe.topHeight)
        )
        // Bottom pipe
        drawRect(
          color = Color(0xFF10B981),
          topLeft = Offset(pipe.x, pipe.topHeight + pipe.gap),
          size = Size(70f, size.height - (pipe.topHeight + pipe.gap))
        )
      }

      // Draw Bird
      drawCircle(
        color = Color(0xFFFFB800),
        radius = 20f,
        center = Offset(200f, birdY.coerceIn(20f, size.height - 20f))
      )
      // Bird Eye
      drawCircle(
        color = Color.Black,
        radius = 4f,
        center = Offset(208f, birdY.coerceIn(20f, size.height - 20f) - 4f)
      )
    }

    // Score on top
    Text(
      text = "$score",
      color = Color.White,
      fontSize = 48.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 80.dp)
    )

    // Start prompt or Game Over card
    if (!isPlaying && !isGameOver) {
      Card(
        modifier = Modifier
          .align(Alignment.Center)
          .padding(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("Uchar Qushcha 🦅", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Qushni uchirish uchun ekranga bosing!", color = Color.LightGray, fontSize = 13.sp)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = { isPlaying = true },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
            modifier = Modifier.testTag("flappy_start_btn")
          ) {
            Text("Boshlash 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    if (isGameOver) {
      Card(
        modifier = Modifier
          .align(Alignment.Center)
          .padding(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
        shape = RoundedCornerShape(20.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("O'yin Tugadi! 💥", color = Color.Red, fontSize = 22.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text("Sizning ballingiz: $score", color = Color.White, fontSize = 16.sp)
          Text("Eng yuqori natija: $highScore", color = HonorCyan, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = {
              isGameOver = false
              isPlaying = true
            },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
            modifier = Modifier.testTag("flappy_retry_btn")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(6.dp))
            Text("Qaytadan O'ynash", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
