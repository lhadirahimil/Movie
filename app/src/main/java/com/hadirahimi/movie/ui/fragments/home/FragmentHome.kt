package com.hadirahimi.movie.ui.fragments.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.datastore.dataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.filter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import cdflynn.android.library.turn.TurnLayoutManager
import coil.load
import com.google.android.material.tabs.TabLayout
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentHomeBinding
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.home.ResponseUser
import com.hadirahimi.movie.ui.activity.MainActivity
import com.hadirahimi.movie.ui.fragments.home.adapters.AdapterNewest
import com.hadirahimi.movie.ui.fragments.home.adapters.AdapterPopularActorsLimited
import com.hadirahimi.movie.ui.fragments.home.adapters.AdapterPopularMovie
import com.hadirahimi.movie.ui.fragments.moreMovie.MoreMovieFragmentDirections
import com.hadirahimi.movie.utils.*
import com.hadirahimi.movie.utils.MyResponse.Status.*
import com.hadirahimi.movie.utils.StoreUserData.Companion.userProfile
import com.hadirahimi.movie.viewModel.ViewModelHome
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
@ActivityScoped
class FragmentHome : Fragment()
{
    //binding
    private lateinit var binding : FragmentHomeBinding
    
    //view Model
    private val viewModel : ViewModelHome by viewModels()
    
    //adapters
    @Inject
    lateinit var adapterPopularActorsLimited : AdapterPopularActorsLimited
    
    // data Store
    @Inject
    lateinit var userData : StoreUserData
    
    @Inject
    lateinit var adapterNewest : AdapterNewest
    
    
    @Inject
    lateinit var pagerNewest : PagerSnapHelper
    
    @Inject
    lateinit var adapterPopularMovies : AdapterPopularMovie
    
    private var selectedTabId = - 1
    
