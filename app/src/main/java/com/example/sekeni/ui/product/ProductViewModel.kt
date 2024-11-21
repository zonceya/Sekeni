package com.example.sekeni.ui.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product

class ProductViewModel : ViewModel() {

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> get() = _products

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _products.value = listOf(
            Product("T-Shirt", "R100.00", 4.5, R.drawable.ic_tshirt),
            Product("Nike Shoes", "R200.00", 4.0, R.drawable.nike_shoe),
            Product( "Adidas Hoodie", "R 500",3.0,R.drawable.adidas_hoodie),
           Product("Puma Cap", "R 150",2.3,R.drawable.pum_cap)
        )
    }
}
