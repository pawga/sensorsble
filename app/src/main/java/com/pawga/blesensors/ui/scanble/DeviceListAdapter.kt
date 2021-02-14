package com.pawga.blesensors.ui.scanble

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pawga.blesensors.R
import com.pawga.blesensors.extensions.inflate
import com.pawga.blesensors.model.ExtendedBluetoothDevice
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.ArrayList

/**
 * Created by sivannikov
 */
class DeviceListAdapter(private val viewModel: ScanViewModel) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val norssi = -1000
    private val devices: MutableList<ExtendedBluetoothDevice> = ArrayList<ExtendedBluetoothDevice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_ITEM -> return ViewHolderItem(parent.inflate(R.layout.device_list_row, false))
            TYPE_TITLE -> return ViewHolderTitle(parent.inflate(R.layout.device_list_title, false))
            else -> ViewHolderEmpty(parent.inflate(R.layout.device_list_empty, false))
        }
    }

    override fun getItemCount(): Int {
        return if (devices.isEmpty()) 2 else devices.size + 1 // 1 for title, 1 for empty text
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            val viewHolder = holder as? ViewHolderItem ?: return
            val device = devices[position - 1]
            viewHolder.name.text = device.name ?: viewHolder.view.context.getString(R.string.not_available)
            viewHolder.address.text = device.device.address
            viewHolder.extendedBluetoothDevice = device
            if (device.rssi != norssi) {
                val rssiPercent =
                    (100.0f * (127.0f + device.rssi) / (127.0f + 20.0f)).toInt()
                viewHolder.rssi.setImageLevel(rssiPercent)
                viewHolder.rssi.visibility = View.VISIBLE
            } else {
                viewHolder.rssi.visibility = View.GONE
                viewHolder.thingy.visibility = View.GONE
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return TYPE_TITLE
        return if (position == itemCount - 1 && devices.isEmpty()) TYPE_EMPTY else TYPE_ITEM
    }

    fun update(results: List<ScanResult>) {
        for (result in results) {
            val device: ExtendedBluetoothDevice? = findDevice(result)
            if (device == null) {
                devices.add(ExtendedBluetoothDevice(result))
            } else {
                device.name = if (result.scanRecord != null) result.scanRecord!!
                    .deviceName else null
                device.rssi = result.rssi
            }
        }
        notifyDataSetChanged()
    }

    fun clearDevices() {
        devices.clear()
        notifyDataSetChanged()
    }

    private fun findDevice(result: ScanResult): ExtendedBluetoothDevice? {
        for (device in devices) if (device.matches(result)) return device
        return null
    }

    inner class ViewHolderItem(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.name)
        val address: TextView = view.findViewById(R.id.address)
        val rssi: ImageView = view.findViewById(R.id.rssi)
        val thingy: ImageView = view.findViewById(R.id.icon_view)
        var extendedBluetoothDevice: ExtendedBluetoothDevice? = null

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val device = extendedBluetoothDevice?.device ?: return
            this@DeviceListAdapter.viewModel.selectBluetoothDevice(device)
        }
    }

    class ViewHolderEmpty(view: View) : RecyclerView.ViewHolder(view)
    class ViewHolderTitle(view: View) : RecyclerView.ViewHolder(view)

    companion object {
        private const val TYPE_TITLE = 0
        private const val TYPE_ITEM = 1
        private const val TYPE_EMPTY = 2
    }
}
