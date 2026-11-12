package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.model.ChatMessage
import com.example.ui.theme.HonorCyan
import com.example.viewmodel.PhoneViewModel

@Composable
fun MessagesApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val conversations by viewModel.conversations.collectAsState()
  val selectedChatId by viewModel.selectedChatId.collectAsState()

  var messageInput by remember { mutableStateOf("") }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("messages_app")
  ) {
    if (selectedChatId != null) {
      val chat = conversations.find { it.id == selectedChatId }
      if (chat != null) {
        Column(modifier = Modifier.fillMaxSize()) {
          // Chat Top Header
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF1E293B))
              .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = { viewModel.backToChatList() }, modifier = Modifier.testTag("chat_back_btn")) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
            }
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(HonorCyan.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Text(chat.avatarText, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
              Text(chat.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
              Text(chat.status, color = HonorCyan, fontSize = 11.sp)
            }
            IconButton(onClick = { viewModel.startCall(chat.name) }) {
              Icon(Icons.Default.Call, contentDescription = "Qo'ng'iroq", tint = HonorCyan)
            }
          }

          // Messages List
          LazyColumn(
            modifier = Modifier
              .fillMaxWidth()
              .weight(1f)
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(chat.messages) { message ->
              ChatBubble(message = message)
            }
          }

          // Message Input Field
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(Color(0xFF1E293B))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = messageInput,
              onValueChange = { messageInput = it },
              placeholder = { Text("Xabar yozing...", color = Color.Gray, fontSize = 13.sp) },
              modifier = Modifier
                .weight(1f)
                .testTag("chat_message_input"),
              shape = RoundedCornerShape(20.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = HonorCyan,
                unfocusedBorderColor = Color(0xFF334155)
              ),
              singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
              onClick = {
                if (messageInput.isNotBlank()) {
                  viewModel.sendMessage(chat.id, messageInput)
                  messageInput = ""
                }
              },
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(HonorCyan)
                .testTag("chat_send_btn")
            ) {
              Icon(Icons.Default.Send, contentDescription = "Yuborish", tint = Color.Black)
            }
          }
        }
      }
    } else {
      // Conversations List
      Column(modifier = Modifier.fillMaxSize()) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.goHome() }) {
              Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
            }
            Text("Xabarlar (Telegram)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
          }
          IconButton(onClick = {}) {
            Icon(Icons.Default.Edit, contentDescription = "Yangi chat", tint = HonorCyan)
          }
        }

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          items(conversations) { chat ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.selectChat(chat.id) }
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("chat_item_${chat.id}"),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(48.dp)
                  .clip(CircleShape)
                  .background(Color(0xFF1E293B)),
                contentAlignment = Alignment.Center
              ) {
                Text(chat.avatarText, fontSize = 20.sp)
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column(modifier = Modifier.weight(1f)) {
                Text(chat.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                val lastMsg = chat.messages.lastOrNull()?.text ?: ""
                Text(lastMsg, color = Color.LightGray, fontSize = 12.sp, maxLines = 1)
              }
              if (chat.unreadCount > 0) {
                Box(
                  modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(HonorCyan),
                  contentAlignment = Alignment.Center
                ) {
                  Text("${chat.unreadCount}", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
            Divider(color = Color(0xFF1E293B), thickness = 0.5.dp)
          }
        }
      }
    }
  }
}

@Composable
fun ChatBubble(message: ChatMessage) {
  Box(
    modifier = Modifier.fillMaxWidth(),
    contentAlignment = if (message.isFromUser) Alignment.CenterEnd else Alignment.CenterStart
  ) {
    Card(
      shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = if (message.isFromUser) 16.dp else 4.dp,
        bottomEnd = if (message.isFromUser) 4.dp else 16.dp
      ),
      colors = CardDefaults.cardColors(
        containerColor = if (message.isFromUser) HonorCyan else Color(0xFF1E293B)
      ),
      modifier = Modifier.widthIn(max = 260.dp)
    ) {
      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = message.text,
          color = if (message.isFromUser) Color.Black else Color.White,
          fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = message.time,
          color = if (message.isFromUser) Color(0xFF334155) else Color.Gray,
          fontSize = 10.sp,
          modifier = Modifier.align(Alignment.End)
        )
      }
    }
  }
}
