package com.example.sekeni.ui.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product

class ProductFragment : Fragment() {

    private lateinit var viewModel: ProductViewModel
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_product, container, false)
       /* val recyclerView = view.findViewById<RecyclerView>(R.id.productRecyclerView)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // Setup RecyclerView
        productAdapter = ProductAdapter(emptyList())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = productAdapter

        // Observe LiveData for products
        viewModel.products.observe(viewLifecycleOwner) { products ->
            productAdapter.updateData(products)*/


        return view
    }
}
