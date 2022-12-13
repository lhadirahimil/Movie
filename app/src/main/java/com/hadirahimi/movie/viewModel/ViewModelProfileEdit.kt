package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.hadirahimi.movie.repository.RepositoryProfileEdit
import com.hadirahimi.movie.repository.RepositoryResetPassword
import com.hadirahimi.movie.repository.RepositoryVerifyEmail
import com.hadirahimi.movie.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelProfileEdit @Inject constructor(private val repository : RepositoryProfileEdit) : ViewModel()
{
    val liveDataNameEdit = MutableLiveData<MyResponse<Response>>()
    val liveDataPasswordEdit = MutableLiveData<MyResponse<Response>>()
    fun changeName(name:String,token:String) = viewModelScope.launch {
        repository.changeName(name,token).asFlow().collect{
            liveDataNameEdit.postValue(it)
        }
    }
    
    fun changePassword(password:String,token:String) = viewModelScope.launch {
        repository.changePassword(password,token).asFlow().collect{
            liveDataPasswordEdit.postValue(it)
        }
    }
    
}