import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.ap.herexamen_owen_heyrman.databinding.ItemQuestionBinding

data class Question(
    val questionText: String = "",
    val questionType: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = ""
)

class QuestionAdapter(
    private var questions: List<Question>
) : RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder>() {

    private val userResponses = mutableMapOf<String, String>()

    inner class QuestionViewHolder(private val binding: ItemQuestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(question: Question) {
            binding.tvQuestionText.text = question.questionText
            binding.tvQuestionType.text = when (question.questionType) {
                "Multiple Choice" -> "Multiple Choice"
                "Open-Ended" -> "Open-Ended"
                else -> "Unknown Type"
            }

            if (question.questionType == "Multiple Choice") {
                binding.optionsContainer.visibility = View.VISIBLE
                binding.optionsContainer.removeAllViews()
                val radioGroup = RadioGroup(binding.root.context).apply {
                    orientation = RadioGroup.VERTICAL
                }
                binding.optionsContainer.addView(radioGroup)

                question.options.forEach { option ->
                    val radioButton = RadioButton(binding.root.context).apply {
                        text = option
                        setOnClickListener {
                            userResponses[question.questionText] = option
                        }
                    }
                    radioGroup.addView(radioButton)
                }
            } else if (question.questionType == "Open-Ended") {
                binding.optionsContainer.visibility = View.VISIBLE
                binding.optionsContainer.removeAllViews()
                val editText = EditText(binding.root.context).apply {
                    hint = "Your answer"
                    addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                            // No action needed here
                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            userResponses[question.questionText] = s.toString()
                        }

                        override fun afterTextChanged(s: Editable?) {
                            // No action needed here
                        }
                    })
                }
                binding.optionsContainer.addView(editText)
            } else {
                // Hide the options container if the type is unknown
                binding.optionsContainer.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val binding = ItemQuestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questions[position])
    }

    override fun getItemCount(): Int {
        return questions.size
    }

    fun updateQuestions(newQuestions: List<Question>) {
        questions = newQuestions
        notifyDataSetChanged()
    }

    fun getUserResponses(): Map<String, String> {
        return userResponses
    }

    fun submitList(newQuestions: List<Question>) {
        this.questions = newQuestions
        notifyDataSetChanged()
    }
}
