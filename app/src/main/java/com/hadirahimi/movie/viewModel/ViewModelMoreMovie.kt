package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.paging.MoreMovie.NewestPagingSource
import com.hadirahimi.movie.paging.MoreMovie.PopularMoviesPagingSource
import com.hadirahimi.movie.repository.RepositoryMoreMovie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelMoreMovie @Inject constructor(private val repository : RepositoryMoreMovie) :
    ViewModel()
{
    val movieResult = MutableLiveData<ResponseMovie>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val empty = MutableLiveData<Boolean>()
    val allGenres = MutableLiveData<ResponseGenre>()
    
    val popularMovie = Pager(PagingConfig(1)) {
        PopularMoviesPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    
    val newestMovie = Pager(PagingConfig(1)) {
        NewestPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    
    //search actor Api
    fun searchMovieByName(movieName : String) = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            error.postValue(false)
            val response = repository.searchMovieByName(movieName)
            if (response.isSuccessful)
            {
                movieResult.postValue(response.body())
                //check result is empty our not
                if (response.body()?.results?.isEmpty() !!)
                    empty.postValue(true)
                else
                    empty.postValue(false)
                
                loading.postValue(false)
                error.postValue(false)
            }
            else
            {
                error.postValue(true)
                loading.postValue(false)
            }
            
        } catch (e : Exception)
        {
            loading.postValue(false)
            error.postValue(true)
        }
        
    }
    
    fun allGenres() = viewModelScope.launch {
        
        try
        {
            val response = repository.allGenres()
            if (response.isSuccessful)
            {
                allGenres.postValue(response.body())
                error.postValue(false)
            }
            else
                error.postValue(true)
            
            
        } catch (e : Exception)
        {
            error.postValue(true)
        }
    }
    
}