package com.company.miniproject1.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// PlaylistSong Entity
@Entity(tableName = "playlist_songs")
data class PlaylistSong(
    @PrimaryKey(autoGenerate = true)
    val playlistSongId: Long = 0,
    val playlistId: Long,
    val title: String,
    val imageUrl: String,
    val songUrl: String,
    val userId: String,
    val lyrics: String,
    val singer: String,
    val genre: String
)