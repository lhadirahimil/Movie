package com.hadirahimi.movie.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SavedEntity::class] , version = 4 , exportSchema = false)
abstract class SavedDatabase() : RoomDatabase()
{
    abstract fun savedDao() : SavedDao
}