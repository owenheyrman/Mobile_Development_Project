package edu.ap.herexamen_owen_heyrman

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance
import edu.ap.herexamen_owen_heyrman.databinding.FragmentExamListBinding

class ExamListFragment : Fragment() {

    private var _binding: FragmentExamListBinding? = null
    private val binding get() = _binding!!

    private val examAdapter by lazy { ExamAdapter(this::onExamSelected) }

    private fun onExamSelected(exam: Exam) {
        Log.d("ExamListFragment", "Exam selected: ${exam.title}")

        val viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        viewModel.selectedExam.value = exam

        (activity as? UserFlowActivity)?.navigateToUserListFragment()
    }

    private val db = FirestoreInstance.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExamListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvExams.layoutManager = LinearLayoutManager(context)
        binding.rvExams.adapter = examAdapter
        fetchExams()
    }

    private fun fetchExams() {
        Log.d("ExamListFragment", "Fetching exams from Firestore")

        db.collection("exams")
            .get()
            .addOnSuccessListener { result ->
                val exams = mutableListOf<Exam>()
                for (document in result) {
                    val exam = document.toObject(Exam::class.java)
                    exams.add(exam)
                }
                examAdapter.submitList(exams)
                Log.d("ExamListFragment", "Exams fetched successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("ExamListFragment", "Error fetching exams", exception)
                Toast.makeText(context, "Error fetching exams: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("ExamListFragment", "onDestroyView called")
        _binding = null
    }
}
