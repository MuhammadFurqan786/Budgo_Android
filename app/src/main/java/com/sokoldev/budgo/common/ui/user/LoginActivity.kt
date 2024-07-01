package com.sokoldev.budgo.common.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.R
import com.sokoldev.budgo.caregiver.ui.DashboardActivity
import com.sokoldev.budgo.caregiver.ui.user.CaregiverRegistrationActivity
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.databinding.ActivityLoginBinding
import com.sokoldev.budgo.patient.ui.home.HomeActivity
import com.sokoldev.budgo.patient.ui.user.UserRegistrationActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var helper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)
        val isPatientUser = helper.isPatientUser()
        binding.continueButton.setOnClickListener {
            if (isPatientUser) {
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
                startActivity(intent)
            }
        }

        binding.signup.setOnClickListener {
            if (isPatientUser) {
                val intent = Intent(this@LoginActivity, UserRegistrationActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this@LoginActivity, CaregiverRegistrationActivity::class.java)
                startActivity(intent)
            }
        }

    }
}