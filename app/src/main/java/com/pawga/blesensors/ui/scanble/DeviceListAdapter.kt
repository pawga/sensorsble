package com.pawga.blesensors.ui.scanble

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pawga.blesensors.R
import com.pawga.blesensors.model.ExtendedBluetoothDevice
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

/**
 * Created by sivannikov
 */

class DeviceListAdapter : BaseAdapter() {

    private val norssi = -1000

    private val mDevices: MutableList<ExtendedBluetoothDevice> =
        ArrayList<ExtendedBluetoothDevice>()

    override fun getCount(): Int {
        return if (mDevices.isEmpty()) 2 else mDevices.size + 1 // 1 for title, 1 for empty text
    }

    override fun getItem(position: Int): Any {
        return if (position == 0) R.string.scanner_subtitle_not_bonded else mDevices[position - 1]
    }

    override fun getViewTypeCount(): Int {
        return 3
    }

    override fun areAllItemsEnabled(): Boolean {
        return false
    }

    override fun isEnabled(position: Int): Boolean {
        return getItemViewType(position) == TYPE_ITEM
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_TITLE
        return if (position == count - 1 && mDevices.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        oldView: View?,
        parent: ViewGroup
    ): View? {
        val inflater = LayoutInflater.from(parent.context)
        val type = getItemViewType(position)
        var view = oldView
        when (type) {
            TYPE_EMPTY -> if (view == null) {
                view = inflater.inflate(R.layout.device_list_empty, parent, false)
            }
            TYPE_TITLE -> {
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_title, parent, false)
                }
                val title = view as TextView?
                title?.setText((getItem(position) as Int))
            }
            else -> {
                if (view == null) {
                    view = inflater.inflate(R.layout.device_list_row, parent, false)
                    val holder = ViewHolder()
                    holder.name = view.findViewById(R.id.name)
                    holder.address = view.findViewById(R.id.address)
                    holder.thingy = view.findViewById(R.id.icon_view)
                    holder.rssi = view.findViewById(R.id.rssi)
                    view.tag = holder
                }
                val device = getItem(position)
                        as ExtendedBluetoothDevice? ?: return null
                val holder =
                    view?.tag as ViewHolder? ?: return null

                val name = device.name
                holder.name?.text = name ?: parent.context.getString(R.string.not_available)
                holder.address?.text = device.device.address
                if (device.rssi != norssi) {
                    val rssiPercent =
                        (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f)).toInt()
                    holder.rssi?.setImageLevel(rssiPercent)
                    holder.rssi?.visibility = View.VISIBLE
                } else {
                    holder.rssi?.visibility = View.GONE
                    holder.thingy?.visibility = View.GONE
                }
            }
        }
        return view
    }

    fun update(results: List<ScanResult>) {
        for (result in results) {
            val device: ExtendedBluetoothDevice? = findDevice(result)
            if (device == null) {
                mDevices.add(ExtendedBluetoothDevice(result))
            } else {
                device.name = if (result.scanRecord != null) result.scanRecord!!
                    .deviceName else null
                device.rssi = result.rssi
            }
        }
        notifyDataSetChanged()
    }

    fun clearDevices() {
        mDevices.clear()
        notifyDataSetChanged()
    }

    private fun findDevice(result: ScanResult): ExtendedBluetoothDevice? {
        for (device in mDevices) if (device.matches(result)) return device
        return null
    }

    private inner class ViewHolder {
        var name: TextView? = null
        var address: TextView? = null
        var rssi: ImageView? = null
        var thingy: ImageView? = null
    }

    companion object {
        private const val TYPE_TITLE = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_EMPTY = 2
    }
}