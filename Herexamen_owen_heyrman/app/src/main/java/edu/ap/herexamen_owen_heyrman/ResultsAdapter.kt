package edu.ap.herexamen_owen_heyrman

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

// Define ViewTypes
private const val VIEW_TYPE_USER_RESULT = 0
private const val VIEW_TYPE_EXAM_RESULT_DETAIL = 1

// Define data classes for the results
data class ExamScore(
    val title: String = "",
    val score: Int = 0
)

data class UserResult(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val results: List<ExamScore> = emptyList()
)

data class UserExamDetail(
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val score: Int = 0,
    val address: String = "",
    val duration: String = "",
    val location: GeoPoint? = null,
    val title: String = ""
)

data class ExamResultDetail(
    val title: String = "",
    val userDetails: List<UserExamDetail> = emptyList()
)

class ResultsAdapter(
    private var results: List<ExamResultDetail>
) : RecyclerView.Adapter<ResultsAdapter.ExamResultDetailViewHolder>() {

    inner class ExamResultDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvExamTitle: TextView = view.findViewById(R.id.tvExamTitle)
        private val tlExamDetails: TableLayout = view.findViewById(R.id.tlExamDetails)

        fun bind(examResultDetail: ExamResultDetail) {
            tvExamTitle.text = examResultDetail.title
            tlExamDetails.removeAllViews()

            // Add the header row programmatically
            val headerRow = TableRow(itemView.context)
            headerRow.setBackgroundColor(ContextCompat.getColor(itemView.context, android.R.color.darker_gray)) // Light gray background for headers

            val headers = listOf("Name", "Score %", "Address", "Duration")
            for (header in headers) {
                val tvHeader = TextView(itemView.context)
                tvHeader.text = header
                tvHeader.setPadding(8, 8, 8, 8)
                tvHeader.setTypeface(null, Typeface.BOLD)
                headerRow.addView(tvHeader)
            }
            tlExamDetails.addView(headerRow)

            // Add user details to the table
            for (userDetail in examResultDetail.userDetails) {
                val tableRow = TableRow(itemView.context)

                // Name
                val tvName = TextView(itemView.context)
                tvName.text = "${userDetail.firstName} ${userDetail.lastName}"
                tvName.setPadding(8, 8, 8, 8)
                tableRow.addView(tvName)

                // Score
                val tvScore = TextView(itemView.context)
                tvScore.text = "${userDetail.score}%"
                tvScore.setPadding(8, 8, 8, 8)
                tableRow.addView(tvScore)

                // Address
                val tvAddress = TextView(itemView.context)
                tvAddress.text = userDetail.address
                tvAddress.setPadding(8, 8, 8, 8)
                tableRow.addView(tvAddress)

                // Duration
                val tvDuration = TextView(itemView.context)
                tvDuration.text = userDetail.duration
                tvDuration.setPadding(8, 8, 8, 8)
                tableRow.addView(tvDuration)

                tlExamDetails.addView(tableRow)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExamResultDetailViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ExamResultDetailViewHolder(layoutInflater.inflate(R.layout.item_exam_result_detail, parent, false))
    }

    override fun onBindViewHolder(holder: ExamResultDetailViewHolder, position: Int) {
        holder.bind(results[position])
    }

    override fun getItemCount(): Int = results.size

    fun submitList(newResults: List<ExamResultDetail>) {
        results = newResults
        notifyDataSetChanged()
    }
}