package com.company.miniproject1

import android.media.MediaPlayer

object MediaPlayerSingleton {
    private var mediaPlayerInstance: MediaPlayer? = null

    // Method to get the instance of the MediaPlayer
    fun getInstance(): MediaPlayer {
        if (mediaPlayerInstance == null) {
            mediaPlayerInstance = MediaPlayer()
            // Set up the MediaPlayer as needed, e.g., setDataSource, prepare, etc.
        }
        return mediaPlayerInstance!!
    }
}
