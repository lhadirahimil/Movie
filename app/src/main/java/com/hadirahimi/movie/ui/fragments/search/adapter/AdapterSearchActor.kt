package com.hadirahimi.movie.ui.fragments.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemSearchBinding
import com.hadirahimi.movie.databinding.ItemSelectedMovieBinding
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies.Cast
import com.hadirahimi.movie.models.search.ResponseActor
import com.hadirahimi.movie.models.search.ResponseActor.Result
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterSearchActor @Inject constructor() : RecyclerView.Adapter<AdapterSearchActor.MyViewHolder>()
{
    //binding
    private lateinit var binding : ItemSearchBinding
    //data list
    private var actorList = emptyList<ResponseActor.Result>()
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(actor : Result)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                if (actor.profilePath.isNullOrEmpty())
                {
                    posterImage.load(R.drawable.actor_avatar)
                }
                else
                {
                    posterImage.load(Constants.BASE_URL_IMAGE + actor.profilePath) {
                        crossfade(true)
                        crossfade(600)
                        error(R.drawable.actor_avatar)
                    }
                }
                
                //Text : Actor name
                actor.name.let {
                    title.text = actor.name.toString()
                }
                
                //click listener
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(actor)
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
        holder.setData(actorList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = actorList.size
    
    fun submitData(data : List<Result>)
    {
        val actorDiffUtils = ActorDiffUtils(actorList,data)
        val diffutils = DiffUtil.calculateDiff(actorDiffUtils)
        actorList = data
        diffutils.dispatchUpdatesTo(this)
    }
    
    class ActorDiffUtils(private val oldItem :List<Result>,
    private val newItem : List<Result>):DiffUtil.Callback()
    {
        override fun getOldListSize() : Int
        {
            return  oldItem.size
        }
    
        override fun getNewListSize() : Int
        {
            return newItem.size
        }
    
        override fun areItemsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean
        {
            return  oldItem[oldItemPosition] === newItem[newItemPosition]
        }
    
        override fun areContentsTheSame(oldItemPosition : Int , newItemPosition : Int) : Boolean
        {
            return  oldItem[oldItemPosition] === newItem[newItemPosition]
        }
    
    }
    
}