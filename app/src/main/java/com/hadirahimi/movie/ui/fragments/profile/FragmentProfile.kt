package com.hadirahimi.movie.ui.fragments.profile

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.BottomSheetDialogBinding
import com.hadirahimi.movie.databinding.DialogPermissionBinding
import com.hadirahimi.movie.databinding.FragmentProfileBinding
import com.hadirahimi.movie.ui.activity.MainActivity
import com.hadirahimi.movie.utils.StoreUserData
import com.hadirahimi.movie.utils.visible
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class FragmentProfile : Fragment()
{
    
    //binding
    private lateinit var binding : FragmentProfileBinding
    
    //activity result
    lateinit var galleryResult : ActivityResultLauncher<Intent>
    lateinit var cameraResult : ActivityResultLauncher<Intent>
    
    // data Store
    @Inject
    lateinit var userData : StoreUserData
    
    private var havePhoto = false
    
    
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
        
        
        //  receive image from gallery
        galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                binding.ivUserProfile.load(intent?.data)

            }
        }
        //  receive image from camera
        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent
                binding.ivUserProfile.setImageBitmap(intent?.extras?.get("data") as Bitmap)
                
            }
        }
        
        //init views
        binding.apply {
    
    
            // setup user image from data store
            lifecycleScope.launchWhenCreated {
                //user Name
                userData.getUserName().collect { userName ->
                    if (userName.isNotEmpty())
                    // set user name from dataStore
                        tvUserName.text = userName
                }
        
            }
    
            // setup user profile image from data store
            lifecycleScope.launchWhenCreated {
                //user profile
                userData.getUserProfile().collect { userProfile ->
                    if (userProfile.isNotEmpty())
                    {
                        // set user name from
                        havePhoto = true
                        ivUserProfile.load(userProfile) {
                            error(R.drawable.ic_default_profile)
                            crossfade(true)
                            crossfade(500)
                        }
                    }
                    
                    else
                        ivUserProfile.load(R.drawable.ic_default_profile)
            
            
                }
            }
            
            
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
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        
        // when user have a photo so show delete button else gone this button
        if (havePhoto) bottomSheetBinding.deletePhoto.visible(true)
        else bottomSheetBinding.deletePhoto.visible(false)
        
        setupBottomSheetClick(bottomSheetBinding,dialog)
        
    }
    
    private fun setupBottomSheetClick(bottomSheetBinding : BottomSheetDialogBinding,dialog:Dialog)
    {
        bottomSheetBinding.apply {
            fromCamera.setOnClickListener {
                if (validatePermissions())
                {
                    capturePicture()
                }
                dialog.dismiss()
                
            }
            fromGallery.setOnClickListener {
                if (validatePermissions())
                {
                    pickFromGallery()
                }
                dialog.dismiss()
            }
            deletePhoto.setOnClickListener {
                Toast.makeText(requireContext() , "delete" , Toast.LENGTH_SHORT).show()
            }
        }
        
    }
    
    private fun capturePicture()
    {
        val intentCamera = Intent("android.media.action.IMAGE_CAPTURE")
        cameraResult.launch(intentCamera)
    }
    
    private fun pickFromGallery()
    {
       val  intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResult.launch(intent)
    }
    
    private fun validatePermissions() : Boolean
    {
        var granted = false
        Dexter.withContext(requireContext())
            .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : PermissionListener
            {
                override fun onPermissionGranted(response : PermissionGrantedResponse?)
                {
                    granted = true
                }
                
                override fun onPermissionDenied(response : PermissionDeniedResponse?)
                {
                    Snackbar.make(binding.root , "permission Denied!" , Snackbar.LENGTH_LONG).show()
                    granted = false
                }
                
                override fun onPermissionRationaleShouldBeShown(
                    permission : PermissionRequest? , token : PermissionToken?
                )
                {
                    granted = false
                    setupDialogReqPermission(token)
                }
                
            }).check()
        return granted
    }
    
    private fun setupDialogReqPermission(token : PermissionToken?)
    {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val dialogPermissionBinding = DialogPermissionBinding.inflate(layoutInflater)
        dialog.setContentView(dialogPermissionBinding.root)
        dialog.show()
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT ,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        
        dialogPermissionBinding.apply {
            btnCancel.setOnClickListener {
                token?.cancelPermissionRequest()
                dialog.dismiss()
            }
            btnRequestPermission.setOnClickListener {
                token?.continuePermissionRequest()
                dialog.dismiss()
                
            }
        }
        
    }
    
}