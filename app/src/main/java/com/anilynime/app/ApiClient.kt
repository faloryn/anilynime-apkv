package com.anilynime.app

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface AnilynimeApi {
    @GET("api/anime")
    suspend fun getAnimeList(
        @Query("action") action: String,
        @Query("page") page: Int = 1,
        @Query("q") query: String? = null,
        @Query("slug") slug: String? = null
    ): AnimeListResponse

    @GET("api/anime")
    suspend fun getEpisode(
        @Query("action") action: String = "episode",
        @Query("slug") slug: String
    ): EpisodeDetailResponse

    @GET("api/anime")
    suspend fun getAnimeDetail(
        @Query("action") action: String = "detail",
        @Query("slug") slug: String
    ): AnimeDetailResponse
}

object ApiClient {
    // Default endpoint points to Vercel production deployment
    private const val BASE_URL = "https://anilynime.vercel.app/"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
    }

    val instance: AnilynimeApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnilynimeApi::class.java)
    }
}
