package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
fun WeatherApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("weather_app")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("weather_back_btn")) {
          Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
        }
        Text("Ob-havo Pro ☀️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Box(modifier = Modifier.size(48.dp))
      }

      Spacer(modifier = Modifier.height(16.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Text("Toshkent, O'zbekiston", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(64.dp))
          Spacer(modifier = Modifier.height(6.dp))
          Text("24°C", color = Color.White, fontSize = 52.sp, fontWeight = FontWeight.Light)
          Text("Quyoshli va ochiq havo", color = HonorCyan, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(14.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Text("Namlik: 42%", color = Color.LightGray, fontSize = 12.sp)
            Text("Shamol: 4 m/s", color = Color.LightGray, fontSize = 12.sp)
            Text("Bosim: 760 mm", color = Color.LightGray, fontSize = 12.sp)
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text("7 Kunlik Prognoz", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))

      Spacer(modifier = Modifier.height(10.dp))

      val weekForecast = listOf(
        Pair("Juma", "25°C • Ochiq ☀️"),
        Pair("Shanba", "26°C • Issiq ☀️"),
        Pair("Yakshanba", "23°C • Bulutli ⛅"),
        Pair("Dushanba", "21°C • Shamol 💨"),
        Pair("Seshanba", "24°C • Ochiq ☀️")
      )

      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(weekForecast.size) { i ->
          val item = weekForecast[i]
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
          ) {
            Row(
              modifier = Modifier.fillMaxWidth().padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(item.first, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(item.second, color = Color.LightGray, fontSize = 13.sp)
            }
          }
        }
      }
    }
  }
}
