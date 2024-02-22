package com.example.finalyearproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalyearproject.databinding.FragmentAddProductBinding
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate View
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Build Date Picker
    private val datePicker: MaterialDatePicker<Long> by lazy {
        MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Context provides access to the applications resources to the DataBaseHandler class
        // This allows it to perform database operations within the current fragment
        val context = requireContext()
        val db = DataBaseHandler(context)

        // Extracting the barcode if exists and setting it
        arguments?.getString("barcode")?.let { barcode ->
            binding.etvBarcode.setText(barcode)
        }

        // Dropdown Menu Set Up
        // Get Reference To The String Array Which Has The Product Categories
        val categories = resources.getStringArray(R.array.categories)
        // Array Adapter Which Passes The Required Parameters
        val arrayAdapter = ArrayAdapter(context, R.layout.dropdown_item, categories)
        // Get Reference To The Autocomplete Text View
        val autocompleteTV = binding.etvCategory
        // Set Adapter To The autocompleteTV To The Array Adapter
        autocompleteTV.setAdapter(arrayAdapter)

        // Insert inputted data into the database
        binding.btnInsert.setOnClickListener {
            if (isValidInput()
            ) {
                val product = createProductFromInput()
                db.insertData(product)
                // Clear the text fields after input
                clearEditTextFields()
            } else {
                Toast.makeText(context, "Please Fill In All Fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Logic Behind Update Button
        binding.btnUpdate.setOnClickListener {
            if (isValidInput()
            ) {
                val product = createProductFromInput()
                db.updateProductData(product)
                Toast.makeText(context, "Product Updated Successfully", Toast.LENGTH_SHORT).show()

                // Clear the text fields after input
                clearEditTextFields()

            } else {
                Toast.makeText(
                    context,
                    "Please input Barcode of product and the updated details",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Logic Behind Delete Button
        // Deletes Specific Record - TODO Lock Behind Validation
        binding.btnDelete.setOnClickListener {
            if (binding.etvBarcode.text.toString().isNotEmpty()) {
                try {
                    val barcodeToDelete = binding.etvBarcode.text.toString().toLong()
                    val product = Product(barcodeToDelete, "", "", "", 0, "", null)
                    db.deleteProductData(product)
                    Toast.makeText(context, "Product Successfully Deleted", Toast.LENGTH_SHORT)
                        .show()
                    // Clear the text fields after input
                    clearEditTextFields()
                } catch (e: NumberFormatException) {
                    Toast.makeText(
                        context,
                        "Invalid Barcode Format. Please enter a valid numeric barcode.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    context,
                    "Please input Barcode of product to Delete",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Logic for barcode scanner which opens the Google Scanner and reads and stores a barcode
        binding.btnScan.setOnClickListener {
            // the GmsBarcodeScanner is a client to open a code scanner powered by Google Play Services, as part of the Google Developers ML Kit
            // Configure Barcode Scanner
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_QR_CODE)
                .enableAutoZoom()
                .build()
            clearEditTextFields()
            val scanner = GmsBarcodeScanning.getClient(context, options)
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    clearEditTextFields()
                    val rawValue: String? = barcode.rawValue.toString()
                    binding.etvBarcode.setText(rawValue)

                    // Call Method to Get Product By Barcode
                    val scannedProduct = db.getProductByBarcode(rawValue?.toLong() ?: 0)

                    if (scannedProduct != null) {
                        // Populate Information

                        binding.etvBrand.setText(scannedProduct.brand)
                        binding.etvDescription.setText(scannedProduct.description)
                        binding.etvNote.setText(scannedProduct.note)
                        binding.etvQuantity.setText(scannedProduct.quantity.toString())
                        binding.etvCategory.setText(scannedProduct.category)
                        // Handle Date Conversion
                        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        val formattedDate = scannedProduct.date?.let { dateFormat.format(it) }
                        binding.etvDate.setText(formattedDate.toString())
                    } else {
                        Toast.makeText(
                            context,
                            "Product Details Could Not Be Found",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }
                .addOnCanceledListener {
                    // Task canceled
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }

        // Clear the Category Dropdown after user selects it allowing them to edit an items category
        binding.etvCategory.setOnClickListener {
            binding.etvCategory.text.clear()
        }

        // Bring up calender on click
        binding.etvDate.setOnClickListener {
            showDatePicker()
        }

        // Save and input the date after the user selects it from the calendar popup
        datePicker.addOnPositiveButtonClickListener {
            // Handle the selected date
            val selectedDate = Date(it)
            val formattedDate =
                SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selectedDate)
            binding.etvDate.setText(formattedDate)
        }
    }

    // Handle date conversion and formatting
    private fun parseDateString(dateString: String): Date? {
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            // Handle parsing exception
            e.printStackTrace()
            null
        }
    }

    // Function to clear EditText fields
    private fun clearEditTextFields() {
        binding.etvBarcode.setText("")
        binding.etvBrand.setText("")
        binding.etvDescription.setText("")
        binding.etvNote.setText("")
        binding.etvQuantity.setText("")
        binding.etvDate.setText("")
        binding.etvCategory.text.clear()
    }

    // Check for Valid Input
    private fun isValidInput(): Boolean {
        return (binding.etvBarcode.text.toString().isNotEmpty() &&
                binding.etvBrand.text.toString().isNotEmpty() &&
                binding.etvDescription.text.toString().isNotEmpty() &&
                binding.etvNote.text.toString().isNotEmpty() &&
                binding.etvQuantity.text.toString().isNotEmpty() &&
                binding.etvDate.text.toString().isNotEmpty() &&
                binding.etvCategory.text.toString().isNotEmpty())
    }

    // Create new Product
    private fun createProductFromInput(): Product {
        return Product(
            binding.etvBarcode.text.toString().toLong(),
            binding.etvBrand.text.toString(),
            binding.etvDescription.text.toString(),
            binding.etvNote.text.toString(),
            binding.etvQuantity.text.toString().toInt(), // Ensure this value is an Int
            binding.etvCategory.text.toString(),
            parseDateString(binding.etvDate.text.toString())

        )
    }

    // Function to show the DatePicker
    fun showDatePicker() {
        datePicker.show(parentFragmentManager, datePicker.toString())
    }

    // Sets the binding object to null to free up resources
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
