package com.like.blebluetoothdemoapplication

import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings

/*
排坑记录 onClientConnectionState
0、使用框架连接测试
1、未配对就连接，可能报 status = 133
2、配对后第一次连接成功，但之后连接都会报 status = 22 ,清除数据或重启蓝牙都不行，必须重新绑定才能恢复
3、换成自己的 连接实现 来测试 ing，配对成功后非第一次连接的之后的连接也会报 status = 22
4、但是自己的 未配对就连接就大不同了：首先会连接成功,紧接着页面弹出“配对请求”,配对请求持续一段时间后自动取消,而无论自动取消还是手动取消都会导致 status 变化,报 status = 22。反之若点确认配对,则不会变化 status
5、重新使用框架连接测试，未配对时绑定的效果反而和自己试验的效果相同，没有报 133 了，所以猜测 133 应该不是框架内的配置，而是发生了另一种错误
6、框架的createBond绑定只能在连接后调用,但是由于连接的特性“自动提示配对,并在否认前为成功状态”可以直接绑定,最终效果就是点击连接自动绑定。但是重连还是报 22
7、综上述,问题有2:1、还未摸清规律的 133。2、配对之后的非第一次连接报 22，除非重新配对。
	解决22: 代码中无法解绑，只能提示用户手动去解绑，如果这个问题不常见,那么提示用户去解绑也可以。但要保证正常关闭时蓝牙不报22

18/12/18 测试
// ----------------------只用直连,在断开后再次直连-------------------------
设备尾号E1
直连成功,提示绑定 -> 绑定提示消失,status22 -> (绑定提示存在30秒,随后自动消失)
直连成功,提示绑定 -> 绑定提示消失,status22 ->
直连失败,status133 ->
直连成功,提示绑定 -> 绑定提示消失,status22 ->
直连成功,提示绑定 -> 绑定提示消失,status22 ->
直连成功,提示绑定 -> 绑定提示消失,status22 ->
直连成功,提示绑定 -> 绑定提示消失,status22 ->
直连成功,提示绑定 -> 绑定提示消失,status22。
------------------- over ----------------------------
实验中133只出现了一次,但昨天连接唐青的设备后多次连不上E1，一直提示133


------------------------直连连上后再次直连-----------------------
直连成功,提示绑定 -> 直连成功 -> 直连成功 -> 直连成功 -> 绑定提示消失,status22 //(提示绑定到结束绑定一共还是30秒,没有延长)，第一次连接稍微耗时,之后的连接都是瞬间连上
------------------- over ----------------------------


------------------------直连成功后销毁App-----------------------
直连成功,提示绑定 -> 退出,绑定提示消失 ->
进入 -> 直连成功,提示绑定
------------------- over ----------------------------


！！！ 偶然多次连续直连,发现没有提示绑定,也没有断开，即保持连接
直连成功 * 7 ,没有注意绑定提示 -> 绑定提示最后发现未弹出,也未消失,status未改变 ->
重启App -> 直连成功,提示绑定 -> 直连 * 7 -> 绑定提示消失,status22
直连 * 5 -> 连接成功,提示绑定 -> 绑定提示消失,status22
//通过日志有新发现“D/BtGatt.GattService: onClientConnUpdate() - connId=19, status=0”这里的 connId 随着我的连接而操作增加,并且并非只打印一行,而是像这样子：
D/BluetoothController: updateConnected complete
D/BtGatt.GattService: onClientConnUpdate() - connId=5, status=0		//从5开始全部打印,5之前的猜测是内部缓存
D/BtGatt.GattService: onClientConnUpdate() - connId=6, status=0		//猜测起始数可能和绑定的蓝牙数有关,但我已绑定的只有3个蓝牙,所以与已绑定蓝牙无关
D/BtGatt.GattService: onClientConnUpdate() - connId=7, status=0		//退出程序会清空 connId,使其重新从5开始
D/BtGatt.GattService: onClientConnUpdate() - connId=8, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=9, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=10, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=11, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=12, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=13, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=14, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=15, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=16, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=17, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=18, status=0
D/BtGatt.GattService: onClientConnUpdate() - connId=19, status=0

//为确认不是 TRANSPORT_LE 导致不报133，使用默认值重新运行
仍然没报133。

----------------------------双联测试-----------------------------------
直连41,提示绑定 -> 直连E1,未提示 -> 41绑定提示消失,status22 //但E1没提示,页保持连接了
//保持连接的关键点就是躲过提示消失时对连接的断开

//唐青：但是计步器并没有配对提示,为何手环有配对提示?
//测试发现，直连恒温器并没有配对提示，直接成功，询问王华柏得知，根据协议规定大量数据传输需要配对，否则IOS会出问题。

 */
