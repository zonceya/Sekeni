package com.example.sekeni.ui.product

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product
import com.example.sekeni.repository.CartRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        // Hide the FAB
        fab?.visibility = View.GONE

        val images = listOf(
            R.drawable.adidas_hoodie,
            R.drawable.adidashodi7,
            R.drawable.adidashodi8
        )
        val viewPager = view.findViewById<ViewPager2>(R.id.viewProductDetailPager)
        viewPager.adapter = ViewPagerAdapterProduct(images)
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
    private fun addToCart(product: Product) {
        CartRepository.addToCart(product)
       // Toast.makeText(requireContext(), "${product.title} added to cart", Toast.LENGTH_SHORT).show()
    }
    private fun fetchProductDetails(productId: String) {
        val product = viewModel.products.value?.find { it.id == productId.toIntOrNull() }
        if (product != null) {
            view?.findViewById<TextView>(R.id.productTitle)?.text = product.name
            view?.findViewById<TextView>(R.id.productDescription)?.text = product.description
            view?.findViewById<TextView>(R.id.sizeM)?.text = product.size
            view?.findViewById<TextView>(R.id.productPrice)?.text = product.price
            view?.findViewById<TextView>(R.id.brandName)?.text = product.brandName
            view?.findViewById<ImageView>(R.id.productImages)?.let {
                Glide.with(this).load(product.imageResId).placeholder(R.drawable.nike_shoe).into(it)
            }
            view?.findViewById<AppCompatButton>(R.id.addToCart)?.setOnClickListener {
                addToCart(product)
                findNavController().navigate(R.id.action_productFragment_to_cartFragment)
            }

        } else {
            Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show()
        }
    }
}
