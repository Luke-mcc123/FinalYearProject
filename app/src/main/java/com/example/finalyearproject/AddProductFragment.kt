package com.example.finalyearproject

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalyearproject.databinding.FragmentAddProductBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.Charset

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"


class AddProductFragment : Fragment() {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Context provides access to the applications resources to the DataBaseHandler class
        // This allows it to perform database operations within the current fragment
        val context = requireContext()
        val db = DataBaseHandler(context)

        // Insert inputted data into the database
        binding.btnInsert.setOnClickListener {
            if (binding.etvBarcode.text.toString().isNotEmpty() &&
                binding.etvBrand.text.toString().isNotEmpty() &&
                binding.etvType.text.toString().isNotEmpty() &&
                binding.etvNote.text.toString().isNotEmpty() &&
                binding.etvQuantity.text.toString().isNotEmpty()
            ) {
                val product = Product(
                    binding.etvBarcode.text.toString().toInt(),
                    binding.etvBrand.text.toString(),
                    binding.etvType.text.toString(),
                    binding.etvNote.text.toString(),
                    binding.etvQuantity.text.toString().toInt()
                )
                db.insertData(product)

                // Clear the text fields after input
                binding.etvBarcode.setText("")
                binding.etvBrand.setText("")
                binding.etvType.setText("")
                binding.etvNote.setText("")
                binding.etvQuantity.setText("")
            } else {
                Toast.makeText(context, "Please Fill In All Fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Iterates through the database and appends rows to the tvResult
        binding.btnRead.setOnClickListener {
            val data = db.readData()
            binding.tvResult.text = ""
            for (i in 0 until data.size) {
                binding.tvResult.append(
                    "${data[i].barcode} | ${data[i].brand} | ${data[i].type} | " +
                            "${data[i].note} | ${data[i].quantity}\n"
                )
            }

        }

//        // Updates the quantity of products
//        binding.btnUpdate.setOnClickListener {
//            db.updateData()
//            binding.btnRead.performClick()
//        }

        binding.btnUpdate.setOnClickListener {
            if (binding.etvBarcode.text.toString().isNotEmpty() &&
                binding.etvBrand.text.toString().isNotEmpty() &&
                binding.etvType.text.toString().isNotEmpty() &&
                binding.etvNote.text.toString().isNotEmpty() &&
                binding.etvQuantity.text.toString().isNotEmpty()
            ) {
                val product = Product(
                    binding.etvBarcode.text.toString().toInt(),
                    binding.etvBrand.text.toString(),
                    binding.etvType.text.toString(),
                    binding.etvNote.text.toString(),
                    binding.etvQuantity.text.toString().toInt()
                )
                db.updateProductData(product)

                // Clear the text fields after input
                binding.etvBarcode.setText("")
                binding.etvBrand.setText("")
                binding.etvType.setText("")
                binding.etvNote.setText("")
                binding.etvQuantity.setText("")

            }else {
                Toast.makeText(context, "Please input Barcode of product and the updated details", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnDelete.setOnClickListener {
//            db.deleteData()
//            binding.btnRead.performClick()
            if (binding.etvBarcode.text.toString().isNotEmpty()
            ) {
                val product = Product(
                    binding.etvBarcode.text.toString().toInt(),
                    binding.etvBrand.text.toString() ,
                    binding.etvType.text.toString(),
                    binding.etvNote.text.toString(),
                    binding.etvQuantity.text.toString().toIntOrNull()
                )
                db.deleteProductData(product)

                // Clear the text fields after input
                binding.etvBarcode.setText("")
                binding.etvBrand.setText("")
                binding.etvType.setText("")
                binding.etvNote.setText("")
                binding.etvQuantity.setText("")

            }else {
                Toast.makeText(context, "Please input Barcode of product to Delete", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnScan.setOnClickListener {
            // Configure Barcode Scanner
            val options = GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_QR_CODE)
                .enableAutoZoom() // available on 16.1.0 and higher
                .build()

            val scanner = GmsBarcodeScanning.getClient(context, options)
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    val rawValue: String? = barcode.rawValue

                    // Call the searchInCSV function with the rawValue
                    val result = searchInCSV(requireContext(), rawValue ?: "")

                    if (result != null) {
                        val (foundBarcode, description) = result
                        // Match found in the CSV file
                        Toast.makeText(context, "Success! Barcode: $foundBarcode, Description: $description", Toast.LENGTH_SHORT).show()
                        binding.etvBarcode.setText(foundBarcode)
                        binding.etvNote.setText(description)
                    } else {
                        // No match found in the CSV file
                        Toast.makeText(context, "No match found in CSV.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnCanceledListener {
                    // Task canceled
                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                }
        }


    }
    // Add this function to your AddProductFragment class
// Modify the searchInCSV function to return a Pair<String, String> representing barcode and description
    private fun searchInCSV(context: Context, rawValue: String): Pair<String, String>? {
        try {
            // Open the CSV file from the Raw Resource folder
            val inputStream = context.resources.openRawResource(R.raw.upc_corpus) // Replace 'your_csv_file' with the actual CSV file name

            // Read the CSV file line by line
            BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8"))).use { reader ->
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    // Check if the rawValue is present in the CSV file
                    if (line?.contains(rawValue) == true) {
                        // Split the line into barcode and description
                        val values = line?.split(",")

                        // Ensure there are at least two values (barcode and description)
                        if (values?.size ?: 0 >= 2) {
                            val barcode = values?.get(0)?.trim() ?: ""
                            val description = values?.get(1)?.trim() ?: ""

                            // Return a Pair representing barcode and description
                            return Pair(barcode, description)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Handle any exceptions that might occur while reading the CSV file
            Toast.makeText(context, "Error searching in CSV: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        // No match found or error occurred
        return null
    }

    // Sets the binding object to null to free up resources
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