class MainActivity : AppCompatActivity() {

	var selectedMac: String = "F8:50:A7:DC:BB:41"
	val adapter = object : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_str) {
		override fun convert(helper: BaseViewHolder?, item: String?) {
			helper!!.setText(R.id.textView, item!!)
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		recyclerView.layoutManager = LinearLayoutManager(this)
		recyclerView.adapter = adapter
		adapter.setOnItemClickListener { adapter, view, position ->

			selectedMac = this.adapter.data[position]
			//连接设备
//			bond(this.adapter.data[position])
		}
	}

	/**开始搜索*/
	fun startScan(v: View) {
		// 搜索配置。注意,这里用的不是官方的 ScanSettings,而是 'no.nordicsemi.android.support.v18:scanner:1.1.0'
		val settings = ScanSettings.Builder()
			.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)//扫描模式。SCAN_MODE_LOW_LATENCY: 低延迟,高消耗,建议仅当前台搜索时使用此模式
			.setReportDelay(500)//扫描报告延时
			.setUseHardwareBatchingIfSupported(false)//批处理,Demo为false
			// 硬件过滤在所选设备上存在一些问题
			.setUseHardwareFilteringIfSupported(false)//Demo为false
			.build()

		//开搜
		MLog.d("开搜")
		//必须有定位权限，必须已打开蓝牙
		BluetoothLeScannerCompat.getScanner().startScan(null, settings, scanCallback)
	}

	private fun bond(address: String?) {
		val scanResult = mScanResults.find {
			it.device.address == address
		}
//		myConnection(scanResult!!.device)
//		itConnection(scanResult!!.device)
		//绑定蓝牙
		MLog.d("开始绑定")
		BondRequest().done {
			Toast.makeText(this, "绑定成功", Toast.LENGTH_SHORT).show()
		}.createBond(this, scanResult!!.device)

//		val remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice("")
//		remoteDevice.setPairingConfirmation()
	}

	/**绑定*/
	fun bond(v: View) {
		//断开操作
//		mGatt?.run {
//			this.disconnect()
//			this.close()
//		}
		//绑定
		manager.createBondRequest()
			.createBond(this, BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedMac))
//		bleManager.disconnect().done {
//			bleManager.close()
//			MLog.d("bleManager.disconnect()done called with: " + "")
//		}.fail { device, status ->
//			MLog.d("bleManager.disconnect()fail called with: " + "device = [" + device + "], status = [" + status + "]")
//		}.invalid {
//			MLog.d("bleManager.disconnect()invalid called with: " + "")
//		}.enqueue()
	}

	/**清除*/
	fun clear(v: View) {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedMac)
		val method = device
			.javaClass.getMethod("removeBond")
		val result = method.invoke(device)
		MLog.d("解绑结果:$result")
