package com.pawga.blesensors.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pawga.blesensors.R
import com.pawga.common.bluetooth.EXTRA_DATA
import com.pawga.common.bluetooth.EXTRA_DATA_TITLE

/**
 * Created by sivannikov
 */

class MessageDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        if (arguments != null) {
            alertDialogBuilder.setIcon(R.drawable.ic_warning_grey)
            alertDialogBuilder.setTitle(requireArguments().getString(EXTRA_DATA_TITLE))
            alertDialogBuilder.setMessage(requireArguments().getString(EXTRA_DATA))
        }
        alertDialogBuilder.setPositiveButton(getString(R.string.ok), null)
        return alertDialogBuilder.create()
    }

    companion object {
        fun newInstance(title: String?, message: String?): MessageDialogFragment {
            val fragment = MessageDialogFragment()
            val args = Bundle()
            args.putString(EXTRA_DATA_TITLE, title)
            args.putString(EXTRA_DATA, message)
            fragment.arguments = args
            return fragment
        }
    }
}
