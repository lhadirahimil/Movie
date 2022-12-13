package com.hadirahimi.movie.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.ConstantsUser
import com.hadirahimi.movie.utils.MyResponse
import okhttp3.*
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class RepositoryProfileEdit @Inject constructor(@Named(Constants.NAMED_USER_CLIENT) val client : OkHttpClient)
{
    suspend fun changeName(name:String,token:String) : MutableLiveData<MyResponse<Response>>
    {
        val liveData = MutableLiveData<MyResponse<Response>>()
        
        
        val body : RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(ConstantsUser.USER_NAME , name)
            .build()
        
        val request : Request = Request.Builder()
            .url(Constants.BASE_USER_URL + "user/edit")
            .method("POST" , body)
            .addHeader("Accept" , "application/json")
            .addHeader("Authorization","Bearer $token")
            .build()
        
        liveData.postValue(MyResponse.loading())
        client.newCall(request).enqueue(object : Callback
        {
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
    
    suspend fun changePassword(password:String,token:String) : MutableLiveData<MyResponse<Response>>
    {
        val liveData = MutableLiveData<MyResponse<Response>>()
        
        
        val body : RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(ConstantsUser.USER_PASSWORD , password)
            .build()
        
        val request : Request = Request.Builder()
            .url(Constants.BASE_USER_URL + "user/edit")
            .method("POST" , body)
            .addHeader("Accept" , "application/json")
            .addHeader("Authorization","Bearer $token")
            .build()
        
        liveData.postValue(MyResponse.loading())
        client.newCall(request).enqueue(object : Callback
        {
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