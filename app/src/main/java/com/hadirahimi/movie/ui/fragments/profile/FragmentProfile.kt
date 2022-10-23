package com.hadirahimi.movie.ui.fragments.profile

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.BottomSheetDialogBinding
import com.hadirahimi.movie.databinding.FragmentProfileBinding
import com.hadirahimi.movie.ui.activity.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentProfile : Fragment()
{
    
    //binding
    private lateinit var binding : FragmentProfileBinding
    
    override fun onCreate(savedInstanceState : Bundle?)
    {
        super.onCreate(savedInstanceState)
    }
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        
        //init views
        binding.apply {
            //edit profile photo click listener
            editProfile.setOnClickListener {
                setupBottomSheet()
            }
            
            // item edit profile selected
            itemEditProfile.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentProfile_to_profileEditFragment)
            }
            
            // item saved selected
            itemSaved.setOnClickListener {
                findNavController().navigate(R.id.action_to_fragmentSaved)
                (activity as MainActivity).savedSelected()
            }
            
            // item app subscription selected
            itemAppSubscription.setOnClickListener {
                Toast.makeText(requireContext() , "soon :)" , Toast.LENGTH_SHORT).show()
            }
    
            // item support selected
            itemSupport.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentProfile_to_supportFragment)
            }
    
            // item application about selected
            itemApplicationAbout.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentProfile_to_appAboutFragment)
            }
        }
    }
    
    private fun setupBottomSheet()
    {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val bottomSheetBinding = BottomSheetDialogBinding.inflate(layoutInflater)
        dialog.setContentView(bottomSheetBinding.root)
        dialog.show()
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        
        
        setupBottomSheetClick(bottomSheetBinding)
        
    }
    
    private fun setupBottomSheetClick(bottomSheetBinding : BottomSheetDialogBinding)
    {
        bottomSheetBinding.apply {
            fromCamera.setOnClickListener {
                Toast.makeText(requireContext() , "from Camera" , Toast.LENGTH_SHORT).show()
            }
            fromGallery.setOnClickListener {
                Toast.makeText(requireContext() , "from Gallery" , Toast.LENGTH_SHORT).show()
            }
            deletePhoto.setOnClickListener {
                Toast.makeText(requireContext() , "delete" , Toast.LENGTH_SHORT).show()
            }
        }
    
    }
    
    
}