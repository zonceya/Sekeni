package com.example.sekeni.ui.product

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Category
import com.example.sekeni.data.local.product.Product

class ProductViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _selectedProductId = MutableLiveData<String?>()
    val selectedProductId: LiveData<String?> get() = _selectedProductId

    private var currentPage = 1
    private val pageSize = 4

    // Single source of truth for product data
    private val allProducts = listOf(
        Product(1, "T-Shirt", "R100.00", 4.5, "Comfortable cotton T-shirt.", R.drawable.ic_tshirt),
        Product(2, "Nike Shoes", "R200.00", 4.0, "Stylish Nike running shoes.", R.drawable.nike_shoe),
        Product(3, "Adidas Hoodie", "R500", 3.0, "Warm Adidas hoodie.", R.drawable.adidas_hoodie),
        Product(4, "Puma Cap", "R150", 2.3, "Trendy Puma cap.", R.drawable.pum_cap)
    )

    init {
        fetchNextPage() // Fetch initial page of products
    }

    fun selectProduct(productId: String?) {
        _selectedProductId.value = productId
    }

    fun fetchNextPage() {
        val startIndex = (currentPage - 1) * pageSize
        val endIndex = (currentPage * pageSize).coerceAtMost(allProducts.size)
        if (startIndex < allProducts.size) {
            val currentData = _products.value.orEmpty()
            _products.value = currentData + allProducts.subList(startIndex, endIndex)
            currentPage++
        }
    }
}


