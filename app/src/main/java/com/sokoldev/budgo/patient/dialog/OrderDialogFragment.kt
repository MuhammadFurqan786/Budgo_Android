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
import com.sokoldev.budgo.caregiver.ui.task.FeedbackActivity
import com.sokoldev.budgo.common.utils.prefs.PreferenceKeys

class OrderDialogFragment : DialogFragment() {

    private var otherUserId: String? = null
    private var bookingId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            otherUserId = it.getString(ARG_OTHER_USER_ID)
            bookingId = it.getString(ARG_BOOKING_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_order_done, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<MaterialButton>(R.id.button_yes).setOnClickListener {

            val intent = Intent(activity, FeedbackActivity::class.java).apply {
                putExtra("otherUserId", otherUserId)
                putExtra(PreferenceKeys.BOOKING_ID, bookingId)
            }
            startActivity(intent)
            dismiss()
        }

        view.findViewById<ImageView>(R.id.cross).setOnClickListener {
            dismiss()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }

    companion object {
        private const val ARG_OTHER_USER_ID = "otherUserId"
        private const val ARG_BOOKING_ID = "bookingId"

        fun newInstance(otherUserId: String, bookingId: String): OrderDialogFragment {
            val fragment = OrderDialogFragment()
            val args = Bundle().apply {
                putString(ARG_OTHER_USER_ID, otherUserId)
                putString(ARG_BOOKING_ID, bookingId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}

