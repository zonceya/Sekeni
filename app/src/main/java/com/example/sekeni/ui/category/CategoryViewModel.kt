package com.example.sekeni.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Category
import com.example.sekeni.data.local.product.Product

class CategoryViewModel : ViewModel() {

    private val _category = MutableLiveData<List<Category>>()
    val category: LiveData<List<Category>> get() = _category

    private val _selectedCategoryId = MutableLiveData<String?>()
    val selectedCategoryId: LiveData<String?> get() = _selectedCategoryId

    private var currentPage = 1
    private val pageSize = 4

    private val allCategory = listOf(
        Category(1, "Men", R.drawable.ic_men),
        Category(2, "Woman", R.drawable.ic_women1),
        Category(3, "Kids", R.drawable.ic_kids),
        Category(4, "Tradition", R.drawable.ic_trade)
    )

    init {
        fetchNextPage() // Load initial categories
    }

    private fun getCategoryForPage(page: Int): List<Category> {
        val startIndex = (page - 1) * pageSize
        val endIndex = (page * pageSize).coerceAtMost(allCategory.size)
        return allCategory.subList(startIndex, endIndex)
    }

    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }

    fun fetchNextPage() {
        val newCategories = getCategoryForPage(currentPage)
        val currentData = _category.value.orEmpty()

        // Avoid duplicates
        val uniqueCategories = newCategories.filterNot { it in currentData }
        if (uniqueCategories.isNotEmpty()) {
            _category.value = currentData + uniqueCategories
            currentPage++
        }
    }
}


