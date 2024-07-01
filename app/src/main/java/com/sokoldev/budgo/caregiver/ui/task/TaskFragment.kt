package com.sokoldev.budgo.caregiver.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.adapter.TaskAdapter
import com.sokoldev.budgo.caregiver.models.Task
import com.sokoldev.budgo.databinding.FragmentTaskBinding

class TaskFragment : Fragment(), OnMapReadyCallback, TaskAdapter.ItemClickListener {

    private var _binding: FragmentTaskBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var jobAdapter: TaskAdapter
    private lateinit var mapView: SupportMapFragment
    private lateinit var googleMap: GoogleMap


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val taskViewModel =
            ViewModelProvider(this)[TaskViewModel::class.java]

        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mapView = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapView.getMapAsync(this)

        taskViewModel.getJobsList()
        initObserver(taskViewModel)
        return root
    }

    private fun initObserver(taskViewModel: TaskViewModel) {
        taskViewModel.listJobs.observe(viewLifecycleOwner) {
            jobAdapter = TaskAdapter(it, this)
            binding.rvJobs.adapter = jobAdapter
        }
    }

    override fun onItemClicked(task: Task) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(task.latLng).title("Task Location"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(task.latLng, 15f))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
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


}