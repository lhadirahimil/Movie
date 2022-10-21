package com.hadirahimi.movie.ui.fragments.moreMovie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.hadirahimi.movie.databinding.FragmentMoreMovieBinding
import com.hadirahimi.movie.ui.fragments.home.FragmentHomeDirections
import com.hadirahimi.movie.ui.fragments.home.adapters.LoadStatesAdapter
import com.hadirahimi.movie.ui.fragments.moreMovie.adapter.AdapterNewestPager
import com.hadirahimi.movie.ui.fragments.moreMovie.adapter.AdapterPopularMoviePager
import com.hadirahimi.movie.ui.fragments.moreMovie.adapter.AdapterSearchMovies
import com.hadirahimi.movie.utils.Constants
import com.hadirahimi.movie.utils.init
import com.hadirahimi.movie.viewModel.ViewModelMoreMovie
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@ActivityScoped
class MoreMovieFragment : Fragment()
{
    //binding
    private lateinit var binding : FragmentMoreMovieBinding
    
    //viewModel
    private val viewModel : ViewModelMoreMovie by viewModels()
    
    //adapter
    @Inject
    lateinit var adapterPopularMovie : AdapterPopularMoviePager
    
    @Inject
    lateinit var adapterNewestMovie : AdapterNewestPager
    
    @Inject
    lateinit var adapterSearchMovies : AdapterSearchMovies
    
    //other
    private var movieMode = 1
    private val args : MoreMovieFragmentArgs by navArgs()
    var isSearch = false
    var modePopular = false
    
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        movieMode = args.movieMode
        checkMovieMode()
        viewModel.allGenres()
    }
    
    private fun checkMovieMode()
    {
        when (movieMode)
        {
            Constants.MODE_POPULAR ->
            {
                modePopular = true
            }
            Constants.MODE_NEWEST ->
            {
                modePopular = false
            }
        }
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentMoreMovieBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        //init views
        binding.apply {
            // all Genres
            viewModel.allGenres.observe(viewLifecycleOwner) { genres ->
                if (modePopular) adapterPopularMovie.submitGenres(genres)
                else adapterNewestMovie.submitGenres(genres)
                
                adapterSearchMovies.submitGenres(genres)
            }
            
            
            
            lifecycleScope.launch {
                //search movie by Name
                viewModel.movieResult.observe(viewLifecycleOwner) { movies ->
                    if (isSearch)
                    {
                        adapterSearchMovies.submitData(movies.results)
                        recyclerMovies.init(
                            GridLayoutManager(requireContext() , 2) , adapterSearchMovies
                        )
                    }
                }
            }
            
            if (! isSearch)
            {
                setupUiWithMode(modePopular)
            }
            else
            {
                //show search result
                
            }
            
        }
        searchTextChange()
        clickListener()
        
    }
    
    private fun setupUiWithMode(modePopular : Boolean)
    {
        binding.apply {
            when (modePopular)
            {
                true ->
                {
                    //Mode : popular Movie
                    lifecycleScope.launchWhenCreated {
                        viewModel.popularMovie.collect {
                            adapterPopularMovie.submitData(it)
                        }
                    }
                    //setup recyclerview
                    recyclerMovies.init(
                        GridLayoutManager(requireContext() , 2) , adapterPopularMovie
                    )
                    recyclerMovies.adapter =
                        adapterPopularMovie.withLoadStateFooter(LoadStatesAdapter { adapterPopularMovie.retry() })
                }
                false ->
                {
                    //Mode : Newest Movie
                    lifecycleScope.launchWhenCreated {
                        viewModel.newestMovie.collect {
                            adapterNewestMovie.submitData(it)
                        }
                    }
                    //setup recyclerview
                    recyclerMovies.init(
                        GridLayoutManager(requireContext() , 2) , adapterNewestMovie
                    )
                    recyclerMovies.adapter =
                        adapterNewestMovie.withLoadStateFooter(LoadStatesAdapter { adapterNewestMovie.retry() })
                }
            }
        }
    }
    
    private fun searchTextChange()
    {
        binding.etSearch.addTextChangedListener { movieName ->
            if (movieName != null)
            {
                if (movieName.toString().trim().length >= Constants.SEARCH_LENGTH)
                {
                    viewModel.searchMovieByName(movieName.toString())
                    isSearch = true
                }
                else
                {
                    isSearch = false
                    setupUiWithMode(modePopular)
                }
                
            }
            else
            {
                isSearch = false
                setupUiWithMode(modePopular)
            }
        }
    }
    
    private fun clickListener()
    {
        //newest movie item click
        adapterNewestMovie.setOnItemClickListener { movie ->
            val direction = movie.id?.let { FragmentHomeDirections.actionToFragmentMovie(it) }
            direction?.let { findNavController().navigate(it) }
        }
        //popular movie item click
        adapterPopularMovie.setOnItemClickListener { movie ->
            val direction = movie.id?.let { FragmentHomeDirections.actionToFragmentMovie(it) }
            direction?.let { findNavController().navigate(it) }
        }
        //popular movie item click
        adapterSearchMovies.setOnItemClickListener { movie ->
            val direction = movie.id.let { FragmentHomeDirections.actionToFragmentMovie(it) }
            direction.let { findNavController().navigate(it) }
        }
    }
    
}