package com.example.model

enum class AppId {
  YOUTUBE,
  INSTAGRAM,
  PLAY_STORE,
  TIKTOK,
  GAME_FLAPPY,
  GAME_TICTACTOE,
  GAME_REFLEX,
  GAME_2048,
  GAME_SUBWAY,
  MESSAGES,
  TELEGRAM,
  PHONE,
  CAMERA,
  GALLERY,
  MUSIC,
  WEATHER,
  COMPASS,
  CALCULATOR,
  NOTES,
  SETTINGS
}

data class AppItem(
  val id: AppId,
  val name: String,
  val category: String,
  val isInstalled: Boolean = true,
  val isDockApp: Boolean = false,
  val badgeCount: Int = 0,
  val downloadProgress: Float = 1f, // 0..1 for store downloads
  val isDownloading: Boolean = false,
  val sizeMb: Int = 35
)

data class YouTubeVideo(
  val id: String,
  val title: String,
  val channel: String,
  val views: String,
  val timeAgo: String,
  val duration: String,
  val likes: Int,
  val comments: List<CommentItem>,
  val isLiked: Boolean = false,
  val isSubscribed: Boolean = false,
  val videoGradient: List<Long> = listOf(0xFF0F2027, 0xFF203A43, 0xFF2C5364)
)

data class ShortsVideo(
  val id: String,
  val title: String,
  val channel: String,
  val likes: Int,
  val commentsCount: Int,
  val musicTitle: String,
  val gradient: List<Long>,
  val isLiked: Boolean = false
)

data class CommentItem(
  val id: String,
  val author: String,
  val text: String,
  val likes: Int,
  val timeAgo: String
)

data class InstagramPost(
  val id: String,
  val username: String,
  val location: String,
  val caption: String,
  val likes: Int,
  val isLiked: Boolean = false,
  val isSaved: Boolean = false,
  val comments: List<CommentItem>,
  val timestamp: String,
  val postColorHex: Long = 0xFF1E293B
)

data class InstagramStory(
  val id: String,
  val username: String,
  val title: String,
  val isSeen: Boolean = false
)

data class ChatMessage(
  val id: String,
  val text: String,
  val isFromUser: Boolean,
  val time: String
)

data class ChatConversation(
  val id: String,
  val name: String,
  val avatarText: String,
  val status: String,
  val messages: List<ChatMessage>,
  val unreadCount: Int = 0
)

data class ContactItem(
  val id: String,
  val name: String,
  val number: String,
  val avatarText: String
)

data class GalleryPhoto(
  val id: String,
  val timestamp: Long,
  val label: String,
  val filterTag: String,
  val colorGradient: List<Long>
)

data class MusicTrack(
  val id: String,
  val title: String,
  val artist: String,
  val durationSec: Int = 180
)

data class PhoneNotification(
  val id: String,
  val appName: String,
  val title: String,
  val message: String,
  val time: String,
  val targetApp: AppId? = null
)
