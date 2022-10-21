package com.hadirahimi.movie.ui.fragments.search_nav

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentSearchBinding
import com.hadirahimi.movie.databinding.FragmentSearchNavBinding
import com.hadirahimi.movie.ui.fragments.search.FragmentSearchDirections
import com.hadirahimi.movie.ui.fragments.search_nav.adapter.AdapterSearchActor
import com.hadirahimi.movie.ui.fragments.search_nav.adapter.AdapterSearchMovie
import com.hadirahimi.movie.utils.*
import com.hadirahimi.movie.viewModel.ViewModelSearch
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchNavFragment : Fragment()
{
    
    //binding
    private lateinit var binding : FragmentSearchNavBinding
    
    // detect search mode by this boolean
    private var isActors = true
    
    // view Model
    private val viewModel : ViewModelSearch by viewModels()
    
    //adapters
    @Inject
    lateinit var adapterActor : AdapterSearchActor
    
    @Inject
    lateinit var adapterMovie : AdapterSearchMovie
    
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentSearchNavBinding.inflate(layoutInflater)
        return binding.root
    }
    
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
        setupTabLayoutSearchMode()
        clickListener()
        textChangeListener()
        destinationChangedListener()
        
        binding.apply {
            viewModel.actors.observe(viewLifecycleOwner) { actors ->
                when (actors.status)
                {
                    MyResponse.Status.LOADING ->
                    {
                        loading.visible(true)
                    }
                    MyResponse.Status.EMPTY ->
                    {
                        loading.visible(false)
                        emptyLayout.show(true , recyclerSearch)
                    }
                    MyResponse.Status.ERROR ->
                    {
                        loading.visible(false)
                        emptyLayout.visible(false)
                    }
                    MyResponse.Status.SUCCESS ->
                    {
                        loading.visible(false)
                        emptyLayout.show(false , recyclerSearch)
                        
                        actors.data?.let {
                            //submit data to adapter
                            adapterActor.submitData(it.results)
                            //setup recyclerview
                            recyclerSearch.init(LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false) , adapterActor)
                        }
                        
                    }
                }
            }
            viewModel.movies.observe(viewLifecycleOwner) { movies ->
                when (movies.status)
                {
                    MyResponse.Status.LOADING ->
                    {
                        loading.visible(true)
                    }
                    MyResponse.Status.EMPTY ->
                    {
                        loading.visible(false)
                        emptyLayout.show(true , recyclerSearch)
                    }
                    MyResponse.Status.ERROR ->
                    {
                        loading.visible(false)
                        emptyLayout.visible(false)
                    }
                    MyResponse.Status.SUCCESS ->
                    {
                        loading.visible(false)
                        emptyLayout.show(false , recyclerSearch)
                        
                        movies.data?.let {
                            //submit data to adapter
                            adapterMovie.submitData(it.results)
                            
                            //setup recyclerview
                            recyclerSearch.init(LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false) , adapterMovie)
                        }
                        
                    }
                }
            }
        }
    }
    
    private fun destinationChangedListener()
    {
        findNavController().addOnDestinationChangedListener { _ , destination , _ ->
            if (destination.id == R.id.fragmentSearch)
            {
                when (isActors)
                {
                    // when come back to this fragment from other fragments. go to last user selected tab
                    true ->
                    {
                        binding.tabSearchMode.getTabAt(0)?.select()
                    }
                    false ->
                    {
                        binding.tabSearchMode.getTabAt(1)?.select()
                    }
                }
            }
        }
    }
    
    private fun textChangeListener()
    {
        binding.etSearch.addTextChangedListener { text ->
            try
            {
                
                if (text.toString().length > Constants.SEARCH_LENGTH)
                {
                    if (isActors)
                    {
                        //search in actors api
                        viewModel.searchActor(text.toString())
                    }
                    else
                    {
                        //search in movies api
                        viewModel.searchMovie(text.toString())
                    }
                }
                
            } catch (e : Exception)
            {
            
            }
        }
    }
    
    private fun setupTabLayoutSearchMode()
    {
        binding.apply {
            tabSearchMode.addTab(tabSearchMode.newTab().setText(ConstantsSearchMode.ACTORS))
            tabSearchMode.addTab(tabSearchMode.newTab().setText(ConstantsSearchMode.MOVIES))
        }
    }
    
    private fun clickListener()
    {
        binding.apply {
            
            
            //TabLayout Search Mode Click Listener
            tabSearchMode.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
            {
                override fun onTabSelected(tab : TabLayout.Tab?)
                {
                    searchHintByTabMode(tab?.text.toString())
                }
                
                override fun onTabUnselected(tab : TabLayout.Tab?)
                {
                
                }
                
                override fun onTabReselected(tab : TabLayout.Tab?)
                {
                
                }
                
            })
            
            //adapter Actor Click
            adapterActor.setOnItemClickListener { actor ->
                
                actor.id.let { actor_id ->
                    val direction = FragmentSearchDirections.actionToActorFragment(actor_id)
                    findNavController().navigate(direction)
                }
                
                
            }
            //adapter Movie Click
            adapterMovie.setOnItemClickListener { movie ->
                movie.id.let { movie_id ->
                    val direction = FragmentSearchDirections.actionToFragmentMovie(movie_id)
                    findNavController().navigate(direction)
                }
                
            }
        }
    }
    private fun searchHintByTabMode(tab_title:String){
        when (tab_title)
        {
            ConstantsSearchMode.ACTORS ->
            {
                isActors = true
                binding.etSearch.hint = "Search a actors ..."
                
            }
            
            ConstantsSearchMode.MOVIES ->
            {
                isActors = false
                binding.etSearch.hint = "Search a movies ..."
                
            }
        }
    }
    
}