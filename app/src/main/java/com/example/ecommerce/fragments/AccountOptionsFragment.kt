package com.example.ecommerce.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.FragmentAccountOptionsBinding
import com.example.ecommerce.databinding.FragmentIntroductionBinding

class AccountOptionsFragment: Fragment(R.layout.fragment_account_options) {

    private lateinit var binding: FragmentAccountOptionsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonRegisterAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment2_to_registerFragment2)
        }

        binding.buttonLoginAccountOptions.setOnClickListener {
            findNavController().navigate(R.id.action_accountOptionsFragment2_to_loginFragment)
        }
    }
}