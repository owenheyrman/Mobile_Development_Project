// ExamAdapter.kt
package edu.ap.herexamen_owen_heyrman

import Question
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ap.herexamen_owen_heyrman.databinding.ItemExamBinding

import android.os.Parcel
import android.os.Parcelable

data class Exam(
    val title: String = "",
    val questions: List<Question> = emptyList()
)



class ExamAdapter(private val onExamSelected: (Exam) -> Unit) :
    RecyclerView.Adapter<ExamAdapter.ExamViewHolder>() {

    private var examList = listOf<Exam>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamViewHolder {
        val binding = ItemExamBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExamViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExamViewHolder, position: Int) {
        val exam = examList[position]
        holder.bind(exam)
    }

    override fun getItemCount(): Int = examList.size

    fun submitList(exams: List<Exam>) {
        examList = exams
        notifyDataSetChanged()
    }

    inner class ExamViewHolder(private val binding: ItemExamBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(exam: Exam) {
            binding.tvExamTitle.text = exam.title
            // No need to set description as it is no longer part of the Exam class
            binding.root.setOnClickListener {
                onExamSelected(exam)
            }
        }
    }
}