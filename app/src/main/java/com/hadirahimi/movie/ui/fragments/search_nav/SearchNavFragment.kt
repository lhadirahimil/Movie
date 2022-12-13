package com.hadirahimi.movie.ui.fragments.search_nav

import android.os.Bundle
import android.util.Log
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
    
    
    // view Model
    private val viewModel : ViewModelSearch by viewModels()
    
    //adapters
    @Inject
    lateinit var adapterActor : com.hadirahimi.movie.ui.fragments.search.adapter.AdapterSearchActor
    
    @Inject
    lateinit var adapterMovie : com.hadirahimi.movie.ui.fragments.search.adapter.AdapterSearchMovie
    
    var prevSearch = ""
    
    
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
                if (viewModel.isActors)
                {
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
            }
            viewModel.movies.observe(viewLifecycleOwner) { movies ->
                if (!viewModel.isActors)
                {
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
    }
    
    private fun destinationChangedListener()
    {
        findNavController().addOnDestinationChangedListener { _ , destination , _ ->
            if (destination.id == R.id.fragmentSearch)
            {
                when (viewModel.isActors)
                {
                    // when come back to this fragment from other fragments. go to last user selected tab
                    true ->
                    {
                        if (binding.tabSearchMode.selectedTabPosition != 0)
                        {
                            Log.d("HECTOR" , "item 0 selected from destination")
                            binding.tabSearchMode.getTabAt(0)?.select()
                        }
                        
                        
                    }
                    false ->
                    {
                        if (binding.tabSearchMode.selectedTabPosition != 1)
                        {
                            Log.d("HECTOR" , "item 1 selected from destination")
                            binding.tabSearchMode.getTabAt(1)?.select()
                        }
                        
                        
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
                    if (viewModel.isActors && text.toString() != prevSearch)
                    {
                        //search in actors api
                        viewModel.searchActor(text.toString())
                        prevSearch = text.toString()
                    }
                    else if (! viewModel.isActors && text.toString() != prevSearch)
                    {
                        //search in movies api
                        viewModel.searchMovie(text.toString())
                        prevSearch = text.toString()
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
            if (tabSearchMode.tabCount == 0)
            {
                tabSearchMode.addTab(tabSearchMode.newTab().setText(ConstantsSearchMode.ACTORS))
                tabSearchMode.addTab(tabSearchMode.newTab().setText(ConstantsSearchMode.MOVIES))
            }
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
                    Log.d("HECTOR" , "tab selected from tab layout")
                    searchHintByTabMode(tab?.text.toString())
                    searchWhenTabSelected(tab?.text.toString())
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
    
    private fun searchWhenTabSelected(tab_title : String)
    {
        val searchText = binding.etSearch.text.trim().toString()
        
        when(tab_title)
        {
            ConstantsSearchMode.MOVIES->{
                //search in movies api
                if (viewModel.isActors && searchText.length > Constants.SEARCH_LENGTH)
                    viewModel.searchMovie(searchText)
                viewModel.isActors = false
                
            }
            ConstantsSearchMode.ACTORS->{
                //search in actors api
                if (!viewModel.isActors && searchText.length > Constants.SEARCH_LENGTH)
                    viewModel.searchActor(searchText)
                viewModel.isActors = true
            }
        }
    }
    
    private fun searchHintByTabMode(tab_title:String){
        when (tab_title)
        {
            
            ConstantsSearchMode.ACTORS -> binding.etSearch.hint = "Search a actors ..."
            
            
            ConstantsSearchMode.MOVIES -> binding.etSearch.hint = "Search a movies ..."
        }
    }
    
}