package com.company.miniproject1.Repository

import android.util.Log
import com.company.miniproject1.model.SongName
import com.company.miniproject1.retrofit.RetrofitInstance
import com.company.miniproject1.retrofit.SongsApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
class SongRepository {
        private val songsApi: SongsApi = RetrofitInstance().getRetrofitInstance().create(SongsApi::class.java)

        fun getSongsFromApi(songName: String): String{
                var recommendedSong: String
                GlobalScope.launch(Dispatchers.Main){
                        val response = songsApi.getSongRecommendation(songName)
                        if (response != null){
                                recommendedSong = response.body()!!
                        }
                }
                return recommendedSong
        }
}*/
class SongRepository {
        private val songsApi: SongsApi = RetrofitInstance().getRetrofitInstance().create(SongsApi::class.java)

        suspend fun getSongsFromApi(songName: String): SongName? {
                return try {
                        val response = songsApi.getSongRecommendation(songName)
                        if (response.isSuccessful) {
                                response.body() // The response body is already a SongName object
                        } else {
                                null
                        }
                } catch (e: Exception) {
                        e.printStackTrace()
                        null
                }
        }
}
