package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadirahimi.movie.db.SavedEntity
import com.hadirahimi.movie.repository.RepositorySaved
import com.hadirahimi.movie.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelSaved @Inject constructor(private val repository:RepositorySaved) : ViewModel()
{
    val savedList = MutableLiveData<MyResponse<List<SavedEntity>>>()
    val isSaved = MutableLiveData<Boolean>()
   
   suspend fun  existsSaved(serverId:Int) = withContext(viewModelScope.coroutineContext){
        repository.existsSaved(serverId)
    }
    
    fun insertOrDelete(serverId:Int,savedEntity : SavedEntity) = viewModelScope.launch(Dispatchers.IO)
    {
        when(repository.existsSaved(serverId))
        {
            
            true->
            {
                isSaved.postValue(false)
                repository.deleteSaved(savedEntity)
            }
            
            false->
            {
                isSaved.postValue(true)
                repository.saveSaved(savedEntity)
            }
            
        }
    }
    
    
    fun getAllSaved() = viewModelScope.launch {
        savedList.postValue(MyResponse.loading())
        repository.getAllSaved()
            .catch { savedList.postValue(MyResponse.error(it.message.toString())) }
            .collect{
                if (it.isNotEmpty())
                     savedList.postValue(MyResponse.success(it))
                else
                    savedList.postValue(MyResponse.empty())
            }
    }
}