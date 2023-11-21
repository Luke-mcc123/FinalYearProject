package com.example.finalyearproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalyearproject.databinding.FragmentAddProductBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddProductFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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

            // Clear the text fields after input
            binding.etvBarcode.setText("")
            binding.etvBrand.setText("")
            binding.etvType.setText("")
            binding.etvNote.setText("")
            binding.etvQuantity.setText("")
        }

        // Updates the quantity of products
        binding.btnUpdate.setOnClickListener {
            db.updateData()
            binding.btnRead.performClick()
        }

        // Deletes Table
        binding.btnDelete.setOnClickListener {
            db.deleteData()
            binding.btnRead.performClick()
        }
    }

    // Sets the binding object to null to free up resources
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
