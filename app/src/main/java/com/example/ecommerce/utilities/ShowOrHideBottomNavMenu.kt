package com.example.ecommerce.utilities

import android.view.View
import androidx.fragment.app.Fragment
import com.example.ecommerce.R
import com.example.ecommerce.activities.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)
    bottomNavigationView.visibility = View.GONE
}

fun Fragment.showBottomNavView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)
    bottomNavigationView.visibility = View.VISIBLE
}
