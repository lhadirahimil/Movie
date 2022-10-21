package com.hadirahimi.movie.ui.fragments.home.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ItemGenreBinding
import com.hadirahimi.movie.models.home.ResponseGenre
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AdapterGenres @Inject constructor(@ApplicationContext val context : Context) :
    RecyclerView.Adapter<AdapterGenres.MyViewHolder>()
{
    private lateinit var binding : ItemGenreBinding
    private var selectedItem : Int = 0
    
    inner class MyViewHolder : RecyclerView.ViewHolder(binding.root)
    {
        fun setData(genres : ResponseGenre.Genre)
        {
            binding.apply {
                tvGenreTitle.text = genres.name
                tvGenreTitle.setBackgroundResource(R.drawable.item_genre)
                
            }
        }
    }
    
    
    override fun onCreateViewHolder(
        parent : ViewGroup , viewType : Int
    ) : AdapterGenres.MyViewHolder
    {
        //init binding
        binding = ItemGenreBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder()
    }
    
    override fun getItemViewType(position : Int) : Int
    {
        return position
    }
    
    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder : AdapterGenres.MyViewHolder , @SuppressLint("RecyclerView") position : Int)
    {
        holder.setData(differ.currentList[position])
        holder.setIsRecyclable(false)
        
//        if (position == selectedItem)
//        {
//            binding.tvGenreTitle.setBackgroundResource(R.drawable.item_genre_selected)
//        }else
//        {
//            binding.tvGenreTitle.setBackgroundResource(R.drawable.item_genre_default)
//        }

//        binding.tvGenreTitle.setOnClickListener {
//            selectedItem = position
//            notifyDataSetChanged()
//        }
        
    }
    
    override fun getItemCount() : Int = differ.currentList.size
    
    private val differCallBack = object : DiffUtil.ItemCallback<ResponseGenre.Genre>()
    {
        override fun areItemsTheSame(
            oldItem : ResponseGenre.Genre , newItem : ResponseGenre.Genre
        ) : Boolean
        {
            return oldItem.id == newItem.id
        }
        
        override fun areContentsTheSame(
            oldItem : ResponseGenre.Genre , newItem : ResponseGenre.Genre
        ) : Boolean
        {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , differCallBack)
    
    
}
