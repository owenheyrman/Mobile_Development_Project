// AddUserFragment.kt
package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import edu.ap.herexamen_owen_heyrman.databinding.FragmentAddUserBinding
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance

class AddUserFragment : Fragment() {

    private var _binding: FragmentAddUserBinding? = null
    private val binding get() = _binding!!

    private val db = FirestoreInstance.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnAddUser.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
                addUserToFirestore(firstName, lastName)

                Toast.makeText(context, "User added: $firstName $lastName", Toast.LENGTH_SHORT).show()
                binding.etFirstName.text.clear()
                binding.etLastName.text.clear()
            } else {
                Toast.makeText(context, "Please enter both first and last name", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBulkAdd.setOnClickListener {
            val bulkUsers = binding.etBulkUsers.text.toString().trim()
            if (bulkUsers.isNotEmpty()) {
                addBulkUsersToFirestore(bulkUsers)
                binding.etBulkUsers.text.clear()
            } else {
                Toast.makeText(context, "Please enter user details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun addUserToFirestore(firstName: String, lastName: String) {
        // Generate a new document with a unique ID
        val userRef = db.collection("users").document()

        // Create a User object with the Firestore ID
        val user = User(
            id = userRef.id,
            firstName = firstName,
            lastName = lastName
        )

        // Add user to Firestore
        userRef.set(user)
            .addOnSuccessListener {
                Toast.makeText(context, "User added: $firstName $lastName", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error adding user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }



    private fun addBulkUsersToFirestore(bulkUsers: String) {
        // Split the bulk input by lines
        val users = bulkUsers.lines()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { line ->
                val parts = line.split(" ")
                if (parts.size >= 2) {
                    Pair(parts[0], parts[1])
                } else {
                    null
                }
            }
            .filterNotNull()

        // Add each user to Firestore
        for ((firstName, lastName) in users) {
            addUserToFirestore(firstName, lastName)
        }

        Toast.makeText(context, "Bulk users added", Toast.LENGTH_SHORT).show()
    }

}
