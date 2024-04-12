package com.company.miniproject1.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    val base_url = "http://127.0.0.1:5000"

    fun getRetrofitInstance(): Retrofit{
        return Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build()
    }
}