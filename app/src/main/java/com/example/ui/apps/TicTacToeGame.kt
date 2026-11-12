package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.ui.theme.HonorTeal
import kotlinx.coroutines.delay

@Composable
fun TicTacToeGameApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var board by remember { mutableStateOf(List(9) { "" }) }
  var isXTurn by remember { mutableStateOf(true) }
  var winner by remember { mutableStateOf<String?>(null) } // "X", "O", "DRAW"
  var vsAi by remember { mutableStateOf(true) }
  var xWins by remember { mutableIntStateOf(0) }
  var oWins by remember { mutableIntStateOf(0) }
  var draws by remember { mutableIntStateOf(0) }

  fun checkWinner(b: List<String>): String? {
    val winningLines = listOf(
      listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
      listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
      listOf(0, 4, 8), listOf(2, 4, 6)
    )
    for (line in winningLines) {
      val (p1, p2, p3) = line
      if (b[p1].isNotEmpty() && b[p1] == b[p2] && b[p2] == b[p3]) {
        return b[p1]
      }
    }
    if (b.none { it.isEmpty() }) return "DRAW"
    return null
  }

  fun resetGame() {
    board = List(9) { "" }
    isXTurn = true
    winner = null
  }

  // AI Turn Logic
  LaunchedEffect(isXTurn, winner) {
    if (vsAi && !isXTurn && winner == null) {
      delay(400)
      val emptyIndices = board.indices.filter { board[it].isEmpty() }
      if (emptyIndices.isNotEmpty()) {
        val move = emptyIndices.random()
        val newBoard = board.toMutableList()
        newBoard[move] = "O"
        board = newBoard
        val result = checkWinner(newBoard)
        if (result != null) {
          winner = result
          if (result == "O") oWins++ else if (result == "DRAW") draws++
        } else {
          isXTurn = true
        }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("tictactoe_screen")
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
        IconButton(onClick = onBack, modifier = Modifier.testTag("ttt_back_btn")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("X-O Battle 🎮", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        IconButton(onClick = { resetGame() }, modifier = Modifier.testTag("ttt_reset_btn")) {
          Icon(Icons.Default.Refresh, contentDescription = "Qayta", tint = HonorCyan)
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Mode Switch: vs AI or 2-Player
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF1E293B))
          .padding(4.dp)
      ) {
        Button(
          onClick = {
            vsAi = true
            resetGame()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (vsAi) HonorCyan else Color.Transparent
          ),
          shape = RoundedCornerShape(16.dp)
        ) {
          Text("Smart AI Bot", color = if (vsAi) Color.Black else Color.White, fontSize = 12.sp)
        }

        Button(
          onClick = {
            vsAi = false
            resetGame()
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (!vsAi) HonorCyan else Color.Transparent
          ),
          shape = RoundedCornerShape(16.dp)
        ) {
          Text("2 O'yinchi", color = if (!vsAi) Color.Black else Color.White, fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Scoreboard Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Siz (X)", color = HonorCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("$xWins", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Durang", color = Color.LightGray, fontSize = 12.sp)
            Text("$draws", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(if (vsAi) "Bot (O)" else "Raqib (O)", color = Color(0xFFFF6699), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("$oWins", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Status Text
      val statusText = when (winner) {
        "X" -> "🎉 X G'ALABA QOZONDI!"
        "O" -> "🤖 O G'ALABA QOZONDI!"
        "DRAW" -> "🤝 DURANG!"
        else -> if (isXTurn) "Navbat: Siz (X)" else if (vsAi) "Bot o'ylamoqda..." else "Navbat: Raqib (O)"
      }
      Text(
        text = statusText,
        color = if (winner != null) HonorTeal else Color.White,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
      )

      Spacer(modifier = Modifier.height(20.dp))

      // 3x3 Grid
      Column(
        modifier = Modifier
          .size(310.dp)
          .clip(RoundedCornerShape(20.dp))
          .background(Color(0xFF131B2E))
          .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        for (row in 0..2) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            for (col in 0..2) {
              val index = row * 3 + col
              val cellValue = board[index]
              Box(
                modifier = Modifier
                  .weight(1f)
                  .fillMaxHeight()
                  .clip(RoundedCornerShape(14.dp))
                  .background(Color(0xFF1E293B))
                  .clickable(enabled = cellValue.isEmpty() && winner == null && (!vsAi || isXTurn)) {
                    val newBoard = board.toMutableList()
                    newBoard[index] = if (isXTurn) "X" else "O"
                    board = newBoard
                    val res = checkWinner(newBoard)
                    if (res != null) {
                      winner = res
                      if (res == "X") xWins++ else if (res == "O") oWins++ else draws++
                    } else {
                      isXTurn = !isXTurn
                    }
                  }
                  .testTag("ttt_cell_$index"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = cellValue,
                  color = if (cellValue == "X") HonorCyan else Color(0xFFFF6699),
                  fontSize = 42.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      if (winner != null) {
        Button(
          onClick = { resetGame() },
          colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
          modifier = Modifier.testTag("ttt_play_again_btn")
        ) {
          Text("Yana O'ynash", color = Color.Black, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
