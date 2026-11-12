package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppId
import com.example.model.AppItem
import com.example.ui.phone.getAppStyle
import com.example.ui.theme.HonorCyan
import com.example.ui.theme.PhoneGreen
import com.example.viewmodel.PhoneViewModel

val PlayStoreGreen = Color(0xFF01875F)
val PlayStoreDarkBg = Color(0xFF1F1F1F)
val PlayCardBg = Color(0xFF2A2A2A)

@Composable
fun AppStoreApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedDevice by remember { mutableStateOf("Windows") } // Windows, Telefon, Planshet, TV
  var selectedTab by remember { mutableStateOf("O'yinlar") } // O'yinlar, Ilovalar, Kitoblar, Bolalar
  var selectedAppDetail by remember { mutableStateOf<AppItem?>(null) }

  val devices = listOf("💻 Windows", "📱 Telefon", "🖥️ Planshet", "📺 TV")
  val tabs = listOf("O'yinlar", "Ilovalar", "Kitoblar", "Bolalar")

  val storeApps = uiState.apps.filter {
    !it.isDockApp && (searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true))
  }

  val gamesList = storeApps.filter { it.category == "O'yin" }
  val appsList = storeApps.filter { it.category != "O'yin" }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(PlayStoreDarkBg)
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("google_play_store_app")
  ) {
    if (selectedAppDetail != null) {
      // Official Google Play Detail Page
      val app = uiState.apps.find { it.id == selectedAppDetail!!.id } ?: selectedAppDetail!!
      GooglePlayAppDetailPage(
        app = app,
        onBack = { selectedAppDetail = null },
        onInstall = { viewModel.downloadApp(app.id) },
        onOpen = { viewModel.openApp(app.id) },
        onUninstall = { viewModel.uninstallApp(app.id) }
      )
    } else {
      // Main Google Play Store Screen
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Google Play App Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.goHome() }) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
            }
            // Google Play Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(28.dp)
                  .clip(RoundedCornerShape(6.dp))
                  .background(
                    Brush.linearGradient(
                      listOf(Color(0xFF00E5BE), Color(0xFF00C9FF), Color(0xFFFF5252), Color(0xFFFFD600))
                    )
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Google Play", tint = Color.White, modifier = Modifier.size(18.dp))
              }
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Google Play",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                letterSpacing = (-0.5).sp
              )
            }
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = {}) {
              Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = Color.White)
            }
            // User Avatar (0 account barrier)
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PlayStoreGreen),
              contentAlignment = Alignment.Center
            ) {
              Text("H", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }

        // Search Bar (Google Play styled)
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF2E2E2E))
            .padding(horizontal = 14.dp, vertical = 2.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
              value = searchQuery,
              onValueChange = { searchQuery = it },
              placeholder = { Text("O'yinlar va dasturlarni qidirish...", color = Color.Gray, fontSize = 13.sp) },
              modifier = Modifier
                .weight(1f)
                .testTag("play_store_search_input"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
              ),
              singleLine = true
            )
            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
          }
        }

        // Device Selector Row (?device=windows style)
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(devices) { device ->
            val isSelected = selectedDevice in device
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isSelected) PlayStoreGreen.copy(alpha = 0.25f) else Color(0xFF2E2E2E))
                .border(1.dp, if (isSelected) PlayStoreGreen else Color.Transparent, RoundedCornerShape(16.dp))
                .clickable { selectedDevice = if (device.contains("Windows")) "Windows" else if (device.contains("Telefon")) "Telefon" else "Boshqa" }
                .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text(
                text = device,
                color = if (isSelected) Color(0xFF70EFB8) else Color.White,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            }
          }
        }

        // Store Category Tabs (O'yinlar, Ilovalar, Kitoblar, Bolalar)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround
        ) {
          tabs.forEach { tab ->
            val isTabSelected = selectedTab == tab
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.clickable { selectedTab = tab }
            ) {
              Text(
                text = tab,
                color = if (isTabSelected) PlayStoreGreen else Color.LightGray,
                fontWeight = if (isTabSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp,
                modifier = Modifier.padding(bottom = 6.dp)
              )
              if (isTabSelected) {
                Box(
                  modifier = Modifier
                    .width(36.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(PlayStoreGreen)
                )
              }
            }
          }
        }

        Divider(color = Color(0xFF2E2E2E), thickness = 1.dp)

        // Main Store Scroll Content
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 14.dp)
        ) {
          // Hero Promotional Banner (Windows & Android Games Spotlight)
          item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Tavsiya etilgan o'yinlar (Windows & Android)",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Icon(Icons.Default.ArrowForward, contentDescription = null, tint = PlayStoreGreen, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Featured Hero Carousel
            LazyRow(
              horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
              items(gamesList.take(4)) { game ->
                GooglePlayHeroCard(
                  app = game,
                  onClick = { selectedAppDetail = game },
                  onInstall = { viewModel.downloadApp(game.id) }
                )
              }
            }
          }

          // Top Free Games Ranking (#1, #2, #3, #4, #5)
          item {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Eng sara bepul o'yinlar reytingi 🏆",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text("Hammasi", color = PlayStoreGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Ranked List of Games
          itemsIndexed(gamesList) { index, game ->
            GooglePlayRankedItem(
              rank = index + 1,
              app = game,
              onClick = { selectedAppDetail = game },
              onInstall = { viewModel.downloadApp(game.id) },
              onOpen = { viewModel.openApp(game.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          // Apps section
          item {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "Mashhur ilovalar va vositalar 📱",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(10.dp))
          }

          items(appsList) { app ->
            GooglePlayRankedItem(
              rank = null,
              app = app,
              onClick = { selectedAppDetail = app },
              onInstall = { viewModel.downloadApp(app.id) },
              onOpen = { viewModel.openApp(app.id) }
            )
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }
  }
}

@Composable
fun GooglePlayHeroCard(
  app: AppItem,
  onClick: () -> Unit,
  onInstall: () -> Unit
) {
  val (bgBrush, iconVector, tintColor) = getAppStyle(app.id)

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = PlayCardBg),
    modifier = Modifier
      .width(260.dp)
      .clickable { onClick() }
      .testTag("play_hero_${app.id.name.lowercase()}")
  ) {
    Column {
      // Artwork Banner Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(120.dp)
          .background(bgBrush),
        contentAlignment = Alignment.Center
      ) {
        Icon(iconVector, contentDescription = null, tint = tintColor.copy(alpha = 0.8f), modifier = Modifier.size(54.dp))
        Box(
          modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text("Windows / Android", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
      }

      // App Details
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgBrush),
          contentAlignment = Alignment.Center
        ) {
          Icon(iconVector, contentDescription = null, tint = tintColor, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(app.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
          Text("${app.category} • 4.9 ★ • ${app.sizeMb} MB", color = Color.LightGray, fontSize = 10.sp)
        }

        if (!app.isInstalled && !app.isDownloading) {
          Button(
            onClick = onInstall,
            colors = ButtonDefaults.buttonColors(containerColor = PlayStoreGreen),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
          ) {
            Text("O'rnatish", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
fun GooglePlayRankedItem(
  rank: Int?,
  app: AppItem,
  onClick: () -> Unit,
  onInstall: () -> Unit,
  onOpen: () -> Unit
) {
  val (bgBrush, iconVector, tintColor) = getAppStyle(app.id)

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .background(PlayCardBg)
      .clickable { onClick() }
      .padding(12.dp)
      .testTag("play_item_${app.id.name.lowercase()}"),
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Rank Number (#1, #2...)
    if (rank != null) {
      Text(
        text = "$rank",
        color = if (rank <= 3) PlayStoreGreen else Color.LightGray,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        modifier = Modifier.width(22.dp)
      )
    }

    // App Icon
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(bgBrush),
      contentAlignment = Alignment.Center
    ) {
      Icon(iconVector, contentDescription = app.name, tint = tintColor, modifier = Modifier.size(26.dp))
    }

    Spacer(modifier = Modifier.width(12.dp))

    // App Information
    Column(modifier = Modifier.weight(1f)) {
      Text(app.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
      Text("${app.category} • 4.9 ★ • ${app.sizeMb} MB", color = Color.Gray, fontSize = 11.sp)

      if (app.isDownloading) {
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
          progress = { app.downloadProgress },
          modifier = Modifier.fillMaxWidth().height(3.dp),
          color = PlayStoreGreen
        )
        Text("Yuklanmoqda: ${(app.downloadProgress * 100).toInt()}%", color = PlayStoreGreen, fontSize = 10.sp)
      }
    }

    Spacer(modifier = Modifier.width(8.dp))

    // Action button
    if (app.isInstalled) {
      Button(
        onClick = onOpen,
        colors = ButtonDefaults.buttonColors(containerColor = PlayStoreGreen),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("play_open_${app.id.name.lowercase()}")
      ) {
        Text("Ochish", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    } else if (app.isDownloading) {
      CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PlayStoreGreen, strokeWidth = 2.dp)
    } else {
      Button(
        onClick = onInstall,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        modifier = Modifier.testTag("play_install_${app.id.name.lowercase()}")
      ) {
        Text("O'rnatish", color = PlayStoreGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}

@Composable
fun GooglePlayAppDetailPage(
  app: AppItem,
  onBack: () -> Unit,
  onInstall: () -> Unit,
  onOpen: () -> Unit,
  onUninstall: () -> Unit
) {
  val (bgBrush, iconVector, tintColor) = getAppStyle(app.id)

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(PlayStoreDarkBg)
      .testTag("play_app_detail_page")
  ) {
    // Detail Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {}) {
          Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.White)
        }
        IconButton(onClick = {}) {
          Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.White)
        }
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 16.dp)
    ) {
      // App Header (Icon, Title, Studio)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(72.dp)
              .clip(RoundedCornerShape(18.dp))
              .background(bgBrush),
            contentAlignment = Alignment.Center
          ) {
            Icon(iconVector, contentDescription = app.name, tint = tintColor, modifier = Modifier.size(40.dp))
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column {
            Text(app.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text("Google Play Verified Studio", color = PlayStoreGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text("Ilova ichida xaridlar mavjud emas • Bepul", color = Color.Gray, fontSize = 11.sp)
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Play Stats Row (4.9 ★, 100M+, PEGI 3)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("4.9", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(14.dp))
            }
            Text("142K sharh", color = Color.Gray, fontSize = 10.sp)
          }

          Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFF333333))

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${app.sizeMb} MB", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("Yuklab olish", color = Color.Gray, fontSize = 10.sp)
          }

          Divider(modifier = Modifier.height(24.dp).width(1.dp), color = Color(0xFF333333))

          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("100M+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("O'rnatilgan", color = Color.Gray, fontSize = 10.sp)
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Big Green Install Button
        if (app.isDownloading) {
          Column(modifier = Modifier.fillMaxWidth()) {
            LinearProgressIndicator(
              progress = { app.downloadProgress },
              modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
              color = PlayStoreGreen
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("O'rnatilmoqda: ${(app.downloadProgress * 100).toInt()}%...", color = PlayStoreGreen, fontSize = 12.sp)
          }
        } else if (app.isInstalled) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = onUninstall,
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF333333)),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.weight(1f)
            ) {
              Text("O'chirish", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
            }
            Button(
              onClick = onOpen,
              colors = ButtonDefaults.buttonColors(containerColor = PlayStoreGreen),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.weight(1.5f).testTag("detail_open_btn")
            ) {
              Text("O'ynash / Ochish 🎮", color = Color.White, fontWeight = FontWeight.Bold)
            }
          }
        } else {
          Button(
            onClick = onInstall,
            colors = ButtonDefaults.buttonColors(containerColor = PlayStoreGreen),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().testTag("detail_install_btn")
          ) {
            Text("O'rnatish (Google Play)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Screenshots Gallery
        Text("Ekran suratlari", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          items(3) { index ->
            Box(
              modifier = Modifier
                .width(180.dp)
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bgBrush),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(iconVector, contentDescription = null, tint = tintColor.copy(alpha = 0.7f), modifier = Modifier.size(36.dp))
                Text("Gameplay Preview ${index + 1}", color = Color.White.copy(alpha = 0.8f), fontSize = 11.sp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // About this app
        Text("Ushbu o'yin haqida", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          "${app.name} — eng yuqori reytingli o'yinlardan biri. Windows va Android qurilmalarida ajoyib 60 FPS bilan ishlaydi. Hech qanday hisob yoki ro'yxatdan o'tish talab etilmaydi.",
          color = Color.LightGray,
          fontSize = 12.sp,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }
}
