package com.company.miniproject1.model

import com.google.gson.annotations.SerializedName

data class SongName(
    @SerializedName("recommended_songs")
    val recommendedSong: String
)
