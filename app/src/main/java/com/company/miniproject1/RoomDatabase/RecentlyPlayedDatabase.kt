package com.company.miniproject1.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.company.miniproject1.model.RecentlyPlayedSong

@Database(entities = [RecentlyPlayedSong::class], version = 1)
abstract class RecentlyPlayedDatabase : RoomDatabase() {
    abstract fun recentlyPlayedSongDao(): RecentlyPlayedSongDao
    companion object {
        private var INSTANCE: RecentlyPlayedDatabase? = null

        fun getInstance(context: Context): RecentlyPlayedDatabase {
            if (INSTANCE == null) {
                synchronized(RecentlyPlayedDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        RecentlyPlayedDatabase::class.java, "recently-played-db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}