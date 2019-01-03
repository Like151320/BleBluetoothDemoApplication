package com.like.blebluetoothdemoapplication.bleLib.bleConnect

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import com.like.blebluetoothdemoapplication.MLog

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 14:24
 * 作用: BLE连接管理总类
 */
object BleConnectManager {
	private var mContext: Application? = null

	/**管理中的设备*/
	val bleManagers = mutableMapOf<String, BleDeviceManager<out AbsBleDevice>>()

	/**使用前必须初始化*/
	fun initialize(context: Application) {
		this.mContext = context
	}

	/**注册管理蓝牙设备*/
	fun <D : AbsBleDevice> registerBleDevice(bleDevice: D): BleDeviceManager<D> {
		//前提检测
		if (!BluetoothAdapter.checkBluetoothAddress(bleDevice.device.address)) {
			error("不能连接不合法的地址 ${bleDevice.device.address}")
		} else if (mContext == null) {
			error("Manager未初始化,无法连接")
		}
		//加入Manager
		if (!bleManagers.contains(bleDevice.device.address)) {
			bleManagers[bleDevice.device.address] =
					BleDeviceManager(mContext!!, bleDevice)
		}

		return bleManagers[bleDevice.device.address] as BleDeviceManager<D>
	}

	/**解除蓝牙管理*/
	fun removeManager(address: String?) {
		//前提检测
		if (address != null && !BluetoothAdapter.checkBluetoothAddress(address)) {
			error("不能移除不合法的地址 $address")
		}
		//移除
		if (address != null) {
			bleManagers.remove(address)?.run {
				disconnect()
					.fail { device, status ->
						MLog.d("设备断开失败-${device.address}-$status")
					}.done {
						MLog.d("设备已断开")
					}.enqueue()
				null
			}
		} else {

			//全移除
			for (key in HashSet(bleManagers.keys)) {
				removeManager(key)
			}
		}
	}


	fun <D : AbsBleDevice> getBleDevice(address: String, clazz: Class<D>): D? {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)

		return if (device == null) null
		else getBleDevice(device, clazz)
	}

	fun <D : AbsBleDevice> getBleDevice(device: BluetoothDevice, clazz: Class<D>): D {
		val constructor = clazz.getConstructor(BluetoothDevice::class.java)
		val bleDevice = constructor.newInstance(device)
		return registerBleDevice(bleDevice).bleDevice
	}
}