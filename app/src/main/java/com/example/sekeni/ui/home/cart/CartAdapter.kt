package com.example.sekeni.ui.home.cart

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.motion.widget.MotionScene
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.sekeni.R
import com.example.sekeni.data.local.product.Product

class CartAdapter(
    private var cartItems: List<Pair<Product, Int>>,
    private val onRemoveClick: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.viewholder_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (product, quantity) = cartItems[position]
        holder.bind(product, quantity)
        holder.itemView.findViewById<View>(R.id.cartRemove).setOnClickListener {
            onRemoveClick(product)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    fun updateItems(newItems: List<Pair<Product, Int>>) {
        cartItems = newItems
        notifyDataSetChanged()
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(product: Product, quantity: Int) {
            itemView.findViewById<TextView>(R.id.cartText).text = product.name
            itemView.findViewById<TextView>(R.id.cartText3).text = "${product.price} x $quantity"

          Glide.with(itemView.context)
                .load(product.imageResId)  // Load the image resource
                .placeholder(R.drawable.zara)  // Set a placeholder image
                .override(200, 200)  // Resize the image to 200x200 pixels
                .into(itemView.findViewById(R.id.image_cartTitle))

        }
    }
}
