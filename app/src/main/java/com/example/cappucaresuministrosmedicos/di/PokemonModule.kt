package com.example.cappucaresuministrosmedicos.di

import android.content.Context
import com.example.cappucaresuministrosmedicos.model.pokemon.DefaultPokemonRepository
import com.example.cappucaresuministrosmedicos.model.pokemon.PokemonRepository
import com.example.cappucaresuministrosmedicos.model.pokemon.remote.ApiService
import com.example.cappucaresuministrosmedicos.model.pokemon.remote.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PokemonModule {

    @Provides
    @Singleton
    fun provideApiService(@ApplicationContext context: Context): ApiService =
        RetrofitInstance.createApi(context.cacheDir)

    @Provides
    @Singleton
    fun providePokemonRepository(api: ApiService): PokemonRepository =
        DefaultPokemonRepository(api)
}