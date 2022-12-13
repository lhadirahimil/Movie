package com.hadirahimi.movie.ui.fragments.movieSingle

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentMovieBinding
import com.hadirahimi.movie.db.SavedEntity
import com.hadirahimi.movie.models.home.ResponseGenre
import com.hadirahimi.movie.models.movie.ResponseMovieDetail
import com.hadirahimi.movie.ui.fragments.movieSingle.Adapters.AdapterSelectedActors
import com.hadirahimi.movie.ui.fragments.movieSingle.Adapters.AdapterSelectedPhotos
import com.hadirahimi.movie.ui.fragments.movieSingle.Adapters.AdapterSimilarMovies
import com.hadirahimi.movie.utils.*
import com.hadirahimi.movie.viewModel.ViewModelMovieDetail
import com.hadirahimi.movie.viewModel.ViewModelSaved
import com.rommansabbir.animationx.Attention
import com.rommansabbir.animationx.Flip
import com.rommansabbir.animationx.animationXAttention
import com.rommansabbir.animationx.animationXFlip
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMovie : Fragment()
{
    //binding
    private lateinit var binding : FragmentMovieBinding
    
    //viewModel
    private val viewModel : ViewModelMovieDetail by viewModels()
    private val viewModelSaved : ViewModelSaved by viewModels()
    
    //adapters
    @Inject
    lateinit var adapterSelectedPhotos : AdapterSelectedPhotos
    
    @Inject
    lateinit var adapterSelectedActors : AdapterSelectedActors
    
    @Inject
    lateinit var adapterSimilarMovies : AdapterSimilarMovies
    
    
    //other
    private var movieId = 0
    private var imageUrl = ""
    private val args : FragmentMovieArgs by navArgs()
    
    @Inject
    lateinit var genres : ResponseGenre
    
    @Inject
    lateinit var pagerNewest : PagerSnapHelper
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        //get actor id from home fragment
        movieId = args.movieId
        
        //call api service
        viewModel.allGenres()
        viewModel.movieDetail(movieId)
        viewModel.movieImages(movieId)
        viewModel.moviesActors(movieId)
        viewModel.similarMovie(movieId)
        
        
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentMovieBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        //init views
        binding.apply {
            
            // Click Listener
            onclick()
            
            //get movie detail from server
            viewModel.movieData.observe(viewLifecycleOwner) { movie ->
                binding.apply {
                    pushData(movie)
                    setupAnimation()
                }
            }
    
            viewModel.error.observe(viewLifecycleOwner){haveError->
                if (haveError)
                    findNavController().navigateUp()
            }
            
            
            //get genre list from server
            viewModel.allGenres.observe(viewLifecycleOwner) { genreList ->
                genres = genreList
            }
            
            
            //get more image for this movies
            viewModel.movieImages.observe(viewLifecycleOwner) { movieImages ->
                //submit data for adapter
                adapterSelectedPhotos.differ.submitList(movieImages.backdrops)
                //init recyclerview
                recyclerSelectedPhotos.init(
                    LinearLayoutManager(
                        requireContext() ,
                        LinearLayoutManager.HORIZONTAL ,
                        false
                    ) , adapterSelectedPhotos
                )
            }
            
            
            //movie images empty checker
            viewModel.emptyMovieImages.observe(viewLifecycleOwner) { isEmpty ->
                layoutSelectedPhotos.visible(! isEmpty)
            }
            
            
            //movies Actors
            viewModel.moviesActors.observe(viewLifecycleOwner) { actors ->
                if (actors.cast?.size != 0)
                {
                    layoutMoviesActors.visible(true)
                    //submit adapter Data
                    adapterSelectedActors.differ.submitList(actors.cast)
                    recyclerMoviesActors.init(
                        LinearLayoutManager(
                            requireContext() ,
                            LinearLayoutManager.HORIZONTAL ,
                            false
                        ) , adapterSelectedActors
                    )
                }
                else
                {
                    layoutMoviesActors.visible(false)
                }
            }
            
            
            //similar movies
            viewModel.similarMovie.observe(viewLifecycleOwner) { movies ->
                //submit adapter Data
                adapterSimilarMovies.differ.submitList(movies.results)
                recyclerSimilarMovies.init(LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false) , adapterSimilarMovies)
                pagerNewest.attachToRecyclerView(recyclerSimilarMovies)
            }
            
            
            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                loading.show(isLoading , layoutMain)
            }
            
            
            //change saved icon when user clicked it
            viewModelSaved.isSaved.observe(viewLifecycleOwner) { isSaved ->
                
                if (isSaved)
                {
                    //change saved button icon to added
                    ivSaved.load(R.drawable.ic_archive_tick)
                    
                }
                else
                {
                    //change saved button icon to default style
                    ivSaved.load(R.drawable.ic_saved)
                }
            }
            
            // change saved icon when fragment opened
            lifecycleScope.launchWhenCreated {
                
                if (viewModelSaved.existsSaved(movieId))
                {
                    //change saved button icon to added
                    ivSaved.load(R.drawable.ic_archive_tick)
                    
                }
                else
                {
                    //change saved button icon to default style
                    ivSaved.load(R.drawable.ic_saved)
                }
            }
            
        }
        
        
    }
    
    private fun setupAnimation()
    {
        binding.apply {
            if((1..2).random()==1)movieMainPoster.animationXFlip(Flip.FLIP_IN_Y,2000)
            else movieMainPoster.animationXFlip(Flip.FLIP_IN_X,2000)
        }
    }
    
    @SuppressLint("SetTextI18n")
    private fun pushData(movie : ResponseMovieDetail)
    {
       binding.apply {
           //set movie title
           movie.title.let {
               movieName.text = movie.title
           }
    
           //load movie poster for background
           movie.posterPath.let {
               //make image url public
               imageUrl = it.toString()
               moviePoster.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                   crossfade(true)
                   crossfade(500)
               }
               //load movie poster
               movieMainPoster.load(Constants.BASE_URL_IMAGE + movie.posterPath) {
                   crossfade(true)
                   crossfade(500)
               }
           }
           //------------- movie Release Date----------------
    
           if (movie.releaseDate != "") movieReleaseDate.text =
               movie.releaseDate.toString().subSequence(0 , 4)
           else movieReleaseDate.visible(false)
           //-------------------------------------------------
    
           //--------- movie rating ----------------
    
           //rating bar
           if (movie.voteAverage != null)
           {
               if (movie.voteAverage != 0.0)
               {
                   // val average = Math.round()
                   ratingBar.rating = (movie.voteAverage / 2).toFloat()
                   val rating = String.format("%.1f" , movie.voteAverage)
                   ratingText.text = "$rating/10"
               }
               else
               {
                   ratingBar.rating = 0f
                   ratingText.text = "0/10"
               }
        
           }
           else ratingBar.visible(false)
           //--------------------------------------
    
    
           // ---------- movie overview ----------------
          val overview =  movie.overview?.trim()
           if (overview?.length !=0)
           {
               aboutContent.text = movie.overview
               aboutLayout.visible(true)
           }
           else aboutLayout.visible(false)
           //-------------------------------------------
    
    
           //----------- movie Time-----------
           if (movie.runtime != 0 && movie.runtime != null)
           {
               val hours = movie.runtime / 60
               val minutes = movie.runtime % 60
               //if hours is equal 0 so just show minutes in view
               if (hours == 0) movieTime.text = "$minutes min"
               else if (minutes == 0) movieTime.text = "$hours h "
               else movieTime.text = "$hours h $minutes min"
           }
           else movieTime.visible(false)
           // ---------------------------------
    
    
           //--------------Movie genre----------------
           if (genres.genres?.size != 0 && movie.genres?.size != 0)
           {
               genres.genres?.forEach { genre ->
                   if (movie.genres?.get(0)?.id == genre?.id) movieGenre.text =
                       genre?.name.toString()
               }
           }
           else movieGenre.visible(false)
           //--------------------------------------------
       }
    }
    
    private fun onclick()
    {
        //button back click listener
        binding.btnBack.setOnClickListener {
            //go back
            findNavController().navigateUp()
        }
        
        //adapter Actors click
        adapterSelectedActors.setOnItemClickListener { actor ->
            actor.id.let {
                val direction = FragmentMovieDirections.actionToActorFragment(actor.id)
                findNavController().navigate(direction)
            }
        }
        
        //adapter similar click
        adapterSimilarMovies.setOnItemClickListener { actor ->
            actor.id.let {
                val direction = FragmentMovieDirections.actionToFragmentMovie(actor.id)
                findNavController().navigate(direction)
            }
        }
        
        //save button click
        binding.ivSaved.setOnClickListener {
            val savedEntity = SavedEntity()
            savedEntity.server_id = movieId
            savedEntity.image_url = imageUrl
            savedEntity.mode = ConstantsSaveMode.MOVIES
            
            viewModelSaved.insertOrDelete(movieId , savedEntity)
        }
    }
    
}