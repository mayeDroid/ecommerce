package com.example.ecommerce.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter ( private val fragments: List<Fragment>,
                             fragmentManager: FragmentManager,
                             lifeCycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifeCycle)
{    // the adapter for the home view
    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

}