//		myConnectGatt?.close()
	}

	var myConnectGatt: BluetoothGatt? = null
	/**连接*/
	fun connect(v: View) {
		fun myConnection(device: BluetoothDevice) {

			//======================自己连接设备======================
			MLog.d("开始我的连接")
			val gattCallback = object : BluetoothGattCallback() {
				override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
					super.onReadRemoteRssi(gatt, rssi, status)
					MLog.d("onReadRemoteRssi() called with: " + "gatt = [" + gatt + "], rssi = [" + rssi + "], status = [" + status + "]")
				}

				override fun onCharacteristicRead(
					gatt: BluetoothGatt?,
					characteristic: BluetoothGattCharacteristic?,
					status: Int
				) {
					super.onCharacteristicRead(gatt, characteristic, status)
					MLog.d("onCharacteristicRead() called with: " + "gatt = [" + gatt + "], characteristic = [" + characteristic + "], status = [" + status + "]")
				}

				override fun onCharacteristicWrite(
					gatt: BluetoothGatt?,
					characteristic: BluetoothGattCharacteristic?,
					status: Int
				) {
					super.onCharacteristicWrite(gatt, characteristic, status)
					MLog.d("onCharacteristicWrite() called with: " + "gatt = [" + gatt + "], characteristic = [" + characteristic + "], status = [" + status + "]")
				}

				override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
					super.onServicesDiscovered(gatt, status)
					MLog.d("onServicesDiscovered() called with: " + "gatt = [" + gatt + "], status = [" + status + "]")
				}

				override fun onPhyUpdate(
					gatt: BluetoothGatt?,
					txPhy: Int,
					rxPhy: Int,
					status: Int
				) {
					super.onPhyUpdate(gatt, txPhy, rxPhy, status)
					MLog.d("onPhyUpdate() called with: " + "gatt = [" + gatt + "], txPhy = [" + txPhy + "], rxPhy = [" + rxPhy + "], status = [" + status + "]")
				}

				override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
					super.onMtuChanged(gatt, mtu, status)
					MLog.d("onMtuChanged() called with: " + "gatt = [" + gatt + "], mtu = [" + mtu + "], status = [" + status + "]")
				}

				override fun onReliableWriteCompleted(gatt: BluetoothGatt?, status: Int) {
					super.onReliableWriteCompleted(gatt, status)
					MLog.d("onReliableWriteCompleted() called with: " + "gatt = [" + gatt + "], status = [" + status + "]")
				}

				override fun onDescriptorWrite(
					gatt: BluetoothGatt?,
					descriptor: BluetoothGattDescriptor?,
					status: Int
				) {
					super.onDescriptorWrite(gatt, descriptor, status)
					MLog.d("onDescriptorWrite() called with: " + "gatt = [" + gatt + "], descriptor = [" + descriptor + "], status = [" + status + "]")
				}

				override fun onCharacteristicChanged(
					gatt: BluetoothGatt?,
					characteristic: BluetoothGattCharacteristic?
				) {
					super.onCharacteristicChanged(gatt, characteristic)
					MLog.d("onCharacteristicChanged() called with: " + "gatt = [" + gatt + "], characteristic = [" + characteristic + "]")
				}

				override fun onDescriptorRead(
					gatt: BluetoothGatt?,
					descriptor: BluetoothGattDescriptor?,
					status: Int
				) {
					super.onDescriptorRead(gatt, descriptor, status)
					MLog.d("onDescriptorRead() called with: " + "gatt = [" + gatt + "], descriptor = [" + descriptor + "], status = [" + status + "]")
				}

				override fun onPhyRead(gatt: BluetoothGatt?, txPhy: Int, rxPhy: Int, status: Int) {
					super.onPhyRead(gatt, txPhy, rxPhy, status)
					MLog.d("onPhyRead() called with: " + "gatt = [" + gatt + "], txPhy = [" + txPhy + "], rxPhy = [" + rxPhy + "], status = [" + status + "]")
				}

				override fun onConnectionStateChange(
					gatt: BluetoothGatt?,
					status: Int,
					newState: Int
				) {
					super.onConnectionStateChange(gatt, status, newState)
					if (status == 133) {
						MLog.d("出现了! 133")
					}
					MLog.d("onConnectionStateChange() called with: " + "gatt = [" + gatt + "], status = [" + status + "], newState = [" + newState + "]")
				}
			}
