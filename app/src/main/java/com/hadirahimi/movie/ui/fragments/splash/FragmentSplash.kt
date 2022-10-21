package com.hadirahimi.movie.ui.fragments.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import androidx.navigation.fragment.findNavController
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentSplashBinding
import com.hadirahimi.movie.utils.StoreUserData
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class FragmentSplash : Fragment()
{
    //binding
    private lateinit var binding : FragmentSplashBinding
    
    @Inject
    lateinit var storeUserData : StoreUserData
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
        //set delay
        lifecycle.coroutineScope.launchWhenCreated {
            delay(2500)
            //check user token
            storeUserData.getUserToken().collect{
                if (it.isEmpty())
                {
                    //user not registered
                 //   findNavController().navigate(R.id.action_to_fragmentLogin)
                }else
                {
                    //user is registered
                  //  findNavController().navigate(R.id.action_to_fragmentHome)
                }
                findNavController().navigate(R.id.action_to_fragmentHome)
                
            }
            
        }
    }
    
    
}