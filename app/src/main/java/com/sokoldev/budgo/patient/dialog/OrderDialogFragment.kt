package com.sokoldev.budgo.patient.utils.dialog

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
import com.sokoldev.budgo.caregiver.ui.task.FeedbackActivity

class OrderDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_order_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle the buttons in the dialog

        view.findViewById<MaterialButton>(R.id.button_yes).setOnClickListener {
            startActivity(Intent(activity, FeedbackActivity::class.java))
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
