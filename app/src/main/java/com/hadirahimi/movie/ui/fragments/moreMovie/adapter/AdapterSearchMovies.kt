package com.hadirahimi.movie.ui.fragments.moreMovie.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemMoreMovieBinding
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponsePopularMovies
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.models.search.ResponseMovie.*
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterSearchMovies @Inject constructor() :
    RecyclerView.Adapter<AdapterSearchMovies.MyViewHolder>()
{
    private lateinit var binding : ItemMoreMovieBinding
    
    private var movieList = emptyList<Result>()
    
    //genre list
    private var genres = ResponseGenre()
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        @SuppressLint("SetTextI18n")
        fun setData(movie : Result)
        {
            binding.apply {
                imageCover.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                    crossfade(true)
                    crossfade(400)
                }
                
                val releaseDate =
                    if (movie.releaseDate != "") movie.releaseDate.toString().subSequence(0 , 4)
                        .toString()
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
    
    
    override fun getItemViewType(position : Int) : Int
    {
        return position
    }
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding = ItemMoreMovieBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(movieList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = movieList.size
    
    
    fun submitData(data : List<Result>)
    {
        val movieDiffUtil = MovieDiffUtils(movieList , data)
        val diffUtils = DiffUtil.calculateDiff(movieDiffUtil)
        movieList = data
        
        diffUtils.dispatchUpdatesTo(this)
    }
    
    class MovieDiffUtils(
        private val oldItem : List<Result> ,
        private val newItem : List<Result>
    ) : DiffUtil.Callback()
    {
        override fun getOldListSize() : Int
        {
            return oldItem.size
        }
        
        override fun getNewListSize() : Int
        {
            return newItem.size
        }
        
        override fun areItemsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean
        {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }
        
        override fun areContentsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean
        {
            return oldItem[oldItemPosition] === newItem[newItemPosition]
        }
        
    }
    fun submitGenres(genres : ResponseGenre)
    {
        this.genres = genres
    }
    
}