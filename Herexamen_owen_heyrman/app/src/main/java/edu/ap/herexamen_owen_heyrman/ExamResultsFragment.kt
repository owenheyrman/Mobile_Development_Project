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
import com.google.firebase.firestore.DocumentSnapshot
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance
import edu.ap.herexamen_owen_heyrman.databinding.FragmentExamResultsBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

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
        // Set a custom user agent string only if the map is used
        if (arguments?.getString(ARG_VIEW_MODE) == "by_user") {
            Configuration.getInstance().userAgentValue = "herexamen_owen_heyrman(owen.heyrman@student.ap.be)"
        }

        _binding = FragmentExamResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewMode = arguments?.getString(ARG_VIEW_MODE)
        Log.d(TAG, "View mode received: $viewMode")

        if (viewMode == "by_user") {
            binding.mapView.visibility = View.VISIBLE
            binding.mapView.setMultiTouchControls(true)
            val mapController = binding.mapView.controller
            val startPoint = GeoPoint(51.05, 3.7) // Default coordinates (example)
            mapController.setZoom(15.0)
            mapController.setCenter(startPoint)

            // Add a marker
            val marker = Marker(binding.mapView)
            marker.position = startPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.mapView.overlays.add(marker)
        } else {
            binding.mapView.visibility = View.GONE
        }

        binding.rvResults.layoutManager = LinearLayoutManager(context)
        resultsAdapter = ResultsAdapter(emptyList(), viewMode == "by_user", ::onRenderMapClick)
        binding.rvResults.adapter = resultsAdapter

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
        db.collection("results")
            .get()
            .addOnSuccessListener { result ->
                val userResultsMap = result.documents.groupBy { it.getString("userId") }
                val userResults = mutableListOf<UserResult>()

                val userDetailTasks = userResultsMap.map { (userId, documents) ->
                    val userRef = db.collection("users").document(userId ?: "Unknown")
                    userRef.get().addOnSuccessListener { userSnapshot ->
                        val firstName = userSnapshot.getString("firstName") ?: "Unknown"
                        val lastName = userSnapshot.getString("lastName") ?: "Unknown"
                        val examScores = documents.map { doc ->
                            ExamScore(
                                title = doc.getString("title") ?: "Unknown Title",
                                score = (doc.getLong("score") ?: 0).toInt()
                            )
                        }
                        val userResult = UserResult(
                            userId = userId ?: "Unknown",
                            firstName = firstName,
                            lastName = lastName,
                            results = examScores
                        )
                        userResults.add(userResult)

                        // Submit the list only after processing all users
                        if (userResults.size == userResultsMap.size) {
                            resultsAdapter.submitList(userResults)
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching results: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching results", exception)
            }
    }


    private fun fetchResultsByExam() {
        Log.d(TAG, "Fetching results by exam")
        db.collection("results")
            .get()
            .addOnSuccessListener { result ->
                // Group documents by exam title
                val groupedDocuments = result.documents.groupBy { it.getString("title") }
                val examResultDetails = mutableListOf<ExamResultDetail>()

                val tasks = groupedDocuments.map { (title, documents) ->
                    // Fetch user details for each exam's documents
                    val userDetailTasks = documents.map { doc ->
                        val userId = doc.getString("userId") ?: "Unknown"
                        db.collection("users").document(userId).get()
                    }

                    // Wait for all user detail tasks to complete
                    Tasks.whenAllSuccess<DocumentSnapshot>(userDetailTasks)
                        .addOnSuccessListener { userSnapshots ->
                            val userDetails = userSnapshots.map { userSnapshot ->
                                val userId = userSnapshot.id
                                UserExamDetail(
                                    userId = userId,
                                    firstName = userSnapshot.getString("firstName") ?: "Unknown",
                                    lastName = userSnapshot.getString("lastName") ?: "Unknown",
                                    score = documents.find { it.getString("userId") == userId }
                                        ?.getLong("score")?.toInt() ?: 0,
                                    address = documents.find { it.getString("userId") == userId }
                                        ?.getString("address") ?: "Unknown Address",
                                    duration = documents.find { it.getString("userId") == userId }
                                        ?.getString("duration") ?: "Unknown Duration"
                                )
                            }
                            // Add the details to the list
                            val examResultDetail = ExamResultDetail(
                                title = title ?: "Unknown Exam",
                                userDetails = userDetails
                            )
                            examResultDetails.add(examResultDetail)

                            // Check if this is the last exam
                            if (examResultDetails.size == groupedDocuments.size) {
                                resultsAdapter.submitList(examResultDetails)
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.e(TAG, "Error fetching user details", exception)
                            Toast.makeText(context, "Error fetching user details", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching results: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching results", exception)
            }
    }

    private fun onRenderMapClick(userExamDetail: UserExamDetail) {
        // Clear previous markers
        binding.mapView.overlays.clear()

        // Extract location from the userExamDetail
        val address = userExamDetail.address
        val location = fetchLocationFromAddress(address) // Implement this method

        // Add a new marker if location is found
        location?.let {
            val marker = Marker(binding.mapView)
            marker.position = it
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.mapView.overlays.add(marker)

            // Center the map on the marker
            val mapController = binding.mapView.controller
            mapController.setZoom(15.0)
            mapController.setCenter(it)
        } ?: run {
            Toast.makeText(context, "Unable to find location for address: $address", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchLocationFromAddress(address: String): GeoPoint? {
        // Implement a method to fetch GeoPoint from address
        // This might involve using a geocoding API
        // For now, return null
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d(TAG, "onDestroyView called")
    }
}
