package com.hadirahimi.movie.ui.fragments.moreMovie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemMoreMovieBinding
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponseNowPlayingMovies
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class AdapterPopularMoviePager @Inject constructor(@ApplicationContext val context : Context) :
    PagingDataAdapter<ResponsePopularMovies.Result , AdapterPopularMoviePager.MyViewHolder>(
        differCallBack
    )
{
    private lateinit var binding : ItemMoreMovieBinding
    
    //genre list
    private var genres = ResponseGenre()
    
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        @SuppressLint("SetTextI18n")
        fun setData(movie : ResponsePopularMovies.Result)
        {
            binding.apply {
                imageCover.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                    crossfade(true)
                    crossfade(400)
                }
    
                val releaseDate = if (movie.releaseDate!="") movie.releaseDate.toString().subSequence(0 , 4).toString()
                else movie.releaseDate.toString()
                
                if (movie.genreIds !=null && movie.genreIds.isNotEmpty() && genres.genres?.size != 0)
                {
                    for (genre in genres.genres !!)
                    {
                        if (genre?.id == movie.genreIds[0]) tvDateAndGenre.text =
                            genre?.name + " / " + releaseDate
                    }
                    
                }
                else
                {
                    //movie genre id is empty show not fount to user
                    tvDateAndGenre.text = releaseDate
                }
                
                tvMovieName.text = movie.title.let { movie.title }
                tvMovieStar.text = movie.voteAverage.let { movie.voteAverage.toString() }
                
                //item click listener
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(movie)
                    }
                }
                
            }
            
            
        }
    }
    
    private var onItemClickListener : ((ResponsePopularMovies.Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (ResponsePopularMovies.Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding = ItemMoreMovieBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        
        getItem(position)?.let { holder.setData(it) }
        holder.setIsRecyclable(false)
        binding.tvMovieName.tag = position
        
    }
    
    companion object
    {
        private val differCallBack = object : DiffUtil.ItemCallback<ResponsePopularMovies.Result>()
        {
            override fun areItemsTheSame(
                oldItem : ResponsePopularMovies.Result , newItem : ResponsePopularMovies.Result
            ) : Boolean
            {
                return oldItem.id == newItem.id
            }
            
            override fun areContentsTheSame(
                oldItem : ResponsePopularMovies.Result , newItem : ResponsePopularMovies.Result
            ) : Boolean
            {
                return oldItem == newItem
            }
        }
        
    }
    
    fun submitGenres(genres : ResponseGenre)
    {
        this.genres = genres
    }
    
    
}