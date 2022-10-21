package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponseLastMovie
import com.hadirahimi.movie.models.home.ResponsePopularActors
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.paging.home.NewestPagingSource
import com.hadirahimi.movie.repository.RepositoryHome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelHome @Inject constructor(private val repository : RepositoryHome) : ViewModel()
{
    val popularActorsLimited = MutableLiveData<ResponsePopularActors>()
    val allGenres = MutableLiveData<ResponseGenre>()
    
    //val newestMovies = MutableLiveData<ResponseNowPlayingMovies>()
    val popularMovies = MutableLiveData<ResponsePopularMovies>()
    val error = MutableLiveData<Boolean>()
    val loadingActors = MutableLiveData<Boolean>()
    val loadingGenres = MutableLiveData<Boolean>()
    
    // val loadingNewest = MutableLiveData<Boolean>()
    val loadingPopularMovies = MutableLiveData<Boolean>()
    val loadingLastMovie = MutableLiveData<Boolean>()
    val lastMovie = MutableLiveData<ResponseLastMovie>()


    
    val newestMovie = Pager(PagingConfig(1)) {
        NewestPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    
    
    fun popularActorsLimited() = viewModelScope.launch {
        loadingActors.postValue(true)
        try
        {
            
            val response = repository.popularActorsLimited()
            if (response.isSuccessful)
            {
                popularActorsLimited.postValue(response.body())
                error.postValue(false)
                loadingActors.postValue(false)
            }
            else
            {
                error.postValue(true)
                loadingActors.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loadingActors.postValue(false)
        }
    }
    
    fun allGenres() = viewModelScope.launch {
        loadingGenres.postValue(true)
        try
        {
            val response = repository.allGenres()
            if (response.isSuccessful)
            {
                allGenres.postValue(response.body())
                error.postValue(false)
                loadingGenres.postValue(false)
            }
            else
            {
                error.postValue(true)
                loadingGenres.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loadingGenres.postValue(false)
        }
        
    }

//    fun movieNewest() = viewModelScope.launch {
//        //loadingNewest.postValue(true)
//        try
//        {
////            val response = repository.newestMovies()
////            if (response.isSuccessful)
////            {
////                newestMovies.postValue(response.body())
////                error.postValue(false)
////                loadingNewest.postValue(false)
////            }
////            else
////            {
////                error.postValue(true)
////               // loadingNewest.postValue(false)
////            }
//            val newestMovie = Pager(PagingConfig(1)) {
//                HomeFragPagingSource(repository)
//            }.flow.cachedIn(viewModelScope)
//        } catch (e : Exception)
//        {
//            error.postValue(true)
//            //loadingNewest.postValue(false)
//        }
//    }
    
    fun popularMovies() = viewModelScope.launch {
        loadingPopularMovies.postValue(true)
        try
        {
            val response = repository.popularMovies(1)
            if (response.isSuccessful)
            {
                popularMovies.postValue(response.body())
                error.postValue(false)
                loadingPopularMovies.postValue(false)
            }
            else
            {
                error.postValue(true)
                loadingPopularMovies.postValue(false)
            }
            
        } catch (e : Exception)
        {
            error.postValue(true)
            loadingPopularMovies.postValue(false)
        }
    }
    
    fun lastMovie()
    {
        viewModelScope.launch {
            loadingLastMovie.postValue(true)
            try
            {
                val response = repository.lastMovie()
                loadingLastMovie.postValue(false)
                if (response.isSuccessful)
                {
                    lastMovie.postValue(response.body())
                    error.postValue(false)
                    loadingLastMovie.postValue(false)
                }
                else
                {
                    error.postValue(true)
                    loadingLastMovie.postValue(false)
                }
            } catch (e : Exception)
            {
                error.postValue(true)
                loadingLastMovie.postValue(false)
            }
        }
    }
}
