package edu.ap.herexamen_owen_heyrman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.util.GeoPoint

class UserResultDetailAdapter(
    private val onShowMapClick: (UserExamDetail) -> Unit // Callback function
) : RecyclerView.Adapter<UserResultDetailAdapter.UserResultDetailViewHolder>() {

    private val examResults = mutableListOf<ExamResultDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserResultDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_result_detail, parent, false)
        return UserResultDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserResultDetailViewHolder, position: Int) {
        holder.bind(examResults[position])
    }

    override fun getItemCount(): Int = examResults.size

    fun submitList(results: List<ExamResultDetail>) {
        examResults.clear()
        examResults.addAll(results)
        notifyDataSetChanged()
    }

    inner class UserResultDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvExamTitle: TextView = view.findViewById(R.id.tvExamTitle)
        private val tvExamDetails: TextView = view.findViewById(R.id.tvExamDetails)
        private val btnShowOnMap: Button = view.findViewById(R.id.btnShowOnMap) // Rename to reflect actual button

        fun bind(examResultDetail: ExamResultDetail) {
            tvExamTitle.text = "Exam: ${examResultDetail.title}"
            val detailsText = examResultDetail.userDetails.joinToString("\n") {
                "Name: ${it.firstName} ${it.lastName}, Score: ${it.score}%"
            }
            tvExamDetails.text = detailsText

            btnShowOnMap.setOnClickListener {
                examResultDetail.userDetails.forEach { userExamDetail ->
                    onShowMapClick(userExamDetail) // Invoke the callback
                }
            }
        }
    }
}
