package com.hadirahimi.movie.ui.fragments.saved.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemSavedBinding
import com.hadirahimi.movie.databinding.ItemSearchBinding
import com.hadirahimi.movie.db.SavedEntity
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.ConstantsSaveMode
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class AdapterSaved @Inject constructor() : RecyclerView.Adapter<AdapterSaved.MyViewHolder>()
{
    private lateinit var binding : ItemSavedBinding
    
    private var movieList = emptyList<SavedEntity>()
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(saved : SavedEntity)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                if (saved.mode == ConstantsSaveMode.ACTORS && saved.image_url.isEmpty())
                {
                    ivSaved.load(R.drawable.actor_avatar) {
                        crossfade(true)
                        crossfade(400)
                    }
                }else
                {
                    ivSaved.load(Constants.BASE_URL_IMAGE + saved.image_url) {
                        crossfade(true)
                        crossfade(400)
                    }
                }
                
               
                
                //click listener
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(saved)
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
        binding = ItemSavedBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(movieList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((SavedEntity) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (SavedEntity) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = movieList.size
    
    
    fun submitData(data : List<SavedEntity>)
    {
        val movieDiffUtil = MovieDiffUtils(movieList , data)
        val diffUtils = DiffUtil.calculateDiff(movieDiffUtil)
        movieList = data
        
        diffUtils.dispatchUpdatesTo(this)
    }
    
    class MovieDiffUtils(
        private val oldItem : List<SavedEntity> ,
        private val newItem : List<SavedEntity>
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
    
    
}