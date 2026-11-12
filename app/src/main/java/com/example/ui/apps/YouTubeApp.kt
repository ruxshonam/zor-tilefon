package com.example.ui.apps

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import com.example.model.CommentItem
import com.example.model.ShortsVideo
import com.example.model.YouTubeVideo
import com.example.ui.theme.HonorCyan
import com.example.ui.theme.YouTubeRed
import com.example.viewmodel.PhoneViewModel
import kotlinx.coroutines.delay

@Composable
fun YouTubeApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val videos by viewModel.youtubeVideos.collectAsState()
  val shorts by viewModel.shortsVideos.collectAsState()
  val activeVideo by viewModel.activeVideo.collectAsState()
  val isPlaying by viewModel.isVideoPlaying.collectAsState()
  val videoProgress by viewModel.videoProgress.collectAsState()
  val uiState by viewModel.uiState.collectAsState()

  var selectedTab by remember { mutableStateOf("home") } // "home" or "shorts"
  var selectedCategory by remember { mutableStateOf("Barchasi") }
  var showUploadDialog by remember { mutableStateOf(false) }
  var newVideoTitle by remember { mutableStateOf("") }
  var commentInput by remember { mutableStateOf("") }

  val categories = listOf("Barchasi", "Shorts", "Honor 400 Lite", "O'yinlar", "Musiqa", "Texnologiya")

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF0F0F0F))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("youtube_app")
  ) {
    if (activeVideo != null) {
      // Full Video Player Screen
      val video = activeVideo!!
      Column(
        modifier = Modifier
          .fillMaxSize()
          .testTag("youtube_video_player")
      ) {
        // Video Header Back
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { viewModel.closeActiveVideo() }, modifier = Modifier.testTag("yt_back_btn")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Orqaga", tint = Color.White)
          }
          Text("YouTube Player", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Virtual Video Display Window
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(Brush.linearGradient(video.videoGradient.map { Color(it) })),
          contentAlignment = Alignment.Center
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
          ) {
            Icon(
              Icons.Default.PlayCircle,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.9f),
              modifier = Modifier
                .size(64.dp)
                .clickable { viewModel.toggleVideoPlay() }
                .testTag("yt_play_toggle_btn")
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = if (isPlaying) "Video ijro etilmoqda... (1080p 60FPS)" else "To'xtatilgan",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }

          // Progress Bar at bottom of video
          LinearProgressIndicator(
            progress = { videoProgress },
            modifier = Modifier
              .fillMaxWidth()
              .align(Alignment.BottomCenter)
              .height(4.dp),
            color = YouTubeRed,
            trackColor = Color.DarkGray
          )
        }

        // Video Info & Details Scroll
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
          item {
            Text(
              text = video.title,
              color = Color.White,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${video.views} • ${video.timeAgo}",
              color = Color.Gray,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Channel Bar
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF272727)),
                  contentAlignment = Alignment.Center
                ) {
                  Text(video.channel.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(video.channel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text("1.42M obunachi", color = Color.Gray, fontSize = 11.sp)
                }
              }

              Button(
                onClick = { viewModel.toggleSubscribe(video.id) },
                colors = ButtonDefaults.buttonColors(
                  containerColor = if (video.isSubscribed) Color(0xFF272727) else Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier.testTag("yt_subscribe_btn")
              ) {
                Text(
                  text = if (video.isSubscribed) "Obuna bo'lindi 🔔" else "Obuna bo'lish",
                  color = if (video.isSubscribed) Color.White else Color.Black,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              AssistChip(
                onClick = { viewModel.toggleLikeVideo(video.id) },
                label = { Text("${video.likes}", color = if (video.isLiked) YouTubeRed else Color.White) },
                leadingIcon = {
                  Icon(
                    Icons.Default.ThumbUp,
                    contentDescription = "Like",
                    tint = if (video.isLiked) YouTubeRed else Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                },
                shape = RoundedCornerShape(20.dp),
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF272727)),
                modifier = Modifier.testTag("yt_like_btn")
              )

              AssistChip(
                onClick = { },
                label = { Text("Ulashish", color = Color.White) },
                leadingIcon = {
                  Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                },
                shape = RoundedCornerShape(20.dp),
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF272727))
              )

              AssistChip(
                onClick = { },
                label = { Text("Yuklab olish", color = Color.White) },
                leadingIcon = {
                  Icon(Icons.Default.Download, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                },
                shape = RoundedCornerShape(20.dp),
                colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFF272727))
              )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Comments Header
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Text("Izohlar (${video.comments.size})", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                  OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("Izoh qoldiring...", color = Color.Gray, fontSize = 12.sp) },
                    modifier = Modifier
                      .weight(1f)
                      .testTag("yt_comment_input"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                      focusedTextColor = Color.White,
                      unfocusedTextColor = Color.White,
                      focusedBorderColor = YouTubeRed,
                      unfocusedBorderColor = Color.DarkGray
                    ),
                    singleLine = true
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  IconButton(
                    onClick = {
                      if (commentInput.isNotBlank()) {
                        viewModel.addCommentToVideo(video.id, commentInput)
                        commentInput = ""
                      }
                    },
                    modifier = Modifier
                      .clip(CircleShape)
                      .background(YouTubeRed)
                      .testTag("yt_send_comment_btn")
                  ) {
                    Icon(Icons.Default.Send, contentDescription = "Yuborish", tint = Color.White, modifier = Modifier.size(18.dp))
                  }
                }

                Spacer(modifier = Modifier.height(12.dp))

                video.comments.forEach { comment ->
                  Row(
                    modifier = Modifier
                      .fillMaxWidth()
                      .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                  ) {
                    Box(
                      modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF333333)),
                      contentAlignment = Alignment.Center
                    ) {
                      Text(comment.author.take(1), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                      Row {
                        Text(comment.author, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(comment.timeAgo, color = Color.Gray, fontSize = 10.sp)
                      }
                      Text(comment.text, color = Color.White, fontSize = 12.sp)
                    }
                  }
                }
              }
            }
          }
        }
      }
    } else if (selectedTab == "shorts") {
      // DEDICATED FAST SHORTS PLAYER ("SHORTI VIDIO KO'NIMAYATI SHUN TEZA ISHLAYDIGAN BOLSIN")
      val currentShort = if (shorts.isNotEmpty()) shorts[uiState.activeShortsIndex % shorts.size] else null
      if (currentShort != null) {
        FastShortsPlayer(
          short = currentShort,
          onNext = { viewModel.nextShorts() },
          onPrev = { viewModel.prevShorts() },
          onLike = { viewModel.toggleLikeShorts(currentShort.id) },
          onBack = { selectedTab = "home" }
        )
      }
    } else {
      // Main YouTube Feed
      Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(YouTubeRed),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PlayArrow, contentDescription = "YouTube", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("YouTube", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = (-0.5).sp)
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = { showUploadDialog = true },
              modifier = Modifier.testTag("yt_upload_btn")
            ) {
              Icon(Icons.Default.VideoCall, contentDescription = "Video yuklash", tint = Color.White)
            }
            IconButton(onClick = {}) {
              Icon(Icons.Default.Search, contentDescription = "Qidiruv", tint = Color.White)
            }
            Box(
              modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(HonorCyan),
              contentAlignment = Alignment.Center
            ) {
              Text("H", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }

        // Category Chips
        LazyRow(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(categories) { category ->
            FilterChip(
              selected = selectedCategory == category,
              onClick = {
                selectedCategory = category
                if (category == "Shorts") {
                  selectedTab = "shorts"
                }
              },
              label = { Text(category, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (category == "Shorts") YouTubeRed else Color.White,
                selectedLabelColor = if (category == "Shorts") Color.White else Color.Black,
                containerColor = Color(0xFF272727),
                labelColor = Color.White
              ),
              shape = RoundedCornerShape(16.dp)
            )
          }
        }

        // Videos Feed
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          // Shorts Banner Card in feed
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable { selectedTab = "shorts" }
                .testTag("yt_shorts_feed_banner"),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(36.dp)
                      .clip(RoundedCornerShape(8.dp))
                      .background(YouTubeRed),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = Color.White)
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text("YouTube Shorts ⚡", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Tezkor vertikal videolar (100% tezlik)", color = HonorCyan, fontSize = 11.sp)
                  }
                }
                Button(
                  onClick = { selectedTab = "shorts" },
                  colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text("Ko'rish", color = Color.White, fontSize = 12.sp)
                }
              }
            }
          }

          items(videos) { video ->
            YouTubeVideoCard(
              video = video,
              onClick = { viewModel.selectVideo(video) }
            )
          }
        }

        // Bottom YouTube Navigation
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F0F0F))
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceAround,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(onClick = { selectedTab = "home" }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Home, contentDescription = "Asosiy", tint = if (selectedTab == "home") Color.White else Color.Gray)
              Text("Asosiy", color = if (selectedTab == "home") Color.White else Color.Gray, fontSize = 10.sp)
            }
          }
          IconButton(onClick = { selectedTab = "shorts" }, modifier = Modifier.testTag("yt_bottom_shorts_tab")) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Bolt, contentDescription = "Shorts", tint = if (selectedTab == "shorts") YouTubeRed else Color.White)
              Text("Shorts", color = if (selectedTab == "shorts") YouTubeRed else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
          }
          IconButton(onClick = { showUploadDialog = true }) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.AddCircleOutline, contentDescription = "Yuklash", tint = Color.White)
              Text("Yuklash", color = Color.White, fontSize = 10.sp)
            }
          }
          IconButton(onClick = {}) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(Icons.Default.Subscriptions, contentDescription = "Obunalar", tint = Color.Gray)
              Text("Obunalar", color = Color.Gray, fontSize = 10.sp)
            }
          }
        }
      }
    }

    // Upload Video Dialog
    if (showUploadDialog) {
      AlertDialog(
        onDismissRequest = { showUploadDialog = false },
        title = { Text("Yutuber: Yangi Video Yuklash 🎥", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Text("Honor 400 Lite kamerasi bilan olingan videoni YouTube kanaliga joylang:", color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = newVideoTitle,
              onValueChange = { newVideoTitle = it },
              label = { Text("Video sarlavhasi") },
              placeholder = { Text("Masalan: Honor 400 Lite da Pubg Mobile!") },
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = YouTubeRed
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("yt_upload_title_input")
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (newVideoTitle.isNotBlank()) {
                viewModel.uploadYouTubeVideo(newVideoTitle)
                newVideoTitle = ""
                showUploadDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = YouTubeRed),
            modifier = Modifier.testTag("yt_confirm_upload_btn")
          ) {
            Text("Yuklash", color = Color.White)
          }
        },
        dismissButton = {
          TextButton(onClick = { showUploadDialog = false }) {
            Text("Bekor qilish", color = Color.LightGray)
          }
        },
        containerColor = Color(0xFF1E1E1E)
      )
    }
  }
}

