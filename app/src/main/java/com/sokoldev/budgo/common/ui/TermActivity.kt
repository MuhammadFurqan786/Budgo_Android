package com.sokoldev.budgo.common.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.databinding.ActivityTermBinding
import com.sokoldev.budgo.patient.ui.home.HomeActivity

class TermActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermBinding
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

        binding.continueButton.setOnClickListener {
            val intent = Intent(this@TermActivity, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}