package com.hadirahimi.movie.paging.MoreActor

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hadirahimi.movie.models.home.ResponseNowPlayingMovies
import com.hadirahimi.movie.models.home.ResponsePopularActors
import com.hadirahimi.movie.models.home.ResponsePopularActors.Result
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.repository.RepositoryHome
import com.hadirahimi.movie.repository.RepositoryMoreActor
import javax.inject.Inject

class ActorsPagingSource @Inject constructor(private val repository : RepositoryMoreActor):
    PagingSource<Int , Result>()
{
    override fun getRefreshKey(state : PagingState<Int , Result>) : Int?
    {
        return null
    }
    
    override suspend fun load(params : LoadParams<Int>) : LoadResult<Int ,Result>
    {
        return try
        {
            val currentPage = params.key?:1
            val response = repository.popularActors(currentPage)
            val data = response.body()?.results?: emptyList()
            val responseData = mutableListOf<Result>()
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