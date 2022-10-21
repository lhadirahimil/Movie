package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadirahimi.movie.models.search.ResponseActor
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.repository.RepositorySearch
import com.hadirahimi.movie.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelSearch @Inject constructor(private val repository : RepositorySearch) : ViewModel()
{
    val actors = MutableLiveData<MyResponse<ResponseActor>>()
    val movies = MutableLiveData<MyResponse<ResponseMovie>>()
    
    //search actor Api
    fun searchActor(actorName : String) = viewModelScope.launch {
        
        actors.postValue(MyResponse.loading())
        repository.searchByActorName(actorName)
            .flowOn(Dispatchers.IO)
            .catch { actors.postValue(MyResponse.error(it.message.toString())) }
            .collect {
                if (it.body()?.results?.isEmpty() == true)
                    actors.postValue(MyResponse.empty())
                else
                    actors.postValue(MyResponse.success(it.body()))
            }
        
    }
    
    //search Movie Api
    fun searchMovie(movieName : String) = viewModelScope.launch {
    
        movies.postValue(MyResponse.loading())
        repository.searchByMovieTitle(movieName)
            .flowOn(Dispatchers.IO)
            .catch { movies.postValue(MyResponse.error(it.message.toString())) }
            .collect {
                if (it.body()?.results?.isEmpty() == true)
                    movies.postValue(MyResponse.empty())
                else
                    movies.postValue(MyResponse.success(it.body()))
            }
    }
    
}