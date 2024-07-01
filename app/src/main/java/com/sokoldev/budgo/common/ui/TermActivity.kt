package com.sokoldev.budgo.common.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.caregiver.ui.DashboardActivity
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.databinding.ActivityTermBinding
import com.sokoldev.budgo.patient.ui.home.HomeActivity

class TermActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermBinding
    private lateinit var helper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTermBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)
        val isPatientUser = helper.isPatientUser()


        binding.continueButton.setOnClickListener {
            if (isPatientUser) {
                val intent = Intent(this@TermActivity, HomeActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@TermActivity, DashboardActivity::class.java)
                startActivity(intent)
            }
        }
    }
}