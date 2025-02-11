package com.example.sekeni.data.local.product

data class Product(
    val id: Int,
    val name: String,
    val price: String,
    val rating: Double,
    val description: String,
    val brandName: String,
    val size: String,
    val imageResId: Int
)