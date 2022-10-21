package com.hadirahimi.movie.di

import android.content.Context
import androidx.room.Room
import com.hadirahimi.movie.db.SavedDatabase
import com.hadirahimi.movie.db.SavedEntity
import com.hadirahimi.movie.utils.Constants.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
{
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context:Context) = Room.databaseBuilder(context,SavedDatabase::class.java,DATABASE_NAME)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()
    
    @Provides
    @Singleton
    fun savedDao(db : SavedDatabase) = db.savedDao()
    
    @Provides
    fun provideSavedEntity() = SavedEntity()
}