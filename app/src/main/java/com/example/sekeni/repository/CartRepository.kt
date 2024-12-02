package com.example.sekeni.repository

import com.example.sekeni.data.local.product.Product

object CartRepository {
    private val cartItems = mutableMapOf<Product, Int>()

    fun addToCart(product: Product) {
        cartItems[product] = cartItems.getOrDefault(product, 0) + 1
    }

    fun getCartItems(): List<Pair<Product, Int>> {
        return cartItems.map { it.key to it.value }
    }

    fun removeFromCart(product: Product) {
        cartItems[product]?.let {
            if (it > 1) {
                cartItems[product] = it - 1
            } else {
                cartItems.remove(product)
            }
        }
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun updateQuantity(product: Product, delta: Int) {
        cartItems[product]?.let {
            val updatedQuantity = it + delta
            when {
                updatedQuantity > 0 -> cartItems[product] = updatedQuantity
                else -> cartItems.remove(product)
            }
        }
    }
}
