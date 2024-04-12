package com.company.miniproject1

import androidx.lifecycle.LiveData
import com.company.miniproject1.model.Song

object QueueManager {
    private val musicQueue: MutableList<Song> = mutableListOf()

    fun addToQueue(song: Song) {
        musicQueue.add(song)
    }

    fun removeFromQueue(song: Song) {
        musicQueue.remove(song)
    }

    fun getQueue(): List<Song> {
        return musicQueue.toList()
    }
}