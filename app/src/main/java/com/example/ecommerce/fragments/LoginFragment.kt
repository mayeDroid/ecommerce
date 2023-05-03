package com.example.ecommerce.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class LoginFragment: Fragment(R.layout.fragment_login) {

        private lateinit var binding: FragmentLoginBinding
        private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            buttonLoginLoginFragment.setOnClickListener {
                val email = editTextEmailLogin.text.toString().trim()
                val password = editTextPasswordLogin.text.toString()
                viewModel.login(email, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect{
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLoginFragment.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonLoginLoginFragment.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also {
                            intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            // this will ensure clicking the back button doesn't takes it to the register fragment
                            startActivity(intent)
                        }
                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        binding.buttonLoginLoginFragment.revertAnimation()

                    }

                    else -> Unit
                }
            }
        }
    }
}