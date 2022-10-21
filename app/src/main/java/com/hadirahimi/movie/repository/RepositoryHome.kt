package com.hadirahimi.movie.repository

import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class RepositoryHome @Inject constructor(private val apiService : ApiService)
{
    // Popular Actors Limited!
    suspend fun popularActorsLimited() =
        apiService.popularActors(Constants.API_KEY , Constants.LANGUAGE_EN , 1)
    
    // All Genre List
    suspend fun allGenres() = apiService.allGenres(Constants.API_KEY , Constants.LANGUAGE_EN)
    
    // Newest Movies
    suspend fun newestMovies(page : Int) =
        apiService.nowPlaying(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    // Popular Movies
    suspend fun popularMovies(page : Int) =
        apiService.popularMovie(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    // Last Movie
    suspend fun lastMovie() =
        apiService.lastMovie(Constants.API_KEY , Constants.LANGUAGE_EN)
    
    
}