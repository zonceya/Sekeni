package com.example.sekeni.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Category

class CategoryAdapter (
    private var categoryList: List<Category>,
    private val onCategoriesClick: (String) -> Unit// Change Any to Product
) : RecyclerView.Adapter<CategoryAdapter.CategoriesViewHolder>() {

    class CategoriesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.category_image)
        val categoryName: TextView = itemView.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_categories, parent, false)
        return CategoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val category = categoryList[position]
        holder.categoryName.text = category.name


        // Load product image
        Glide.with(holder.categoryImage.context)
            .load(category.imageResId)
            .placeholder(R.drawable.zara)
            .into(holder.categoryImage)

        // Handle click
        holder.itemView.setOnClickListener {
            onCategoriesClick(category.id.toString())

        }
    }

    override fun getItemCount(): Int = categoryList.size

    fun updateData(newCategories: List<Category>) {
        categoryList = newCategories
        notifyDataSetChanged()
    }

}
