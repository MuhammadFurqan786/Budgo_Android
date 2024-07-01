package com.sokoldev.budgo.caregiver.ui.task

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.models.Task

class TaskViewModel : ViewModel() {

    private val _listJobs: MutableLiveData<List<Task>> = MutableLiveData()
    val listJobs: LiveData<List<Task>>
        get() = _listJobs


    fun getJobsList() {
        val arraylist = ArrayList<Task>()
        arraylist.add(
            Task(
                R.drawable.image_1,
                50,
                "Customer",
                "Product ",
                "1.2 miles",
                2,
                LatLng(40.7580, -73.9855)
            )
        )
        arraylist.add(
            Task(
                R.drawable.image__2,
                100,
                "Customer 2",
                "Product 2 ",
                "1.2 miles",
                4,
                LatLng(40.7851, -73.9683)
            )
        )
        _listJobs.value = arraylist
    }

}