//			mGatt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//				device.connectGatt(this, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
//			} else {
//			}
			myConnectGatt = device.connectGatt(this, false, gattCallback)
		}

		fun itConnection(device: BluetoothDevice) {
			//======================框架连接设备======================

			//未绑定就连接 可能报133，所以连接要在绑定之后，但是此框架的 createBond() 必须在连接后调用，不知道有什么考虑。但无法使用。
			//BLE包搜索后连接,没有报133,可能必须先搜索再连接
			//找到规律：绑定后第一次连接可以成功，但退出后再次连接就会一直报 status = 22。清除App数据、重启蓝牙 也无效， 重新绑定 可以解决此问题。


			manager.setGattCallbacks(managerCallback)
			manager.connect(device)
				.fail { device, status ->
					MLog.d("connection fail() called with: " + "device = [" + device + "], status = [" + status + "]")
				}
				.invalid {
					MLog.d("connection invalid() called with: " + "")
				}.done {
					MLog.d("connection done() called with: " + "")

//					bleManager.createBondSuper().done {
//						MLog.d("bleManager.done")
//					}.fail { device, status ->
//						MLog.d("bleManager.fail called with: " + "device = [" + device + "], status = [" + status + "]")
//					}.enqueue()
				}
				.retry(3, 100)
				.useAutoConnect(false)//持续的自动连接 或者 直接连接设备
				.enqueue()
		}

		val handler = Handler {

			MLog.d("在新的线程连接-$selectedMac - " + Thread.currentThread().name)
			itConnection(
				BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedMac)
			)

			true
		}
		handler.sendEmptyMessageDelayed(0, 500)

//		MLog.d("isReady = " + bleManager.isReady)
//		bleManager
	}

	/**蓝牙搜索回调*/
	private val scanCallback = object : ScanCallback() {
		override fun onScanFailed(errorCode: Int) {
			super.onScanFailed(errorCode)
			MLog.d("onScanFailed() called with: " + "errorCode = [" + errorCode + "]")
		}

		override fun onScanResult(callbackType: Int, result: ScanResult?) {
			super.onScanResult(callbackType, result)
			MLog.d("onScanResult() called with: " + "callbackType = [" + callbackType + "], result = [" + result + "]")
		}

		/**扫描结果列表(会重复),搜不到新的设备时size=0
		 *	提示：搜到就可以看ServiceUUID了: ScanResult.scanRecord!!.serviceUuids */
		override fun onBatchScanResults(results: MutableList<ScanResult>?) {
			super.onBatchScanResults(results)

			//搜索结果改变
			var isScanResultChanged = false
			//过滤重复
			results?.forEach { result ->
				//已重复
				val isContains = mScanResults.any {
					it.device.address == result.device.address
				}
				if (!isContains) {
					mScanResults.add(result)
					MLog.d("addNewDevice ${result.scanRecord?.deviceName} _ ${result.device.address}")
					isScanResultChanged = true
				}
			}

			if (isScanResultChanged) {
//				MLog.d("onBatchScanResults() called with: " + "results = [" + mScanResults + "]")
				val list = mScanResults.mapTo(mutableListOf()) { it.device.address }
				adapter.replaceData(list)
			}
		}
	}

	/**过滤重复后的搜索结果*/
	val mScanResults = mutableListOf<ScanResult>()
	var mGatt: BluetoothGatt? = null

	val manager by lazy { MyManager(this@MainActivity) }

	val managerCallback = object : BleManagerCallbacks {

		/**开始连接了*/
		override fun onDeviceConnecting(device: BluetoothDevice) {
			MLog.d("onDeviceConnecting() called with: " + "device = [" + device + "]")
		}

		/**因设备不支持而失败 (设备上没有对应 Service)*/
		override fun onDeviceNotSupported(device: BluetoothDevice) {
			MLog.d("onDeviceNotSupported() called with: " + "device = [" + device + "]")
		}

		override fun onDeviceConnected(device: BluetoothDevice) {
			//连接成功
			manager.mLedCharacteristic?.descriptors?.forEach { callback ->
				println("Li_ke-连接成功:" + callback.characteristic)
			}

		}

		override fun onBondingFailed(device: BluetoothDevice) {
			MLog.d("onBondingFailed() called with: " + "device = [" + device + "]")
		}

		override fun onServicesDiscovered(
			device: BluetoothDevice,
			optionalServicesFound: Boolean
		) {
			MLog.d("onServicesDiscovered() called with: " + "device = [" + device + "], optionalServicesFound = [" + optionalServicesFound + "]")
		}

		override fun onBondingRequired(device: BluetoothDevice) {
			MLog.d("onBondingRequired() called with: " + "device = [" + device + "]")
		}

		override fun onLinkLossOccurred(device: BluetoothDevice) {
			MLog.d("onLinkLossOccurred() called with: " + "device = [" + device + "]")
		}

		override fun onBonded(device: BluetoothDevice) {
			MLog.d("onBonded() called with: " + "device = [" + device + "]")
		}

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
}

