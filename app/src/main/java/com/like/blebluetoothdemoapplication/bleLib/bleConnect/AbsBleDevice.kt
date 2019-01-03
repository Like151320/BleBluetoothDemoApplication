package com.like.blebluetoothdemoapplication.bleLib.bleConnect

import android.bluetooth.BluetoothDevice
import com.like.blebluetoothdemoapplication.MLog
import no.nordicsemi.android.ble.WriteRequest

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 14:25
 * 作用: BLE设备基类,扩展(继承)此类以自定义交互协议
 * ID - 在连接时会检测,蓝牙设备内必须包含此类中所有 SID、CID
 *
 * 不要使用直接创建的[AbsBleDevice],使用[BleConnectManager.getBleDevice] 或 [BleConnectManager.registerBleDevice] 得到的对象
 *
 * 假设账户有6个设备,那么运行时应有6个[BleDeviceManager]实例,每个实例管理一个设备的连接操作,
 * 	和6个[AbsBleDevice]实例,每个对象代表一个设备,
 *
 * 注意：先初始化[BleConnectManager.initialize]
 */
abstract class AbsBleDevice(val device: BluetoothDevice) {

	/**读取的 CID - SID, 连接后会监听这些CID的讯息*/
	abstract val readID: Array<BleIdBean>
	/**写入的 CID - SID*/
	abstract val writeID: Array<BleIdBean>
	/**连接状态*/
	var isConnected = false
	/**此设备的连接管理*/
	protected val bleManager: BleDeviceManager<out AbsBleDevice>
		get() {
			//使用时自动去注册
			val deviceManager = BleConnectManager.bleManagers[device.address]
			if (deviceManager == null)//尚未注册 -> 注册
				BleConnectManager.registerBleDevice(this)
			val manager = BleConnectManager.bleManagers[device.address]!!

			if (manager.bleDevice != this)
				error("不要使用直接创建的 AbsBleDevice!，使用 BleConnectManager.getBleDevice 使其保持唯一")
			return manager
		}

	var notifyCallback = mutableSetOf<((cid: String, value: ByteArray) -> Unit)?>()

	/**创建连接*/
	open fun connect() {
		if (!isConnected) {
			bleManager.connectBle()
				?.fail { device, status ->
					MLog.d("创建连接失败-${device.address} - $status")
				}
				?.done {

				}?.enqueue()
		}
	}

	/**断开连接*/
	open fun disconnect() {
		if (isConnected) {
			bleManager.disconnect()
				.fail { device, status ->
					MLog.d("断开连接失败-${device.address} - $status")
				}.done {

				}.enqueue()
		}
	}

	/**封装调用此方法来统一交互接口*/
	protected fun writeData(id: BleIdBean, vararg bytes: Byte): WriteRequest? {
		return bleManager.writeData(id.sid, id.cid, bytes)
	}

	//======================通知信息======================

	/**监听到发来的通知信息*/
	open fun onNotification(cid: String, value: ByteArray?) {
		for (function in notifyCallback) {
			function?.invoke(cid, value!!)
		}
//		notifyCallback?.invoke(cid, value!!)
	}

	/**连接成功*/
	open fun onConnected() {
		isConnected = true
	}

	/**监听到连接断开*/
	open fun onDisconnected() {
		isConnected = false
	}

}