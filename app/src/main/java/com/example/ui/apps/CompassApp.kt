package com.example.ui.apps

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HonorCyan

@Composable
fun CompassApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("compass_app")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("compass_back_btn")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("Raqamli Kompas 🧭", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Box(modifier = Modifier.size(48.dp))
      }

      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("342° SHIMOL", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
        Text("Toshkent 41°18'N 69°16'E • Balandlik: 450m", color = HonorCyan, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(28.dp))

        // Compass Rose Dial
        Box(
          modifier = Modifier
            .size(240.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E293B)),
          contentAlignment = Alignment.Center
        ) {
          Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            // North indicator (Red)
            drawLine(
              color = Color.Red,
              start = center,
              end = Offset(center.x, 30f),
              strokeWidth = 6f
            )
            // South indicator (White)
            drawLine(
              color = Color.White,
              start = center,
              end = Offset(center.x, size.height - 30f),
              strokeWidth = 6f
            )
            drawCircle(Color(0xFF0F172A), radius = 18f, center = center)
          }

          Text("N", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.TopCenter).padding(top = 10.dp))
          Text("S", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp))
          Text("W", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterStart).padding(start = 12.dp))
          Text("E", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterEnd).padding(end = 12.dp))
        }
      }

      Box(modifier = Modifier.height(60.dp))
    }
  }
}
