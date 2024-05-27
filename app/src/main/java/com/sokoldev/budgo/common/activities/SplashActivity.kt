package com.sokoldev.budgo.common.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.MainActivity
import com.sokoldev.budgo.databinding.ActivitySplashBinding
import com.sokoldev.budgo.patient.utils.dialog.CustomAgeDialogFragment

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
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

        binding.apply {
            getStarted.setOnClickListener {
                getStarted.visibility = View.GONE
                selectRole.visibility = View.VISIBLE
            }
            patient.setOnClickListener {
                val dialogFragment = CustomAgeDialogFragment()
                dialogFragment.show(supportFragmentManager, "CustomDialog")
            }
            caregiver.setOnClickListener {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }

        }

    }
}