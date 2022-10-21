package com.hadirahimi.movie.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemActorsBinding
import com.hadirahimi.movie.models.home.ResponsePopularActors
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterPopularActorsLimited @Inject constructor() :
    RecyclerView.Adapter<AdapterPopularActorsLimited.MyViewHolder>()
{
    private lateinit var binding : ItemActorsBinding
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(actors : ResponsePopularActors.Result)
        {
            binding.apply {
                ivActorsProfile.load(Constants.BASE_URL_IMAGE + actors.profilePath) {
                    crossfade(true)
                    crossfade(400)
                    placeholder(R.drawable.actor_avatar)
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
    
    override fun getItemViewType(position : Int) : Int
    {
        return differ.currentList[position].id !!
    }
    
    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : MyViewHolder
    {
        //init binding
        binding = ItemActorsBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(differ.currentList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((ResponsePopularActors.Result) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (ResponsePopularActors.Result) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int =
        if (differ.currentList.size > 6) 6 else differ.currentList.size
    
    private val differCallBack = object : DiffUtil.ItemCallback<ResponsePopularActors.Result>()
    {
        override fun areItemsTheSame(
            oldItem : ResponsePopularActors.Result , newItem : ResponsePopularActors.Result
        ) : Boolean
        {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(
            oldItem : ResponsePopularActors.Result , newItem : ResponsePopularActors.Result
        ) : Boolean
        {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    
    
}