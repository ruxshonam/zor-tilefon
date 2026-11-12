package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
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
import com.example.model.AppId
import com.example.model.GalleryPhoto
import com.example.ui.theme.HonorCyan
import com.example.viewmodel.PhoneViewModel

@Composable
fun GalleryApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val photos by viewModel.photos.collectAsState()
  var selectedPhoto by remember { mutableStateOf<GalleryPhoto?>(null) }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("gallery_app")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = { viewModel.goHome() }) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
          }
          Text("Galereya (${photos.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Text("Honor 400 108MP", color = HonorCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      // Photos Grid
      LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        items(photos) { photo ->
          Box(
            modifier = Modifier
              .aspectRatio(1f)
              .clip(RoundedCornerShape(8.dp))
              .background(Brush.linearGradient(photo.colorGradient.map { Color(it) }))
              .clickable { selectedPhoto = photo }
              .testTag("gallery_photo_${photo.id}"),
            contentAlignment = Alignment.BottomStart
          ) {
            Box(
              modifier = Modifier
                .padding(4.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
              Text(photo.filterTag, color = Color.White, fontSize = 9.sp)
            }
          }
        }
      }
    }

    // Photo Details Dialog
    selectedPhoto?.let { photo ->
      AlertDialog(
        onDismissRequest = { selectedPhoto = null },
        title = { Text(photo.label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Brush.linearGradient(photo.colorGradient.map { Color(it) }))
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("Rejim: ${photo.filterTag} • Tiniqlik: 108MP Ultra HD", color = Color.LightGray, fontSize = 12.sp)
          }
        },
        confirmButton = {
          Button(
            onClick = {
              viewModel.addNewInstagramPost("Honor 400 Lite bilan olingan ajoyib surat! 📸 #magiccam")
              viewModel.openApp(AppId.INSTAGRAM)
              selectedPhoto = null
            },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan)
          ) {
            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Instagramga joylash", color = Color.Black, fontSize = 12.sp)
          }
        },
        dismissButton = {
          TextButton(
            onClick = {
              viewModel.deletePhoto(photo.id)
              selectedPhoto = null
            }
          ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("O'chirish", color = Color.Red, fontSize = 12.sp)
          }
        },
        containerColor = Color(0xFF1E293B)
      )
    }
  }
}
