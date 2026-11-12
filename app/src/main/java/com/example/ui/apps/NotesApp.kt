package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HonorCyan

data class NoteItem(val id: String, val title: String, val content: String, val date: String)

@Composable
fun NotesApp(
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val notes = remember {
    mutableStateListOf(
      NoteItem("1", "Honor 400 Lite Paroli", "Ekran qulfi: 1234. Barmoq izi qo'shilgan.", "15-Oktabr"),
      NoteItem("2", "Yutuber uchun reja", "1. Honor 400 Lite test videosi\n2. 4 ta o'yin gameplayi\n3. Instagram reels yuklash", "14-Oktabr"),
      NoteItem("3", "Bozorlik ro'yxati", "Meva, non, choy, shirinlik", "12-Oktabr")
    )
  }

  var showAddDialog by remember { mutableStateOf(false) }
  var noteTitle by remember { mutableStateOf("") }
  var noteContent by remember { mutableStateOf("") }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("notes_app")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 16.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
          }
          Text("Qaydlar 📝", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        IconButton(onClick = { showAddDialog = true }, modifier = Modifier.testTag("add_note_btn")) {
          Icon(Icons.Default.Add, contentDescription = "Qo'shish", tint = HonorCyan)
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(notes) { note ->
          Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(note.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                IconButton(onClick = { notes.remove(note) }) {
                  Icon(Icons.Default.Delete, contentDescription = "O'chirish", tint = Color.Gray, modifier = Modifier.size(18.dp))
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(note.content, color = Color.LightGray, fontSize = 13.sp)
              Spacer(modifier = Modifier.height(6.dp))
              Text(note.date, color = HonorCyan, fontSize = 10.sp)
            }
          }
        }
      }
    }

    if (showAddDialog) {
      AlertDialog(
        onDismissRequest = { showAddDialog = false },
        title = { Text("Yangi Qayd", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            OutlinedTextField(
              value = noteTitle,
              onValueChange = { noteTitle = it },
              label = { Text("Sarlavha") },
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = noteContent,
              onValueChange = { noteContent = it },
              label = { Text("Matn") },
              modifier = Modifier.fillMaxWidth(),
              colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (noteTitle.isNotBlank()) {
                notes.add(0, NoteItem(System.currentTimeMillis().toString(), noteTitle, noteContent, "Bugun"))
                noteTitle = ""
                noteContent = ""
                showAddDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan)
          ) {
            Text("Saqlash", color = Color.Black)
          }
        },
        dismissButton = {
          TextButton(onClick = { showAddDialog = false }) {
            Text("Bekor qilish", color = Color.LightGray)
          }
        },
        containerColor = Color(0xFF1E293B)
      )
    }
  }
}
