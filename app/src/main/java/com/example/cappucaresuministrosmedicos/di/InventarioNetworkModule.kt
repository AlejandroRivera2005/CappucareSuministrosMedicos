package com.example.cappucaresuministrosmedicos.di

import com.example.cappucaresuministrosmedicos.model.remote.InventarioApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object InventarioNetworkModule {
    private const val BASE_URL = "http://54.88.63.9:8080/"

    @Provides
    @Singleton
    fun provideInventarioApi(): InventarioApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(InventarioApi::class.java)
}
