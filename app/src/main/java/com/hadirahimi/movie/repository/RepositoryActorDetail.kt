package com.hadirahimi.movie.repository

import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class RepositoryActorDetail @Inject constructor(private val apiService : ApiService)
{
    suspend fun actorInfo(actorId : Int) = apiService.actorInfo(actorId , Constants.API_KEY)
    
    suspend fun actorSelectedMovies(actor_id : Int) = apiService.actorSelectedMovies(actor_id,Constants.API_KEY)
}