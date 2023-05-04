package com.example.ecommerce.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.example.ecommerce.databinding.FragmentLoginBinding
import com.example.ecommerce.dialog.setUpBottomSheetDialog
import com.example.ecommerce.utilities.RegistrationValidation
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

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

        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmailLogin)
        val editTextPassword = view.findViewById<EditText>(R.id.editTextPasswordLogin)
        val buttonLogin = view.findViewById<Button>(R.id.buttonLoginLoginFragment)

        buttonLogin.isEnabled = false

        val textChange = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val edtMail = editTextEmail.text.toString().trim()
                val edtPassWord = editTextPassword.text.toString().trim()
                buttonLogin.isEnabled = edtMail.isNotEmpty() && edtPassWord.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        buttonLogin.addTextChangedListener(textChange)
        editTextEmail.addTextChangedListener(textChange)
        editTextPassword.addTextChangedListener(textChange)


        binding.tvDontHaveAnAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.apply {
            buttonLoginLoginFragment.setOnClickListener {
                val email = editTextEmailLogin.text.toString().trim()
                val password = editTextPasswordLogin.text.toString()
                viewModel.login(email, password)

                // viewModel.loginWithEmailWithPassword(email, password)    // when we want flows
            }
        }

        binding.tvForgotPasswordLogin.setOnClickListener {
            binding.editTextPasswordLogin.setText("")
            binding.editTextEmailLogin.setText("")
            setUpBottomSheetDialog { email ->
                viewModel.resetPassword(email)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collect {
                when (it) {
                    is Resource.Loading -> {
                        binding.buttonLoginLoginFragment.startAnimation()
                    }

                    is Resource.Success -> {
                        binding.buttonLoginLoginFragment.revertAnimation()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
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

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect { validation ->
                if (validation.emailValidation is RegistrationValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.editTextEmailLogin.apply {
                            requestFocus()
                            error = validation.emailValidation.message
                        }
                    }
                }

                if (validation.passwordValidation is RegistrationValidation.Failed) {
                    withContext(Dispatchers.Main) {
                        binding.editTextPasswordLogin.apply {
                            requestFocus()
                            error = validation.passwordValidation.message
                        }
                    }
                }

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect {
                when (it) {
                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        Snackbar.make(
                            requireView(),
                            "Reset password link has been sent to your mail",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }

                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_SHORT)
                            .show()
                    }

                    else -> Unit
                }
            }
        }

    }
}