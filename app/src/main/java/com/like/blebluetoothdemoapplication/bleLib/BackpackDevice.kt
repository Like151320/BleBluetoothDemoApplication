package com.like.blebluetoothdemoapplication.bleLib

import android.bluetooth.BluetoothDevice
import com.like.blebluetoothdemoapplication.MLog
import com.like.blebluetoothdemoapplication.bleLib.bleConnect.AbsBleDevice
import com.like.blebluetoothdemoapplication.bleLib.bleConnect.BleIdBean

/**
 * 作者: Li_ke
 * 日期: 2019/1/2 11:51
 * 作用:
 */
class BackpackDevice(device: BluetoothDevice) : AbsBleDevice(device) {
	override val readID: Array<BleIdBean>
		get() = arrayOf()
	override val writeID: Array<BleIdBean>
		get() = arrayOf(
			BleIdBean(
				"0000ffe0-0000-1000-8000-00805f9b34fb", "0000ffe1-0000-1000-8000-00805f9b34fb"
			)
		)

	fun testSend1() {
		writeData(writeID[0], 0, 1)
			?.fail { device, status ->
				MLog.d("发送失败-$status")
			}?.done {
				MLog.d("发送成功")
			}?.enqueue()
	}
}