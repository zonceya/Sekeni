package com.example.sekeni.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product

class ProductViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    private val _selectedProductId = MutableLiveData<String?>()
    val selectedProductId: LiveData<String?> get() = _selectedProductId

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _products.value = listOf(
            Product(1, "T-Shirt", "R100.00", 4.5, "Comfortable cotton T-shirt.", R.drawable.ic_tshirt),
            Product(2, "Nike Shoes", "R200.00", 4.0, "Stylish Nike running shoes.", R.drawable.nike_shoe),
            Product(3, "Adidas Hoodie", "R500", 3.0, "Warm Adidas hoodie.", R.drawable.adidas_hoodie),
            Product(4, "Puma Cap", "R150", 2.3, "Trendy Puma cap.", R.drawable.pum_cap)
        )
    }
    fun selectProduct(productId: String?) {
        _selectedProductId.value = productId
    }
}

