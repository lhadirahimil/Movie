package com.hadirahimi.movie.ui.fragments.actorSingle.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemSelectedMovieBinding
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies.Cast
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterSelectedMovie @Inject constructor() :
    RecyclerView.Adapter<AdapterSelectedMovie.MyViewHolder>()
{
    private lateinit var binding : ItemSelectedMovieBinding
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(movie : Cast)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                moviePoster.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                    crossfade(true)
                    crossfade(400)
                }
                
                //rating bar
                if (movie.voteAverage != null)
                {
                    if (movie.voteAverage != 0f)
                    {
                        val average = Math.round(movie.voteAverage)
                        ratingBar.rating = average / 2.toFloat()
                    }
                    else
                    {
                        ratingBar.rating = 0f
                    }
                    
                }
                else ratingBar.visibility = View.GONE
                
                
                //Text : Actor name
                movieTitle.text = movie.title.toString()
                
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
        return differ.currentList[position].id
    }
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding =
            ItemSelectedMovieBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(differ.currentList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((Cast) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (Cast) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = differ.currentList.size
    
    
    private val differCallBack = object : DiffUtil.ItemCallback<Cast>()
    {
        override fun areItemsTheSame(
            oldItem : Cast , newItem : Cast
        ) : Boolean
        {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(
            oldItem : Cast , newItem : Cast
        ) : Boolean
        {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    
    
}