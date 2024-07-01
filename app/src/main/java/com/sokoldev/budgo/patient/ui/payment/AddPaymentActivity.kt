package com.sokoldev.budgo.patient.ui.payment

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sokoldev.budgo.databinding.ActivityAddPaymentBinding

class AddPaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPaymentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        binding.apply {
            addPaymentButton.setOnClickListener {
                layoutNoMethod.visibility = View.GONE
                layoutMethod.visibility = View.VISIBLE
                addCard.visibility = View.VISIBLE
            }
            arrowMasterCard.setOnClickListener {
                addVisaCardLayout.visibility = View.GONE
                addMasterCardLayout.visibility = View.VISIBLE
            }

            arrowVisaCard.setOnClickListener {
                addMasterCardLayout.visibility = View.GONE
                addVisaCardLayout.visibility = View.VISIBLE
            }

            addCard.setOnClickListener {
                startActivity(Intent(this@AddPaymentActivity, MakePaymentActivity::class.java))
            }


        }
    }
}