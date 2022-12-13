package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponsePopularActors
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.models.home.ResponseUser
import com.hadirahimi.movie.paging.home.NewestPagingSource
import com.hadirahimi.movie.repository.RepositoryHome
import com.hadirahimi.movie.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelHome @Inject constructor(private val repository : RepositoryHome) : ViewModel()
{
    val popularActorsLimited = MutableLiveData<MyResponse<List<ResponsePopularActors.Result>>>()
    val allGenres = MutableLiveData<MyResponse<ResponseGenre>>()
    val liveDataUserData = MutableLiveData<MyResponse<ResponseUser>>()
    val popularMovies = MutableLiveData<MyResponse<ResponsePopularMovies>>()
    val error = MutableLiveData<Boolean>()
    var selectedTabIndex = 0
    
    
    val newestMovie = Pager(PagingConfig(pageSize = 1)) {
        NewestPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    
    
    fun popularActorsLimited() = viewModelScope.launch(Dispatchers.IO) {
        repository.popularActorsLimited().collect {
            popularActorsLimited.postValue(it)
        }
    }
    
    
    fun allGenres() = viewModelScope.launch(Dispatchers.IO) {
        repository.allGenres().collect { allGenres.postValue(it) }
    }
    
    
    fun popularMovies() = viewModelScope.launch(Dispatchers.IO) {
        repository.popularMovies(1).collect { popularMovies.postValue(it) }
    }
    fun userData(userToken:String) = viewModelScope.launch (Dispatchers.IO){
        repository.userData(userToken).collect{ liveDataUserData.postValue(it) }
    }
}
