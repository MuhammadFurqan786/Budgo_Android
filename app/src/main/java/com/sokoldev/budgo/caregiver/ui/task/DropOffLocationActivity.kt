package com.sokoldev.budgo.caregiver.ui.task

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.R
import com.sokoldev.budgo.databinding.ActivityDropOffLocationBinding


class DropOffLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDropOffLocationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDropOffLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener { finish() }
        binding.continueButton.setOnClickListener {
            startActivity(Intent(this@DropOffLocationActivity, ToDoActivity::class.java))
        }
    }
}