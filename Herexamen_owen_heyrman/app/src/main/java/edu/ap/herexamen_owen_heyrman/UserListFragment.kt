// UserListFragment.kt
package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance
import edu.ap.herexamen_owen_heyrman.databinding.FragmentUserListBinding

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val userAdapter by lazy { UserAdapter(this::onUserSelected) }
    private val db = FirestoreInstance.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvUsers.layoutManager = LinearLayoutManager(context)
        binding.rvUsers.adapter = userAdapter

        fetchUsers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchUsers() {
        db.collection("users").get()
            .addOnSuccessListener { result ->
                val users = result.map { document ->
                    User(
                        id = document.id,
                        firstName = document.getString("firstName") ?: "",
                        lastName = document.getString("lastName") ?: ""
                    )
                }
                userAdapter.submitList(users)
            }
            .addOnFailureListener { e ->
                // Handle the error
            }
    }

    private fun onUserSelected(user: User) {
        // Save selected user in ViewModel
        val viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel.selectedUser.value = user

        // Navigate to ExamDetailsFragment
        val activity = activity as? UserFlowActivity
        (activity as? UserFlowActivity)?.navigateToExamDetailsFragment()
    }
}