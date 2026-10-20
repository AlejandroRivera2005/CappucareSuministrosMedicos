package com.example.cappucaresuministrosmedicos.di

import android.content.Context
import androidx.room.Room
import com.example.cappucaresuministrosmedicos.model.local.AppDatabase
import com.example.cappucaresuministrosmedicos.model.local.dao.MovimientoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.ProductoDao
import com.example.cappucaresuministrosmedicos.model.local.dao.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "cappucare.db").build()

    @Provides
    fun provideProductoDao(db: AppDatabase): ProductoDao = db.productoDao()

    @Provides
    fun provideMovimientoDao(db: AppDatabase): MovimientoDao = db.movimientoDao()

    @Provides
    fun provideUsuarioDao(db: AppDatabase): UsuarioDao = db.usuarioDao()
}