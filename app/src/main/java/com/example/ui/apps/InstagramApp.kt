package com.example.ui.apps

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.InstagramPost
import com.example.model.InstagramStory
import com.example.ui.theme.*
import com.example.viewmodel.PhoneViewModel
import kotlinx.coroutines.delay

@Composable
fun InstagramApp(
  viewModel: PhoneViewModel,
  modifier: Modifier = Modifier
) {
  val posts by viewModel.instaPosts.collectAsState()
  val stories by viewModel.instaStories.collectAsState()

  var currentTab by remember { mutableStateOf("home") } // home, search, reels, profile
  var activeStory by remember { mutableStateOf<InstagramStory?>(null) }
  var showCreatePostDialog by remember { mutableStateOf(false) }
  var newPostCaption by remember { mutableStateOf("") }
  var activeCommentPostId by remember { mutableStateOf<String?>(null) }
  var commentText by remember { mutableStateOf("") }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFF000000))
      .padding(top = 28.dp, bottom = 48.dp)
      .testTag("instagram_app")
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Instagram Top Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Instagram",
          color = Color.White,
          fontSize = 24.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Cursive
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = { showCreatePostDialog = true },
            modifier = Modifier.testTag("insta_create_post_btn")
          ) {
            Icon(Icons.Default.AddBox, contentDescription = "Rasm joylash", tint = Color.White)
          }
          IconButton(onClick = {}) {
            Icon(Icons.Outlined.FavoriteBorder, contentDescription = "Layklar", tint = Color.White)
          }
          IconButton(onClick = { viewModel.openApp(com.example.model.AppId.MESSAGES) }) {
            BadgedBox(badge = { Badge { Text("2") } }) {
              Icon(Icons.Default.Send, contentDescription = "Direct", tint = Color.White)
            }
          }
        }
      }

      // Content based on bottom tab
      Box(modifier = Modifier.weight(1f)) {
        when (currentTab) {
          "home" -> {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
              // Stories Carousel
              item {
                LazyRow(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                  horizontalArrangement = Arrangement.spacedBy(12.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                  items(stories) { story ->
                    InstagramStoryItem(
                      story = story,
                      onClick = { activeStory = story }
                    )
                  }
                }
                Divider(color = Color(0xFF1E293B), thickness = 0.5.dp)
              }

              // Feed Posts
              items(posts) { post ->
                InstagramPostCard(
                  post = post,
                  onLike = { viewModel.toggleLikePost(post.id) },
                  onComment = { activeCommentPostId = post.id },
                  onSave = { viewModel.toggleSavePost(post.id) }
                )
              }
            }
          }
          "search" -> {
            // Explore Grid
            InstagramExploreView()
          }
          "reels" -> {
            // Reels View
            InstagramReelsView()
          }
          "profile" -> {
            // User Profile
            InstagramProfileView(postsCount = posts.size)
          }
        }
      }

      // Bottom Navigation Bar
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.Black)
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = { currentTab = "home" }, modifier = Modifier.testTag("insta_tab_home")) {
          Icon(
            if (currentTab == "home") Icons.Default.Home else Icons.Outlined.Home,
            contentDescription = "Asosiy",
            tint = Color.White
          )
        }
        IconButton(onClick = { currentTab = "search" }, modifier = Modifier.testTag("insta_tab_search")) {
          Icon(
            if (currentTab == "search") Icons.Default.Search else Icons.Outlined.Search,
            contentDescription = "Qidiruv",
            tint = Color.White
          )
        }
        IconButton(onClick = { showCreatePostDialog = true }, modifier = Modifier.testTag("insta_tab_add")) {
          Icon(Icons.Default.AddCircleOutline, contentDescription = "Qo'shish", tint = Color.White)
        }
        IconButton(onClick = { currentTab = "reels" }, modifier = Modifier.testTag("insta_tab_reels")) {
          Icon(
            if (currentTab == "reels") Icons.Default.PlayCircle else Icons.Outlined.PlayCircle,
            contentDescription = "Reels",
            tint = Color.White
          )
        }
        IconButton(onClick = { currentTab = "profile" }, modifier = Modifier.testTag("insta_tab_profile")) {
          Box(
            modifier = Modifier
              .size(24.dp)
              .clip(CircleShape)
              .background(HonorCyan)
              .border(1.dp, if (currentTab == "profile") Color.White else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text("H", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }
      }
    }

    // Active Story Viewer Modal
    activeStory?.let { story ->
      InstagramStoryViewer(
        story = story,
        onClose = { activeStory = null }
      )
    }

    // Comment Sheet Dialog
    activeCommentPostId?.let { postId ->
      val targetPost = posts.find { it.id == postId }
      AlertDialog(
        onDismissRequest = { activeCommentPostId = null },
        title = { Text("Izohlar", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column(modifier = Modifier.fillMaxWidth()) {
            targetPost?.comments?.forEach { c ->
              Text("${c.author}: ${c.text}", color = Color.LightGray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = commentText,
              onValueChange = { commentText = it },
              placeholder = { Text("Fikringizni yozing...", color = Color.Gray) },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("insta_comment_field"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
              )
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (commentText.isNotBlank()) {
                viewModel.addCommentToPost(postId, commentText)
                commentText = ""
                activeCommentPostId = null
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan)
          ) {
            Text("Yuborish", color = Color.Black)
          }
        },
        containerColor = Color(0xFF1E293B)
      )
    }

    // Create New Post Dialog
    if (showCreatePostDialog) {
      AlertDialog(
        onDismissRequest = { showCreatePostDialog = false },
        title = { Text("Yangi Rasm Joylash 📸", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
          Column {
            Text("Honor 400 Lite 108MP kamerasidagi suratni lentaga qo'shing:", color = Color.LightGray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
              value = newPostCaption,
              onValueChange = { newPostCaption = it },
              placeholder = { Text("Izoh yozing... #honor400lite #tashkent") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("insta_new_post_caption"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = HonorCyan
              )
            )
          }
        },
        confirmButton = {
          Button(
            onClick = {
              if (newPostCaption.isNotBlank()) {
                viewModel.addNewInstagramPost(newPostCaption)
                newPostCaption = ""
                showCreatePostDialog = false
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = HonorCyan),
            modifier = Modifier.testTag("insta_submit_post_btn")
          ) {
            Text("Ulashish", color = Color.Black, fontWeight = FontWeight.Bold)
          }
        },
        dismissButton = {
          TextButton(onClick = { showCreatePostDialog = false }) {
            Text("Bekor qilish", color = Color.LightGray)
          }
        },
        containerColor = Color(0xFF1E293B)
      )
    }
  }
}

@Composable
fun InstagramStoryItem(
  story: InstagramStory,
  onClick: () -> Unit
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .clickable { onClick() }
      .testTag("insta_story_${story.id}")
  ) {
    Box(
      modifier = Modifier
        .size(64.dp)
        .clip(CircleShape)
        .background(
          Brush.linearGradient(
            listOf(InstagramGradientStart, InstagramGradientMid, InstagramGradientEnd)
          )
        )
        .padding(2.5.dp),
      contentAlignment = Alignment.Center
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .clip(CircleShape)
          .background(Color(0xFF1E293B)),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = story.username.take(2).uppercase(),
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp
        )
      }
    }
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = story.username,
      color = Color.White,
      fontSize = 11.sp,
      maxLines = 1
    )
  }
}

@Composable
fun InstagramPostCard(
  post: InstagramPost,
  onLike: () -> Unit,
  onComment: () -> Unit,
  onSave: () -> Unit
) {
  var showHeartAnimation by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxWidth()
      .padding(bottom = 16.dp)
      .testTag("insta_post_${post.id}")
  ) {
    // Post Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Color(0xFF334155)),
          contentAlignment = Alignment.Center
        ) {
          Text(post.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(post.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          Text(post.location, color = Color.Gray, fontSize = 11.sp)
        }
      }
      IconButton(onClick = {}) {
        Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.LightGray)
      }
    }

    // Photo Box (With double tap like)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .background(Color(post.postColorHex))
        .clickable {
          onLike()
          showHeartAnimation = true
        },
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(54.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Honor 400 Lite Ultra Clarity", color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
      }

      // Heart Pop Animation
      if (showHeartAnimation) {
        LaunchedEffect(Unit) {
          delay(800)
          showHeartAnimation = false
        }
        Icon(
          Icons.Default.Favorite,
          contentDescription = null,
          tint = Color.Red,
          modifier = Modifier.size(80.dp)
        )
      }
    }

    // Action Icons Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onLike, modifier = Modifier.testTag("insta_like_btn_${post.id}")) {
          Icon(
            if (post.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Layk",
            tint = if (post.isLiked) Color.Red else Color.White
          )
        }
        IconButton(onClick = onComment, modifier = Modifier.testTag("insta_comment_btn_${post.id}")) {
          Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Izoh", tint = Color.White)
        }
        IconButton(onClick = {}) {
          Icon(Icons.Default.Send, contentDescription = "Yuborish", tint = Color.White)
        }
      }
      IconButton(onClick = onSave) {
        Icon(
          if (post.isSaved) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
          contentDescription = "Saqlash",
          tint = Color.White
        )
      }
    }

    // Likes & Caption
    Column(modifier = Modifier.padding(horizontal = 14.dp)) {
      Text("${post.likes} ta belgilash", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
      Spacer(modifier = Modifier.height(4.dp))
      Row {
        Text(post.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(post.caption, color = Color.White, fontSize = 13.sp)
      }
      if (post.comments.isNotEmpty()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          "Barcha ${post.comments.size} ta izohni ko'rish",
          color = Color.Gray,
          fontSize = 12.sp,
          modifier = Modifier.clickable { onComment() }
        )
      }
      Spacer(modifier = Modifier.height(2.dp))
      Text(post.timestamp, color = Color.Gray, fontSize = 10.sp)
    }
  }
}

