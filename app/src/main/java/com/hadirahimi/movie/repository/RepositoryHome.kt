package com.hadirahimi.movie.repository

import androidx.lifecycle.MutableLiveData
import com.hadirahimi.movie.api.ApiService
import com.hadirahimi.movie.api.UserApiService
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponsePopularActors
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.models.home.ResponseUser
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.MyResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RepositoryHome @Inject constructor(private val apiService : ApiService,private val userApi : UserApiService)
{
   
    // Popular Actors Limited!
    suspend fun popularActorsLimited() :Flow<MyResponse<List<ResponsePopularActors.Result>>>{
        return flow {
            emit(MyResponse.loading())
            val result = apiService.popularActors(Constants.API_KEY , Constants.LANGUAGE_EN , 1)
            val filtered = result.body()?.results?.filter {
                //filter porn star actors by id
                it.id !in listOf(3194176,1907997,2710789,3030333,2472212,2590519,1468490,2349944,3371806,1549899,1721615,2243993,2619408,2790319,1721638,1914924,2473962)
            }
            emit(MyResponse.success(filtered))
        }.catch {
            emit(MyResponse.error("error"))
        }
    }
    
    
    // All Genre List
    suspend fun allGenres() : Flow<MyResponse<ResponseGenre>>
    {
        return flow {
            emit(MyResponse.loading())
            val result = apiService.allGenres(Constants.API_KEY , Constants.LANGUAGE_EN)
            emit(MyResponse.success(result.body()))
        }.catch {
            emit(MyResponse.error("error"))
        }.flowOn(Dispatchers.IO)
    }
    
    // Newest Movies
    suspend fun newestMovies(page : Int) =
        apiService.nowPlaying(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    // Popular Movies
    suspend fun popularMovies(page:Int) : Flow<MyResponse<ResponsePopularMovies>>
    {
        return flow {
            emit(MyResponse.loading())
            val result = apiService.popularMovie(Constants.API_KEY , Constants.LANGUAGE_EN , page)
            emit(MyResponse.success(result.body()))
        }.catch { emit(MyResponse.error("error")) }.flowOn(Dispatchers.IO)
    }
    
    suspend fun popularMoviesPager(page : Int) = apiService.popularMovie(Constants.API_KEY , Constants.LANGUAGE_EN , page)
    
    
    
    suspend fun userData(userToken :String) :Flow<MyResponse<ResponseUser>> =
        flow {
            emit(MyResponse.loading())
            val res = userApi.userData("Bearer $userToken")
            emit(MyResponse.success(res.body()))
        }.catch {
            emit(MyResponse.error(it.message.toString()))
        }
    
}