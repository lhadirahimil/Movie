package com.hadirahimi.movie.db

import android.database.sqlite.SQLiteDatabase
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.hadirahimi.movie.utils.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedDao
{
    @Insert(onConflict = SQLiteDatabase.CONFLICT_REPLACE)
    suspend fun saveSaved(saved : SavedEntity)
    
    @Delete
    suspend fun deleteSaved(saved : SavedEntity)
    
    @Query("SELECT * FROM ${Constants.SAVED_TABLE} ORDER BY server_id DESC")
    fun getAllSaved() : Flow<List<SavedEntity>>
    
    @androidx.room.Query("SELECT EXISTS (SELECT 1 FROM ${Constants.SAVED_TABLE} WHERE server_id = :server_id)")
    suspend fun existsSaved(server_id:Int):Boolean
}