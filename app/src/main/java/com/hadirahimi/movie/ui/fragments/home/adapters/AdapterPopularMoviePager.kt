package com.hadirahimi.movie.ui.fragments.home.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemPopularMoviesBinding
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
    private lateinit var binding : ItemPopularMoviesBinding
    
    
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
            }
        }
        
        
    }
    
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding =
            ItemPopularMoviesBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
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
    
    
}