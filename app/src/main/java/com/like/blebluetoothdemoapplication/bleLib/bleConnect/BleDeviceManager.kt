package com.like.blebluetoothdemoapplication.bleLib.bleConnect

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.support.annotation.MainThread
import android.util.Log
import com.like.blebluetoothdemoapplication.MLog
import no.nordicsemi.android.ble.*
import java.util.*

/**
 * 负责管理蓝牙设备的蓝牙操作，但外部通过[AbsBleDevice]操作,不提供直接操作接口
 * 可通过 [AbsBleDevice.bleManager] 获取
 *
 * 扩展：虽然负责管理蓝牙操作,但由于设备多样蓝牙操作必然不是同一的,所以必须可扩展蓝牙操作,
 * 	设计为在扩展[AbsBleDevice]时进行自定义蓝牙操作,不建议在[AbsBleDevice]外部直接使用
 */
class BleDeviceManager<D : AbsBleDevice> internal constructor(context: Context, val bleDevice: D) :
	BleManager<BleManagerCallbacks>(context) {

	var mGatt: BluetoothGatt? = null

	private val mGattCallback = object : BleManagerGattCallback() {

		/**判断 设备是否支持 传输需要的Service
		 * 因为Service是设备人员定的协议，所以肯定是自己判断 在连接操作时回调
		 * @return 是否连接此设备
		 */
		override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
			/**测试-打印SID*/
			fun logSID(gatt: BluetoothGatt) {
				/**测试-打印CID*/
				fun logCID(service: BluetoothGattService) {
					for (characteristic in service.characteristics) {
						MLog.d("CID:${characteristic.uuid}")
					}
				}
				for (service in gatt.services) {
					MLog.d("SID:${service.uuid}")
					logCID(service)
				}
			}
			logSID(gatt)

			//确认协议中的服务
			val noFindService: Boolean =
				bleDevice.writeID.any { id ->
					//没发现写入SID
					gatt.getService(UUID.fromString(id.sid)) == null
				} || bleDevice.readID.any { id ->
					//没发现读取SID
					gatt.getService(UUID.fromString(id.sid)) == null
				}

			//服务全部存在才算支持 - 没需要的SID也可以
			val isSupported: Boolean = !noFindService
			if (isSupported)
				mGatt = gatt
			return isSupported
		}

		/**在这里初始化连接配置,如注册信息回调监听,此方法在 [isRequiredServiceSupported]==true 后调用*/
		override fun initialize() {
			//监听需要监听的CID
			for (id in bleDevice.readID) {

				//get需要监听的CID
				val characteristic = mGatt
					?.getService(UUID.fromString(id.sid))
					?.getCharacteristic(UUID.fromString(id.cid))

				//启用通知监听
				enableNotifications(characteristic).fail { device, status ->
					MLog.d("监听${id.cid}失败")
				}.done {
					MLog.d("监听${id.cid}成功")
				}.enqueue()

				//添加通知监听回调 - 监听设备发来信息
				setNotificationCallback(characteristic).with { device, data ->
					MLog.d(
						"${this@BleDeviceManager.javaClass.simpleName} 收到信息 mac=${device.address} " +
								"cid=${id.cid} " +
								"value=${Arrays.toString(data.value)}"
					)
					//通知BleDevice
					bleDevice.onNotification(id.cid, data.value)
				}
			}
		}

		/**连接断开*/
		override fun onDeviceDisconnected() {
			MLog.d("gattCallback.onDeviceDisconnected() called with: " + "")
			bleDevice.onDisconnected()
		}
	}

	/**API要求：一直返回一个实例，不要重复创建*/
	override fun getGattCallback(): BleManagerGattCallback = mGattCallback

	//BleManager的createBond只能在连接后才可用,并且不是 public 修饰开始连接失败
	fun createBondSuper() = super.createBond()

	/**必须覆盖此方法才能使[BleManager]其打印日志*/
	override fun log(priority: Int, message: String) {
		//日志发现此包内已经注册了 Broadcast 监听绑定结果
		Log.println(priority, "Li_ke-BleManager", message)
	}

	//======================连接======================
	/**连接回调*/
	private val managerCallback = object : BleManagerCallbacks {

		/**开始连接了*/
		@MainThread
		override fun onDeviceConnecting(device: BluetoothDevice) {
			MLog.d("onDeviceConnecting() called with: " + "device = [" + device + "]")
		}

		/**因设备不支持而失败 (设备上没有对应 Service)*/
		override fun onDeviceNotSupported(device: BluetoothDevice) {
			MLog.d("onDeviceNotSupported() called with: " + "device = [" + device + "]")
		}

		/**连接成功*/
		@MainThread
		override fun onDeviceConnected(device: BluetoothDevice) {
			MLog.d("onDeviceConnected() called with: " + "device = [" + device + "]")
			bleDevice.onConnected()
		}

		/**绑定请求*/
		override fun onBondingRequired(device: BluetoothDevice) {
			MLog.d("onBondingRequired() called with: " + "device = [" + device + "]")
		}

		/**绑定失败*/
		override fun onBondingFailed(device: BluetoothDevice) {
			MLog.d("onBondingFailed() called with: " + "device = [" + device + "]")
		}

		/**绑定成功*/
		override fun onBonded(device: BluetoothDevice) {
			MLog.d("onBonded() called with: " + "device = [" + device + "]")
		}

		/**发现了服务*/
		override fun onServicesDiscovered(device: BluetoothDevice, optionalServicesFound: Boolean) {
			MLog.d("onServicesDiscovered() called with: " + "device = [" + device + "], optionalServicesFound = [" + optionalServicesFound + "]")
		}

		/**链路丢失了*/
		override fun onLinkLossOccurred(device: BluetoothDevice) {
			MLog.d("onLinkLossOccurred() called with: " + "device = [" + device + "]")
		}

		/**设备准备ok*/
		override fun onDeviceReady(device: BluetoothDevice) {
			MLog.d("onDeviceReady() called with: " + "device = [" + device + "]")
		}

		override fun onError(device: BluetoothDevice, message: String, errorCode: Int) {
			MLog.d("onError() called with: " + "device = [" + device + "], message = [" + message + "], errorCode = [" + errorCode + "]")
		}

		/**开始断开连接了*/
		override fun onDeviceDisconnecting(device: BluetoothDevice) {
			MLog.d("onDeviceDisconnecting() called with: " + "device = [" + device + "]")
		}

		/**已断开连接*/
		override fun onDeviceDisconnected(device: BluetoothDevice) {
			MLog.d("onDeviceDisconnected() called with: " + "device = [" + device + "]")
		}
	}

	/**创建连接请求*/
	internal fun connectBle(): ConnectRequest? {
		setGattCallbacks(managerCallback)
		return connect(bleDevice.device)
			.retry(3, 1000)//失败后重试的次数与延时
			.timeout(10 * 1000L)//连接超时时间 0=关闭超时功能
			.useAutoConnect(false)//失败后自动重复连接
	}

	//======================BLE通讯======================

	/**创建写入请求*/
	internal fun writeData(sid: String, cid: String, bytes: ByteArray): WriteRequest {
		val characteristic =
			mGatt?.getService(UUID.fromString(sid))
				?.getCharacteristic(UUID.fromString(cid))

		return writeCharacteristic(characteristic, bytes)
	}

	/**读取Descriptor信息*/
	internal fun readDescriptor(sid: String, cid: String, did: String): ReadRequest {
		val descriptor =
			mGatt?.getService(UUID.fromString(sid))
				?.getCharacteristic(UUID.fromString(cid))
				?.getDescriptor(UUID.fromString(did))

		return readDescriptor(descriptor)
	}
}