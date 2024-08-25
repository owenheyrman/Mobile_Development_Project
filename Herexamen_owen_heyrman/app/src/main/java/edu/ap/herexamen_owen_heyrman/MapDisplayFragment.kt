package edu.ap.herexamen_owen_heyrman

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.ap.herexamen_owen_heyrman.databinding.FragmentMapDisplayBinding
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapDisplayFragment : Fragment() {

    private var _binding: FragmentMapDisplayBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve latitude and longitude from arguments
        val latitude = arguments?.getDouble(ARG_LATITUDE)
        val longitude = arguments?.getDouble(ARG_LONGITUDE)

        // Set up OSMDroid configuration
        Configuration.getInstance().userAgentValue = "herexamen_owen_heyrman"

        // Initialize the map view
        binding.mapView.setMultiTouchControls(true)
        val mapController = binding.mapView.controller

        if (latitude != null && longitude != null) {
            val geoPoint = GeoPoint(latitude, longitude)
            mapController.setZoom(15.0)
            mapController.setCenter(geoPoint)

            // Add a marker to the map
            val marker = Marker(binding.mapView)
            marker.position = geoPoint
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            binding.mapView.overlays.add(marker)

            binding.mapView.invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_LATITUDE = "latitude"
        private const val ARG_LONGITUDE = "longitude"

        fun newInstance(latitude: Double, longitude: Double): MapDisplayFragment {
            val fragment = MapDisplayFragment()
            val args = Bundle().apply {
                putDouble(ARG_LATITUDE, latitude)
                putDouble(ARG_LONGITUDE, longitude)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
