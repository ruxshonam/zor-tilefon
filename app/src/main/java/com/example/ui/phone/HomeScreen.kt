package com.example.ui.phone

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppId
import com.example.model.AppItem
import com.example.ui.theme.*
import com.example.viewmodel.PhoneUiState
import com.example.viewmodel.PhoneViewModel

@Composable
fun HomeScreen(
  uiState: PhoneUiState,
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .testTag("home_screen")
  ) {
    // Wallpaper background
    if (uiState.wallpaperIndex == 0) {
      Image(
        painter = painterResource(id = R.drawable.wp_honor_default),
        contentDescription = "Wallpaper",
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )
    } else {
      val gradientColors = when (uiState.wallpaperIndex) {
        1 -> listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
        2 -> listOf(Color(0xFF2E0854), Color(0xFF5B147D), Color(0xFF8E2DE2))
        else -> listOf(Color(0xFF1F4037), Color(0xFF99F2C8))
      }
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Brush.verticalGradient(gradientColors))
      )
    }

    // Subtle dark tint for readability
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black.copy(alpha = 0.25f))
    )

    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(38.dp))

        // Honor Smart Widget: Clock + Weather
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.toggleControlCenter() }
            .testTag("weather_widget"),
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.45f))
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = uiState.timeString,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = uiState.dateString,
                color = HonorCyan,
                fontSize = 12.sp
              )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.WbSunny,
                contentDescription = "Ob-havo",
                tint = Color(0xFFFFB800),
                modifier = Modifier.size(28.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column(horizontalAlignment = Alignment.End) {
                Text(text = "24°C", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "Toshkent, Ochiq", color = Color.LightGray, fontSize = 11.sp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black.copy(alpha = 0.45f))
            .clickable { viewModel.openApp(AppId.PLAY_STORE) }
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .testTag("home_search_bar"),
          contentAlignment = Alignment.CenterStart
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Search, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(
              text = "Ilovalarni qidirish yoki yuklab olish...",
              color = Color.LightGray,
              fontSize = 13.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // App Grid (Installed apps not in dock)
        val gridApps = uiState.apps.filter { !it.isDockApp && it.isInstalled }
        LazyVerticalGrid(
          columns = GridCells.Fixed(4),
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          items(gridApps) { app ->
            AppIconItem(
              app = app,
              onClick = { viewModel.openApp(app.id) }
            )
          }
        }
      }

      // Bottom Dock
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Card(
          shape = RoundedCornerShape(28.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.55f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
          ) {
            val dockApps = uiState.apps.filter { it.isDockApp }
            dockApps.forEach { app ->
              AppIconItem(
                app = app,
                isDock = true,
                onClick = { viewModel.openApp(app.id) }
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun AppIconItem(
  app: AppItem,
  isDock: Boolean = false,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .clickable { onClick() }
      .testTag("app_icon_${app.id.name.lowercase()}"),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box(contentAlignment = Alignment.TopEnd) {
      // App Icon Box
      val (bgBrush, iconVector, tintColor) = getAppStyle(app.id)
      Box(
        modifier = Modifier
          .size(if (isDock) 52.dp else 56.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(bgBrush),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = iconVector,
          contentDescription = app.name,
          tint = tintColor,
          modifier = Modifier.size(if (isDock) 28.dp else 30.dp)
        )
      }

      // Notification Badge
      if (app.badgeCount > 0) {
        Box(
          modifier = Modifier
            .offset(x = 4.dp, y = (-4).dp)
            .size(18.dp)
            .clip(CircleShape)
            .background(Color.Red),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = app.badgeCount.toString(),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    if (!isDock) {
      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = app.name,
        color = Color.White,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
      )
    }
  }
}

fun getAppStyle(appId: AppId): Triple<Brush, ImageVector, Color> {
  return when (appId) {
    AppId.YOUTUBE -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFFF0000), Color(0xFFCC0000))),
      Icons.Default.PlayArrow,
      Color.White
    )
    AppId.INSTAGRAM -> Triple(
      Brush.linearGradient(listOf(InstagramGradientStart, InstagramGradientMid, InstagramGradientEnd)),
      Icons.Default.CameraAlt,
      Color.White
    )
    AppId.PLAY_STORE -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF00C9FF), Color(0xFF92FE9D))),
      Icons.Default.ShoppingBag,
      Color.Black
    )
    AppId.GAME_FLAPPY -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFFFB300), Color(0xFFF57C00))),
      Icons.Default.Flight,
      Color.White
    )
    AppId.GAME_TICTACTOE -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF8E2DE2), Color(0xFF4A00E0))),
      Icons.Default.GridOn,
      Color.White
    )
    AppId.GAME_REFLEX -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFFF416C), Color(0xFFFF4B2B))),
      Icons.Default.Bolt,
      Color.White
    )
    AppId.GAME_2048 -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
      Icons.Default.Extension,
      Color.White
    )
    AppId.PHONE -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF10B981), Color(0xFF059669))),
      Icons.Default.Call,
      Color.White
    )
    AppId.MESSAGES -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF0088CC), Color(0xFF0072B2))),
      Icons.Default.Send,
      Color.White
    )
    AppId.CAMERA -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))),
      Icons.Default.PhotoCamera,
      HonorCyan
    )
    AppId.GALLERY -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFEC4899), Color(0xFFBE185D))),
      Icons.Default.Collections,
      Color.White
    )
    AppId.MUSIC -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF6366F1), Color(0xFF4338CA))),
      Icons.Default.Headphones,
      Color.White
    )
    AppId.CALCULATOR -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFF97316), Color(0xFFEA580C))),
      Icons.Default.Calculate,
      Color.White
    )
    AppId.NOTES -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFEAB308), Color(0xFFCA8A04))),
      Icons.Default.EditNote,
      Color.White
    )
    AppId.TIKTOK -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF000000), Color(0xFF25F4EE))),
      Icons.Default.VideoLibrary,
      Color.White
    )
    AppId.GAME_SUBWAY -> Triple(
      Brush.verticalGradient(listOf(Color(0xFFE65100), Color(0xFFFF9800))),
      Icons.Default.DirectionsRun,
      Color.White
    )
    AppId.WEATHER -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF0288D1), Color(0xFF26C6DA))),
      Icons.Default.Cloud,
      Color.White
    )
    AppId.COMPASS -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF37474F), Color(0xFF78909C))),
      Icons.Default.Explore,
      Color.White
    )
    AppId.TELEGRAM -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF0088CC), Color(0xFF29B6F6))),
      Icons.Default.Send,
      Color.White
    )
    AppId.SETTINGS -> Triple(
      Brush.verticalGradient(listOf(Color(0xFF475569), Color(0xFF334155))),
      Icons.Default.Settings,
      Color.White
    )
  }
}
