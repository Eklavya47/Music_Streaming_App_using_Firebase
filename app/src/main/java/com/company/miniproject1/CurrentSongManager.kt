package com.company.miniproject1

import com.company.miniproject1.model.Song

object CurrentSongManager {
    private var currentSong: Song? = null

    fun setCurrentSong(song: Song?) {
        currentSong = song
    }

    fun getCurrentSong(): Song? {
        return currentSong
    }
}