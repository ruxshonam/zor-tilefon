package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HonorCyan
import com.example.ui.theme.PhoneRed
import com.example.viewmodel.PhoneUiState
import com.example.viewmodel.PhoneViewModel

@Composable
fun SettingsApp(
  uiState: PhoneUiState,
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  var showWallpaperDialog by remember { mutableStateOf(false) }
  var showChangePinDialog by remember { mutableStateOf(false) }
  var newPinInput by remember { mutableStateOf("") }
  var pinSavedMessage by remember { mutableStateOf(false) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("settings_app")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.goHome() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
          }
          Text("Sozlamalar ⚙️", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Text("MagicOS 8.0", color = HonorCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // About Phone Hero Card
        item {
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(HonorCyan.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.PhoneAndroid, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text("HONOR 400 Lite", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                  Text("Model: HNR-400L-UZ", color = Color.Gray, fontSize = 12.sp)
                }
              }

              Spacer(modifier = Modifier.height(14.dp))
              Divider(color = Color(0xFF334155))
              Spacer(modifier = Modifier.height(14.dp))

              SpecRow("Protsessor", "Snapdragon 7 Gen 3 (4nm)")
              SpecRow("Xotira (RAM)", "12 GB + 8 GB Turbo")
              SpecRow("Doimiy Xotira", "512 GB (45 GB band)")
              SpecRow("Kamera", "108 MP Ultra Aniq AI Matrix")
              SpecRow("Batareya", "5000 mAh (35W Fast Charge)")
              SpecRow("Tizim", "MagicOS 8.0 (Android 15)")
            }
          }
        }

        // Security & Password Section ("PAROL QILADIGAN BO'LSIN")
        item {
          Text("Xavfsizlik va Parol (PIN)", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        item {
          SettingsTile(
            icon = Icons.Default.Lock,
            title = "Ekran Parolini O'zgartirish",
            subtitle = "Joriy parol: ${uiState.pinCode} (O'zgartirish uchun bosing)",
            onClick = {
              newPinInput = ""
              showChangePinDialog = true
            }
          )
        }

        item {
          SettingsToggleTile(
            icon = Icons.Default.Password,
            title = "Parol bilan qulflash",
            subtitle = if (uiState.isPinLockEnabled) "Parol yoqilgan (PIN: ${uiState.pinCode})" else "Parolsiz ochiladi",
            isChecked = uiState.isPinLockEnabled,
            onToggle = { viewModel.togglePinLockEnabled(!uiState.isPinLockEnabled) }
          )
        }

        // Call Control Section ("TELFON QILIB BO'LMAYATIN QILADIGAN QILISH")
        item {
          Text("Qo'ng'iroqlarni Boshqarish", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        item {
          SettingsToggleTile(
            icon = Icons.Default.PhoneDisabled,
            title = "Qo'ng'iroqlarni Taqiqlash / Bloklash",
            subtitle = if (uiState.isCallBlocked) "Faol: Telefon qilib ham, qabul qilib ham bo'lmaydi" else "O'chiq: Qo'ng'iroq qilish mumkin",
            isChecked = uiState.isCallBlocked,
            activeColor = PhoneRed,
            onToggle = { viewModel.toggleCallBlock() }
          )
        }

        // Customization Section
        item {
          Text("Ekranni sozlash", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        item {
          SettingsTile(
            icon = Icons.Default.Wallpaper,
            title = "Fon rasmini almashtirish",
            subtitle = "Siz tanlagan fon: ${when(uiState.wallpaperIndex) { 0 -> "Honor Silk (Default)"; 1 -> "Cyber Neon"; 2 -> "Deep Galaxy"; else -> "Emerald Glow" }}",
            onClick = { showWallpaperDialog = true }
          )
        }

        item {
          Text("Tarmoq va Ulanish", color = HonorCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        item {
          SettingsToggleTile(
            icon = Icons.Default.Wifi,
            title = "Wi-Fi Tarmoq",
            subtitle = if (uiState.isWifiOn) "5G Tarmoqqa ulangan" else "O'chiq",
            isChecked = uiState.isWifiOn,
            onToggle = { viewModel.toggleWifi() }
          )
        }

        item {
          SettingsToggleTile(
            icon = Icons.Default.Bluetooth,
            title = "Bluetooth",
            subtitle = if (uiState.isBluetoothOn) "Ulangan" else "O'chiq",
            isChecked = uiState.isBluetoothOn,
            onToggle = { viewModel.toggleBluetooth() }
          )
        }

        item {
          SettingsToggleTile(
            icon = Icons.Default.AirplanemodeActive,
            title = "Parvoz rejimi",
            subtitle = if (uiState.isAirplaneMode) "Yoqilgan (Qo'ng'iroqlar uzilgan)" else "O'chirilgan",
            isChecked = uiState.isAirplaneMode,
            onToggle = { viewModel.toggleAirplane() }
          )
        }
      }
    }

    // Change PIN Dialog
    if (showChangePinDialog) {
      AlertDialog(
        onDismissRequest = { showChangePinDialog = false },
        title = { Text("Yangi 4 Xonali Parol 🔒", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Text("Honor 400 Lite ekrani uchun yangi 4 xonali PIN kodni tering:", color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
              value = newPinInput,
              onValueChange = { if (it.length <= 4 && it.all { ch -> ch.isDigit() }) newPinInput = it },
              placeholder = { Text("Masalan: 5566") },
              singleLine = true,
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = HonorCyan
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("new_pin_input_field")
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (newPinInput.length == 4) {
                viewModel.setPinCode(newPinInput)
                showChangePinDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
            modifier = Modifier.testTag("save_new_pin_btn")
          ) {
            Text("Saqlash", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showChangePinDialog = false }) {
            Text("Bekor qilish", color = Color.LightGray)
          }
        },
        containerColor = Color(0xFF1E293B)
      )
    }

    // Wallpaper Selection Dialog
    if (showWallpaperDialog) {
      AlertDialog(
        onDismissRequest = { showWallpaperDialog = false },
        title = { Text("Fon Rasmini Tanlang 🎨", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val wallpapers = listOf(
              "Honor Silk Waves (Rasmiy)",
              "Cyberpunk Neon",
              "Deep Galaxy Violet",
              "Emerald Forest Glow"
            )
            wallpapers.forEachIndexed { index, name ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable {
                    viewModel.setWallpaper(index)
                    showWallpaperDialog = false
                  }
                  .testTag("wp_choice_$index"),
                colors = CardDefaults.cardColors(
                  containerColor = if (uiState.wallpaperIndex == index) HonorCyan.copy(alpha = 0.25f) else Color(0xFF1E293B)
                ),
                shape = RoundedCornerShape(12.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = uiState.wallpaperIndex == index,
                    onClick = {
                      viewModel.setWallpaper(index)
                      showWallpaperDialog = false
                    },
                    colors = RadioButtonDefaults.colors(selectedColor = HonorCyan)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(name, color = Color.White, fontSize = 13.sp)
                }
              }
            }
          }
        },
        confirmButton = {
          TextButton(onClick = { showWallpaperDialog = false }) {
            Text("Yopish", color = HonorCyan)
          }
        },
        containerColor = Color(0xFF1E293B)
      )
    }
  }
}

@Composable
fun SpecRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(label, color = Color.Gray, fontSize = 12.sp)
    Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
fun SettingsTile(
  icon: ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Color(0xFF334155)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(20.dp))
      }
      Spacer(modifier = Modifier.width(12.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(subtitle, color = Color.Gray, fontSize = 11.sp)
      }
      Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
  }
}

@Composable
fun SettingsToggleTile(
  icon: ImageVector,
  title: String,
  subtitle: String,
  isChecked: Boolean,
  activeColor: Color = HonorCyan,
  onToggle: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color(0xFF334155)),
          contentAlignment = Alignment.Center
        ) {
          Icon(icon, contentDescription = null, tint = activeColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Text(subtitle, color = Color.Gray, fontSize = 11.sp)
        }
      }
      Switch(
        checked = isChecked,
        onCheckedChange = { onToggle() },
        colors = SwitchDefaults.colors(checkedThumbColor = activeColor, checkedTrackColor = activeColor.copy(alpha = 0.5f))
      )
    }
  }
}
