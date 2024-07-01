package com.sokoldev.budgo.common.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.common.ui.user.LoginActivity
import com.sokoldev.budgo.common.utils.prefs.PreferenceHelper
import com.sokoldev.budgo.databinding.ActivitySplashBinding
import com.sokoldev.budgo.patient.utils.dialog.CustomAgeDialogFragment

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var helper: PreferenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        helper = PreferenceHelper.getPref(this)

        binding.apply {
            getStarted.setOnClickListener {
                getStarted.visibility = View.GONE
                selectRole.visibility = View.VISIBLE
            }
            patient.setOnClickListener {
                helper.setPatientUser(true)
                val dialogFragment = CustomAgeDialogFragment()
                dialogFragment.show(supportFragmentManager, "CustomDialog")
            }
            caregiver.setOnClickListener {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                helper.setPatientUser(false)
                startActivity(intent)
            }

        }

    }
}