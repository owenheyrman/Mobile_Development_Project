package edu.ap.herexamen_owen_heyrman

import Question
import QuestionAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import edu.ap.herexamen_owen_heyrman.data.FirestoreInstance
import edu.ap.herexamen_owen_heyrman.databinding.FragmentAddExamBinding



class AddExamFragment : Fragment() {

    private var _binding: FragmentAddExamBinding? = null
    private val binding get() = _binding!!

    private val questions = mutableListOf<Question>()
    private val questionAdapter = QuestionAdapter(questions)
    private val db = FirestoreInstance.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddExamBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvQuestions.adapter = questionAdapter

        binding.btnAddQuestion.setOnClickListener {
            showAddQuestionDialog()
        }

        binding.btnSaveExam.setOnClickListener {
            val examTitle = binding.etExamTitle.text.toString().trim()
            if (examTitle.isNotEmpty() && questions.isNotEmpty()) {
                saveExamToFirestore(examTitle, questions)
            } else {
                Toast.makeText(context, "Please enter exam title and add at least one question", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showAddQuestionDialog() {
        // Inflate the dialog view
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_question, null)

        // Find views
        val etQuestionText = dialogView.findViewById<EditText>(R.id.etQuestionText)
        val spinnerQuestionType = dialogView.findViewById<Spinner>(R.id.spinnerQuestionType)
        val layoutOptionsContainer = dialogView.findViewById<LinearLayout>(R.id.layoutOptionsContainer)
        val etOption1 = dialogView.findViewById<EditText>(R.id.etOption1)
        val etOption2 = dialogView.findViewById<EditText>(R.id.etOption2)
        val etCorrectAnswer = dialogView.findViewById<EditText>(R.id.etCorrectAnswer)
        val btnSaveQuestion = dialogView.findViewById<Button>(R.id.btnSaveQuestion)

        // Create the dialog
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Add Question")
            .setView(dialogView)
            .setNegativeButton("Cancel", null)
            .create()

        // Show the dialog
        dialog.show()

        // Handle the question type change
        spinnerQuestionType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val questionType = parent?.getItemAtPosition(position).toString()
                if (questionType == "Multiple Choice") {
                    layoutOptionsContainer.visibility = View.VISIBLE
                } else {
                    layoutOptionsContainer.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Handle saving the question
        btnSaveQuestion.setOnClickListener {
            val questionText = etQuestionText.text.toString().trim()
            val questionType = spinnerQuestionType.selectedItem.toString()
            val correctAnswer = etCorrectAnswer.text.toString().trim()
            val options = mutableListOf<String>()

            if (questionType == "Multiple Choice") {
                val option1 = etOption1.text.toString().trim()
                val option2 = etOption2.text.toString().trim()
                if (option1.isNotEmpty()) options.add(option1)
                if (option2.isNotEmpty()) options.add(option2)
            }

            if (questionText.isNotEmpty()) {
                val question = Question(
                    questionText = questionText,
                    questionType = questionType,
                    options = options,
                    correctAnswer = correctAnswer
                )
                questions.add(question)
                questionAdapter.updateQuestions(questions)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Please enter the question text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveExamToFirestore(title: String, questions: List<Question>) {
        fun Question.toMap(): Map<String, Any> {
            return mapOf(
                "questionText" to questionText,
                "questionType" to questionType,
                "options" to options,
                "correctAnswer" to correctAnswer
            )
        }


        val exam = hashMapOf(
            "title" to title,
            "questions" to questions.map { it.toMap() }
        )
        db.collection("exams").add(exam)
            .addOnSuccessListener {
                Toast.makeText(context, "Exam saved successfully", Toast.LENGTH_SHORT).show()
                binding.etExamTitle.text.clear()
                //questions.clear()

            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to save exam", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}