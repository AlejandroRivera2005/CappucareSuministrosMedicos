package com.example.cappucaresuministrosmedicos.model.pokemon.remote

import com.example.cappucaresuministrosmedicos.model.pokemon.Pokemon
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection.HTTP_GATEWAY_TIMEOUT
import java.util.concurrent.TimeUnit

interface ApiService {

    @GET("pokemon/{idOrName}")
    suspend fun getPokemon(
        @Path("idOrName") idOrName: String
    ): Response<Pokemon>
}

class OfflineCacheInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        val request = chain.request()
        return try {
            chain.proceed(request)
        } catch (e: IOException) {
            val cacheOnly = CacheControl.Builder()
                .onlyIfCached()
                .maxStale(MAX_STALE_DAYS, TimeUnit.DAYS)
                .build()
            val cached = chain.proceed(request.newBuilder().cacheControl(cacheOnly).build())

            if (cached.code == HTTP_GATEWAY_TIMEOUT) {
                cached.close()
                throw e
            }
            cached
        }
    }

    private companion object {
        const val MAX_STALE_DAYS = 30
    }
}

object RetrofitInstance {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"
    private const val CACHE_SIZE_BYTES = 10L * 1024 * 1024

    fun createApi(cacheDir: File): ApiService {
        val client = OkHttpClient.Builder()
            .cache(Cache(File(cacheDir, "http"), CACHE_SIZE_BYTES))
            .addInterceptor(OfflineCacheInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}