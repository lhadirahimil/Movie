package com.hadirahimi.movie.ui.fragments.movieSingle.Adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemSelectedPhotosBinding
import com.hadirahimi.movie.databinding.ItemSimilarMoviesBinding
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies.Cast
import com.hadirahimi.movie.models.movie.ResponseMoviePhotos
import com.hadirahimi.movie.models.movie.ResponseMoviePhotos.Backdrop
import com.hadirahimi.movie.models.movie.ResponseSimilarMovie
import com.hadirahimi.movie.models.movie.ResponseSimilarMovie.Result
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterSimilarMovies @Inject constructor() :
    RecyclerView.Adapter<AdapterSimilarMovies.MyViewHolder>()
{
    private lateinit var binding : ItemSimilarMoviesBinding
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        @SuppressLint("SetTextI18n")
        fun setData(movie : Result)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                moviePoster.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                    crossfade(true)
                    crossfade(400)
                }
                
                //movie title
                movie.title.let {
                    movieTitle.text = movie.title.toString()
                }
                
                // movie time
                movieRunTime.text = "2h 13min"
                //movie rating
                if (movie.voteAverage != null)
                {
                    if (movie.voteAverage != 0.0)
                    {
                        val rating = String.format("%.1f" , movie.voteAverage)
                        movieRating.text = "$rating/10"
                    }
                    else
                    {
                        movieRating.text = "0/10"
                    }
        
                }
                //--------------------------------------
                
                
                //click listener
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
        binding =
            ItemSimilarMoviesBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(differ.currentList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = differ.currentList.size
    
    
    private val differCallBack = object : DiffUtil.ItemCallback<Result>()
    {
        override fun areItemsTheSame(
            oldItem : Result , newItem : Result
        ) : Boolean
        {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(
            oldItem : Result , newItem : Result
        ) : Boolean
        {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    
    
}