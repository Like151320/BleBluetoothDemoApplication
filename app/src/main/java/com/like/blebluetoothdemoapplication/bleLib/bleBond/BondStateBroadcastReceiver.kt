package com.like.blebluetoothdemoapplication.bleLib.bleBond

import android.bluetooth.BluetoothDevice

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.like.blebluetoothdemoapplication.MLog
import java.util.*
/**
 * 作者: Li_ke
 * 日期: 2018/12/22 0022 14:02
 * 作用:
 */
class BondStateBroadcastReceiver : BroadcastReceiver() {

	private val mBondListeners = HashSet<IBondStateListener>()


	override fun onReceive(context: Context, intent: Intent) {
		val action = intent.action
		when (action) {
			BluetoothDevice.ACTION_BOND_STATE_CHANGED -> {
				//配对的设备
				val device =
					intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
				//新的配对状态
				val bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, 0)
				//上次的配对状态
				val previousBondState =
					intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, 0)

				MLog.d(
					"配对状态-${device.address}-" +
							"${parseBondState(previousBondState)}->${parseBondState(bondState)}"
				)

				//通知配对监听
				for (listener in HashSet(mBondListeners)) {//防止循环时操作集合出错
					listener.onBondStateChanged(context, device, previousBondState, bondState)
				}
			}
		}
	}

	fun addBondListener(iBondStateListener: IBondStateListener) {
		mBondListeners.add(iBondStateListener)
	}

	fun removeBondListener(iBondStateListener: IBondStateListener) {
		mBondListeners.remove(iBondStateListener)
	}

	companion object {
		fun parseBondState(bondState: Int): String {
			return when (bondState) {
				BluetoothDevice.BOND_BONDING -> "配对中"
				BluetoothDevice.BOND_BONDED -> "配对已成功"
				BluetoothDevice.BOND_NONE -> "配对已解除"
				else -> "不是配对状态! $bondState"
			}
		}
	}
}
