package com.hadirahimi.movie.ui.fragments.home.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hadirahimi.movie.databinding.LoadMoreBinding

class LoadStatesAdapter(private val retry : () -> Unit) :
    LoadStateAdapter<LoadStatesAdapter.MyViewHolder>()
{
    
    private lateinit var binding : LoadMoreBinding
    
    override fun onBindViewHolder(holder : LoadStatesAdapter.MyViewHolder , loadState : LoadState)
    {
        holder.bindData(loadState)
    }
    
    override fun onCreateViewHolder(
        parent : ViewGroup , loadState : LoadState
    ) : LoadStatesAdapter.MyViewHolder
    {
        
        binding = LoadMoreBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return MyViewHolder(retry)
        
    }
    
    inner class MyViewHolder(private val retry : () -> Unit) : RecyclerView.ViewHolder(binding.root)
    {
        init
        {
            binding.btnRetry.setOnClickListener {
                retry()
            }
        }
        
        fun bindData(state : LoadState)
        {
            //InitViews
            binding.apply {
                //progressbar.isVisible = state is LoadState.Loading
                tvError.isVisible = state is LoadState.Error
                btnRetry.isVisible = state is LoadState.Error
                progress.isVisible = state !is LoadState.Error
            }
        }
    }
    
    
}