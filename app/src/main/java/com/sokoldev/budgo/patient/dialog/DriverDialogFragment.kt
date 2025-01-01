package com.sokoldev.budgo.patient.utils.dialog

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.imageview.ShapeableImageView
import com.sokoldev.budgo.R
import com.sokoldev.budgo.patient.ui.order.BookingDetailsActivity

class DriverDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_driver_verification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val crossImage: ImageView = view.findViewById(R.id.cross)
        val userImage: ShapeableImageView = view.findViewById(R.id.image)
        val name: AppCompatTextView = view.findViewById(R.id.name)
        val driverId: AppCompatTextView = view.findViewById(R.id.driver_id)
        val location: AppCompatTextView = view.findViewById(R.id.location)
        val contact: AppCompatTextView = view.findViewById(R.id.contact)
        val gotoDohButton: AppCompatButton = view.findViewById(R.id.goto_doh_website)
        val checkTackingFalse: AppCompatButton = view.findViewById(R.id.checkTackingFalse)
        val checkTacking: AppCompatButton = view.findViewById(R.id.checkTacking)
        val allDoneButton: LinearLayout = view.findViewById(R.id.allDone)


        gotoDohButton.setOnClickListener {
            gotoDohButton.visibility = View.GONE
            allDoneButton.visibility = View.VISIBLE
            checkTackingFalse.visibility = View.GONE
            checkTacking.visibility = View.VISIBLE
        }


        checkTacking.setOnClickListener {
            val intent = Intent(requireContext(), BookingDetailsActivity::class.java)
            startActivity(intent)
            dismiss()
        }

        crossImage.setOnClickListener {
            // Handle "cross" button click
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Set dialog properties here if needed
        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_white_rounded_all)
        return dialog
    }
}
