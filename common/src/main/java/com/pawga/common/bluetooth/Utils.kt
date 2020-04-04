package com.pawga.common.bluetooth

import android.app.Activity
import android.widget.Toast

/**
 * Created by sivannikov
 */

const val REQUEST_ENABLE_BT = 1020
const val REQUEST_ACCESS_COARSE_LOCATION = 1021
const val REQUEST_ACCESS_FINE_LOCATION = 1022
const val REQ_PERMISSION_WRITE_EXTERNAL_STORAGE = 1023
const val REQ_PERMISSION_READ_EXTERNAL_STORAGE = 1024
const val REQ_PERMISSION_RECORD_AUDIO = 1024
const val EXTRA_DATA_TITLE = "EXTRA_DATA_TITLE"
const val EXTRA_DATA = "EXTRA_DATA"

fun showToast(activity: Activity?, message: String?) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}