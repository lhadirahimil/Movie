package com.hadirahimi.movie.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.paging.map
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import cdflynn.android.library.turn.TurnLayoutManager
import coil.load
import com.google.android.material.tabs.TabLayout
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentHomeBinding
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.ui.activity.MainActivity
import com.hadirahimi.movie.ui.fragments.home.adapters.*
import com.hadirahimi.movie.ui.fragments.moreMovie.MoreMovieFragmentDirections
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.init
import com.hadirahimi.movie.viewModel.ViewModelHome
import com.jackandphantom.carouselrecyclerview.CarouselLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.android.scopes.FragmentScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton


@AndroidEntryPoint
@Singleton
class FragmentHome : Fragment()
{
    //binding
    private lateinit var binding : FragmentHomeBinding
    
    //view Model
    private val viewModel : ViewModelHome by viewModels()
    
    //adapters
    @Inject
    lateinit var adapterPopularActorsLimited : AdapterPopularActorsLimited
    
    @Inject
    lateinit var adapterGenres : AdapterGenres
    
    @Inject
    lateinit var adapterNewest : AdapterNewest
    
    
    @Inject
    lateinit var pagerNewest : PagerSnapHelper
    
    @Inject
    lateinit var adapterPopularMovies : AdapterPopularMovie
    
    private var selectedTabId = - 1
    
    @Inject
    lateinit var publicGenres : ResponseGenre
    
    //animation id
    private var animationId = 16
    
    
    
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        //Call Api Requests And Collect
        viewModel.popularActorsLimited()
        viewModel.allGenres()
        viewModel.popularMovies()
        viewModel.popularMovies()
    }
    
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
        // init views
        binding.apply {
            
            // get last movie
            viewModel.lastMovie.observe(viewLifecycleOwner) {
                
                if (it?.posterPath != null && it.adult == false)
                {
                    cardLastMovie.visibility = View.VISIBLE
                    tvLastMovieTitle.text = it.title
                    ivLastMoviePoster.load(Constants.BASE_URL_IMAGE + it.posterPath) {
                        crossfade(true)
                        crossfade(1000)
                    }
                }
                else cardLastMovie.visibility = View.GONE
                
            }
            
            // get popular Actors From ViewModel
            viewModel.popularActorsLimited.observe(viewLifecycleOwner) { actors ->
                
                //submit adapter list
                adapterPopularActorsLimited.differ.submitList(actors.results)
                
                // init Recyclerview
                recyclerPopularActors.init(
                    LinearLayoutManager(
                        requireContext() , LinearLayoutManager.HORIZONTAL , false
                    ) , adapterPopularActorsLimited
                )
                
            }
            
            // get All Genres From ViewModel
            viewModel.allGenres.observe(viewLifecycleOwner) { genres ->
                
                //make genre list public for filter Newest Movies by Genre
                publicGenres = genres
                
                //submit genre list for newest Movie Adapter
                adapterNewest.submitGenres(publicGenres)
                //submit genre list for popular Movie Adapter
                adapterPopularMovies.submitGenres(publicGenres)
                
                //init adapter
                adapterGenres.differ.submitList(genres.genres)
                
                //set Default selected tab as first genre id
                selectedTabId = genres.genres !![0] !!.id
                
                //fill tab Layout tabs as Genres
                for (genre in genres.genres !!)
                {
                    //create a new Tab For TabLayout
                    tabGenre.addTab(tabGenre.newTab().setText(genre?.name).setTag(genre?.id))
                }
                
                //
                tabGenre.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
                {
                    override fun onTabSelected(tab : TabLayout.Tab?)
                    {
                        if (tab != null) selectedTabId = tab.tag as Int
                        adapterNewest.refresh()
                        pushToRecyclerNewest()
                    }
                    
                    override fun onTabUnselected(tab : TabLayout.Tab?)
                    {
                    
                    }
                    
                    override fun onTabReselected(tab : TabLayout.Tab?)
                    {
                    
                    }
                    
                })
            }
            
            // Newest Movie
            lifecycleScope.launchWhenCreated {
                
                viewModel.newestMovie.map {
                    it.filter { response ->
                        if (response.genreIds !!.isNotEmpty())
                        {
                            response.genreIds[0] == selectedTabId
                        }
                        else false
                    }
                }.collect {

                    //adapterNewest = AdapterNewest(requireContext())
                    adapterNewest.submitGenres(genres = publicGenres)
                    adapterNewest.submitData(it)
                }
                
            }
            pushToRecyclerNewest()
            
            
            //get error connection
            viewModel.error.observe(viewLifecycleOwner) { error ->
                if (error)
                //error in get data show error network to user
                {
                    (activity as MainActivity).networkError(true)
                    layoutMain.visibility = View.GONE
                }
                else
                {
                    (activity as MainActivity).networkError(false)
                    layoutMain.visibility = View.VISIBLE
                }
                
            }
            
            //loading actors
            viewModel.loadingActors.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading)
                {
                    recyclerPopularActors.visibility = View.INVISIBLE
                    loadingActors.visibility = View.VISIBLE
                }
                else
                {
                    recyclerPopularActors.visibility = View.VISIBLE
                    loadingActors.visibility = View.GONE
                }
            }
            
            //loading genre
            viewModel.loadingGenres.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading)
                {
                    tabGenre.visibility = View.INVISIBLE
                    loadingGenres.visibility = View.VISIBLE
                }
                else
                {
                    tabGenre.visibility = View.VISIBLE
                    loadingGenres.visibility = View.GONE
                }
            }
            
            //newest movie Loading
            lifecycleScope.launchWhenCreated {
                adapterNewest.loadStateFlow.collect {
                    val state = it.refresh
                    loadingNewest.isVisible = state is LoadState.Loading
                    
                    if (state is LoadState.Loading)
                    {
                        delay(3000)
                        if (adapterNewest.itemCount > 2) recyclerNewest.smoothScrollToPosition(1)
                        
                    }
                    
                }
            }
            
            //popular movies
            viewModel.popularMovies.observe(viewLifecycleOwner) { popularMovies ->
                recyclerPopularMovies.apply {
                    adapterPopularMovies.differ.submitList(popularMovies.results)
                    adapter = adapterPopularMovies
                    set3DItem(true)
                    setInfinite(true)
                    setAlpha(true)
                    setFlat(false)
                    setIsScrollingEnabled(true)
                }
            }


