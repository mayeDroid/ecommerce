package com.example.ecommerce.fragments.shopping

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.R
import com.example.ecommerce.adapters.ChooseColorOfItem
import com.example.ecommerce.adapters.ChooseSizeOfItem
import com.example.ecommerce.adapters.ViewPagerForImageDetails
import com.example.ecommerce.databinding.FragmentProductDetailsBinding
import com.example.ecommerce.dataclasses.CartProducts
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.utilities.hideBottomNavView
import com.example.ecommerce.viewmodel.DetailsOfCartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()       // we added product details to nav graph
    private val viewPagerAdapter by lazy { ViewPagerForImageDetails() }
    private val sizesAdapter by lazy { ChooseSizeOfItem() }
    private val colorsAdapter by lazy { ChooseColorOfItem() }
    private var selectedColour: Int? = null
    private var selectedSize: String? = null
    private val viewModel by viewModels<DetailsOfCartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*// we want to hide the bottom menu when we enter the product details
        val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            R.id.bottomNavigation)
        bottomNavigationView.visibility = View.GONE*/
        hideBottomNavView()

        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val products = args.products

        setupSizesRv()
        setUpColoursRv()
        setUpViewPager()

        // clicking the cancel button to return
        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClicked = {
            selectedSize = it
        }

        colorsAdapter.onItemClicked = {
            selectedColour = it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateOrUpdateCart(CartProducts(products, 1, selectedColour, selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when (it){
                    is Resource.Loading -> {
                        binding.buttonAddToCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonAddToCart.revertAnimation()
                        Toast.makeText(requireContext(),  "Item added", Toast.LENGTH_SHORT).show()
                    //binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }
                    is Resource.Error -> {
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(),  it.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            textViewProductName.text = products.name
            textViewProductPrice.text = "₦${products.price}"
            textViewProductDescription.text = products.description

            // to hide colors and sizes if non
            if (products.colors.isNullOrEmpty())textViewProductColors.visibility = View.GONE
            if (products.sizes.isNullOrEmpty())textViewProductSizes.visibility = View.GONE
        }

        viewPagerAdapter.differ.submitList(products.images)

        products.colors?.let { colorsAdapter.differ.submitList(it) }    // if there are colors

        products.sizes?.let { sizesAdapter.differ.submitList(it) }  //

    }

    private fun setUpViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setUpColoursRv() {
        binding.recyclerViewColors.apply {
            adapter = colorsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.recyclerViewSizes.apply {
            adapter = sizesAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
}