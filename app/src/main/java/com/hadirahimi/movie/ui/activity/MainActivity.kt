package com.hadirahimi.movie.ui.activity

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.ActivityMainBinding
import com.hadirahimi.movie.utils.ConstantsBottomNav
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{
    //binding
    private lateinit var binding : ActivityMainBinding
    
    //bottom navigation last item selected
    private var selectedItem = ConstantsBottomNav.HOME
    
    //nav Controller
    private lateinit var navController : NavController
    
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
        //binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavController()
        bottomNavigationVisibility()
        bottomNavClick()
        networkCheckAgainClickListener()
    }
    
    private fun networkCheckAgainClickListener()
    {
        binding.apply {
            checkAgain.setOnClickListener {
                //check request Data Again
                navController.navigate(R.id.fragmentHome)
                homeSelected()
                goneNetworkError()
            }
        }
    }
    
    private fun goneNetworkError()
    {
        if (binding.layoutNetwork.isVisible) binding.layoutNetwork.visibility = View.GONE
    }
    
    private fun setupNavController()
    {
        navController = findNavController(R.id.navHost)
        
    }
    
    private fun bottomNavigationVisibility()
    {
        //
        navController.addOnDestinationChangedListener { _ , destination , _ ->
            
            //check gone error network view
            goneNetworkError()
            
            if (destination.id == R.id.supportFragment||destination.id == R.id.appAboutFragment || destination.id == R.id.profileEditFragment || destination.id == R.id.fragmentSplash || destination.id == R.id.fragmentLogin || destination.id == R.id.fragmentRegister || destination.id == R.id.actorFragment || destination.id == R.id.fragmentMovie || destination.id == R.id.moreActorFragment || destination.id == R.id.moreMovieFragment) binding.bottomNav.visibility =
                View.GONE
            else binding.bottomNav.visibility = View.VISIBLE
            
        }
        
    }
    
    
    private fun deSelectLastItem()
    {
        
        when (selectedItem)
        {
            ConstantsBottomNav.HOME ->
            {
                binding.tvTitleHome.visibility = View.GONE
                binding.homeSelected.background = ColorDrawable(Color.TRANSPARENT)
            }
            ConstantsBottomNav.SAVED ->
            {
                binding.tvTitleSaved.visibility = View.GONE
                binding.savedSelected.background = ColorDrawable(Color.TRANSPARENT)
            }
            ConstantsBottomNav.SEARCH ->
            {
                binding.tvTitleSearch.visibility = View.GONE
                binding.searchSelected.background = ColorDrawable(Color.TRANSPARENT)
            }
            ConstantsBottomNav.PROFILE ->
            {
                binding.tvTitleProfile.visibility = View.GONE
                binding.profileSelected.background = ColorDrawable(Color.TRANSPARENT)
            }
        }
    }
    
    private fun bottomNavClick()
    {
        
        //item home click listener
        binding.itemHome.setOnClickListener {
            homeSelected()
            goToHome()
        }
        
        //item home click listener
        binding.itemSaved.setOnClickListener {
            savedSelected()
            goToSaved()
        }
        
        //item search click listener
        binding.itemSearch.setOnClickListener {
            searchSelected()
            goToSearch()
        }
        
        //item profile click listener
        binding.itemProfile.setOnClickListener {
            profileSelected()
            goToProfile()
        }
        
    }
    
    private fun profileSelected()
    {
        deSelectLastItem()
        selectedItem = ConstantsBottomNav.PROFILE
        
        binding.tvTitleProfile.visibility = View.VISIBLE
        binding.profileSelected.background =
            ContextCompat.getDrawable(this@MainActivity , R.drawable.bottom_nav_selected_)
    }
    
    fun searchSelected()
    {
        deSelectLastItem()
        selectedItem = ConstantsBottomNav.SEARCH
        
        binding.tvTitleSearch.visibility = View.VISIBLE
        binding.searchSelected.background =
            ContextCompat.getDrawable(this@MainActivity , R.drawable.bottom_nav_selected_)
    }
    
   
    
    fun savedSelected()
    {
        deSelectLastItem()
        selectedItem = ConstantsBottomNav.SAVED
        
        binding.tvTitleSaved.visibility = View.VISIBLE
        binding.savedSelected.background =
            ContextCompat.getDrawable(this@MainActivity , R.drawable.bottom_nav_selected_)
    }
    
    private fun homeSelected()
    {
        deSelectLastItem()
        selectedItem = ConstantsBottomNav.HOME
        
        binding.tvTitleHome.visibility = View.VISIBLE
        binding.homeSelected.background =
            ContextCompat.getDrawable(this@MainActivity , R.drawable.bottom_nav_selected_)
        
    }
    
    private fun goToHome()
    {
        navController.navigate(R.id.action_to_fragmentHome)
    }
    
    private fun goToProfile()
    {
        navController.navigate(R.id.action_to_fragmentProfile)
    }
    
    private fun goToSearch()
    {
        navController.navigate(R.id.action_to_searchNavFragment)
    }
    
    private fun goToSaved()
    {
        navController.navigate(R.id.action_to_fragmentSaved)
    }
    
    override fun onNavigateUp() : Boolean
    {
        return navController.navigateUp() || super.onNavigateUp()
    }
    
    override fun onBackPressed()
    {
        if (navController.currentDestination !!.id == R.id.fragmentHome) finish()
        super.onBackPressed()
        
        
        if (navController.currentDestination != null)
        {
            bottomNavigationItemChanger(navController.currentDestination !!.id)
        }
    }
    
    private fun bottomNavigationItemChanger(id : Int)
    {
        //when user pressed back manage bottom navigation items by current fragment
        when (id)
        {
            // current fragment is home so close application
            R.id.fragmentHome -> homeSelected()
            // current fragment is saved so switch to home fragment
            R.id.fragmentSaved -> savedSelected()
            // current fragment is search so switch to saved fragment
            R.id.fragmentSearch -> searchSelected()
            // current fragment is profile so switch to search fragment
            R.id.fragmentProfile -> profileSelected()
        }
    }
    
    //show error network layout for user
    fun networkError(isError : Boolean)
    {
        if (isError)
        {
            binding.layoutNetwork.visibility = View.VISIBLE
            
        }
        else binding.layoutNetwork.visibility = View.GONE
    }
    
    
}