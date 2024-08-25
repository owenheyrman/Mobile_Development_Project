package edu.ap.herexamen_owen_heyrman

import QuestionAdapter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance
import edu.ap.herexamen_owen_heyrman.databinding.FragmentExamDetailsBinding
import edu.ap.herexamen_owen_heyrman.location.LocationManager
import java.util.concurrent.TimeUnit
import android.Manifest

class ExamDetailsFragment : Fragment() {

    private var _binding: FragmentExamDetailsBinding? = null
    private val binding get() = _binding!!

    private val db = FirestoreInstance.firestore
    private lateinit var locationManager: LocationManager
    private lateinit var viewModel: SharedViewModel
    private lateinit var examAdapter: QuestionAdapter
    private var examStartTime: Long = 0
    private val timerHandler = Handler(Looper.getMainLooper())
    private var timerRunnable: Runnable? = null

    private var locationData: Triple<Double, Double, String>? = null // To store location data

    companion object {
        private const val TAG = "ExamDetailsFragment"
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExamDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated called")

        // Request location permissions if they are not already granted
        Log.d(TAG, "checking if location permissions are already granted")
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "requesting permissions")
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                REQUEST_LOCATION_PERMISSION)
        } else {
            // Permissions are already granted
            Log.d(TAG, "permissions already granted")
        }

        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        locationManager = LocationManager(requireContext())

        val selectedExam = viewModel.selectedExam.value ?: run {
            Log.e(TAG, "No selected exam found")
            return
        }
        val selectedUser = viewModel.selectedUser.value ?: run {
            Log.e(TAG, "No selected user found")
            return
        }

        Log.d(TAG, "Selected exam: ${selectedExam.title}")
        Log.d(TAG, "Selected user: ${selectedUser.id}")

        binding.rvQuestions.layoutManager = LinearLayoutManager(context)
        examAdapter = QuestionAdapter(emptyList()) // Initialize with empty list
        binding.rvQuestions.adapter = examAdapter

        // Fetch exam questions by title
        fetchExamQuestions(selectedExam.title)

        startTimer()

        // Start fetching location data
        fetchLocationData()

        binding.btnSubmit.setOnClickListener {
            Log.d(TAG, "Submit button clicked")
            submitExam(selectedUser, selectedExam)
        }
    }

    private fun fetchLocationData() {
        locationManager.fetchCurrentLocationAndAddress { locationData ->
            // Handle nullable values by providing default values
            val (latitude, longitude, address) = locationData
            val safeLatitude = latitude ?: 0.0 // Default latitude if null
            val safeLongitude = longitude ?: 0.0 // Default longitude if null
            val safeAddress = address ?: "Unknown" // Default address if null

            this.locationData = Triple(safeLatitude, safeLongitude, safeAddress)
            Log.d(TAG, "Location data fetched: ${this.locationData}")
        }
    }


    private fun startTimer() {
        Log.d(TAG, "Starting timer")
        examStartTime = System.currentTimeMillis()
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = System.currentTimeMillis() - examStartTime
                val elapsedTime = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60,
                    TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
                )
                binding.tvTimer.text = elapsedTime
                timerHandler.postDelayed(this, 1000)
            }
        }
        timerHandler.post(timerRunnable!!)
    }

    private fun fetchExamQuestions(title: String) {
        Log.d(TAG, "Fetching exam questions for title: $title")
        db.collection("exams")
            .whereEqualTo("title", title)
            .get()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Fetch exam questions success: ${result.size()} documents found")
                if (result.isEmpty) {
                    Toast.makeText(context, "No exam found with title: $title", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "No exam found with title: $title")
                    return@addOnSuccessListener
                }
                val exam = result.documents.firstOrNull()?.toObject(Exam::class.java)
                if (exam != null) {
                    Log.d(TAG, "Exam fetched: ${exam.questions.size} questions")
                    examAdapter.submitList(exam.questions)
                } else {
                    Toast.makeText(context, "Error fetching exam questions", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Error fetching exam questions: Exam object is null")
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "Error fetching exam questions: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error fetching exam questions", exception)
            }
    }

    private fun submitExam(selectedUser: User, selectedExam: Exam) {
        Log.d(TAG, "Submitting exam for user: ${selectedUser.id}, exam: ${selectedExam.title}")

        // Stop the timer
        timerRunnable?.let {
            timerHandler.removeCallbacks(it)
        }
        Log.d(TAG, "Timer stopped")

        val elapsedMillis = System.currentTimeMillis() - examStartTime
        val duration = String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(elapsedMillis) % 60,
            TimeUnit.MILLISECONDS.toSeconds(elapsedMillis) % 60
        )
        Log.d(TAG, "Exam duration: $duration")

        val (latitude, longitude, address) = locationData ?: Triple(0.0, 0.0, "Unknown") // Default values if location data is not available

        val userResponses = examAdapter.getUserResponses()
        Log.d(TAG, "User responses: $userResponses")
        val score = calculateScore(userResponses)
        Log.d(TAG, "Calculated score: $score")

        val result = hashMapOf(
            "userId" to selectedUser.id,
            "title" to selectedExam.title,
            "score" to score,
            "latitude" to latitude,
            "longitude" to longitude,
            "address" to address,
            "date" to System.currentTimeMillis(),
            "duration" to duration
        )

        db.collection("results").add(result)
            .addOnSuccessListener {
                Toast.makeText(context, "Exam submitted successfully", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Exam submitted successfully")

                // Navigate back to MainActivity
                activity?.finish() // Close current activity
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error submitting exam: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Error submitting exam", e)
            }
    }

    private fun calculateScore(userResponses: Map<String, String>): Int {
        Log.d(TAG, "Calculating score")

        // Get the list of questions and correct answers
        val questionsWithAnswers = viewModel.selectedExam.value?.questions?.associateBy { it.questionText }
            ?.mapValues { it.value.correctAnswer } ?: run {
            Log.e(TAG, "No correct answers found")
            return 0
        }

        // Calculate the number of correct answers provided by the user
        val correctAnswers = userResponses.count { (question, answer) -> questionsWithAnswers[question] == answer }

        // Calculate the total number of questions
        val totalQuestions = questionsWithAnswers.size

        // Calculate the score as a percentage
        val percentageScore = if (totalQuestions > 0) {
            (correctAnswers.toDouble() / totalQuestions * 100).toInt()
        } else {
            0
        }

        Log.d(TAG, "Calculated score: $percentageScore%")
        return percentageScore
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView called")
        _binding = null
        timerRunnable?.let {
            timerHandler.removeCallbacks(it)
        }
    }
}

