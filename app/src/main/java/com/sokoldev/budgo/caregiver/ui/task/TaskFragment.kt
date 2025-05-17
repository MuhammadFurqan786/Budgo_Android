package com.sokoldev.budgo.caregiver.ui.task

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.adapter.TaskAdapter
import com.sokoldev.budgo.caregiver.viewmodels.JobViewModel
import com.sokoldev.budgo.common.data.models.response.Job
import com.sokoldev.budgo.common.data.remote.network.ApiResponse
import com.sokoldev.budgo.common.ui.notification.NotificationActivity
import com.sokoldev.budgo.common.utils.GPSTracker
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys
import com.sokoldev.budgo.databinding.FragmentTaskBinding

class TaskFragment : Fragment(), OnMapReadyCallback, TaskAdapter.ItemClickListener {

    private var _binding: FragmentTaskBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var jobAdapter: TaskAdapter
    private lateinit var mapView: SupportMapFragment
    private lateinit var googleMap: GoogleMap
    private lateinit var helper: PreferenceHelper
    private var latitude: String = ""
    private var longitude: String = ""
    private var isOnline: String = ""


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val taskViewModel =
            ViewModelProvider(this)[JobViewModel::class.java]

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root
        helper = PreferenceHelper.getPref(requireContext())
        isOnline = helper.getStringValue(PreferenceKeys.IS_ONLINE, "Online").toString()
        if (isOnline == "Online") {
            binding.onlineStatus.text = "• Taking Offers"
        } else {
            binding.onlineStatus.text = "• Unavailable"
        }
        binding.notification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        getLatLon()

        mapView = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync(this)

        val token = helper.getStringValue(PreferenceKeys.PREF_USER_TOKEN)

        token?.let { taskViewModel.getNewJobs(it) }
        initObserver(taskViewModel)
        return root
    }

    private fun initObserver(taskViewModel: JobViewModel) {
        taskViewModel.jobsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ApiResponse.Success -> {
//                    binding.loadingView.visibility = View.GONE
                    if (it.data.message != null) {
                        val adapter = TaskAdapter(
                            latitude.toDouble(),
                            longitude.toDouble(),
                            it.data.message,
                            this
                        )
                        binding.rvJobs.adapter = adapter

                    } else {
                        Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show()

                    }
                }

                is ApiResponse.Error -> {
                    Toast.makeText(requireContext(), it.errorMessage, Toast.LENGTH_SHORT).show()
//                    binding.loadingView.visibility = View.GONE
//                    binding.loadingView.hide()
                }

                ApiResponse.Loading -> {
                    Log.d("LoginActivity", "Loading state triggered")
//                    binding.loadingView.visibility = View.VISIBLE
//                    binding.loadingView.show()
                }
            }

        }

    }

    override fun onItemClicked(task: Job) {
        googleMap.clear()
        val latLng =
            LatLng(
                task.userDetails.latitude?.toDouble() ?: 0.0,
                task.userDetails.longitude?.toDouble() ?: 0.0
            )
        googleMap.addMarker(MarkerOptions().position(latLng).title("Task Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        // Enable My Location layer if permissions are granted
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true

            // Move camera to current location
            val currentLatLng = LatLng(latitude.toDouble(), longitude.toDouble())
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
            googleMap.addMarker(MarkerOptions().position(currentLatLng).title("Your Location"))
        }

    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun getLatLon() {
        val gpsTracker = GPSTracker(requireContext())

        if (gpsTracker.isGPSTrackingEnabled) {
            latitude = gpsTracker.getLatitude().toString()
            longitude = gpsTracker.getLongitude().toString()

            helper.saveStringValue(PreferenceKeys.KEY_LATITUDE, latitude)
            helper.saveStringValue(PreferenceKeys.KEY_LONGITUDE, longitude)


        } else {
            gpsTracker.showSettingsAlert()
        }
    }


}