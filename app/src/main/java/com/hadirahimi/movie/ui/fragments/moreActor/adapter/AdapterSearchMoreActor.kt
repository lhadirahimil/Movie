package com.hadirahimi.movie.ui.fragments.moreActor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemMoreActorsBinding
import com.hadirahimi.movie.databinding.ItemSearchBinding
import com.hadirahimi.movie.databinding.ItemSelectedMovieBinding
import com.hadirahimi.movie.models.actor.ResponseSelectedMovies.Cast
import com.hadirahimi.movie.models.search.ResponseActor
import com.hadirahimi.movie.models.search.ResponseActor.Result
import com.hadirahimi.movie.models.search.ResponseMovie
import com.hadirahimi.movie.utils.Constants
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import dagger.hilt.android.scopes.ViewScoped
import javax.inject.Inject

@ActivityScoped
class AdapterSearchMoreActor @Inject constructor() : RecyclerView.Adapter<AdapterSearchMoreActor.MyViewHolder>()
{
    //binding
    private lateinit var binding : ItemMoreActorsBinding
    //data list
    private var actorList = emptyList<ResponseActor.Result>()
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(actor : Result)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                ivActorsProfile.load(Constants.BASE_URL_IMAGE + actor.profilePath) {
                    placeholder(R.drawable.actor_avatar)
                    crossfade(true)
                    crossfade(400)
                }
                
                //Text : Actor name
                actor.name.let {
                    tvActorName.text = actor.name.toString()
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
            ItemMoreActorsBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
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