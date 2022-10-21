package com.hadirahimi.movie.ui.fragments.moreActor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.databinding.ItemMoreActorsBinding
import com.hadirahimi.movie.models.home.ResponsePopularActors.Result
import com.hadirahimi.movie.utils.Constants
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class AdapterPopularActorsPager @Inject constructor() :
    PagingDataAdapter<Result , AdapterPopularActorsPager.MyViewHolder>(differCallBack)
{
    private lateinit var binding : ItemMoreActorsBinding
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(actors : Result)
        {
            binding.apply {
                ivActorsProfile.load(Constants.BASE_URL_IMAGE + actors.profilePath) {
                    crossfade(true)
                    crossfade(400)
                }
                
                
                //Text : Actor name
                tvActorName.text = actors.name
                
                //click listener
                root.setOnClickListener {
                    onItemClickListener?.let {
                        it(actors)
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
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        getItem(position)?.let {
            holder.setData(it)
        }
        holder.setIsRecyclable(false)
    }
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding =
            ItemMoreActorsBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    companion object
    {
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