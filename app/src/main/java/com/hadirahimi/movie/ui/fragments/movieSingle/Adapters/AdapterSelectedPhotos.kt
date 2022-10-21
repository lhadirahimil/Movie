package com.hadirahimi.movie.ui.fragments.movieSingle.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemSelectedPhotosBinding
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies.Cast
import com.hadirahimi.movie.models.movie.ResponseMoviePhotos
import com.hadirahimi.movie.models.movie.ResponseMoviePhotos.Backdrop
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterSelectedPhotos @Inject constructor() :
    RecyclerView.Adapter<AdapterSelectedPhotos.MyViewHolder>()
{
    private lateinit var binding : ItemSelectedPhotosBinding
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(movie : Backdrop)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                movieImage.load(Constants.BASE_URL_IMAGE + movie.filePath) {
                    crossfade(true)
                    crossfade(400)
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
            ItemSelectedPhotosBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(differ.currentList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((Backdrop) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (Backdrop) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = differ.currentList.size
    
    
    private val differCallBack = object : DiffUtil.ItemCallback<Backdrop>()
    {
        override fun areItemsTheSame(
            oldItem : Backdrop , newItem : Backdrop
        ) : Boolean
        {
            return oldItem.filePath == newItem.filePath
        }
        
        override fun areContentsTheSame(
            oldItem : Backdrop , newItem : Backdrop
        ) : Boolean
        {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    
    
}