package com.hadirahimi.movie.ui.fragments.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemSearchBinding
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.models.search.ResponseMovie.*
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject
import kotlin.Result

class AdapterSearchMovie @Inject constructor() : RecyclerView.Adapter<AdapterSearchMovie.MyViewHolder>()
{
    private lateinit var binding : ItemSearchBinding
    
    private var movieList= emptyList<ResponseMovie.Result>()
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(movie : ResponseMovie.Result)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                posterImage.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                    crossfade(true)
                    crossfade(400)
                }
                
                //Text : Actor name
                movie.title.let {
                    title.text = movie.title.toString()
                }
                
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
            ItemSearchBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(movieList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((ResponseMovie.Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (ResponseMovie.Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = movieList.size
    
    
   fun submitData(data : List<ResponseMovie.Result>)
   {
        val movieDiffUtil = MovieDiffUtils(movieList,data)
       val diffUtils = DiffUtil.calculateDiff(movieDiffUtil)
       movieList = data
       
       diffUtils.dispatchUpdatesTo(this)
   }
    
    class MovieDiffUtils(private val oldItem : List< ResponseMovie.Result>,
    private val newItem : List< ResponseMovie.Result>):DiffUtil.Callback()
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
    
    
}