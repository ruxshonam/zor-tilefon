package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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

@Composable
fun Game2048App(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var grid by remember { mutableStateOf(init2048Grid()) }
  var score by remember { mutableIntStateOf(0) }
  var bestScore by remember { mutableIntStateOf(1024) }

  fun spawnTile(g: Array<IntArray>): Boolean {
    val empty = mutableListOf<Pair<Int, Int>>()
    for (r in 0..3) {
      for (c in 0..3) {
        if (g[r][c] == 0) empty.add(Pair(r, c))
      }
    }
    if (empty.isEmpty()) return false
    val (r, c) = empty.random()
    g[r][c] = if (Math.random() < 0.9) 2 else 4
    return true
  }

  fun moveLeft(g: Array<IntArray>): Pair<Array<IntArray>, Int> {
    var points = 0
    val res = Array(4) { IntArray(4) }
    for (r in 0..3) {
      val rowVals = g[r].filter { it != 0 }.toMutableList()
      val merged = mutableListOf<Int>()
      var i = 0
      while (i < rowVals.size) {
        if (i + 1 < rowVals.size && rowVals[i] == rowVals[i + 1]) {
          val newVal = rowVals[i] * 2
          merged.add(newVal)
          points += newVal
          i += 2
        } else {
          merged.add(rowVals[i])
          i += 1
        }
      }
      for (c in 0..3) {
        res[r][c] = if (c < merged.size) merged[c] else 0
      }
    }
    return Pair(res, points)
  }

  fun rotate(g: Array<IntArray>): Array<IntArray> {
    val res = Array(4) { IntArray(4) }
    for (r in 0..3) {
      for (c in 0..3) {
        res[c][3 - r] = g[r][c]
      }
    }
    return res
  }

  fun executeMove(direction: String) {
    var cur = Array(4) { r -> grid[r].clone() }
    val rotations = when (direction) {
      "LEFT" -> 0
      "DOWN" -> 1
      "RIGHT" -> 2
      "UP" -> 3
      else -> 0
    }
    for (i in 0 until rotations) cur = rotate(cur)
    val (moved, pts) = moveLeft(cur)
    var finalG = moved
    for (i in 0 until (4 - rotations) % 4) finalG = rotate(finalG)

    // Check if board changed
    var changed = false
    for (r in 0..3) {
      for (c in 0..3) {
        if (finalG[r][c] != grid[r][c]) changed = true
      }
    }

    if (changed) {
      spawnTile(finalG)
      grid = finalG
      score += pts
      if (score > bestScore) bestScore = score
    }
  }

  fun reset() {
    grid = init2048Grid()
    score = 0
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("game_2048_screen")
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
        IconButton(onClick = onBack, modifier = Modifier.testTag("game2048_back_btn")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("2048 Puzzle 🧩", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = { reset() }, modifier = Modifier.testTag("game2048_reset_btn")) {
          Icon(Icons.Default.Refresh, contentDescription = "Qayta", tint = HonorCyan)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Scores
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("BALL", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$score", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
        }
        Card(
          modifier = Modifier.weight(1f),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("REKORD", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text("$bestScore", color = HonorCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // 4x4 Board
      Column(
        modifier = Modifier
          .size(310.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(Color(0xFF1E293B))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (r in 0..3) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (c in 0..3) {
              val num = grid[r][c]
              val (tileBg, tileText) = getTileColors(num)
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(8.dp))
                  .background(tileBg),
                contentAlignment = Alignment.Center
              ) {
                if (num > 0) {
                  Text(
                    text = "$num",
                    color = tileText,
                    fontSize = if (num > 512) 18.sp else 22.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Direction Controller (D-Pad)
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        IconButton(
          onClick = { executeMove("UP") },
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .testTag("2048_btn_up")
        ) {
          Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Tepa", tint = Color.White)
        }
        Row(
          horizontalArrangement = Arrangement.spacedBy(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = { executeMove("LEFT") },
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1E293B))
              .testTag("2048_btn_left")
          ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Chap", tint = Color.White)
          }
          Box(
            modifier = Modifier
              .size(36.dp)
              .clip(RoundedCornerShape(10.dp))
              .background(HonorCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Text("2048", color = HonorCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
          }
          IconButton(
            onClick = { executeMove("RIGHT") },
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF1E293B))
              .testTag("2048_btn_right")
          ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "O'ng", tint = Color.White)
          }
        }
        IconButton(
          onClick = { executeMove("DOWN") },
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E293B))
            .testTag("2048_btn_down")
        ) {
          Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Past", tint = Color.White)
        }
      }
    }
  }
}

fun init2048Grid(): Array<IntArray> {
  val g = Array(4) { IntArray(4) }
  val r1 = (0..3).random()
  val c1 = (0..3).random()
  var r2 = (0..3).random()
  var c2 = (0..3).random()
  while (r1 == r2 && c1 == c2) {
    r2 = (0..3).random()
    c2 = (0..3).random()
  }
  g[r1][c1] = 2
  g[r2][c2] = 2
  return g
}

fun getTileColors(num: Int): Pair<Color, Color> {
  return when (num) {
    0 -> Pair(Color(0xFF334155).copy(alpha = 0.3f), Color.Transparent)
    2 -> Pair(Color(0xFFEEE4DA), Color(0xFF776E65))
    4 -> Pair(Color(0xFFEDE0C8), Color(0xFF776E65))
    8 -> Pair(Color(0xFFF2B179), Color.White)
    16 -> Pair(Color(0xFFF59563), Color.White)
    32 -> Pair(Color(0xFFF67C5F), Color.White)
    64 -> Pair(Color(0xFFF65E3B), Color.White)
    128 -> Pair(Color(0xFFEDCF72), Color.White)
    256 -> Pair(Color(0xFFEDCC61), Color.White)
    512 -> Pair(Color(0xFFEDC850), Color.White)
    1024 -> Pair(Color(0xFFEDC53F), Color.White)
    2048 -> Pair(Color(0xFFEDC22E), Color.White)
    else -> Pair(Color(0xFF3C3A32), Color.White)
  }
}
