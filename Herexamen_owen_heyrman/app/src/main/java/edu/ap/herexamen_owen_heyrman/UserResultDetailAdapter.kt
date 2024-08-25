package edu.ap.herexamen_owen_heyrman

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
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
        private val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        private val tlExamDetails: TableLayout = view.findViewById(R.id.tlExamDetails)

        fun bind(examResultDetail: ExamResultDetail) {
            tvUserName.text = "${examResultDetail.userDetails[0].firstName} ${examResultDetail.userDetails[0].lastName}"

            // Clear previous details
            tlExamDetails.removeAllViews()

            // Add header row programmatically
            val headerRow = TableRow(itemView.context)
            val headers = listOf("Title", "Score %", "Map")
            for (header in headers) {
                val tvHeader = TextView(itemView.context)
                tvHeader.text = header
                tvHeader.setPadding(8, 8, 8, 8)
                tvHeader.setTypeface(null, Typeface.BOLD)
                headerRow.addView(tvHeader)
            }
            tlExamDetails.addView(headerRow)

            // Add user exam details to the table
            examResultDetail.userDetails.forEach { userExamDetail ->
                val tableRow = TableRow(itemView.context)

                // Title
                val tvTitle = TextView(itemView.context)
                tvTitle.text = examResultDetail.title
                tvTitle.setPadding(8, 8, 8, 8)
                tableRow.addView(tvTitle)

                // Score
                val tvScore = TextView(itemView.context)
                tvScore.text = "${userExamDetail.score}%"
                tvScore.setPadding(8, 8, 8, 8)
                tableRow.addView(tvScore)

                // Map Button
                val btnShowOnMap = Button(itemView.context)
                btnShowOnMap.text = "Show on Map"
                btnShowOnMap.setPadding(8, 8, 8, 8)
                btnShowOnMap.setOnClickListener {
                    onShowMapClick(userExamDetail) // Invoke the callback
                }
                tableRow.addView(btnShowOnMap)

                tlExamDetails.addView(tableRow)
            }
        }
    }

}
