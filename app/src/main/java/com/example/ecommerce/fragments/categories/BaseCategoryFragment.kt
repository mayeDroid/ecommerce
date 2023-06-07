package com.example.ecommerce.fragments.categories

// we want all other fragments (chairs, cupboard, ...) to inherit from this fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerce.R
import com.example.ecommerce.adapters.BestProductsAdapter
import com.example.ecommerce.databinding.FragmentBaseCategoryBinding
import com.example.ecommerce.utilities.showBottomNavView

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
   /* private lateinit var offerAdapter: BestProductsAdapter
    private lateinit var bestProducts: BestProductsAdapter*/

    // we want it to be accessible by other fragments eg Mobile phones etc.
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpOfferRecyclerView()
        setUpBestProducts()

        // clicking a picture or onclick listener
        bestProductsAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("products", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }

        offerAdapter.onClick = {
            val bundle = Bundle().apply { putParcelable("products", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, bundle)
        }



        binding.recyclerViewOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollHorizontally(1) && dx != 0){
                    onOfferPagingRequest()
                }
            }
        })

        // this enables us to fetch more products after using limits on fetchBestProducts()
        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{
                view,_,scrollY,_,_ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY){
                onBestProductsPagingRequests()
            }
        })

    }

    fun showProgressBarBestProducts(){
        binding.progressBarBestProduct.visibility = View.VISIBLE
    }

    fun hideProgressBarBarBestProducts(){
        binding.progressBarBestProduct.visibility = View.GONE
    }

    fun showProgressBarOfferProducts(){
        binding.progressBarBestProduct.visibility = View.VISIBLE
    }

    fun hideProgressBarOfferProducts(){
        binding.progressBarBestProduct.visibility = View.GONE
    }

    open fun onOfferPagingRequest(){

    }

    open fun onBestProductsPagingRequests(){

    }

    private fun setUpBestProducts() {
        // bestProducts = BestProductsAdapter() // no need since we decided to initialise it by lazy
        binding.recyclerViewBestProducts.apply {
            //layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            // we used grid layout because we want to display it in 2 rows
            adapter = bestProductsAdapter
        }
    }

    private fun setUpOfferRecyclerView() {
        // offerAdapter = BestProductsAdapter()
        binding.recyclerViewOffer.apply {
            layoutManager = LinearLayoutManager(requireContext(),  LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }
    }

    // when we hide the bottomNav on the productDetails the bottomNav is hidden when we return so this helps fix it
    override fun onResume() {
        super.onResume()
        showBottomNavView()
    }
}