package com.hadirahimi.movie.repository

import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.models.search.ResponseActor
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class RepositorySearch @Inject constructor(private val api : ApiService)
{
    
    suspend fun searchByActorName(actorName : String) : Flow<Response<ResponseActor>> = flow {
        emit(api.searchActor(actorName , Constants.API_KEY))
    }
    
    suspend fun searchByMovieTitle(movieTitle : String) : Flow<Response<ResponseMovie>> = flow {
        emit(api.searchMovie(movieTitle , Constants.API_KEY))
    }
    
}