package com.hadirahimi.movie.ui.fragments.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentLoginBinding


class FragmentLogin : Fragment()
{
    //Binding
    private lateinit var binding : FragmentLoginBinding
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
      
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment\
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }
    
    
}