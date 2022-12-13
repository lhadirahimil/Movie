package com.hadirahimi.movie.ui.fragments.login

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.hadirahimi.movie.R
import com.hadirahimi.movie.databinding.FragmentLoginBinding
import com.hadirahimi.movie.utils.MyResponse.Status.*
import com.hadirahimi.movie.utils.StoreUserData
import com.hadirahimi.movie.utils.visible
import com.hadirahimi.movie.viewModel.ViewModelLogin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class FragmentLogin : Fragment()
{
    //Binding
    private lateinit var binding : FragmentLoginBinding
    
    //viewModel
    private val viewModel : ViewModelLogin by viewModels()
    
    // data Store
    @Inject
    lateinit var userData : StoreUserData
    
    //other
    private var isLogin = true
    
    @Inject
    lateinit var gson : Gson
    
    override fun onCreateView(inflater : LayoutInflater , container : ViewGroup? , savedInstanceState : Bundle?) : View
    {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }
    
    override fun onViewCreated(view : View , savedInstanceState : Bundle?)
    {
        super.onViewCreated(view , savedInstanceState)
        //init views
        binding.apply {
            // change ui from boolean isLogin mode
            setupUi()
            //view click event
            click()
            //receive login data from server
            viewModel.liveDataLogin.observe(viewLifecycleOwner) { loginResponse ->
                if (loginResponse != null)
                {
                    when (loginResponse.status)
                    {
                        LOADING ->
                        {
                            tvWarning.visible(false)
                            loading.visible(true)
                            disableClick(true)
                        }
                        ERROR ->
                        {
                            tvWarning.visible(false)
                            loading.visible(false)
                            disableClick(false)
                            tvWarning.visible(false)
                            Snackbar.make(binding.root , "Ops! Please try again " , Snackbar.LENGTH_LONG).show()
                            Log.e("HECTOR",loginResponse.message.toString())
                        }
                        SUCCESS ->
                        {
                            disableClick(false)
                            loading.visible(false)
                            // get result of login from server
                            // if success is true so lets export token from api
                            //if success is false so show message to user : user or password is wrong
                            if(loginResponse.data?.code == 200)
                            {
                                //check result
                                val response = loginResponse.data.body?.string()
                                val responseStatus = gson.fromJson(response , ApiStatusResponse::class.java)
                                if (responseStatus.success)
                                {
                                    
                                    val convertedResponse = gson.fromJson(response , ApiMainResponse::class.java)
                                    convertedResponse.data.apply {
                                        lifecycle.coroutineScope.launch {
                                            tvWarning.visible(false)
                                            userData.saveUser(access_token , expires_in)
                                        }
                                    }
                                }
                                else
                                {
                                    tvWarning.visible(true)
                                }
                            }else if(loginResponse.data?.code == 403)
                            {
                                tvWarning.visible(true)
                            }
                            
                        }
                    }
                   
                    
                }
            }
            //receive register data from server
            viewModel.liveDataRegister.observe(viewLifecycleOwner) { registerResponse ->
                if (registerResponse!=null)
                {
                    when (registerResponse.status)
                    {
                        LOADING ->
                        {
                            loading.visible(true)
                            disableClick(true)
                        }
                        ERROR ->
                        {
                            loading.visible(false)
                            disableClick(false)
                            Snackbar.make(binding.root , "Ops!Please try again" , Snackbar.LENGTH_LONG).show()
                            Log.e("HECTOR",registerResponse.message.toString())
                        }
                        SUCCESS ->
                        {
                            disableClick(false)
                            loading.visible(false)
                            val data = registerResponse.data

//                        //if status code equal with 422 so email is taken
                            if (data?.code == 422)
                                etUserMail.error = "The email has already been taken."
                            else if (data?.code == 403)
                                etUserName.error = "This Username already exists"
                            else if (data != null && data.isSuccessful)
                            {
//                            // get result of register from server
//                            // if success is true so lets export token from api
                                val response = data.body?.string()
                
                                val responseStatus = gson.fromJson(response , ApiStatusResponse::class.java)
                                if (responseStatus.success)
                                {
                                    // run verify code fragment
                                    //...
                                    //remove observers
                                    viewModel.liveDataRegister.postValue(null)
                                    viewModel.liveDataRegister.removeObservers(viewLifecycleOwner)
                                    viewModel.liveDataLogin.removeObservers(viewLifecycleOwner)
                                    viewModel.liveDataLogin.postValue(null)
                                    
                                    //send data to verify fragment
                                    val username = etUserName.text.toString().trim()
                                    val password = etPassword.text.toString().trim()
                                    val email = etUserMail.text.toString().trim()
                                    val cookie = registerResponse.data.headers["Set-Cookie"].toString()
                                    val direction = FragmentLoginDirections.actionFragmentLoginToVerifyEmailFragment(username,password,email,cookie)
                                    findNavController().navigate(direction)
                                    
                                }else
                                {
                                    Snackbar.make(binding.root , "Ops!Please try again later" , Snackbar.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                }
            }
            
            
        }
    }
    
    private fun setupUi()
    {
        if (isLogin)
            changeUiToLogin()
        else
            changeUiToRegister()
    }
    
    
    private fun disableClick(isEnable : Boolean)
    {
        binding.apply {
            btnGetStartOrLogin.isEnabled = ! isEnable
            tvForgotPassword.isEnabled = ! isEnable
            etUserName.isEnabled = ! isEnable
            etPassword.isEnabled = ! isEnable
            etUserMail.isEnabled = ! isEnable
            tvLinkLoginOrRegister.isEnabled = ! isEnable
            haveAccountOrNot.isEnabled = ! isEnable
        }
    }
    
    private fun click()
    {
        //reset password
        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentLogin_to_resetPasswordFragment)
        }
        
        //change ui to Login or Register
        binding.tvLinkLoginOrRegister.setOnClickListener {
            if (isLogin)
            {
                changeUiToRegister()
                binding.tvWarning.visible(false)
                isLogin = false
            }
            else
            {
                changeUiToLogin()
                binding.tvWarning.visible(false)
                isLogin = true
            }
        }
        binding.btnGetStartOrLogin.setOnClickListener {
            if (isLogin)
            {
                //lets login user
                val userName = binding.etUserName.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
    
                // validate user data
                if (isUserValid())
                        if (isPasswordValid())
                            viewModel.login(userName , password)
                
                
                
            }
            else
            {
                // create an Account for User
                val userName = binding.etUserName.text.toString().trim()
                val password =  binding.etPassword.text.toString().trim()
                val mail = binding.etUserMail.text.toString().trim()
    
                
                
    
                //validate user data
                if (isUserValid())
                    if ( isMailValid())
                        if (isPasswordValid())
                            viewModel.register(userName,password,mail)
              
            }
            
        }
    }
    
    private fun isUserValid() : Boolean
    {
        val userName = binding.etUserName.text.toString().trim()
        //validate user and password by regex
        val userValid = userName.matches(Regex("^[A-Za-z][A-Za-z0-9]{5,31}\$"))
        if (userValid) {
            binding.etUserName.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design)
            binding.tvUserNameWarning.visible(false)
            return true
        }
        else{
            binding.etUserName.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design_error)
            binding.tvUserNameWarning.visible(true)
            return false
        }
    }
    
    private fun isMailValid() : Boolean
    {
        val mail = binding.etUserMail.text.toString().trim()
        //validate user and password by regex
        
        if (isValidEmail(mail)) {
            binding.etUserMail.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design)
            binding.tvEmailWarning.visible(false)
            return true
        }
        else{
            binding.etUserMail.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design_error)
            binding.tvEmailWarning.visible(true)
            return false
        }
    }
    
    private fun isPasswordValid() : Boolean
    {
        val password = binding.etPassword.text.toString().trim()
        //validate user and password by regex
        val passwordValid = password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$"))
        if (passwordValid) {
            binding.etPassword.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design)
            binding.tvPasswordWarning.visible(false)
            return true
        }
        else{
            binding.etPassword.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design_error)
            binding.tvPasswordWarning.visible(true)
            return false
        }
    }
    private fun isValidEmail(target : CharSequence?) : Boolean
    {
        return if (TextUtils.isEmpty(target)) false
        else Patterns.EMAIL_ADDRESS.matcher(target !!).matches()
    }
    
    private fun changeUiToLogin()
    {
        binding.apply {
            tvTitle.text = resources.getString(R.string.sign_in)
            btnGetStartOrLogin.text = resources.getString(R.string.login)
            haveAccountOrNot.text = resources.getString(R.string.dont_you_have_an_account)
            tvLinkLoginOrRegister.text = resources.getString(R.string.sign_up)
            tvForgotPassword.visible(true)
            etUserMail.visible(false)
            
            defaultStyle()
            
            clearInput()
        }
    }
    
    private fun defaultStyle()
    {
        binding.etUserName.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design)
        binding.tvUserNameWarning.visible(false)
    
        binding.etUserMail.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design)
        binding.tvEmailWarning.visible(false)
    
        binding.etPassword.background = ActivityCompat.getDrawable(requireContext(),R.drawable.et_design)
        binding.tvPasswordWarning.visible(false)
    }
    
    private fun clearInput()
    {
        binding.etUserName.setText("")
        binding.etPassword.setText("")
        binding.etUserMail.setText("")
    }
    
    private fun changeUiToRegister()
    {
        binding.apply {
            tvTitle.text = resources.getString(R.string.sign_up)
            btnGetStartOrLogin.text = resources.getString(R.string.get_start)
            haveAccountOrNot.text = resources.getString(R.string.do_you_have_an_account)
            tvLinkLoginOrRegister.text = resources.getString(R.string.login)
            tvForgotPassword.visible(false)
            etUserMail.visible(true)
            defaultStyle()
            clearInput()
        }
    }
    
    class ApiStatusResponse(val success : Boolean)
    class ApiMainResponse(val data : data)
    class data(val access_token : String , val expires_in : Int)
}