    @Inject
    lateinit var publicGenres : ResponseGenre
    
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        //Call Api Requests And Collect
        viewModel.popularActorsLimited()
        viewModel.allGenres()
        viewModel.popularMovies()
        lifecycleScope.launch { userData.getUserToken().collect{token-> viewModel.userData(token) } } }
    
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
    
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
        // init views
        binding.apply {
            
            // setup user image from data store
            lifecycleScope.launchWhenCreated {
                //user Name
                userData.getUserName().collect { userName ->
                    if (userName.isNotEmpty())
                    // set user name from dataStore
                        tvWelcomeUser.text = "Hello $userName \uD83D\uDC4B"
                }
               
            }
            
            // setup user profile image from data store
            lifecycleScope.launchWhenCreated {
                //user profile
                userData.getUserProfile().collect { user_profile ->
                    if (user_profile.isNotEmpty())
                        // set user name from dataStore
                        ivProfile.load(user_profile) {
                            error(R.drawable.ic_default_profile)
                            crossfade(true)
                            crossfade(500)
                        }
                    else
                        ivProfile.load(R.drawable.ic_default_profile)
                    
        
                }
            }
            
            
            lifecycleScope.launchWhenResumed {
                if ((! publicGenres.genres.isNullOrEmpty()))
                {
                    binding.tabGenre.getTabAt(viewModel.selectedTabIndex)?.select()
                }
            }
            
            // get popular Actors From ViewModel
            viewModel.popularActorsLimited.observe(viewLifecycleOwner) { actors ->
                
                when (actors.status)
                {
                    LOADING ->
                    {
                        recyclerPopularActors.visible(false)
                        loadingActors.visible(true)
                    }
                    SUCCESS ->
                    {
                        recyclerPopularActors.visible(true)
                        loadingActors.visible(false)
                        //submit adapter list
                        adapterPopularActorsLimited.differ.submitList(actors.data)
                        
                        // init Recyclerview
                        recyclerPopularActors.init(
                            LinearLayoutManager(
                                requireContext() , LinearLayoutManager.HORIZONTAL , false
                            ) , adapterPopularActorsLimited
                        )
                    }
                    ERROR ->
                    {
                        recyclerPopularActors.visible(true)
                        loadingActors.visible(false)
                        
                    }
                }
                
            }
            
            //user Data
            viewModel.liveDataUserData.observe(viewLifecycleOwner){userData->
                when(userData.status)
                {
                    SUCCESS->{
                        val user = userData.data?.data
                        saveUserData(user)
                    }
                }
                
            }
            
            
            // get All Genres From ViewModel
            viewModel.allGenres.observe(requireActivity()) { genres ->
                when (genres.status)
                {
                    LOADING ->
                    {
                        showErrorLayout(false)
                        loadingGenres.visible(true)
                    }
                    SUCCESS ->
                    {
                        
                        Log.e("DATA" , "genres Done")
                        showErrorLayout(false)
                        loadingGenres.visible(false)
                        
                        genres.data?.let {
                            publicGenres = genres.data
                            //submit genre list for newest Movie Adapter
                            adapterNewest.submitGenres(it)
                            //submit genre list for popular Movie Adapter
                            adapterPopularMovies.submitGenres(it)
                            
                            //set Default selected tab as first genre id
                            if (selectedTabId == - 1)
                                selectedTabId = it.genres?.get(0) !!.id
                            
                            if (tabGenre.tabCount == 0)
                            {
                                //fill tab Layout tabs as Genres
                                for (genre in genres.data.genres !!)
                                {
                                    //create a new Tab For TabLayout
                                    tabGenre.addTab(
                                        tabGenre.newTab().setText(genre?.name).setTag(genre?.id)
                                    )
                                }
                            }
                            
                            
                        }
                    }
                    ERROR ->
                    {
                        loadingGenres.visible(false)
                        showErrorLayout(true)
                    }
                }
            }
            
            // Newest Movie
            lifecycleScope.launch {
                
                viewModel.newestMovie.map {
                    it.filter { response ->
                        if (response.genreIds !!.isNotEmpty())
                        {
                            response.genreIds[0] == selectedTabId
                        }
                        else false
                    }
                }.collect {
                    Log.e("DATA" , "newest Done")
                    adapterNewest = AdapterNewest(requireContext())
                    adapterNewest.submitGenres(publicGenres)
                    adapterNewest.submitData(it)
                }
            }
            pushToRecyclerNewest()
            
            //newest movie Loading
            lifecycleScope.launchWhenCreated {
                adapterNewest.loadStateFlow.collect {
                    val state = it.refresh
//                    loadingNewest.isVisible = state is LoadState.Loading
                    
                    if (state is LoadState.Loading)
                    {
                        delay(3000)
                        if (adapterNewest.itemCount > 2) recyclerNewest.smoothScrollToPosition(1)
                    }
                    
                }
            }
            
            //popular movies
            viewModel.popularMovies.observe(viewLifecycleOwner) { popularMovies ->
                when (popularMovies.status)
                {
                    LOADING ->
                    {
                        showErrorLayout(false)
                        loadingPopularMovies.show(true , viewPagerPopularMovies)
                    }
                    ERROR ->
                    {
                        loadingPopularMovies.visible(false)
                        showErrorLayout(true)
                    }
                    SUCCESS ->
                    {
                        Log.e("DATA" , "popular Done")
                        showErrorLayout(false)
                        loadingPopularMovies.show(false , viewPagerPopularMovies)
                        viewPagerPopularMovies.apply {
                            adapterPopularMovies.submitViewPager(viewPagerPopularMovies)
                            adapterPopularMovies.differ.submitList(popularMovies.data?.results)
                            adapter = adapterPopularMovies
                            offscreenPageLimit = 3
                            clipToPadding = false
                            clipChildren = false
                            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
                        }
                        
                    }
                }
            }
            clickListener()
        }
        
        
    }
    
    private fun saveUserData(user : ResponseUser.Data?)
    {
        lifecycleScope.launchWhenCreated {
            if (user?.image != null && user.name != null)
                userData.saveUserInfo(user.name as String , user.image as String)
            else if (user?.image == null && user?.name != null)
                userData.saveUserInfo(user.name , "")
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun setUserData(user : ResponseUser.Data?)
    {
        binding.apply {
            tvWelcomeUser.text = "Hello ${user?.name} \uD83D\uDC4B"
            
            if (user?.image !=null)
                ivProfile.load(user.image.toString()){
                    crossfade(true)
                    crossfade(500)
                }
            else
                ivProfile.load(R.drawable.ic_default_profile)
        }
    }
    
    
    private fun clickListener()
    {
        binding.apply {
            //go to search fragment
            tvSearch.setOnClickListener {
                findNavController().navigate(R.id.action_to_fragmentSearch)
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
            
            // go to Movie Detail fragment for popular movies
            adapterPopularMovies.setOnItemClickListener { movie ->
                val direction = movie.id?.let { FragmentHomeDirections.actionToFragmentMovie(it) }
                direction?.let { findNavController().navigate(it) }
            }
            // when tab item selected
            tabGenre.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener
            {
                override fun onTabSelected(tab : TabLayout.Tab?)
                {
                    //if we have any tabs and selected tab is different with previous tab so lets show data
                    if (tab != null && selectedTabId != tab.tag as Int)
                    {
                        //change selected tab id with current tab
                        selectedTabId = tab.tag as Int
                        // change selected tab index with current position. because when user press back button fragment save here states
                        viewModel.selectedTabIndex = tab.position
                        //refresh adapter for new genre id and new data
                        adapterNewest.refresh()
                        // push to recyclerView
                        pushToRecyclerNewest()
                    }
                    
                    
                }
                
                override fun onTabUnselected(tab : TabLayout.Tab?)
                {
                
                }
                
                override fun onTabReselected(tab : TabLayout.Tab?)
                {
                
                }
                
            })
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
            
            
        }
        
        
    }
    
    private fun showErrorLayout(error : Boolean)
    {
        if (error)
        //error in get data show error network to user
        {
            (activity as MainActivity).networkError(true)
            binding.layoutMain.visibility = View.GONE
        }
        else
        {
            (activity as MainActivity).networkError(false)
            binding.layoutMain.visibility = View.VISIBLE
        }
    }
}