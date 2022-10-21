package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.movie.ResponseMovieDetail
import com.hadirahimi.movie.models.movie.ResponseMoviePhotos
import com.hadirahimi.movie.models.movie.ResponseMoviesActors
import com.hadirahimi.movie.models.movie.ResponseSimilarMovie
import com.hadirahimi.movie.repository.RepositoryMovieDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelMovieDetail @Inject constructor(private val repository : RepositoryMovieDetail) :
    ViewModel()
{
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    
    //Movie Data
    val movieData = MutableLiveData<ResponseMovieDetail>()
    //Genre Data
    val allGenres = MutableLiveData<ResponseGenre>()
    //Movie Images Data
    val movieImages = MutableLiveData<ResponseMoviePhotos>()
    val emptyMovieImages = MutableLiveData<Boolean>()
    
    //similar Movies
    val similarMovie = MutableLiveData<ResponseSimilarMovie>()
    
    //Movies Actors List
    val moviesActors = MutableLiveData<ResponseMoviesActors>()
    
    fun movieDetail(movie_id : Int) = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            val response = repository.movieDetail(movie_id)
            if (response.isSuccessful)
            {
                error.postValue(false)
                loading.postValue(false)
                movieData.postValue(response.body())
            }
            else
            {
                error.postValue(true)
                loading.postValue(false)
            }
        } catch (e : Exception)
        {
            error.postValue(true)
            loading.postValue(false)
        }
    }
    
    fun allGenres() = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            val response = repository.allGenres()
            if (response.isSuccessful)
            {
                allGenres.postValue(response.body())
                error.postValue(false)
                loading.postValue(false)
            }
            else
            {
                error.postValue(true)
                loading.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loading.postValue(false)
        }
        
    }
    
    fun movieImages(movieId:Int) = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            val response = repository.movieImages(movieId)
            if (response.isSuccessful)
            {
                if (response.body()!= null && response.body()?.backdrops?.isEmpty() == true)
                    emptyMovieImages.postValue(true)
                else
                {
                    movieImages.postValue(response.body())
                    emptyMovieImages.postValue(false)
                }
                error.postValue(false)
                loading.postValue(false)
            }
            else
            {
                error.postValue(true)
                loading.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loading.postValue(false)
        }
        
    }
    
    
    //movies ActorsList
    fun moviesActors(movieId:Int) = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            val response = repository.moviesActors(movieId)
            if (response.isSuccessful)
            {
                moviesActors.postValue(response.body())
                error.postValue(false)
                loading.postValue(false)
            }
            else
            {
                error.postValue(true)
                loading.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loading.postValue(false)
        }
        
    }
    
    //movies ActorsList
    fun similarMovie(movieId:Int) = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            val response = repository.similarMovies(movieId)
            if (response.isSuccessful)
            {
                similarMovie.postValue(response.body())
                error.postValue(false)
                loading.postValue(false)
            }
            else
            {
                error.postValue(true)
                loading.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loading.postValue(false)
        }
        
    }
    
}