package com.hadirahimi.movie.ui.fragments.profile_edit

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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.*
import com.hadirahimi.movie.ui.activity.MainActivity
import com.hadirahimi.movie.utils.MyResponse
import com.hadirahimi.movie.utils.StoreUserData
import com.hadirahimi.movie.viewModel.ViewModelProfileEdit
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ProfileEditFragment : Fragment()
{
    //binding
    private lateinit var binding : FragmentProfileEditBinding
    
    //activity result
    lateinit var galleryResult : ActivityResultLauncher<Intent>
    lateinit var cameraResult : ActivityResultLauncher<Intent>
    private lateinit var bottomSheetUsernameBinding : BottomSheetUsernameBinding
    private lateinit var bottomSheetPasswordBinding : BottomSheetPasswordBinding
    private lateinit var  dialogUserName : Dialog
    private lateinit var  dialogEditPassword : Dialog
    
    // data Store
    @Inject
    lateinit var userData : StoreUserData
    private val  viewModel : ViewModelProfileEdit by viewModels()
    
    override fun onCreateView(
        inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?
    ) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentProfileEditBinding.inflate(layoutInflater , container , false)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        // init views
        binding.apply {
            
            
            
            //activity result
    
            galleryResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    // Handle the Intent
                    binding.userProfile.load(intent?.data)
                }
            }
    
            cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    // Handle the Intent
                    binding.userProfile.setImageBitmap(intent?.extras?.get("data") as Bitmap)
            
                }
            }
            
            viewModel.liveDataNameEdit.observe(viewLifecycleOwner){response->
                when(response.status)
                {
                    MyResponse.Status.LOADING -> {
                        bottomSheetUsernameBinding.etUserName.isEnabled = false
                        bottomSheetUsernameBinding.btnSend.isEnabled = false
                    }
                    MyResponse.Status.SUCCESS -> {
                        if (response.data?.code == 422)
                        {
                            bottomSheetUsernameBinding.etUserName.error = "UserName Exists"
                            bottomSheetUsernameBinding.etUserName.isEnabled = true
                            bottomSheetUsernameBinding.btnSend.isEnabled = true
                        }
                        else if (response.data?.code == 200)
                        {
                            
                            lifecycleScope.launchWhenCreated {
                                Toast.makeText(requireContext() , "username successfully changed." , Toast.LENGTH_SHORT).show()
                                userData.saveUserName(bottomSheetUsernameBinding.etUserName.text.toString().trim())
                                dialogUserName.dismiss()
                                (activity as MainActivity).homeSelected()
                            }
                            
                        }
                    }
                    MyResponse.Status.ERROR -> {
                        bottomSheetUsernameBinding.etUserName.isEnabled = true
                        bottomSheetUsernameBinding.btnSend.isEnabled = true
                        Toast.makeText(requireContext() , "ops.please try again later" , Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            viewModel.liveDataPasswordEdit.observe(viewLifecycleOwner){response->
                when(response.status)
                {
                    MyResponse.Status.LOADING -> {
                        bottomSheetPasswordBinding.etNewPassword.isEnabled = false
                        bottomSheetPasswordBinding.btnSend.isEnabled = false
                    }
                    MyResponse.Status.SUCCESS -> {
                        if (response.data?.code == 200)
                        {
                
                            lifecycleScope.launchWhenCreated {
                                Toast.makeText(requireContext() , "password successfully changed." , Toast.LENGTH_SHORT).show()
                                dialogEditPassword.dismiss()
                                (activity as MainActivity).homeSelected()
                             
                            }
                
                        }
                    }
                    MyResponse.Status.ERROR -> {
                        bottomSheetPasswordBinding.etNewPassword.isEnabled = true
                        bottomSheetPasswordBinding.btnSend.isEnabled = true
                        Toast.makeText(requireContext() , "ops.please try again later" , Toast.LENGTH_SHORT).show()
                    }
                }
            }
            
            //click listener
            
            //edit user name
            ivEditUserName.setOnClickListener {
                setupDialogUserName()
            }
            
            //edit user password
            ivEditPassword.setOnClickListener {
                setupDialogPassword()
            }
            //change user profile picture
            ivUserProfileEdit.setOnClickListener {
                setupBottomSheet()
            }
            //back button
            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }
            //log out from account
            tvLogout.setOnClickListener {
                lifecycleScope.launchWhenCreated {
                    userData.logOutUser()
                }
               // findNavController().navigate(R.id.action_to_fragmentLogin)
                
            }
        }
        
    }
    
    
   
    
    private fun checkUserNameInput()
    {
        bottomSheetUsernameBinding.apply {
            etUserName.addTextChangedListener { text ->
                if (text != null)
                {
                    val userValid = text.matches(Regex("^[A-Za-z][A-Za-z0-9]{5,31}\$"))
                    btnSend.isEnabled = userValid
                }
            }
        }
        
    }
    private fun checkUserPasswordInput()
    {
        bottomSheetPasswordBinding.apply {
            etNewPassword.addTextChangedListener { text ->
                if (text != null)
                {
                    val passwordValid = text.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$"))
                    btnSend.isEnabled = passwordValid
                }
            }
        }
        
    }
    
    private fun setupDialogUserName()
    {
        dialogUserName = Dialog(requireContext())
        dialogUserName.requestWindowFeature(Window.FEATURE_NO_TITLE)
        bottomSheetUsernameBinding = BottomSheetUsernameBinding.inflate(layoutInflater)
        dialogUserName.setContentView(bottomSheetUsernameBinding.root)
        dialogUserName.show()
        dialogUserName.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT)
        dialogUserName.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogUserName.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialogUserName.window?.setGravity(Gravity.BOTTOM)
    
        checkUserNameInput()
        
        bottomSheetUsernameBinding.apply {
            
            btnSend.setOnClickListener {
                
                lifecycleScope.launchWhenCreated {
                    val name = etUserName.text.toString().trim()
                    userData.getUserToken().collect{ token->
                        //request to server
                        viewModel.changeName(name,token)
                    }
                    
                }
                
            }
        }
        
    }
    
    private fun setupDialogPassword()
    {
        dialogEditPassword = Dialog(requireContext())
        dialogEditPassword.requestWindowFeature(Window.FEATURE_NO_TITLE)
         bottomSheetPasswordBinding = BottomSheetPasswordBinding.inflate(layoutInflater)
        dialogEditPassword.setContentView(bottomSheetPasswordBinding.root)
        dialogEditPassword.show()
        dialogEditPassword.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT ,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialogEditPassword.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogEditPassword.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialogEditPassword.window?.setGravity(Gravity.BOTTOM)
        checkUserPasswordInput()
        bottomSheetPasswordBinding.apply {
            
            btnSend.setOnClickListener {
    
                lifecycleScope.launchWhenCreated {
                    val password = etNewPassword.text.toString().trim()
                    userData.getUserToken().collect{ token->
                        //request to server
                        viewModel.changePassword(password,token)
                    }
        
                }
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
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT ,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.dialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)
        
        
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