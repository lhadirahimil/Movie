package com.hadirahimi.movie.ui.fragments.actorSingle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentActorBinding
import com.hadirahimi.movie.db.SavedEntity
import com.hadirahimi.movie.ui.fragments.actorSingle.adapter.AdapterSelectedMovie
import com.hadirahimi.movie.utils.*
import com.hadirahimi.movie.viewModel.ViewModelActorDetail
import com.hadirahimi.movie.viewModel.ViewModelSaved
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ActorFragment : Fragment()
{
    
    //binding
    lateinit var binding : FragmentActorBinding
    
    //viewModel
    private val viewModel : ViewModelActorDetail by viewModels()
    private val viewModelSaved : ViewModelSaved by viewModels()
    
    //other
    private var actorId = 0
    private var imageUrl = ""
    private val args : ActorFragmentArgs by navArgs()
    
    //adapters
    @Inject
    lateinit var adapterSelectedMovie : AdapterSelectedMovie
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        
        //get actor id from home fragment
        actorId = args.actorId
        viewModel.actorDetail(actorId)
        viewModel.actorSelectedMovies(actorId)
        
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentActorBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
     
        //init View
        binding.apply {
            
            
            // actor detail from server
            viewModel.actorDetail.observe(viewLifecycleOwner) { actor ->
                if (actor != null)
                {
                    // actor data is founded
                    binding.apply {
                        //actor name
                        actorName.text = actor.name
                        //actor about
                        if (actor.biography.isNullOrEmpty()) aboutLayout.visibility = View.GONE
                        else
                        {
                            aboutLayout.visibility = View.VISIBLE
                            aboutContent.text = actor.biography
                        }
                        //actor poster
                        if (actor.profilePath.isNullOrEmpty())
                        {
                            actorImage.load(R.drawable.actor_avatar) {
                                crossfade(true)
                                crossfade(600)
                                
                            }
                        }
                        else
                        {
                            imageUrl = actor.profilePath.toString()
                            actorImage.load(Constants.BASE_URL_IMAGE + actor.profilePath) {
                                placeholder(R.drawable.actor_avatar)
                                crossfade(true)
                                crossfade(600)
                                error(R.drawable.actor_avatar)
                            }
                        }
                    }
                }
                else
                {
                    // actor data is null finish this fragment
                    findNavController().navigateUp()
                }
            }
            
            viewModel.error.observe(viewLifecycleOwner){haveError->
                if (haveError)
                findNavController().navigateUp()
            }
            
            
            
            // actor selected Movie
            viewModel.actorSelectedMovies.observe(viewLifecycleOwner) { movies ->
                if (movies != null)
                {
                    val filtered = movies.cast?.filter { movie ->
                        movie?.adult != true && ! movie?.posterPath.isNullOrBlank()
                    }
                    //push to adapter
                    adapterSelectedMovie.differ.submitList(filtered)
                    recyclerSelectedMovie.init(
                        LinearLayoutManager(
                            requireContext() , LinearLayoutManager.HORIZONTAL , false
                        ) , adapterSelectedMovie
                    )
                    
                }
            }
            
            
            //actor selected movie checker
            viewModel.selectedMovieEmpty.observe(viewLifecycleOwner) { isEmpty ->
                if (isEmpty) layoutSelectedMovie.visible(false)
                else layoutSelectedMovie.visible(true)
            }
            
            
            viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
                loading.show(isLoading , layoutMain)
            }
    
    
            //change saved icon when user clicked it
            viewModelSaved.isSaved.observe(viewLifecycleOwner){ isSaved->
                
                    if(isSaved)
                    {
                        //change saved button icon to added
                        ivSaved.load(R.drawable.ic_archive_tick)
        
                    }else
                    {
                        //change saved button icon to default style
                        ivSaved.load(R.drawable.ic_saved)
                    }
            }
    
            // change saved icon when fragment opened
            lifecycleScope.launchWhenCreated {
                
                if(viewModelSaved.existsSaved(actorId))
                {
                    //change saved button icon to added
                    ivSaved.load(R.drawable.ic_archive_tick)
                    
                }else
                {
                    //change saved button icon to default style
                    ivSaved.load(R.drawable.ic_saved)
                }
            }
            
            
            onClickListener()
        }
    }
    
    
    private fun onClickListener()
    {
        binding.apply {
            
            //back click
            btnBack.setOnClickListener {
                
                //back to home fragment when back image view is pressed
                findNavController().navigateUp()
                
            }
            
            //adapter click
            adapterSelectedMovie.setOnItemClickListener { movie ->
                
                movie.id.let { movie_id ->
                    
                    val direction = ActorFragmentDirections.actionToFragmentMovie(movie_id)
                    findNavController().navigate(direction)
                    
                }
                
            }
    
            //save button click
            binding.ivSaved.setOnClickListener {
                val savedEntity = SavedEntity()
                savedEntity.server_id = actorId
                imageUrl.let { savedEntity.image_url = imageUrl }
                savedEntity.mode = ConstantsSaveMode.ACTORS
                viewModelSaved.insertOrDelete(actorId,savedEntity)
            }
        }
    }
    
}