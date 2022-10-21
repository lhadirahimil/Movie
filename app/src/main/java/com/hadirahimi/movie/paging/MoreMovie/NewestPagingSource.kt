package com.hadirahimi.movie.paging.MoreMovie

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hadirahimi.movie.models.home.ResponseNowPlayingMovies
import com.hadirahimi.movie.repository.RepositoryHome
import com.hadirahimi.movie.repository.RepositoryMoreMovie
import javax.inject.Inject

class NewestPagingSource @Inject constructor(private val repository : RepositoryMoreMovie) :
    PagingSource<Int , ResponseNowPlayingMovies.Result>()
{
    override fun getRefreshKey(state : PagingState<Int , ResponseNowPlayingMovies.Result>) : Int?
    {
        return null
    }
    
    override suspend fun load(params : LoadParams<Int>) : LoadResult<Int , ResponseNowPlayingMovies.Result>
    {
        return try
        {
            val currentPage = params.key?:1
            val response = repository.newestMovie(currentPage)
            val data = response.body()?.results?: emptyList()
            val responseData = mutableListOf<ResponseNowPlayingMovies.Result>()
            responseData.addAll(data)
            
            LoadResult.Page(
                data = responseData,
                prevKey = if (currentPage == 1) null else -1,
                nextKey = currentPage.plus(1)
            )
        }catch (e:Exception)
        {
            LoadResult.Error(e)
        }
    }
}