package com.company.miniproject1.RoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.company.miniproject1.model.Playlist
import com.company.miniproject1.model.Song

@Dao
interface PlaylistDao {
    @Insert
    fun insert(playlist: Playlist): Long

    @Query("SELECT * FROM playlists WHERE userId = :userId")
    fun getPlaylists(userId: String): List<Playlist>
}

