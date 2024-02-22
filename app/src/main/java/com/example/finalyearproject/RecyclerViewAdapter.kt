package com.example.finalyearproject // Change this to your actual package name

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewAdapter(
    private var products: MutableList<Product>,
    private val listener: OnItemClickListener

) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(product: Product)
    }

    private var productsFull: List<Product> = ArrayList(products) // No change needed here

    fun filter(category: String) {
        products = if (category == "All") {
            productsFull.toMutableList() // Ensure this is mutable
        } else {
            productsFull.filter { it.category == category }
                .toMutableList() // Filter and convert to MutableList
        }
        notifyDataSetChanged() // Notify the adapter to refresh the view
    }

    // Binds product data to the view and sets click listeners for each product on the list
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var productName: TextView = itemView.findViewById(R.id.product_name)
        private var productDescription: TextView = itemView.findViewById(R.id.product_description)
        private var productWarning: TextView = itemView.findViewById(R.id.product_warning)

        fun bind(product: Product, listener: OnItemClickListener) {
            val context = itemView.context
            productName.text = product.brand
            productDescription.text = product.description

            // Initialise warning message and hide the warning view by default
            var warningMessage = ""
            productWarning.visibility = View.GONE  // Hide the warning initially

            // Check conditions and update warning message accordingly
            val isExpiryApproaching = product.isExpiryApproaching(2) // Assuming this checks for expiry within 2 days
            val isQuantityLow = product.isQuantityLow(5) // Assuming this checks for quantity less than 5

            when {
                isExpiryApproaching && isQuantityLow -> {
                    warningMessage = "Quantity Low, Expiry Date Approaching"
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.productWarning)) // Change as per your color for combined warning
                }
                isExpiryApproaching -> {
                    warningMessage = "Expiry Date Approaching"
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.productWarning)) // Use color for expiry warning
                }
                isQuantityLow -> {
                    warningMessage = "Quantity Low"
                    itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.productWarning)) // Use color for quantity warning
                }
            }

            // If there is a warning message, display it
            if (warningMessage.isNotEmpty()) {
                productWarning.text = warningMessage
                productWarning.visibility = View.VISIBLE
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT) // Reset to default if no warnings
            }

            itemView.setOnClickListener { listener.onItemClick(product) }
        }
    }


    // Creates a new View from layout specified in RecyclerView Layout file
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // View inflated so that it can be passed to the Viewholder
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_layout, parent, false)
        // Viewholder finds individual layout components to update
        return ViewHolder(view)
    }

    // Adapter takes data (product) and binds it to the ViewHolder which then updates
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product, listener)
    }

    override fun getItemCount() = products.size

}
