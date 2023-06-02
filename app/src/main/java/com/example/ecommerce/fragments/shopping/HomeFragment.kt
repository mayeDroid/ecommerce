package com.example.ecommerce.fragments.shopping
//app:tabRippleColor="@color/white" the bubble effect when you click on the tab view
// <androidx.viewpager2.widget.ViewPager2 helps shows selected fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.example.ecommerce.adapters.HomeViewPagerAdapter
import com.example.ecommerce.databinding.FragmentHomeBinding
import com.example.ecommerce.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragmentList = arrayListOf<Fragment>(
            //to get the categories list of fragments
            MainOrHomeCategoryFragment(),
            ChairsFragment(),
            CupboardFragment(),
            TableFragment(),
            AccessoriesFragment(),
            FurnitureFragment(),

            )

        binding.viewPagerHomeFragment.isUserInputEnabled = false    // helps cancels the swipe behaviour in the view pager

        val viewPagerToAdapter =
            HomeViewPagerAdapter(categoriesFragmentList, childFragmentManager, lifecycle)
        binding.viewPagerHomeFragment.adapter = viewPagerToAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPagerHomeFragment) { tab, position ->
            when (position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessories"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }
}