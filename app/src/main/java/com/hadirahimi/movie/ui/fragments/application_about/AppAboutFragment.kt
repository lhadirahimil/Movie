package com.hadirahimi.movie.ui.fragments.application_about

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hadirahimi.movie.databinding.FragmentAppAboutBinding

class AppAboutFragment : Fragment()
{
    //binding
    private lateinit var binding : FragmentAppAboutBinding
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentAppAboutBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
    
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        //init views
        binding.apply {
            //set version
            tvPrescription.text = "prescription : ${getAppVersion()}"
            //back click listener
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    
    private fun getAppVersion() : String
    {
        var version = ""
        try
        {
            val pInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName , 0)
            version = pInfo.versionName
        } catch (e : PackageManager.NameNotFoundException)
        {
            e.printStackTrace()
        }
        
        return version
    }
}