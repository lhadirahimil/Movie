package com.hadirahimi.movie.api

import com.hadirahimi.movie.models.actor.ResponseActorDetail
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies
import com.hadirahimi.movie.models.home.*
import com.hadirahimi.movie.models.movie.ResponseMovieDetail
import com.hadirahimi.movie.models.movie.ResponseMoviePhotos
import com.hadirahimi.movie.models.movie.ResponseMoviesActors
import com.hadirahimi.movie.models.movie.ResponseSimilarMovie
import com.hadirahimi.movie.models.search.ResponseActor
import com.hadirahimi.movie.models.search.ResponseMovie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService
{
    // Genres Api Service
    //    @GET("genre/tv/list")
    @GET("genre/movie/list")
    suspend fun allGenres(
        @Query("api_key") api_key : String ,
        @Query("language") language : String
    ) : Response<ResponseGenre>
    
    // Popular Actors Api Service
    @GET("person/popular")
    suspend fun popularActors(
        @Query("api_key") api_key : String ,
        @Query("language") language : String ,
        @Query("page") page : Int
    ) : Response<ResponsePopularActors>
    
    //now playing in cinema : Newest Movies Api Service
    @GET("movie/now_playing")
    suspend fun nowPlaying(
        @Query("api_key") api_key : String ,
        @Query("language") language : String ,
        @Query("page") page : Int
    ) : Response<ResponseNowPlayingMovies>
    
    // Popular Movies Api Service
    @GET("movie/popular")
    suspend fun popularMovie(
        @Query("api_key") api_key : String ,
        @Query("language") language : String ,
        @Query("page") page : Int
    ) : Response<ResponsePopularMovies>
    
    @GET("movie/latest")
    suspend fun lastMovie(
        @Query("api_key") api_key : String , @Query("language") language : String
    ) : Response<ResponseLastMovie>
    
    @GET("search/movie")
    suspend fun searchMovie(
        @Query("query") movieName : String , @Query("api_key") apikey : String
    ) : Response<ResponseMovie>
    
    @GET("search/person")
    suspend fun searchActor(
        @Query("query") actorName : String , @Query("api_key") apikey : String
    ) : Response<ResponseActor>
    
    
    @GET("person/{actor_id}")
    suspend fun actorInfo(
        @Path("actor_id") actor_id : Int , @Query("api_key") apikey : String
    ) : Response<ResponseActorDetail>
    
    @GET("movie/{movie_id}")
    suspend fun movieInfo(
        @Path("movie_id") movie_id : Int , @Query("api_key") apikey : String
    ) : Response<ResponseMovieDetail>
    
    @GET("person/{actor_id}/movie_credits")
    suspend fun actorSelectedMovies(
        @Path("actor_id") actor_id : Int , @Query("api_key") apikey : String
    ) : Response<ResponseSelectedMovies>
    
    @GET("movie/{movie_id}/images")
    suspend fun movieImages(
        @Path("movie_id") movie_id : Int , @Query("api_key") apikey : String
    ) : Response<ResponseMoviePhotos>
    
    @GET("movie/{movie_id}/credits")
    suspend fun movieActorsList(
        @Path("movie_id") movie_id : Int ,
        @Query("api_key") apikey : String
    ) : Response<ResponseMoviesActors>
    
    @GET("movie/{movie_id}/similar")
    suspend fun similarMovies(
        @Path("movie_id") movieId : Int ,
        @Query("api_key") apikey : String
    ) : Response<ResponseSimilarMovie>
    
}