package com.example.ecommerce.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentRegisterBinding
import com.example.ecommerce.dataclasses.User
import com.example.ecommerce.utilities.RegistrationValidation
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.utilities.validateEmail
import com.example.ecommerce.viewmodel.RegistrationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

private val TAG = "RegistrationFragment"
@AndroidEntryPoint
class RegistrationFragment: Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegistrationViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegisterLoginRegister.setOnClickListener {
               val user = User(
                   editTextFirstName.text.toString().trim(),
                   editTextLastName.text.toString().trim(),
                   editTextEmailLoginRegister.text.toString().trim())
                val password = editTextPasswordLoginRegister.text.toString()
                viewModel.createAccountAndEmailWithPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.registerStateFlows.collect{
                when(it){
                    is Resource.Loading -> {
                        binding.buttonRegisterLoginRegister.startAnimation()
                    }
                    is Resource.Success -> {
                       // Toast.makeText(activity?.applicationContext, "Account created", Toast.LENGTH_SHORT ).show()

                        Log.d("test", it.data.toString())
                        binding.buttonRegisterLoginRegister.revertAnimation()   //to stop animation if successful
                    }

                    is Resource.Error -> {
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(activity?.applicationContext, it.message.toString(), Toast.LENGTH_SHORT).show()
                        binding.buttonRegisterLoginRegister.revertAnimation()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{
                validation ->
                if (validation.emailValidation is RegistrationValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextEmailLoginRegister.apply {
                            requestFocus()
                            error = validation.emailValidation.message
                        }
                    }
                }

                if (validation.passwordValidation is RegistrationValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextPasswordLoginRegister.apply {
                            requestFocus()
                            error = validation.passwordValidation.message
                        }
                    }
                }

                if (validation.firstNameValidation is RegistrationValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextFirstName.apply {
                            requestFocus()
                            error = validation.firstNameValidation.message
                        }
                    }
                }

                if (validation.lastNameValidation is RegistrationValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.editTextLastName.apply {
                            requestFocus()
                            error = validation.lastNameValidation.message
                        }
                    }
                }
            }
        }
    }
}