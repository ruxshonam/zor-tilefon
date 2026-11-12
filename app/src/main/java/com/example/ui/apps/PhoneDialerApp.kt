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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ContactItem
import com.example.ui.theme.HonorCyan
import com.example.ui.theme.PhoneGreen
import com.example.ui.theme.PhoneRed
import com.example.viewmodel.PhoneViewModel

@Composable
fun PhoneDialerApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val uiState by viewModel.uiState.collectAsState()
  var dialedNumber by remember { mutableStateOf("") }
  var currentTab by remember { mutableStateOf("keypad") } // "keypad" or "contacts"

  val contacts = listOf(
    ContactItem("1", "Onajonim ❤️", "+998 90 123 45 67", "❤️"),
    ContactItem("2", "Sardor Do'stim 🎮", "+998 93 987 65 43", "🎮"),
    ContactItem("3", "Honor Servis Markazi", "+998 71 200 00 00", "🏢"),
    ContactItem("4", "Tez Yordam", "103", "🚑")
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("phone_dialer_app")
  ) {
    if (uiState.isCallActive) {
      // Active In-Call Screen
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(top = 40.dp)
        ) {
          Box(
            modifier = Modifier
              .size(90.dp)
              .clip(CircleShape)
              .background(HonorCyan.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Person, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(54.dp))
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(uiState.activeCallName ?: "Noma'lum", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
          val min = uiState.callSeconds / 60
          val sec = uiState.callSeconds % 60
          Text(String.format("%02d:%02d", min, sec), color = HonorCyan, fontSize = 16.sp, fontWeight = FontWeight.Medium)
          Text("Honor 400 Lite HD Ovoz", color = Color.Gray, fontSize = 12.sp)
        }

        // Call Actions
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          IconButton(onClick = {}, modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF1E293B))) {
            Icon(Icons.Default.MicOff, contentDescription = "Mute", tint = Color.White)
          }
          IconButton(onClick = {}, modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF1E293B))) {
            Icon(Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White)
          }
          IconButton(onClick = {}, modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF1E293B))) {
            Icon(Icons.Default.Dialpad, contentDescription = "Keypad", tint = Color.White)
          }
        }

        // End Call Button
        IconButton(
          onClick = { viewModel.endCall() },
          modifier = Modifier
            .size(68.dp)
            .clip(CircleShape)
            .background(PhoneRed)
            .testTag("end_call_button")
        ) {
          Icon(Icons.Default.CallEnd, contentDescription = "Tugatish", tint = Color.White, modifier = Modifier.size(36.dp))
        }
      }
    } else {
      // Keypad / Contacts View
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { viewModel.goHome() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
          }
          Text("Telefon 📞", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          // Simulation trigger button
          Button(
            onClick = { viewModel.simulateIncomingCall() },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("simulate_incoming_call_btn")
          ) {
            Text("Qo'ng'iroq kutish", color = HonorCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }

        // Call Barring Mode Banner ("Telefon qilib bo'lmaydigan qilish")
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (uiState.isCallBlocked) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF1E293B)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                if (uiState.isCallBlocked) Icons.Default.PhoneDisabled else Icons.Default.PhoneEnabled,
                contentDescription = null,
                tint = if (uiState.isCallBlocked) PhoneRed else HonorCyan,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (uiState.isCallBlocked) "Qo'ng'iroqlar taqiqlangan 🚫" else "Qo'ng'iroq qilish mumkin",
                  color = Color.White,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (uiState.isCallBlocked) "Telefon qilib bo'lmaydi (Bloklangan)" else "Qo'ng'iroq qilishni taqiqlash",
                  color = if (uiState.isCallBlocked) Color(0xFFFF8888) else Color.Gray,
                  fontSize = 10.sp
                )
              }
            }
            Switch(
              checked = uiState.isCallBlocked,
              onCheckedChange = { viewModel.toggleCallBlock() },
              colors = SwitchDefaults.colors(checkedThumbColor = PhoneRed, checkedTrackColor = PhoneRed.copy(alpha = 0.5f)),
              modifier = Modifier.testTag("toggle_call_block_switch")
            )
          }
        }

        // Call Block Alert Message if user tries to dial
        if (uiState.callBlockReason != null) {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PhoneRed)
          ) {
            Row(
              modifier = Modifier.padding(10.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
              Spacer(modifier = Modifier.width(8.dp))
              Text(uiState.callBlockReason!!, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        // Tabs
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1E293B))
            .padding(4.dp)
        ) {
          Button(
            onClick = { currentTab = "keypad" },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (currentTab == "keypad") HonorCyan else Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Tugmalar", color = if (currentTab == "keypad") Color.Black else Color.White, fontSize = 12.sp)
          }
          Button(
            onClick = { currentTab = "contacts" },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (currentTab == "contacts") HonorCyan else Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Kontaktlar", color = if (currentTab == "contacts") Color.Black else Color.White, fontSize = 12.sp)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (currentTab == "keypad") {
          // Number display
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (dialedNumber.isEmpty()) "Raqamni tering" else dialedNumber,
              color = if (dialedNumber.isEmpty()) Color.Gray else Color.White,
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold
            )
            if (dialedNumber.isNotEmpty()) {
              IconButton(onClick = { dialedNumber = dialedNumber.dropLast(1) }) {
                Icon(Icons.Default.Backspace, contentDescription = "O'chirish", tint = Color.LightGray)
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // 12-button Keypad
          val keys = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("*", "0", "#")
          )

          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            for (row in keys) {
              Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                for (key in row) {
                  Box(
                    modifier = Modifier
                      .size(58.dp)
                      .clip(CircleShape)
                      .background(Color(0xFF1E293B))
                      .clickable { dialedNumber += key }
                      .testTag("dialer_key_$key"),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(key, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Call Button
          IconButton(
            onClick = {
              if (dialedNumber.isNotBlank()) {
                viewModel.startCall(dialedNumber)
              }
            },
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(if (uiState.isCallBlocked) Color.Gray else PhoneGreen)
              .testTag("start_call_button")
          ) {
            Icon(
              if (uiState.isCallBlocked) Icons.Default.PhoneDisabled else Icons.Default.Call,
              contentDescription = "Qo'ng'iroq",
              tint = Color.White,
              modifier = Modifier.size(32.dp)
            )
          }
        } else {
          // Contacts Tab
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            contacts.forEach { contact ->
              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .clickable { viewModel.startCall(contact.name) }
                  .testTag("contact_item_${contact.id}"),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(16.dp)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .clip(CircleShape)
                      .background(HonorCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(contact.avatarText, fontSize = 18.sp)
                  }
                  Spacer(modifier = Modifier.width(12.dp))
                  Column(modifier = Modifier.weight(1f)) {
                    Text(contact.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(contact.number, color = Color.Gray, fontSize = 12.sp)
                  }
                  IconButton(onClick = { viewModel.startCall(contact.name) }) {
                    Icon(
                      if (uiState.isCallBlocked) Icons.Default.PhoneDisabled else Icons.Default.Call,
                      contentDescription = null,
                      tint = if (uiState.isCallBlocked) PhoneRed else PhoneGreen
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
