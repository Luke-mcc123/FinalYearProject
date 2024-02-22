package com.example.finalyearproject

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.finalyearproject.databinding.FragmentProductDetailsBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.EditText

class ProductDetailsFragment : Fragment() {

    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate View
        _binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Navigates back to the List Fragment
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Load Product Details passed in from List Fragment
        arguments?.getLong("barcode")?.let { barcode ->
            if (barcode != -1L) {
                loadProductDetails(barcode)
            }
        }

        // Decrease quantity when button is clicked
        binding.btnDecrease.setOnClickListener {
            var currentQuantity: Int = binding.etvProductQuantity.text.toString().toInt()
            if (currentQuantity > 0) {
                currentQuantity--
                binding.etvProductQuantity.setText(currentQuantity.toString())
            }
            quantityCheck()
            binding.btnAccept.visibility = View.VISIBLE
        }

        // Increase quantity when button is clicked
        binding.btnIncrease.setOnClickListener {
            var currentQuantity: Int = binding.etvProductQuantity.text.toString().toInt()
            currentQuantity++
            binding.etvProductQuantity.setText(currentQuantity.toString())
            quantityCheck()
            binding.btnAccept.visibility = View.VISIBLE
        }

        // Update Product Quantity when Accept Button is Clicked
        binding.btnAccept.setOnClickListener {
            arguments?.getLong("barcode")?.let { barcode ->
                if (barcode != -1L) {
                    val newQuantity = binding.etvProductQuantity.text.toString().toInt()
                    updateQuantity(barcode, newQuantity)
                }
            }
        }

