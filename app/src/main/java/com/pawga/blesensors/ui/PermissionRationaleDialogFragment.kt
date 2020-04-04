package com.pawga.blesensors.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pawga.blesensors.R

/**
 * Created by sivannikov
 */

class PermissionRationaleDialogFragment : DialogFragment(), DialogInterface.OnClickListener {
    private var mRationaleMessage: String? = null
    private var mRequestCode = 0
    private var mPermission: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mPermission = requireArguments().getString(PERMISSION)
            mRequestCode = requireArguments().getInt(REQUEST_CODE)
            mRationaleMessage = requireArguments().getString(RATIONALE_MESSAGE)
        }
    }

    interface PermissionDialogListener {
        fun onRequestPermission(permission: String?, requestCode: Int)
        fun onCancellingPermissionRationale()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val alertDialog: AlertDialog =
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.rationale_title)
                .setMessage(mRationaleMessage)
                .setPositiveButton(R.string.rationale_request, this)
                .setNegativeButton(R.string.rationale_cancel, this).create()
        alertDialog.setCanceledOnTouchOutside(false)
        return alertDialog
    }

    override fun onClick(dialogInterface: DialogInterface?, position: Int) {
        if (position == DialogInterface.BUTTON_POSITIVE) {
            val fragment = parentFragment
            if (fragment != null) {
                (parentFragment as PermissionDialogListener?)!!.onRequestPermission(
                    mPermission,
                    mRequestCode
                )
            } else {
                (requireActivity() as PermissionDialogListener).onRequestPermission(
                    mPermission,
                    mRequestCode
                )
            }
        } else if (position == DialogInterface.BUTTON_NEGATIVE) {
            val fragment = parentFragment
            if (fragment != null) {
                (parentFragment as PermissionDialogListener?)!!.onCancellingPermissionRationale()
            } else {
                (requireActivity() as PermissionDialogListener).onCancellingPermissionRationale()
            }
        }
    }

    companion object {
        private const val PERMISSION = "PERMISSION"
        private const val REQUEST_CODE = "REQUEST_CODE"
        private const val RATIONALE_MESSAGE = "RATIONALE_MESSAGE"

        fun getInstance(
            permission: String?,
            requestCode: Int,
            message: String?
        ) : PermissionRationaleDialogFragment {
            val fragment = PermissionRationaleDialogFragment()
            val bundle = Bundle()
            bundle.putString(PERMISSION, permission)
            bundle.putInt(REQUEST_CODE, requestCode)
            bundle.putString(RATIONALE_MESSAGE, message)
            fragment.arguments = bundle
            return fragment
        }
    }
}
