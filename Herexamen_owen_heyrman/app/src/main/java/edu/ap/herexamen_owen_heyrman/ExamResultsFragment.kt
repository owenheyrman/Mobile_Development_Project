package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.Tasks
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance
import edu.ap.herexamen_owen_heyrman.databinding.FragmentExamResultsBinding
import com.google.firebase.firestore.FirebaseFirestore

class ExamResultsFragment : Fragment() {

    private var _binding: FragmentExamResultsBinding? = null
    private val binding get() = _binding!!

    private val db = FirestoreInstance.firestore
    private lateinit var resultsAdapter: ResultsAdapter

    companion object {
        private const val TAG = "ExamResultsFragment"
        private const val ARG_VIEW_MODE = "view_mode"
        fun newInstance(viewMode: String): ExamResultsFragment {
            val fragment = ExamResultsFragment()
            val args = Bundle()
            args.putString(ARG_VIEW_MODE, viewMode)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExamResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvResults.layoutManager = LinearLayoutManager(context)
        resultsAdapter = ResultsAdapter(emptyList())
        binding.rvResults.adapter = resultsAdapter

        val viewMode = arguments?.getString(ARG_VIEW_MODE)
        Log.d(TAG, "View mode received: $viewMode")

        when (viewMode) {
            "by_user" -> fetchResultsByUser()
            "by_exam" -> fetchResultsByExam()
            else -> {
                Toast.makeText(context, "Invalid view mode", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Invalid view mode: $viewMode")
            }
        }
    }

    private fun fetchResultsByUser() {
        Log.d(TAG, "Fetching results by user")
        db.collection("results").get()
            .addOnSuccessListener { result ->
                val userIds = result.documents.mapNotNull { it.getString("userId") }.distinct()
                val userResults = mutableListOf<UserResult>()

                val firestore = FirebaseFirestore.getInstance()
                val userTasks = userIds.map { userId ->
                    firestore.collection("users").document(userId).get()
                        .continueWith { task ->
                            val userDocument = task.result
                            val user = userDocument?.toObject(User::class.java)
                            val examScores = result.documents.filter { it.getString("userId") == userId }
                                .map { doc ->
                                    ExamScore(
                                        title = doc.getString("title") ?: "Unknown Title",
                                        score = (doc.getLong("score") ?: 0).toInt()
                                    )
                                }
                            user?.let {
                                userResults.add(
                                    UserResult(
                                        userId = user.id,
                                        firstName = user.firstName,
                                        lastName = user.lastName,
                                        results = examScores
                                    )
                                )
                            }
                        }
                }

                // Wait for all user data fetches to complete
                Tasks.whenAllSuccess<Void>(*userTasks.toTypedArray()).addOnCompleteListener {
                    resultsAdapter.submitList(userResults)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching results: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching results", exception)
            }
    }

    private fun fetchResultsByExam() {
        Log.d(TAG, "Fetching results by exam")
        db.collection("results").get()
            .addOnSuccessListener { result ->
                val examResults = result.documents.groupBy { it.getString("title") }
                    .map { (title, documents) ->
                        val userExamDetails = documents.map { doc ->
                            UserExamDetail(
                                userId = doc.getString("userId") ?: "Unknown User",
                                firstName = "", // To be populated after fetching user details
                                lastName = "",
                                score = (doc.getLong("score") ?: 0).toInt(),
                                address = doc.getString("address") ?: "Unknown Address",
                                duration = doc.getString("duration") ?: "Unknown Duration"
                            )
                        }
                        ExamResultDetail(title ?: "Unknown Exam", userExamDetails)
                    }

                val firestore = FirebaseFirestore.getInstance()
                val userIds = examResults.flatMap { it.userDetails }.map { it.userId }.distinct()

                // Fetch user details for each userId
                val userDetailsTasks = userIds.map { userId ->
                    firestore.collection("users").document(userId).get()
                        .continueWith { task ->
                            val userDocument = task.result
                            val user = userDocument?.toObject(User::class.java)
                            user?.let {
                                examResults.forEach { examResult ->
                                    examResult.userDetails.forEach { userDetail ->
                                        if (userDetail.userId == userId) {
                                            userDetail.firstName = user.firstName
                                            userDetail.lastName = user.lastName
                                        }
                                    }
                                }
                            }
                        }
                }

                // Wait for all user detail fetches to complete
                Tasks.whenAllSuccess<Void>(*userDetailsTasks.toTypedArray()).addOnCompleteListener {
                    resultsAdapter.submitList(examResults)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching results: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching results", exception)
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
