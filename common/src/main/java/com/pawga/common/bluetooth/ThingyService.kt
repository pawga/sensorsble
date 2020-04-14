package com.pawga.common.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.Intent
import no.nordicsemi.android.thingylib.BaseThingyService
import no.nordicsemi.android.thingylib.ThingyConnection

/**
 * Created by sivannikov
 */

class ThingyService : BaseThingyService() {

    inner class ThingyBinder : BaseThingyBinder() {
        override fun getThingyConnection(device: BluetoothDevice): ThingyConnection {
            return mThingyConnections[device]!!
        }
    }

    override fun onBind(intent: Intent?): BaseThingyBinder? {
        return ThingyBinder()
    }
}