@Composable
fun InstagramStoryViewer(
  story: InstagramStory,
  onClose: () -> Unit
) {
  var progress by remember { mutableFloatStateOf(0f) }

  LaunchedEffect(story) {
    while (progress < 1f) {
      delay(50)
      progress += 0.02f
    }
    onClose()
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0F172A))
      .clickable { onClose() }
      .testTag("insta_story_viewer")
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = Color.White,
        trackColor = Color.DarkGray
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(HonorCyan),
            contentAlignment = Alignment.Center
          ) {
            Text(story.username.take(1).uppercase(), color = Color.Black, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(story.username, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        IconButton(onClick = onClose) {
          Icon(Icons.Default.Close, contentDescription = "Yopish", tint = Color.White)
        }
      }

      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Text("✨ Honor 400 Lite bilan bugun ajoyib kun!", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(8.dp))
          Text(story.title, color = HonorCyan, fontSize = 14.sp)
        }
      }
    }
  }
}

@Composable
fun InstagramExploreView() {
  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    modifier = Modifier.fillMaxSize(),
    horizontalArrangement = Arrangement.spacedBy(2.dp),
    verticalArrangement = Arrangement.spacedBy(2.dp)
  ) {
    items(15) { index ->
      val colors = listOf(0xFF1E293B, 0xFF334155, 0xFF0F766E, 0xFF4338CA, 0xFFBE185D)
      Box(
        modifier = Modifier
          .height(110.dp)
          .background(Color(colors[index % colors.size])),
        contentAlignment = Alignment.Center
      ) {
        Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White.copy(alpha = 0.4f))
      }
    }
  }
}

