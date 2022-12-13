package com.hadirahimi.movie.ui.fragments.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentWelcomeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class WelcomeFragment : Fragment()
{
    //binding
    private lateinit var binding : FragmentWelcomeBinding
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentWelcomeBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        //init Views
        binding.apply {
            btnGetStart.setOnClickListener {
                findNavController().navigate(R.id.action_to_fragmentLogin)
            }
        }
    }
}