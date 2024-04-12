package com.company.miniproject1.retrofit

import com.company.miniproject1.model.Song
import com.company.miniproject1.model.SongName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SongsApi {
    @GET("recommend")
    suspend fun getSongRecommendation(@Query("song_name") songName: String): Response<SongName>
}