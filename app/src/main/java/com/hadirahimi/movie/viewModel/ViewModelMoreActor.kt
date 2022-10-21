package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.hadirahimi.movie.models.search.ResponseActor
import com.hadirahimi.movie.paging.MoreActor.ActorsPagingSource
import com.hadirahimi.movie.repository.RepositoryMoreActor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelMoreActor @Inject constructor(private val repository : RepositoryMoreActor) :
    ViewModel()
{
    val actorResult = MutableLiveData<ResponseActor>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val empty = MutableLiveData<Boolean>()
    
    
    val actors = Pager(PagingConfig(1)) {
        ActorsPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
    
    //search actor Api
    fun searchActor(actorName : String) = viewModelScope.launch {
        loading.postValue(true)
        try
        {
            error.postValue(false)
            val response = repository.searchByActorName(actorName)
            if (response.isSuccessful)
            {
                actorResult.postValue(response.body())
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
    
    
}