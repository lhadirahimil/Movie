package com.hadirahimi.movie.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val expiresIn = intPreferencesKey(Constants.EXPIRES_IN)
        val userName = stringPreferencesKey(Constants.USER_NAME)
        val userProfile = stringPreferencesKey(Constants.USER_PROFILE)
    }
    
    suspend fun saveUser(token : String,expires_in:Int)
    {
        context.dataStore.edit {preferences->
            preferences[userToken] = token
            preferences[expiresIn] = expires_in
        }
    }
    
    suspend fun saveUserInfo(user_name:String,user_profile:String)
    {
        context.dataStore.edit { preferences->
            preferences[userName] = user_name
            preferences[userProfile] = user_profile
        }
    }
    
    suspend fun saveUserName(user_name:String)
    {
        context.dataStore.edit { preferences->
            preferences[userName] = user_name
        }
    }
    
    suspend fun  logOutUser()
    {
        context.dataStore.edit { preferences->
            preferences.clear()
        }
    }
    fun getUserToken() = context.dataStore.data.map {
        it[userToken] ?: ""
    }
    fun getExpiresIn() = context.dataStore.data.map {
        it[expiresIn] ?: ""
    }
    fun getUserProfile() = context.dataStore.data.map {
        it[userProfile] ?: ""
    }
    fun getUserName() = context.dataStore.data.map {
        it[userName] ?: ""
    }
}