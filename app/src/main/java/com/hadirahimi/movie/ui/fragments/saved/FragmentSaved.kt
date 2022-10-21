package com.hadirahimi.movie.ui.fragments.saved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.hadirahimi.movie.databinding.FragmentSavedBinding
import com.hadirahimi.movie.ui.fragments.movieSingle.FragmentMovieDirections
import com.hadirahimi.movie.ui.fragments.saved.adapter.AdapterSaved
import com.hadirahimi.movie.utils.*
import com.hadirahimi.movie.viewModel.ViewModelSaved
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FragmentSaved : Fragment()
{
    //binding
    private lateinit var binding : FragmentSavedBinding
    
    //viewModel
    private val viewModel : ViewModelSaved by viewModels()
    
    //adapter
    @Inject
    lateinit var adapterSaved : AdapterSaved
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentSavedBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
       
        //init views
        binding.apply {
            viewModel.getAllSaved()
            viewModel.savedList.observe(viewLifecycleOwner) { response->
                when (response.status)
                {
                    MyResponse.Status.LOADING ->
                    {
                        loading.show(true , recyclerSaved)
                    }
                    
                    MyResponse.Status.ERROR ->
                    {
                        loading.visible(false)
                        Toast.makeText(requireContext() , response.message , Toast.LENGTH_SHORT).show()
                    }
                    
                    MyResponse.Status.EMPTY ->
                    {
                        loading.visible(false)
                        Toast.makeText(requireContext() , "empty" , Toast.LENGTH_SHORT).show()
                    }
                    
                    MyResponse.Status.SUCCESS ->
                    {
                        recyclerSaved.show(true,loading)
                        response.data?.let {
                            
                            //submit data to adapter
                            adapterSaved.submitData(it)
                            //init recyclerview
                            recyclerSaved.init(GridLayoutManager(requireContext(),3),adapterSaved)
                        
                        }
                        
                    }
                }
                
            }
            
        }
        onClick()
    }
    
    private fun onClick()
    {
        // each item click
        adapterSaved.setOnItemClickListener { saved->
            
            when(saved.mode)
            {
                ConstantsSaveMode.ACTORS->{
                    
                        val direction = FragmentMovieDirections.actionToActorFragment(saved.server_id)
                        findNavController().navigate(direction)
                    
                }
                ConstantsSaveMode.MOVIES->{
                    
                        val direction = FragmentMovieDirections.actionToFragmentMovie(saved.server_id)
                        findNavController().navigate(direction)
                    
                }
            }
            
        }
    }
    
}