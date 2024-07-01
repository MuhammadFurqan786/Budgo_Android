package com.sokoldev.budgo.caregiver.ui.user

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.R
import com.sokoldev.budgo.common.ui.TermActivity
import com.sokoldev.budgo.databinding.ActivityCaregiverRegistrationBinding

class CaregiverRegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCaregiverRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityCaregiverRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.continueButton.setOnClickListener {
            val intent = Intent(this@CaregiverRegistrationActivity, TermActivity::class.java)
            startActivity(intent)
        }

    }
}