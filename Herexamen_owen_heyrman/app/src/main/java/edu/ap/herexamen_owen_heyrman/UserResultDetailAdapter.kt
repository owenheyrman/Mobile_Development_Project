package edu.ap.herexamen_owen_heyrman

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class UserResultDetailAdapter(
    private val onShowMapClick: (UserExamDetail) -> Unit
) : RecyclerView.Adapter<UserResultDetailAdapter.UserResultDetailViewHolder>() {

    private val userExamDetails = mutableListOf<UserExamDetail>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserResultDetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user_result_detail, parent, false)
        return UserResultDetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserResultDetailViewHolder, position: Int) {
        holder.bind(userExamDetails[position])
    }

    override fun getItemCount(): Int {
        return userExamDetails.size
    }

    fun submitList(results: List<UserExamDetail>) {
        userExamDetails.clear()
        userExamDetails.addAll(results)
        notifyDataSetChanged()
    }

    inner class UserResultDetailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvExamTitle: TextView = view.findViewById(R.id.tvExamTitle)
        private val tvScore: TextView = view.findViewById(R.id.tvScore)
        private val tvAddress: TextView = view.findViewById(R.id.tvAddress)
        private val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        private val btnShowMap: Button = view.findViewById(R.id.btnRenderMap)
        private val mapView: MapView = view.findViewById(R.id.mapView)

        private var examLocation: GeoPoint? = null

        init {
            btnShowMap.setOnClickListener {
                if (mapView.visibility == View.GONE) {
                    mapView.visibility = View.VISIBLE
                    mapView.setMultiTouchControls(true)
                    mapView.controller.setZoom(15.0)

                    examLocation?.let {
                        val marker = Marker(mapView)
                        marker.position = it
                        marker.title = "Exam Location"
                        mapView.overlays.add(marker)
                        mapView.controller.setCenter(it)
                    }
                    mapView.invalidate()
                } else {
                    mapView.visibility = View.GONE
                }
            }
        }

        fun bind(userExamDetail: UserExamDetail) {
            tvExamTitle.text = "Exam: ${userExamDetail.firstName} ${userExamDetail.lastName}"
            tvScore.text = "Score: ${userExamDetail.score}%"
            tvAddress.text = "Address: ${userExamDetail.address}"
            tvDuration.text = "Duration: ${userExamDetail.duration}"

            examLocation = userExamDetail.location?.let { GeoPoint(it.latitude, it.longitude) }
        }

        // MapView lifecycle methods
        fun onResume() {
            mapView.onResume()
        }

        fun onPause() {
            mapView.onPause()
        }

    }
}
