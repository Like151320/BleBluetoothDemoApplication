package com.like.blebluetoothdemoapplication.bleLib

import android.bluetooth.BluetoothDevice
import com.like.blebluetoothdemoapplication.bleLib.bleConnect.AbsBleDevice
import com.like.blebluetoothdemoapplication.bleLib.bleConnect.BleIdBean
import no.nordicsemi.android.ble.WriteRequest

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 14:34
 * 作用:
 */
class StepDevice(device: BluetoothDevice) : AbsBleDevice(device) {

	override val readID: Array<BleIdBean> = arrayOf(
		BleIdBean("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400003-b5a3-f393-e0a9-e50e24dcca9e")
	)
	val stepID =
		BleIdBean("6e400001-b5a3-f393-e0a9-e50e24dcca9e", "6e400002-b5a3-f393-e0a9-e50e24dcca9e")
	override val writeID = arrayOf(stepID)

	fun requestStep(): WriteRequest? {

		return writeData(stepID, *byteArrayOf(1))
	}
}