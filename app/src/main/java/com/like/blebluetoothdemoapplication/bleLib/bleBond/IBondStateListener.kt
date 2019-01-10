package com.like.blebluetoothdemoapplication.bleLib.bleBond

import android.bluetooth.BluetoothDevice
import android.content.Context

/**
 * 作者: Li_ke
 * 日期: 2019/1/9 14:14
 * 作用: 配对状态改变监听
 */
interface IBondStateListener {
	fun onBondStateChanged(
		context: Context,
		device: BluetoothDevice,
		previousBondState: Int,
		bondState: Int
	)
}