package com.hadirahimi.movie.repository

import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class RepositoryMovieDetail @Inject constructor(private val api:ApiService)
{
    suspend fun movieDetail(movieId:Int) = api.movieInfo(movieId,Constants.API_KEY)
    
    // All Genre List
    suspend fun allGenres() = api.allGenres(Constants.API_KEY , Constants.LANGUAGE_EN)
    
    // movies more images
    suspend fun movieImages(movieId:Int) = api.movieImages(movieId,Constants.API_KEY)
    
    //movies actors list
    suspend fun moviesActors(movieId:Int) = api.movieActorsList(movieId,Constants.API_KEY)
    
    //similar movies
    suspend fun similarMovies(movieId:Int) = api.similarMovies(movieId,Constants.API_KEY)
}