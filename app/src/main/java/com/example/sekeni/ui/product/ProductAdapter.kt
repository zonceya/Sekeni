package com.example.sekeni.ui.product

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sekeni.data.local.product.Product
import com.example.sekeni.R
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.example.sekeni.repository.CartRepository


class ProductAdapter(
    private var productList: List<Product>,
    private val onProductClick: (String) -> Unit// Change Any to Product
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.product_image)
        val productTitle: TextView = itemView.findViewById(R.id.textStar2)
        val productPrice: TextView = itemView.findViewById(R.id.Price)
        val productRating: TextView = itemView.findViewById(R.id.textStar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_holder_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.productTitle.text = product.name
        holder.productPrice.text = product.price
        holder.productRating.text = product.rating.toString()
        holder.productRating.text = product.size

        // Load product image
        Glide.with(holder.productImage.context)
            .load(product.imageResId)
            .placeholder(R.drawable.zara)
            .into(holder.productImage)

        // Handle click
        holder.itemView.setOnClickListener {
            onProductClick(product.id.toString())

        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateData(newProducts: List<Product>) {
        productList = newProducts
        notifyDataSetChanged()
    }
    private fun addToCart(product: Product) {
        CartRepository.addToCart(product)
        //Toast.makeText(requireContext(), "${product.name} added to cart", Toast.LENGTH_SHORT).show()
    }
    class DiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Product, newItem: Product) = oldItem == newItem
    }
}