class MyManager(context: Context) : BleManager<BleManagerCallbacks>(context) {
	var mLedCharacteristic: BluetoothGattCharacteristic? = null

	val LBS_UUID_SERVICE = null
	val LBS_UUID_LED_CHAR = null
	private val mGattCallback = object : BleManagerGattCallback() {


		/**断开连接*/
		override fun onDeviceDisconnected() {
			MLog.d("onDeviceDisconnected() called with: " + "")
		}

		/**判断 设备是否支持 传输需要的Service，因为Service是设备人员定的协议，所以肯定是自己判断
		 * 在连接操作时调用
		 */
		override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
			MLog.d("isRequiredServiceSupported() called with: " + "gatt = [" + gatt + "]")
			//查看所有Service
			fun logServices(gatt: BluetoothGatt) {
				for (service in gatt.services) {
					MLog.d("" + service.uuid)
				}
				//00001800-0000-1000-8000-00805f9b34fb
				//00001801-0000-1000-8000-00805f9b34fb
				//8e400001-f315-4f60-9fb8-838830daea50
				//6e400001-b5a3-f393-e0a9-e50e24dcca9e
			}

			//查看所有特征码
			fun logCharacteristics(service: BluetoothGattService) {
				for (characteristic in service.characteristics) {
					MLog.d("" + characteristic.uuid)
				}
				//6e400004-b5a3-f393-e0a9-e50e24dcca9e
				//6e400003-b5a3-f393-e0a9-e50e24dcca9e
				//6e400002-b5a3-f393-e0a9-e50e24dcca9e
			}

//			//测试 - 写入数据 //TODO 失败
//			val characteristic =
//				gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))
//					.getCharacteristic(UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e"))
//			characteristic.value = byteArrayOf(22, 1)
//			val b = gatt.writeCharacteristic(characteristic)
//			MLog.d("发送:" + b)
//
//			//测试 - 读
//			val readC =
//				gatt.getService(UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e"))
//					.getCharacteristic(UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e"))
//			readCharacteristic(readC).awaitValid(object : ProfileReadResponse() {
//				override fun onInvalidDataReceived(device: BluetoothDevice, data: Data) {
//					MLog.d("readCharacteristic.done" + Arrays.toString(data.value))
//					super.onInvalidDataReceived(device, data)
//				}
//			})

			return true
		}
	}

	//BleManager的createBond只能在连接后才可用 super.createBond()
	fun createBondSuper() = super.createBond()

	/**必须先配对才能连接*/
	fun createBondRequest(): BondRequest = BondRequest()

	/**一直返回一个实例，不要重复创建*/
	override fun getGattCallback(): BleManagerGattCallback = mGattCallback

	/**需覆盖此方法来打印日志*/
	override fun log(priority: Int, message: String) {
		Log.println(priority, "Li_ke-BleManager", message)
	}

	fun read() {
//		writeCharacteristic(g)
	}


}

/**配对回调*/
class BondRequest : IBondStateChanged {

