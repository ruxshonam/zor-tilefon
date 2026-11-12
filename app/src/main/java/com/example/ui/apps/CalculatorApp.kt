package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun CalculatorApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var display by remember { mutableStateOf("0") }
  var operand1 by remember { mutableDoubleStateOf(0.0) }
  var pendingOp by remember { mutableStateOf<String?>(null) }
  var isNewOp by remember { mutableStateOf(false) }

  fun onDigit(d: String) {
    if (display == "0" || isNewOp) {
      display = d
      isNewOp = false
    } else {
      display += d
    }
  }

  fun onOp(op: String) {
    operand1 = display.toDoubleOrNull() ?: 0.0
    pendingOp = op
    isNewOp = true
  }

  fun onEqual() {
    val operand2 = display.toDoubleOrNull() ?: 0.0
    val res = when (pendingOp) {
      "+" -> operand1 + operand2
      "-" -> operand1 - operand2
      "×" -> operand1 * operand2
      "÷" -> if (operand2 != 0.0) operand1 / operand2 else 0.0
      else -> operand2
    }
    display = if (res % 1.0 == 0.0) res.toLong().toString() else String.format("%.2f", res)
    pendingOp = null
    isNewOp = true
  }

  fun onClear() {
    display = "0"
    operand1 = 0.0
    pendingOp = null
    isNewOp = false
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("calculator_app")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("Kalkulyator 🧮", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Box(modifier = Modifier.size(48.dp))
      }

      // Display Area
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp),
        contentAlignment = Alignment.BottomEnd
      ) {
        Text(
          text = display,
          color = Color.White,
          fontSize = 44.sp,
          fontWeight = FontWeight.Light,
          maxLines = 1
        )
      }

      // Keypad
      val buttons = listOf(
        listOf("C", "±", "%", "÷"),
        listOf("7", "8", "9", "×"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "=")
      )

      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        for (row in buttons) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            for (btn in row) {
              val isOp = btn in listOf("÷", "×", "-", "+", "=")
              val isAction = btn in listOf("C", "±", "%")
              val flex = if (btn == "0") 2f else 1f

              Box(
                modifier = Modifier
                  .weight(flex)
                  .height(60.dp)
                  .clip(CircleShape)
                  .background(
                    if (isOp) HonorCyan else if (isAction) Color(0xFF334155) else Color(0xFF1E293B)
                  )
                  .clickable {
                    when (btn) {
                      "C" -> onClear()
                      "=" -> onEqual()
                      "÷", "×", "-", "+" -> onOp(btn)
                      "±" -> display = if (display.startsWith("-")) display.drop(1) else "-$display"
                      "%" -> display = ((display.toDoubleOrNull() ?: 0.0) / 100).toString()
                      "." -> if (!display.contains(".")) display += "."
                      else -> onDigit(btn)
                    }
                  }
                  .testTag("calc_btn_$btn"),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = btn,
                  color = if (isOp) Color.Black else Color.White,
                  fontSize = 22.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }
  }
}
