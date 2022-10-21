package com.hadirahimi.movie.repository

import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject


class RepositoryMoreMovie @Inject constructor(private val apiService : ApiService)
{
    // Popular Movie with Pager
    suspend fun popularMovie(page : Int) =
        apiService.popularMovie(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    // newest Movies with Pager
    suspend fun newestMovie(page : Int) =
        apiService.nowPlaying(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    //search movie by Name
    suspend fun searchMovieByName(movieName : String) =
        apiService.searchMovie(movieName , Constants.API_KEY)
    
    
    // All Genre List
    suspend fun allGenres() = apiService.allGenres(Constants.API_KEY , Constants.LANGUAGE_EN)
}