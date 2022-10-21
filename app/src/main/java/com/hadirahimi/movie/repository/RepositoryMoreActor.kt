package com.hadirahimi.movie.repository

import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject


class RepositoryMoreActor @Inject constructor(private val apiService : ApiService)
{
    // Popular Actors with Pager
    suspend fun popularActors(page : Int) =
        apiService.popularActors(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    //search actor by Name
    suspend fun searchByActorName(actorName : String) =
        apiService.searchActor(actorName , Constants.API_KEY)
}