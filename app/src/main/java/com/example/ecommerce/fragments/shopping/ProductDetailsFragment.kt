package com.example.ecommerce.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerce.adapters.ChooseColorOfItem
import com.example.ecommerce.adapters.ChooseSizeOfItem
import com.example.ecommerce.adapters.ViewPagerForImageDetails
import com.example.ecommerce.databinding.FragmentProductDetailsBinding
import com.example.ecommerce.utilities.hideBottomNavView

class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    private val args by navArgs<ProductDetailsFragmentArgs>()       // we added product details to nav graph
    private val viewPagerAdapter by lazy { ViewPagerForImageDetails() }
    private val sizesAdapter by lazy { ChooseSizeOfItem() }
    private val colorsAdapter by lazy { ChooseColorOfItem() }

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