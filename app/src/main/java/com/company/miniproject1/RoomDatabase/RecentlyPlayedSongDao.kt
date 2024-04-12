package com.company.miniproject1.RoomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.company.miniproject1.model.RecentlyPlayedSong

@Dao
interface RecentlyPlayedSongDao {
    @Insert
    fun insert(song: RecentlyPlayedSong)

    @Query("SELECT * FROM recently_played_songs ORDER BY playTime DESC LIMIT :limit")
    fun getRecentlyPlayedSongs(limit: Int): List<RecentlyPlayedSong>

    @Query("SELECT EXISTS (SELECT 1 FROM recently_played_songs WHERE songUrl = :songUrl)")
    fun isSongAlreadyInDatabase(songUrl: String): Boolean


}