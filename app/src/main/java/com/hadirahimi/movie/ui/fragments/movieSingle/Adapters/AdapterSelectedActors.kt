package com.hadirahimi.movie.ui.fragments.movieSingle.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemSelectedActorsBinding
import com.hadirahimi.movie.models.movie.ResponseMoviesActors
import com.hadirahimi.movie.utils.Constants
import javax.inject.Inject

class AdapterSelectedActors @Inject constructor() :
    RecyclerView.Adapter<AdapterSelectedActors.MyViewHolder>()
{
    private lateinit var binding : ItemSelectedActorsBinding
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(actor : ResponseMoviesActors.Cast)
        {
            binding.apply {
                //i can check if image is null so choose a default image
                if (actor.profilePath.isNullOrEmpty())
                {
                    actorImage.load(R.drawable.actor_avatar) {
                        crossfade(true)
                        crossfade(600)
                        error(R.drawable.actor_avatar)
                    }
                }
                else
                {
                    actorImage.load(Constants.BASE_URL_IMAGE + actor.profilePath) {
                        crossfade(true)
                        crossfade(600)
                        error(R.drawable.actor_avatar)
                    }
                }
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
            ItemSelectedActorsBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun onBindViewHolder(holder : MyViewHolder , position : Int)
    {
        holder.setData(differ.currentList[position])
        holder.setIsRecyclable(false)
    }
    
    
    private var onItemClickListener : ((ResponseMoviesActors.Cast) -> Unit?)? = null
    
    fun setOnItemClickListener(listener : (ResponseMoviesActors.Cast) -> Unit)
    {
        onItemClickListener = listener
    }
    
    
    override fun getItemCount() : Int = differ.currentList.size
    
    
    private val differCallBack = object : DiffUtil.ItemCallback<ResponseMoviesActors.Cast>()
    {
        override fun areItemsTheSame(
            oldItem : ResponseMoviesActors.Cast , newItem : ResponseMoviesActors.Cast
        ) : Boolean
        {
            return oldItem === newItem
        }
        
        override fun areContentsTheSame(
            oldItem : ResponseMoviesActors.Cast , newItem : ResponseMoviesActors.Cast
        ) : Boolean
        {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    
    
}