@Composable
fun InstagramReelsView() {
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF18181B)),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Icon(Icons.Default.PlayCircle, contentDescription = null, tint = HonorCyan, modifier = Modifier.size(60.dp))
      Spacer(modifier = Modifier.height(12.dp))
      Text("Honor Reels (TikTok / Short format)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
      Text("Vertikal videolar va musiqalar", color = Color.LightGray, fontSize = 12.sp)
    }
  }
}

@Composable
fun InstagramProfileView(postsCount: Int) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(72.dp)
          .clip(CircleShape)
          .background(HonorCyan),
        contentAlignment = Alignment.Center
      ) {
        Text("HONOR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
      Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        ProfileStat(number = "$postsCount", label = "Postlar")
        ProfileStat(number = "1,840", label = "Obunachilar")
        ProfileStat(number = "240", label = "Kuzatmoqda")
      }
    }

    Spacer(modifier = Modifier.height(12.dp))
    Text("Honor 400 Lite Foydalanuvchisi 📱", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    Text("MagicOS 8.0 • 108MP Kamera • Tashkent City ✨", color = Color.LightGray, fontSize = 12.sp)

    Spacer(modifier = Modifier.height(16.dp))
    Button(
      onClick = {},
      modifier = Modifier.fillMaxWidth(),
      colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626)),
      shape = RoundedCornerShape(8.dp)
    ) {
      Text("Profilni tahrirlash", color = Color.White, fontSize = 12.sp)
    }
  }
}

@Composable
fun ProfileStat(number: String, label: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(number, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    Text(label, color = Color.Gray, fontSize = 11.sp)
  }
}
