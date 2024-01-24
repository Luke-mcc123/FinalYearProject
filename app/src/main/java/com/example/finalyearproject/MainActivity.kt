package com.example.finalyearproject
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.finalyearproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // Create variables to allow us manipulate the navController and ActivityMainBinding
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
//    val db = DataBaseHandler(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Force lightmode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        //Inflate Activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Navigation Controller for the Bottom Navigation Bar
        // Navigation Bar code adapted from: https://androidgeek.co/how-to-create-a-bottom-navigation-bar-in-kotlin-in-depth-guide-cd6876e94441
        navController = Navigation.findNavController(this, R.id.activity_main_nav_host_fragment)
        setupWithNavController(binding.bottomNavigationView, navController)
        setSupportActionBar(findViewById(R.id.my_toolbar))

    }
}