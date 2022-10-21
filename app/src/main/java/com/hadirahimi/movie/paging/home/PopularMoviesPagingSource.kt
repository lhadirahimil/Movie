package com.hadirahimi.movie.paging.home

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hadirahimi.movie.models.home.ResponseNowPlayingMovies
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.repository.RepositoryHome
import javax.inject.Inject

class PopularMoviesPagingSource @Inject constructor(private val repository : RepositoryHome):
    PagingSource<Int , ResponsePopularMovies.Result>()
{
    override fun getRefreshKey(state : PagingState<Int , ResponsePopularMovies.Result>) : Int?
    {
        return null
    }
    
    override suspend fun load(params : LoadParams<Int>) : LoadResult<Int , ResponsePopularMovies.Result>
    {
        return try
        {
            val currentPage = params.key?:1
            val response = repository.popularMovies(currentPage)
            val data = response.body()?.results?: emptyList()
            val responseData = mutableListOf<ResponsePopularMovies.Result>()
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