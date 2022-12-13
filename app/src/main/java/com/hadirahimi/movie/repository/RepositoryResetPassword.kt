package com.hadirahimi.movie.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.ConstantsUser
import com.hadirahimi.movie.utils.MyResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.*
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named


class RepositoryResetPassword @Inject constructor(@Named(Constants.NAMED_USER_CLIENT) val client : OkHttpClient)
{
    
    suspend fun resetPassword(email:String) : MutableLiveData<MyResponse<Response>>
    {
        val liveData = MutableLiveData<MyResponse<Response>>()
        
        
        val body : RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(ConstantsUser.USER_EMAIL , email)
            .build()
        
        val request : Request = Request.Builder()
            .url(Constants.BASE_USER_URL + "password/reset")
            .method("POST" , body)
            .addHeader("Accept" , "application/json")
            .build()
        
            liveData.postValue(MyResponse.loading())
            client.newCall(request).enqueue(object :Callback{
                override fun onFailure(call : Call , e : IOException)
                {
                    
                    Log.e("HECTOR",e.message.toString())
                    liveData.postValue(MyResponse.error(e.message.toString()))
               
                }
        
                override fun onResponse(call : Call , response : Response)
                {
                    liveData.postValue(MyResponse.success(response))
                }
        
            })
        return liveData
        
    }
}