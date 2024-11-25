package com.example.sekeni.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product

class ProductFragment : Fragment() {

    private lateinit var viewModel: ProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[ProductViewModel::class.java]
        return inflater.inflate(R.layout.fragment_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe products LiveData
        viewModel.products.observe(viewLifecycleOwner) { products ->
            if (products.isNotEmpty()) {
                viewModel.selectedProductId.value?.let { productId ->
                    fetchProductDetails(productId)
                }
            } else {
                Toast.makeText(requireContext(), "No products available", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe selected product ID
        viewModel.selectedProductId.observe(viewLifecycleOwner) { productId ->
            if (productId != null) {
                fetchProductDetails(productId)
            }
        }
    }

    private fun fetchProductDetails(productId: String) {
        val parsedId = productId.toIntOrNull()

        // Find product by ID
        val product = viewModel.products.value?.find { it.id == parsedId }

        if (product != null) {
            // Update UI elements
            view?.findViewById<TextView>(R.id.productTitle)?.text = product.name
            view?.findViewById<TextView>(R.id.productDescription)?.text = product.description

            val productImage = view?.findViewById<ImageView>(R.id.productDetailImage)
            if (productImage == null) {
                Log.e("ProductFragment", "ImageView not found!")
            } else {
                Log.d("ProductFragment", "ImageView is ready.")
            }
            productImage?.let {
                Glide.with(this)
                    .load(product.imageResId)
                    .placeholder(R.drawable.nike_shoe)
                    .into(it)
            }
        } else {
            Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show()
        }
    }
}