        // Delete current product from database
        binding.btnDelete.setOnClickListener {

        }
    }

    private fun quantityCheck() {
        if (binding.etvProductQuantity.text.toString().toInt() < 5) {
            binding.etvQuantityWarning.visibility = View.VISIBLE
            binding.etvQuantityWarning.text = "Warning Quantity Low"
        } else {
            binding.etvQuantityWarning.visibility = View.GONE
        }
        adjustWarningTextViews()
    }

    private fun updateQuantity(barcode: Long, newQuantity: Int) {
        val dbHandler = DataBaseHandler(requireContext())
        val product = dbHandler.getProductByBarcode(barcode)
        product?.let {
            it.quantity = newQuantity
            dbHandler.updateProductData(it)
            Toast.makeText(requireContext(), "Quantity Updated", Toast.LENGTH_SHORT).show()
        } ?: run {
            Toast.makeText(requireContext(), "Product not found", Toast.LENGTH_SHORT).show()
        }

    }

    // Function to check expiry date and return true/false if within two weeks of expiry date
    private fun checkDate(productDate: Date): Boolean {
        val currentDate = Calendar.getInstance()
        val expiryDate = Calendar.getInstance().apply {
            time = productDate
        }
        // Add one day to the current date to check if the product is close to its expiry date
        currentDate.add(Calendar.DAY_OF_YEAR, 1)

        // Return true if the product date is before or equal to the current date plus one day
        return !expiryDate.after(currentDate)

    }

    // Show pop up box to waste out of date items
    private fun showDiscardDialog(product: Product) {
        val editText = EditText(context).apply {
            hint = "Enter quantity to discard"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        AlertDialog.Builder(requireContext()).apply {
            setTitle("Product Expired")
            setMessage("This product is out of date. How many would you like to discard?")
            setView(editText)
            setPositiveButton("Discard") { dialog, which ->
                val discardQuantity = editText.text.toString().toIntOrNull()
                discardQuantity?.let {
                    if (it > 0 && it <= product.quantity) {
                        // Use the existing updateQuantity function to update the database
                        updateQuantity(product.barcode, product.quantity - it)
                        // Update the displayed quantity in the UI
                        binding.etvProductQuantity.setText((product.quantity - it).toString())
                        // After discarding, show the DatePickerDialog to set a new date
                        showDatePickerDialog(product)
                    } else {
                        Toast.makeText(context, "Invalid quantity", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Cancel", null)
            show()
        }
    }

    // Create the Calendar Date Picker
    private fun showDatePickerDialog(product: Product) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(requireContext(), { view, year, monthOfYear, dayOfMonth ->
                // Update the product with the new date
                val newDate = Calendar.getInstance()
                newDate.set(year, monthOfYear, dayOfMonth)
                product.date = newDate.time
                updateProductDate(product)
            }, year, month, day)

        // Restricts past dates
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        // Show calendar
        datePickerDialog.show()
    }

    // Update Product Date for the Expiry Date Popup Box
    private fun updateProductDate(product: Product) {
        val dbHandler = DataBaseHandler(requireContext())
        dbHandler.updateProductData(product) // Assuming this updates all fields of the product
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        binding.etvProductDate.text = dateFormat.format(product.date)
        Toast.makeText(requireContext(), "Expiry date updated", Toast.LENGTH_SHORT).show()
    }

    // Get and display product details from barcode passed in from recyclerview
    private fun loadProductDetails(barcode: Long) {
        val dbHandler = DataBaseHandler(requireContext())
        val product = dbHandler.getProductByBarcode(barcode)
        val dateFormat = SimpleDateFormat("dd-MMM-yy", Locale.getDefault())
        val formattedDate: String = dateFormat.format(product?.date ?: 0)

        // Let block executes if product is not null
        product?.let {
            // bind product data (it) to controls
            binding.etvProductName.text = it.brand
            binding.etvProductDescription.text = it.description
            binding.etvProductCategory.text = it.category
            binding.etvProductDate.text = formattedDate
            binding.etvProductQuantity.setText(it.quantity.toString())
            binding.etvProductNotes.text = it.note

            // Check if quantity is low
            if (it.quantity < 5) {
                binding.etvQuantityWarning.visibility = View.VISIBLE
                binding.etvQuantityWarning.text = "Warning Quantity Low"
                adjustWarningTextViews()

            } else {
                binding.etvQuantityWarning.visibility = View.GONE
                adjustWarningTextViews()
            }

            // Check if the product's expiry date is close to or has passed
            if (it.date != null && checkDate(it.date!!)) {
                binding.etvExpiryWarning.visibility = View.VISIBLE
                binding.etvExpiryWarning.text =
                    "Product is close to or is past its sell by date, please appropriately waste"
                adjustWarningTextViews()
                showDiscardDialog(it)
            } else {
                binding.etvExpiryWarning.visibility = View.GONE
                adjustWarningTextViews()
            }
        } ?: run {
            // Handle case where product is null
            binding.etvProductName.text = getString(R.string.product_not_found)
            binding.etvProductDescription.text = ""
            binding.etvProductCategory.text = ""
            binding.etvProductDate.text = ""
            binding.etvProductQuantity.setText("")
            binding.etvProductNotes.text = ""

        }
    }

    // Generated with the help of chatgpt
    private fun adjustWarningTextViews() {
        // Get Constraint Layout and duplicate it into a ConstraintSet object allowing constraints to be changed without modifying original layout
        val constraintLayout =
            binding.constraintLayout // Assuming you've given your ConstraintLayout an ID in the XML.
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)

        // Check if the Quantity warning is visible
        // If it is change expiry warning constraints to top of quantity warning
        // Else keep them binded to the linear layout
        if (binding.etvQuantityWarning.visibility == View.VISIBLE) {
            constraintSet.connect(
                R.id.etvExpiryWarning,
                ConstraintSet.TOP,
                R.id.etvQuantityWarning,
                ConstraintSet.BOTTOM,
                dpToPx(8) // Assuming 8dp as your margin
            )
        } else {
            constraintSet.connect(
                R.id.etvExpiryWarning,
                ConstraintSet.TOP,
                R.id.llQuantitySelector,
                ConstraintSet.BOTTOM,
                dpToPx(8)
            )
        }

        // Apply new constraint values to current constraint
        constraintSet.applyTo(constraintLayout)
    }

    // Generated with the help of Chatgpt
    // ConstraintSets expect values in pixels instead of dp
    // Ensures spacing and sizing remains consistent when layout values are being changed by converting back to dp after adjustments
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
