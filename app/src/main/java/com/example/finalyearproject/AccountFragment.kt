package com.example.finalyearproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.finalyearproject.databinding.FragmentAccountBinding
import com.example.finalyearproject.databinding.FragmentAddProductBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Context provides access to the applications resources to the DataBaseHandler class
        // This allows it to perform database operations within the current fragment
        val context = requireContext()
        val db = DataBaseHandler(context)

        // Register New User
        binding.btnRegister.setOnClickListener{
            if(binding.etvUsername.text.toString().isNotEmpty() &&
                binding.etvPassword.text.toString().isNotEmpty()&&
                binding.etvRole.text.toString().isNotEmpty()
                ) {
                val user = User(
                    binding.etvUsername.text.toString(),
                    binding.etvPassword.text.toString(),
                    binding.etvRole.text.toString()
                )
                db.insertUserData(user)
            } else {
                Toast.makeText(context, "Please Fill in All fields", Toast.LENGTH_SHORT).show()
            }
            // Read User
            // TESTING PURPOSES ONLY
//            val data = db.readUserData()
//            binding.tvUserResult.text = ""
//            for (i in 0 until data.size) {
//                binding.tvUserResult.append(
//                    "${data[i].username} | ${data[i].password} | ${data[i].role}\n"
//                )
//            }
        }

        binding.btnLogin.setOnClickListener {
            val username = binding.etvUsername.text.toString()
            val password = binding.etvPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                if (db.checkUserCredentials(username, password)) {
                    // User credentials are valid
                    Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                    // Add code to navigate to the next screen or perform the necessary actions
                } else {
                    // Invalid credentials
                    Toast.makeText(context, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

    }
}