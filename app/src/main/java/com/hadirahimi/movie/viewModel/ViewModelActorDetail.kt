package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadirahimi.movie.models.actor.ResponseActorDetail
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies
import com.hadirahimi.movie.repository.RepositoryActorDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelActorDetail @Inject constructor(private val repository : RepositoryActorDetail) :
    ViewModel()
{
    val actorDetail = MutableLiveData<ResponseActorDetail>()
    val actorSelectedMovies = MutableLiveData<ResponseSelectedMovies>()
    val loading = MutableLiveData<Boolean>()
    val error = MutableLiveData<Boolean>()
    val selectedMovieEmpty = MutableLiveData<Boolean>()
    
    fun actorDetail(actorId : Int) = viewModelScope.launch {
        
        loading.postValue(true)
        try
        {
            error.postValue(false)
            val response = repository.actorInfo(actorId)
            if (response.isSuccessful)
            {
                
                actorDetail.postValue(response.body())
                
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
    
    
    fun actorSelectedMovies(actor_id : Int) = viewModelScope.launch {
        
        
        loading.postValue(true)
        try
        {
            error.postValue(false)
            val response = repository.actorSelectedMovies(actor_id)
            if (response.isSuccessful)
            {

                actorSelectedMovies.postValue(response.body())
                
                //check selected movie is have item or null
                if (response.body()!=null && response.body()?.cast?.isEmpty() == true)
                    selectedMovieEmpty.postValue(true)
                else
                    selectedMovieEmpty.postValue(false)
                
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
