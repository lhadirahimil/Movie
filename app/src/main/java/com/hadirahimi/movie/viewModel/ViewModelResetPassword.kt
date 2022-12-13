package com.hadirahimi.movie.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.hadirahimi.movie.repository.RepositoryResetPassword
import com.hadirahimi.movie.repository.RepositoryVerifyEmail
import com.hadirahimi.movie.utils.MyResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.Response
import javax.inject.Inject

@HiltViewModel
class ViewModelResetPassword @Inject constructor(private val repositoryResetPassword : RepositoryResetPassword) : ViewModel()
{
    val liveDataReset = MutableLiveData<MyResponse<Response>>()
    fun resetPassword(email:String) = viewModelScope.launch {
        repositoryResetPassword.resetPassword(email).asFlow().collect{
            liveDataReset.postValue(it)
        }
        
    }
    
}