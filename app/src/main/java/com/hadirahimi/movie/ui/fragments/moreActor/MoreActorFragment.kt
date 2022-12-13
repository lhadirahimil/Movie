package com.hadirahimi.movie.ui.fragments.moreActor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.filter
import androidx.recyclerview.widget.GridLayoutManager
import com.hadirahimi.movie.databinding.FragmentMoreActorBinding
import com.hadirahimi.movie.ui.fragments.home.FragmentHomeDirections
import com.hadirahimi.movie.ui.fragments.moreActor.adapter.AdapterPopularActorsPager
import com.hadirahimi.movie.ui.fragments.moreActor.adapter.AdapterSearchMoreActor
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.init
import com.hadirahimi.movie.viewModel.ViewModelMoreActor
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@AndroidEntryPoint
@ActivityScoped
class MoreActorFragment : Fragment()
{
    //binding
    private lateinit var binding : FragmentMoreActorBinding
    
    //adapter
    @Inject
    lateinit var adapter : AdapterPopularActorsPager
    
    @Inject
    lateinit var adapterSearch : AdapterSearchMoreActor
    
    //viewModel
    private val viewModel : ViewModelMoreActor by viewModels()
    
    var isSearch = false
    
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentMoreActorBinding.inflate(layoutInflater)
        return binding.root
        
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
        //init Views
        binding.apply {
    
            lifecycleScope.launch {
                //search actor by Name
                viewModel.actorResult.observe(viewLifecycleOwner){actors->
                    if (isSearch)
                    {
                        adapterSearch.submitData(actors.results)
                        recyclerActors.init(GridLayoutManager(requireContext() , 3) , adapterSearch)
                    }
                }
            }
            
            //all actors
            lifecycleScope.launchWhenCreated {
                //get all actors from paging
                viewModel.actors.map {
                    it.filter { actor->
                        //filter porn star actors by id
                        actor.id !in listOf(3194176,1907997,2710789,3030333,2472212,2590519,1468490,2349944,3371806,1549899,1721615,2243993,2619408,2790319,1721638,1914924,2473962)
                    }
                }.collect {
                    if (!isSearch) adapter.submitData(it)
                }
            }
    
    
    

            
            //setup recyclerview
            recyclerActors.init(GridLayoutManager(requireContext() , 3) , adapter)
            
            searchWatcher()
        }
        onClickListener()
    }
    
    private fun searchWatcher()
    {
        binding.etSearch.addTextChangedListener { text ->
            //change adapter for search
            if (text != null)
            {
                if (text.toString().trim().length >= Constants.SEARCH_LENGTH)
                {
                    //adapterSearch.submitData()
                    isSearch = true
                    viewModel.searchActor(text.toString())
                    
                    binding.recyclerActors.init(GridLayoutManager(requireContext() , 3) , adapterSearch)
                }
                else
                {
                    isSearch = false
                    binding.recyclerActors.init(GridLayoutManager(requireContext() , 3) , adapter)
                }
            }
            else
            {
                isSearch = false
                binding.recyclerActors.init(GridLayoutManager(requireContext() , 3) , adapter)
            }
        }
    }
    
    private fun onClickListener()
    {
        //item click
        adapter.setOnItemClickListener { actor ->
            val direction = actor.id?.let { FragmentHomeDirections.actionToActorFragment(it) }
            direction?.let { findNavController().navigate(it) }
        }
        //search item click
        adapterSearch.setOnItemClickListener { actor ->
            val direction = actor.id.let { FragmentHomeDirections.actionToActorFragment(it) }
            direction.let { findNavController().navigate(it) }
        }
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        
    }
    
}