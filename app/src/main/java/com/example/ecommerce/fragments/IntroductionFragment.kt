package com.example.ecommerce.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ecommerce.R
import com.example.ecommerce.activities.LoginRegisterActivity
import com.example.ecommerce.activities.ShoppingActivity
import com.example.ecommerce.databinding.FragmentIntroductionBinding
import com.example.ecommerce.viewmodel.IntroductionViewModel
import com.example.ecommerce.viewmodel.IntroductionViewModel.Companion.ACCOUNT_OPTIONS_FRAGMENT
import com.example.ecommerce.viewmodel.IntroductionViewModel.Companion.SHOPPING_ACTIVITY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
@AndroidEntryPoint
class IntroductionFragment: Fragment(R.layout.fragment_introduction) {
        private lateinit var binding: FragmentIntroductionBinding
        private val viewModel by viewModels<IntroductionViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentIntroductionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.navigate.collect{
                when (it){
                    SHOPPING_ACTIVITY -> {
                        //This will ensure that after registration the Introduction fragment will not come up but the start activity will show
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            // this will ensure clicking the back button doesn't takes it to the register fragment
                            startActivity(intent)

                    }
                }
                    ACCOUNT_OPTIONS_FRAGMENT -> {
                        findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment2)
                    }

                    else -> Unit
                }
            }
        }

        binding.buttonStart.setOnClickListener {
            viewModel.startButtonClick()
            findNavController().navigate(R.id.action_introductionFragment_to_accountOptionsFragment2)
        }
    }
}