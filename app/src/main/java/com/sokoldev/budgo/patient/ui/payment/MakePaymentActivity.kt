package com.sokoldev.budgo.patient.ui.payment

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.sokoldev.budgo.databinding.ActivityMakePaymentBinding
import com.sokoldev.budgo.patient.adapter.PaymentMethodAdapter
import com.sokoldev.budgo.patient.utils.dialog.PaymentDialogFragment

class MakePaymentActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMakePaymentBinding
    private lateinit var viewModel: PaymentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMakePaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[PaymentViewModel::class.java]
        viewModel.getPaymentMethods()
        intiObserver()

    }

    private fun intiObserver() {
        viewModel.listPaymentMethod.observe(this){
            val adapter =  PaymentMethodAdapter(it)
            binding.rvPayment.adapter = adapter

            binding.proceedButton.setOnClickListener {
                val selectedItem = adapter.getSelectedItem()
                if (selectedItem != null) {
                    val dialogFragment = PaymentDialogFragment(this)
                    dialogFragment.show(supportFragmentManager, "CustomDialog")
                    Toast.makeText(this, "Selected: ${selectedItem.cardName}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}