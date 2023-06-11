package com.example.ecommerce.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.ecommerce.R
import com.example.ecommerce.databinding.ActivityShoppingBinding
import com.example.ecommerce.utilities.Resource
import com.example.ecommerce.viewmodel.CartViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ShoppingActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityShoppingBinding.inflate(layoutInflater)
    }

    // to get notification of the number of items in cart on the bottom cart icon
    val viewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val navController = findNavController(R.id.shoppingHostFragment)    //The shpHostFrag is the
        binding.bottomNavigation.setupWithNavController(navController)

        // to get notification of the number of items in cart on the bottom cart icon
        lifecycleScope.launchWhenStarted {
            viewModel.cartProductsss.collectLatest {
                when(it){
                    is Resource.Success ->  {
                        val count = it.data?.size?: 0
                        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
                        bottomNavigationView.getOrCreateBadge(R.id.cartFragment).apply {
                            number = count
                            backgroundColor = resources.getColor(R.color.g_blue)
                        }
                    }
                    else -> Unit
                }
            }
        }
    }
}