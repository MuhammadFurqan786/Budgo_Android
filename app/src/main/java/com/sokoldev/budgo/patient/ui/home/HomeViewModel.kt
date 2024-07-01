package com.sokoldev.budgo.patient.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Brand
import com.sokoldev.budgo.patient.models.Category

class HomeViewModel : ViewModel() {

    private val _listCategories: MutableLiveData<List<Category>> = MutableLiveData()
    val listCategory: LiveData<List<Category>>
        get() = _listCategories


    private val _listBrands: MutableLiveData<List<Brand>> = MutableLiveData()
    val listBrands: LiveData<List<Brand>>
        get() = _listBrands


    fun getCategoriesList() {
        val arraylist = ArrayList<Category>()
        for (i in 1..8) {
            arraylist.add(Category(R.drawable.image_1, "Category $i", "Dispensary Name $i"))
        }
        _listCategories.value = arraylist
    }

    fun getBrandsList() {
        val arraylist = ArrayList<Brand>()
        for (i in 1..8) {
            arraylist.add(Brand(R.drawable.image__2, "Category $i"))
        }
        _listBrands.value = arraylist
    }

}