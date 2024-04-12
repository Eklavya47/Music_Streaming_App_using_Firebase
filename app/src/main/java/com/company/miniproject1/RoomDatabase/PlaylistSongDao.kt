package com.company.miniproject1.RoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.company.miniproject1.model.PlaylistSong

@Dao
interface PlaylistSongDao {
    @Insert
    fun insert(playlistSong: PlaylistSong): Long

    @Query("SELECT * FROM playlist_songs WHERE playlistId = :playlistId")
    fun getSongsInPlaylist(playlistId: Long): List<PlaylistSong>
}