@Composable
fun FastShortsPlayer(
  short: ShortsVideo,
  onNext: () -> Unit,
  onPrev: () -> Unit,
  onLike: () -> Unit,
  onBack: () -> Unit
) {
  var isPlaying by remember { mutableStateOf(true) }
  var progress by remember { mutableFloatStateOf(0.3f) }

  LaunchedEffect(short.id) {
    progress = 0f
    while (true) {
      delay(100)
      if (isPlaying) {
        progress += 0.02f
        if (progress >= 1f) progress = 0f
      }
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Brush.linearGradient(short.gradient.map { Color(it) }))
      .testTag("shorts_player_screen")
  ) {
    // Header overlay
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBack, modifier = Modifier.testTag("shorts_back_btn")) {
        Icon(Icons.Default.ArrowBack, contentDescription = "Chiqish", tint = Color.White)
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.Bolt, contentDescription = null, tint = YouTubeRed, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text("Shorts", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      }
      IconButton(onClick = {}) {
        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.White)
      }
    }

    // Center Play/Pause indicator
    Box(
      modifier = Modifier
        .fillMaxSize()
        .clickable { isPlaying = !isPlaying },
      contentAlignment = Alignment.Center
    ) {
      if (!isPlaying) {
        Icon(
          Icons.Default.PlayCircle,
          contentDescription = null,
          tint = Color.White.copy(alpha = 0.8f),
          modifier = Modifier.size(72.dp)
        )
      }
    }

    // Right action sidebar
    Column(
      modifier = Modifier
        .align(Alignment.CenterEnd)
        .padding(end = 12.dp, bottom = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Like button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = onLike,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .testTag("shorts_like_btn")
        ) {
          Icon(
            if (short.isLiked) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
            contentDescription = "Like",
            tint = if (short.isLiked) YouTubeRed else Color.White
          )
        }
        Text("${short.likes}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      // Comments button
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = {},
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
        ) {
          Icon(Icons.Default.Comment, contentDescription = "Izoh", tint = Color.White)
        }
        Text("${short.commentsCount}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }

      // Share button
      IconButton(
        onClick = {},
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.4f))
      ) {
        Icon(Icons.Default.Share, contentDescription = "Ulashish", tint = Color.White)
      }

      // Next Video button
      IconButton(
        onClick = onNext,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(YouTubeRed)
          .testTag("shorts_next_btn")
      ) {
        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Keyingisi", tint = Color.White, modifier = Modifier.size(28.dp))
      }

      // Prev Video button
      IconButton(
        onClick = onPrev,
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.4f))
          .testTag("shorts_prev_btn")
      ) {
        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Oldingisi", tint = Color.White, modifier = Modifier.size(28.dp))
      }
    }

    // Bottom Video Info & Music Bar
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(start = 14.dp, end = 80.dp, bottom = 14.dp)
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color.White),
          contentAlignment = Alignment.Center
        ) {
          Text(short.channel.take(1), color = Color.Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(short.channel, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(10.dp))
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(YouTubeRed)
            .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
          Text("Obuna", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))
      Text(short.title, color = Color.White, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)

      Spacer(modifier = Modifier.height(6.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Default.MusicNote, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(short.musicTitle, color = Color.LightGray, fontSize = 11.sp, maxLines = 1)
      }
    }

    // Bottom progress scrubber
    LinearProgressIndicator(
      progress = { progress },
      modifier = Modifier
        .fillMaxWidth()
        .align(Alignment.BottomCenter)
        .height(3.dp),
      color = YouTubeRed,
      trackColor = Color.Transparent
    )
  }
}

@Composable
fun YouTubeVideoCard(
  video: YouTubeVideo,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(bottom = 16.dp)
      .testTag("yt_card_${video.id}")
  ) {
    // Thumbnail
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(190.dp)
        .background(Brush.linearGradient(video.videoGradient.map { Color(it) })),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        Icons.Default.PlayCircle,
        contentDescription = null,
        tint = Color.White.copy(alpha = 0.8f),
        modifier = Modifier.size(48.dp)
      )

      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(Color.Black.copy(alpha = 0.8f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(text = video.duration, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.Top
    ) {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape)
          .background(Color(0xFF333333)),
        contentAlignment = Alignment.Center
      ) {
        Text(video.channel.take(1), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = video.title,
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.SemiBold,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${video.channel} • ${video.views} • ${video.timeAgo}",
          color = Color.Gray,
          fontSize = 12.sp,
          maxLines = 1
        )
      }
      IconButton(onClick = {}) {
        Icon(Icons.Default.MoreVert, contentDescription = "Menu", tint = Color.LightGray)
      }
    }
  }
}
