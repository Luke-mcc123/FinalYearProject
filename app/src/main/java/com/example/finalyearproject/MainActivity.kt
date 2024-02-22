package com.example.finalyearproject
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.finalyearproject.databinding.ActivityMainBinding
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // Variables for deferred navigation
    private var pendingNavigationAction: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        // Wait for application lifecycle to be in a state to conduct navigation
        lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            fun onResumed() {
                pendingNavigationAction?.invoke()
                pendingNavigationAction = null // Clear the action after executing
            }
        })

        setupBottomNavigation()
    }

    // Logic for scan button in Bottom Navigation to open ML Kit Google Scanner
    private fun setupBottomNavigation() {
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.scanFragment -> {
                    startBarcodeScanner()
                    true
                }
                else -> NavigationUI.onNavDestinationSelected(item, navController)
            }
        }
    }


    // Barcode Scanner
    private fun startBarcodeScanner() {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
        val scanner = GmsBarcodeScanning.getClient(this, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Define navigation based on barcode scanning result
                val navigateAction = {
                    val dbHandler = DataBaseHandler(this)
                    val product = dbHandler.getProductByBarcode(barcode.rawValue!!.toLong())

                    if (product != null) {
                        // If product exists, navigate to ProductDetailsFragment
                        val bundle = Bundle().apply {
                            putLong("barcode", product.barcode)
                        }
                        navController.navigate(R.id.action_global_to_productDetailsFragment, bundle)
                    } else {
                        // If product does not exist, navigate to AddProductFragment
                        val bundle = Bundle().apply {
                            putString("barcode", barcode.rawValue)
                        }
                        navController.navigate(R.id.action_global_to_addProductFragment, bundle)
                    }
                }

                // Check if the activity is currently resumed
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    navigateAction()
                } else {
                    // If not resumed, defer the navigation action
                    pendingNavigationAction = navigateAction
                }
            }
            .addOnFailureListener { e ->
                // Handle failure
                Toast.makeText(this, "Scan failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
            .addOnCanceledListener {
                // Handle cancellation
                Toast.makeText(this, "Scan was cancelled", Toast.LENGTH_SHORT).show()
            }
    }

    // No Longer Used
    private fun processScannedBarcode(barcode: String) {
        val dbHandler = DataBaseHandler(this)
        val productExists = dbHandler.getProductByBarcode(barcode.toLong()) != null

        if (productExists) {
            val bundle = Bundle().apply { putString("barcode", barcode) }
            navController.navigate(R.id.action_global_to_productDetailsFragment, bundle)
        } else {
            val bundle = Bundle().apply { putString("barcode", barcode) }
            navController.navigate(R.id.action_global_to_addProductFragment, bundle)
        }
    }
}