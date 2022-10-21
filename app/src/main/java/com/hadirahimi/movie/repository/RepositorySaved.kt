package com.hadirahimi.movie.repository

import com.hadirahimi.movie.db.SavedDao
import com.hadirahimi.movie.db.SavedEntity
import javax.inject.Inject

class RepositorySaved @Inject constructor(private val dao : SavedDao)
{
    suspend fun saveSaved(savedEntity : SavedEntity) = dao.saveSaved(savedEntity)
    suspend fun deleteSaved(savedEntity : SavedEntity) = dao.deleteSaved(savedEntity)
    suspend fun existsSaved(serverId:Int) = dao.existsSaved(serverId)
    fun getAllSaved() = dao.getAllSaved()
}