	private var mContext: Context? = null
	private val mBondReceiver by lazy { DeviceBondReceiver(this) }

	private var mDoneBlock: ((BluetoothDevice) -> Unit)? = null

	fun done(doneBlock: (BluetoothDevice) -> Unit): BondRequest {
		mDoneBlock = doneBlock
		return this
	}

	//createBond() 不会只是悄悄地配对,配不上就算了，如果悄悄地配对失败,它会启动系统的配对提示让用户手动点配对
	//悄悄的配对很快,1秒左右就会失败,并toast失败信息，但系统的配对提示有30s的存在时间,30秒后才会失败，再30秒内可以成功连接设备
	fun createBond(context: Context, device: BluetoothDevice): Boolean {
		val bondedDevice: BluetoothDevice? =
		//尝试使用已绑定的Device，失败,还是报22
//			BluetoothAdapter.getDefaultAdapter().bondedDevices.find { it.address == device.address }
		//尝试永远让它重新绑定，失败,再次绑定不会发送广播
			null

		return if (bondedDevice == null) {
			MLog.d("未绑定,开始绑定")
			//若还未配对
//		return if (device.bondState == BluetoothDevice.BOND_NONE) {
			if (!isRegister) {
				context.registerReceiver(
					mBondReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
				)
				isRegister = true
			}

			mContext = context

			return device.createBond()
		} else {
			MLog.d("已绑定")
			mDoneBlock?.invoke(bondedDevice)
			false
		}
	}

	fun cancelBondListener(context: Context? = mContext) {
		if (mContext != null)
			mContext!!.unregisterReceiver(mBondReceiver)
	}

	override fun onBondStateChanged(
		device: BluetoothDevice, previousBondState: Int, bondState: Int
	) {
		MLog.d("onBondStateChanged ${device.address} $previousBondState -> $bondState")
		if (bondState == BluetoothDevice.BOND_BONDED) {
			MLog.d("绑定成功")
			mDoneBlock?.invoke(device)
		} else if (bondState == BluetoothDevice.BOND_BONDING) {
			val pin = device.setPin(
				byteArrayOf(
					'0'.toByte(),
					'0'.toByte(),
					'0'.toByte(),
					'0'.toByte()
				)
			)
			MLog.d("尝试配对 $pin")
		}
	}

	companion object {
		var isRegister = false
	}
}

interface IBondStateChanged {
	fun onBondStateChanged(device: BluetoothDevice, previousBondState: Int, bondState: Int)
}

class DeviceBondReceiver(
	private val mBondStatusChanged: IBondStateChanged
) : BroadcastReceiver() {
	override fun onReceive(context: Context?, intent: Intent?) {
		if (intent?.action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
			val device = intent.extras!![BluetoothDevice.EXTRA_DEVICE]!! as BluetoothDevice
			//设备之前绑定状态
			val previousBondState =
				intent.extras!![BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE]!! as Int
			//设备当前绑定状态
			val bondState = intent.extras!![BluetoothDevice.EXTRA_BOND_STATE]!! as Int

			mBondStatusChanged.onBondStateChanged(device, previousBondState, bondState)
		}
	}
}

object MLog {
	fun d(str: String) {
		Log.i("Li_ke", "thread=${Thread.currentThread().name} - $str")
	}
}

/*
17:42:59
谢永许.QILOO.公司 2018/12/5 星期三 17:42:59
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error
{
    for (CBDescriptor *descriptor in characteristic.descriptors) {
        [peripheral readValueForDescriptor:descriptor];
    }
}

- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error
{
    //获取设备固件版本
    if ([descriptor.characteristic.UUID.UUIDString isEqualToString:@"6E400004-B5A3-F393-E0A9-E50E24DCCA9E"])
    {
        if ([self.delegate respondsToSelector:@selector(didUpdateWristbandVersionDescriptor:value:)])
        {
            [self.delegate didUpdateWristbandVersionDescriptor:peripheral value:descriptor.value];
        }
    }
}
 */