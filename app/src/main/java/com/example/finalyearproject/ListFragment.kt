package com.example.finalyearproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalyearproject.databinding.FragmentListBinding

class ListFragment : Fragment(), RecyclerViewAdapter.OnItemClickListener {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var databaseHandler: DataBaseHandler

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHandler = DataBaseHandler(requireContext())
        setUpRecyclerView()
        setupCategorySpinner()
    }

    // Initialise the RecyclerView and its adapter with products from db
    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val products = databaseHandler.readData().toMutableList()
        val adapter = RecyclerViewAdapter(products, this)
        binding.recyclerView.adapter = adapter
    }

    private fun setupCategorySpinner() {
        // Load categories from resources and convert to mutable list to add "All"
        val categories = resources.getStringArray(R.array.categories).toMutableList()
        // Add "All" at the beginning of the list
        categories.add(0, "All")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        binding.spinnerCategories.adapter = adapter
        binding.spinnerCategories.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedCategory = parent.getItemAtPosition(position) as String
                    // Update your RecyclerView based on the selected category
                    (binding.recyclerView.adapter as? RecyclerViewAdapter)?.filter(selectedCategory)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }


    // Navigate to to ProductDetailsFragment when user clicks product and passes in the barcode with bundle
    override fun onItemClick(product: Product) {
        val bundle = Bundle().apply {
            putLong("barcode", product.barcode)
        }
        findNavController().navigate(R.id.action_global_to_productDetailsFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}