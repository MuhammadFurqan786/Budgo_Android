package com.sokoldev.budgo.patient.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.ActivityHomeBinding


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_activity_home) as NavHostFragment?
        val navCo = navHostFragment!!.navController

        navView.setupWithNavController(navCo)

        val radius = resources.getDimension(R.dimen.radius_small)
        val bottomNavigationViewBackground = navView.background as MaterialShapeDrawable
        bottomNavigationViewBackground.shapeAppearanceModel =
            bottomNavigationViewBackground.shapeAppearanceModel.toBuilder()
                .setTopRightCorner(CornerFamily.ROUNDED, radius)
                .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                .build()


        val destination = intent.getIntExtra("destination", R.id.navigation_home)
        if (navView.menu.findItem(destination) != null) {
            navView.selectedItemId = destination
        } else {
            navView.selectedItemId = R.id.navigation_home
        }


    }
}