//             popular movies Loading
            viewModel.loadingPopularMovies.observe(viewLifecycleOwner) { isLoading ->
                if (isLoading)
                {
                    recyclerPopularMovies.visibility = View.INVISIBLE
                    loadingPopularMovies.visibility = View.VISIBLE
                }
                else
                {
                    recyclerPopularMovies.visibility = View.VISIBLE
                    loadingPopularMovies.visibility = View.GONE
                }
            }
            
            clickListener()
            
        }
    }
    
    private fun clickListener()
    {
        binding.apply {
            //go to search fragment
            tvSearch.setOnClickListener {
                findNavController().navigate(R.id.action_to_fragmentSearch)
                (activity as MainActivity).searchSelected()
            }
            //more actor
            tvSeeAllPopularActors.setOnClickListener {
                findNavController().navigate(R.id.action_to_moreActorFragment)
            }
            
            // more newest movies
            tvSeeAllNewestMovie.setOnClickListener {
                val direction =
                    MoreMovieFragmentDirections.actionToMoreMovieFragment(Constants.MODE_NEWEST)
                findNavController().navigate(direction)
            }
            // more popular movies
            tvSeeAllPopularMovie.setOnClickListener {
                val direction =
                    MoreMovieFragmentDirections.actionToMoreMovieFragment(Constants.MODE_POPULAR)
                findNavController().navigate(direction)
            }
            
            //go to Actor Detail fragment
            adapterPopularActorsLimited.setOnItemClickListener { actor ->
                val direction = actor.id?.let { FragmentHomeDirections.actionToActorFragment(it) }
                direction?.let { findNavController().navigate(it) }
            }
            // go to Movie Detail fragment for newest Movies
            adapterNewest.setOnItemClickListener { movie ->
                val direction = movie.id?.let { FragmentHomeDirections.actionToFragmentMovie(it) }
                direction?.let { findNavController().navigate(it) }
            }
            // go to Movie Detail fragment for popular movies
            adapterPopularMovies.setOnItemClickListener { movie ->
                val direction = movie.id?.let { FragmentHomeDirections.actionToFragmentMovie(it) }
                direction?.let { findNavController().navigate(it) }
            }
        }
    }
    
    
    private fun pushToRecyclerNewest()
    {
        //init views
        binding.apply {
            recyclerNewest.init(
                TurnLayoutManager(
                    context ,
                    TurnLayoutManager.Gravity.END ,
                    TurnLayoutManager.Orientation.HORIZONTAL ,
                    6000 ,
                    10 ,
                    true
                ) , adapter = adapterNewest
            )
            
            //single Item Scroll
            pagerNewest.attachToRecyclerView(recyclerNewest)


//            lifecycleScope.launchWhenCreated {
//                //when a tab selected scroll to item 2 for beautiful designLoadStatesAdapter
//                delay(700)
//                if (adapterNewest.itemCount > 1)
//                {
//                    binding.recyclerNewest.smoothScrollToPosition(1)
//                }
//            }
            
        }
        
        
    }
    
    
}