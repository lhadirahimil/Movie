package com.hadirahimi.movie.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ModuleApi
{
    @Provides
    @Singleton
    @Named(Constants.NAMED_BASE_URL)
    fun provideUrl() = Constants.BASE_URL
    
    
    @Provides
    @Singleton
    @Named(Constants.NAMED_BASE_URL_IMAGE)
    fun provideImageUrl() = Constants.BASE_URL_IMAGE
    
    
    @Provides
    @Singleton
    fun provideGson() : Gson = GsonBuilder().setLenient().create()
    
    @Provides
    @Singleton
    fun provideTimeOut() = Constants.TIMEOUT_TIME
    
    
    @Provides
    @Singleton
    @Named(Constants.NAMED_HEADER_INTERCEPTOR)
    fun provideHeaderInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.HEADERS
    }
    
    @Provides
    @Singleton
    @Named(Constants.NAMED_BODY_INTERCEPTOR)
    fun provideBodyInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    
    @Provides
    @Singleton
    fun provideClient(
        time : Long ,
        @Named(Constants.NAMED_BODY_INTERCEPTOR) bodyInterceptor : HttpLoggingInterceptor ,
        @Named(Constants.NAMED_HEADER_INTERCEPTOR) headerInterceptor : HttpLoggingInterceptor
    ) = OkHttpClient.Builder().connectTimeout(time , TimeUnit.SECONDS)
        .readTimeout(time , TimeUnit.SECONDS).writeTimeout(time , TimeUnit.SECONDS)
        .addInterceptor(headerInterceptor)
        .retryOnConnectionFailure(true).build()
    
    
    @Provides
    @Singleton
    fun provideRetrofit(
        @Named(Constants.NAMED_BASE_URL) base_url : String , gson : Gson , client : OkHttpClient
    ) : ApiService =
        Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create(gson))
            .client(client).build().create(ApiService::class.java)
    
}