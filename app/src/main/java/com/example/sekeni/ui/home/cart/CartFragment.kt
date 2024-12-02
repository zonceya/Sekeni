package com.example.sekeni.ui.home.cart

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product
import com.example.sekeni.repository.CartRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CartFragment : Fragment() {

    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = activity?.findViewById<FloatingActionButton>(R.id.fab)
        // Hide the FAB
        fab?.visibility = View.GONE
        // Initialize RecyclerView
        val cartRecyclerView = view.findViewById<RecyclerView>(R.id.cartRecyclerView)
        cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get cart items from the repository
        val cartItems = CartRepository.getCartItems()

        // Set up the adapter
        cartAdapter = CartAdapter(
            cartItems,
            ::removeItem
        )
        cartRecyclerView.adapter = cartAdapter

        // Handle clear cart button
        view.findViewById<View>(R.id.cartRemove)?.setOnClickListener {
            CartRepository.clearCart()
            cartAdapter.updateItems(emptyList())
        }
    }

    private fun removeItem(product: Product) {
        CartRepository.removeFromCart(product)
        cartAdapter.updateItems(CartRepository.getCartItems())
    }

    private fun increaseQuantity(product: Product) {
        CartRepository.updateQuantity(product, 1) // Assuming updateQuantity increases by 1
        cartAdapter.updateItems(CartRepository.getCartItems())
    }

    private fun decreaseQuantity(product: Product) {
        CartRepository.updateQuantity(product, -1) // Assuming updateQuantity decreases by 1
        cartAdapter.updateItems(CartRepository.getCartItems())
    }
}
