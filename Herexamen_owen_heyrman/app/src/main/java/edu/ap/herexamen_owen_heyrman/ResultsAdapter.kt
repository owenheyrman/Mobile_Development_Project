package edu.ap.herexamen_owen_heyrman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
    val location: GeoPoint? = null // Added location property
)

data class ExamResultDetail(
    val title: String = "",
    val userDetails: List<UserExamDetail> = emptyList()
)

class ResultsAdapter(
    private var results: List<Any>,
    private val showRenderMapButton: Boolean,
    private val onRenderMapClick: (UserExamDetail) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class UserResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvName: TextView = view.findViewById(R.id.tvUserName)
        private val tvResults: TextView = view.findViewById(R.id.tvExamResults)

        fun bind(userResult: UserResult) {
            tvName.text = "${userResult.firstName} ${userResult.lastName}"
            tvResults.text = userResult.results.joinToString("\n") { "${it.title}: ${it.score}" }
        }
    }

    inner class ExamResultDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvExamTitle: TextView = view.findViewById(R.id.tvExamTitle)
        private val tvExamDetails: TextView = view.findViewById(R.id.tvExamDetails)
        private val btnRenderMap: Button = view.findViewById(R.id.btnRenderMap)

        init {
            // Set the visibility of the button based on the mode
            btnRenderMap.visibility = if (showRenderMapButton) View.VISIBLE else View.GONE
        }

        fun bind(examResultDetail: ExamResultDetail) {
            tvExamTitle.text = "Exam: ${examResultDetail.title}"
            tvExamDetails.text = examResultDetail.userDetails.joinToString("\n") {
                "Name: ${it.firstName} ${it.lastName}, Score: ${it.score}, Address: ${it.address}, Duration: ${it.duration}"
            }

            btnRenderMap.setOnClickListener {
                // Trigger the callback when the button is clicked
                examResultDetail.userDetails.firstOrNull()?.let { userExamDetail ->
                    onRenderMapClick(userExamDetail)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_USER_RESULT -> UserResultViewHolder(layoutInflater.inflate(R.layout.item_user_result, parent, false))
            VIEW_TYPE_EXAM_RESULT_DETAIL -> ExamResultDetailViewHolder(layoutInflater.inflate(R.layout.item_exam_result_detail, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is UserResultViewHolder -> holder.bind(results[position] as UserResult)
            is ExamResultDetailViewHolder -> holder.bind(results[position] as ExamResultDetail)
        }
    }

    override fun getItemCount(): Int = results.size

    override fun getItemViewType(position: Int): Int {
        return when (results[position]) {
            is UserResult -> VIEW_TYPE_USER_RESULT
            is ExamResultDetail -> VIEW_TYPE_EXAM_RESULT_DETAIL
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    fun submitList(newResults: List<Any>) {
        results = newResults
        notifyDataSetChanged()
    }
}
