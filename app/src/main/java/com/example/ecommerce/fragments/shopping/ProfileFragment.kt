package com.example.ecommerce.fragments.shopping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.ecommerce.BuildConfig
import com.example.ecommerce.R
import com.example.ecommerce.activities.LoginRegisterActivity
import com.example.ecommerce.databinding.FragmentProfileBinding
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.utilities.showBottomNavView
import com.example.ecommerce.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
@AndroidEntryPoint
class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    val viewModel by viewModels<ProfileViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.constraintProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)

        }

        binding.linearAllOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_allOrdersFragment)
        }

        binding.linearBilling.setOnClickListener {
            val action = ProfileFragmentDirections.actionProfileFragmentToBillingFragment(0f, emptyArray(), false)
            findNavController().navigate(action)
        }

        // to logOut of the app
        binding.linearLogOut.setOnClickListener {
            viewModel.logOut()  // function logOut was implemented in the viewModel
            val intent = Intent(requireActivity(), LoginRegisterActivity::class.java)
            startActivity(intent)
            requireActivity().finish()  // so when you press back key nothing happens again else it will re navigate
        }

        binding.tvVersion.text = "Version ${BuildConfig.VERSION_CODE}"  // to get the software version

        // to collect the state of the user details
        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        binding.progressbarSettings.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Glide.with(requireView()).load(it.data!!.imagePath).error(ColorDrawable(Color.BLACK)).into(binding.imageUser)
                        binding.tvUserName.text = "${it.data.firstName} ${it.data.lastname}"
                    }
                    is Resource.Error -> {
                        binding.progressbarSettings.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

    }

    // when we navigate back we want to ensure the showBottom navigation is visible
    override fun onResume() {
        super.onResume()

        showBottomNavView()
    }
}