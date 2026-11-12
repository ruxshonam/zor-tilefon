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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ShortsVideo
import com.example.ui.theme.HonorCyan
import kotlinx.coroutines.delay

@Composable
fun TikTokApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val tikTokVideos = remember {
    listOf(
      ShortsVideo("tt1", "Honor 400 Lite MagicOS 8.0 sirlari! 🤫⚡", "@tech_uzbekistan", 125000, 3200, "Phonk Remix 2026", listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)),
      ShortsVideo("tt2", "PUBG Mobile da 1 vs 4 Cluch! 🎮💥", "@gamer_samarqand", 89000, 1500, "Gaming Victory Sound", listOf(0xFF8A2387, 0xFFE94057, 0xFFF27121)),
      ShortsVideo("tt3", "Toshkent City da yangi favvora shousi ✨🇺🇿", "@tashkent_live", 240000, 4800, "O'zbek Folk Lo-Fi", listOf(0xFF11998E, 0xFF38EF7D)),
      ShortsVideo("tt4", "Cristiano Ronaldo yangi jamoasida mashg'ulotda ⚽🔥", "@cr7_daily", 520000, 11000, "Cristiano Anthem", listOf(0xFF2E0854, 0xFF5B147D, 0xFF8E2DE2))
    )
  }

  var currentIndex by remember { mutableIntStateOf(0) }
  val currentVideo = tikTokVideos[currentIndex % tikTokVideos.size]
  var isLiked by remember { mutableStateOf(false) }
  var likesCount by remember { mutableIntStateOf(currentVideo.likes) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Brush.linearGradient(currentVideo.gradient.map { Color(it) }))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("tiktok_app")
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("tiktok_back_btn")) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
      }
      Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Obunalar", color = Color.Gray, fontSize = 14.sp)
        Text("Tavsiya (FYP)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }
      IconButton(onClick = {}) {
        Icon(Icons.Default.Search, contentDescription = "Qidiruv", tint = Color.White)
      }
    }

    // Right Action Sidebar
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp, bottom = 20.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Like
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = {
            isLiked = !isLiked
            likesCount = if (isLiked) likesCount + 1 else likesCount - 1
          },
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .testTag("tiktok_like_btn")
        ) {
          Icon(Icons.Default.Favorite, contentDescription = "Like", tint = if (isLiked) Color.Red else Color.White)
        }
        Text("$likesCount", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      // Comments
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = {},
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
        ) {
          Icon(Icons.Default.ChatBubble, contentDescription = "Izohlar", tint = Color.White)
        }
        Text("${currentVideo.commentsCount}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      // Next Video
      IconButton(
        onClick = {
          currentIndex = (currentIndex + 1) % tikTokVideos.size
          isLiked = false
          likesCount = tikTokVideos[currentIndex % tikTokVideos.size].likes
        },
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color(0xFF25F4EE))
          .testTag("tiktok_next_btn")
      ) {
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Keyingisi", tint = Color.Black, modifier = Modifier.size(28.dp))
      }
    }

    // Bottom Video Info
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(start = 16.dp, end = 80.dp, bottom = 16.dp)
    ) {
      Text(currentVideo.channel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Text(currentVideo.title, color = Color.White, fontSize = 13.sp)
      Spacer(modifier = Modifier.height(8.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.MusicNote, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(currentVideo.musicTitle, color = Color.LightGray, fontSize = 11.sp)
      }
    }
  }
}
