package com.sokoldev.budgo.patient.ui.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.models.Brand
import com.sokoldev.budgo.patient.models.Product

class MenuViewModel : ViewModel() {

    private val _listCategories: MutableLiveData<List<Brand>> = MutableLiveData()
    val listCategory: LiveData<List<Brand>>
        get() = _listCategories


    private val _listNewProduct: MutableLiveData<List<Product>> = MutableLiveData()
    val listNewProduct: LiveData<List<Product>>
        get() = _listNewProduct


    private val _listEdibles: MutableLiveData<List<Product>> = MutableLiveData()
    val listEdibles: LiveData<List<Product>>
        get() = _listEdibles


    fun getCategoriesList() {
        val arraylist = ArrayList<Brand>()
        for (i in 1..8) {
            arraylist.add(Brand(R.drawable.image_1, "Category $i"))
        }
        _listCategories.value = arraylist
    }

    fun getNewProductList() {
        val arraylist = ArrayList<Product>()
        for (i in 1..7) {
            arraylist.add(
                Product(
                    R.drawable.image__2,
                    "Product $i",
                    50,
                    "Hybrid",
                    "20.20%",
                    "20.20%",
                    "Dispensary Name $i"
                )
            )
        }
        _listNewProduct.value = arraylist
    }

    fun getEdibles() {
        val arraylist = ArrayList<Product>()
        for (i in 1..8) {
            arraylist.add(
                Product(
                    R.drawable.image_1,
                    "Product $i",
                    50,
                    "Hybrid",
                    "20.20%",
                    "20.20%",
                    "Dispensary Name $i"
                )
            )
        }
        _listEdibles.value = arraylist
    }







}