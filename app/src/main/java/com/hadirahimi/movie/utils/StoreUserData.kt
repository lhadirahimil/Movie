package com.hadirahimi.movie.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class StoreUserData @Inject constructor(@ApplicationContext val context : Context)
{
    companion object
    {
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore(Constants.USER_INFO_DATASTORE)
        val userToken = stringPreferencesKey(Constants.USER_TOKEN)
    }
    
    suspend fun saveUserToken(token : String)
    {
        context.dataStore.edit {
            it[userToken] = token
        }
    }
    
    fun getUserToken() = context.dataStore.data.map {
        it[userToken] ?: ""
    }
}