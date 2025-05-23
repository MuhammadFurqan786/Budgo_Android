package com.sokoldev.budgo.patient.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.ui.user.UserRegistrationActivity

class CustomAgeDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_age, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle the buttons in the dialog
        view.findViewById<MaterialButton>(R.id.button_no).setOnClickListener {
            // Handle "No" button click
            dismiss()
        }

        view.findViewById<MaterialButton>(R.id.button_yes).setOnClickListener {
            val intent = Intent(requireContext(), UserRegistrationActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        view.findViewById<ImageView>(R.id.cross).setOnClickListener {
            // Handle "cross" button click
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Set dialog properties here if needed
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}
