package com.hadirahimi.movie.ui.fragments.home.adapters

import android.R.attr.category
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemNewestBinding
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponseNowPlayingMovies.Result
import com.hadirahimi.movie.ui.fragments.home.FragmentHome
import com.hadirahimi.movie.ui.fragments.moreMovie.MoreMovieFragmentDirections
import com.hadirahimi.movie.ui.fragments.movieSingle.FragmentMovieDirections
import com.hadirahimi.movie.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject


@ActivityScoped
class AdapterNewest @Inject constructor(@ApplicationContext val context : Context) :
    PagingDataAdapter<Result , AdapterNewest.MyViewHolder>(differCallBack)
{
    private lateinit var binding : ItemNewestBinding
    
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
                
                if (movie.genreIds != null && movie.genreIds.isNotEmpty() && genres.genres?.size != 0)
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
                     movie.id?.let { it1 ->
                         val direction = MoreMovieFragmentDirections.actionToFragmentMovie(it1)
                        
                        Navigation.findNavController(root).navigate(direction,NavOptions.Builder().setRestoreState(true).build())
                    }
                    
                    
                    
                }
            }
        }
        
        
    }
    
    private var onItemClickListener : ((Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding = ItemNewestBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(getItem(position) !!)
        holder.setIsRecyclable(false)
        binding.tvMovieName.tag = position
        
    }
    
    
    fun submitGenres(genres : ResponseGenre)
    {
        this.genres = genres
    }
    
    
    
    
 companion object{
    
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
 }
    
    
}


