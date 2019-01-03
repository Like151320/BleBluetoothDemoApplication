package com.like.blebluetoothdemoapplication.useNordicsemiBle

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.like.blebluetoothdemoapplication.MLog
import com.like.blebluetoothdemoapplication.R
import kotlinx.android.synthetic.main.activity_my_ble.*
import no.nordicsemi.android.ble.BleManager
import no.nordicsemi.android.ble.BleManagerCallbacks
import no.nordicsemi.android.ble.WriteRequest
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat
import no.nordicsemi.android.support.v18.scanner.ScanCallback
import no.nordicsemi.android.support.v18.scanner.ScanResult
import no.nordicsemi.android.support.v18.scanner.ScanSettings
import java.util.*

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 9:11
 * 作用: 使用 Nordicsemi 包执行蓝牙操作
 */
class NordicsemiActivity : AppCompatActivity() {
	private lateinit var listAdapter: ArrayAdapter<String>
	private var selectedAddress: String? = null
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_my_ble)

		listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
		listView.setOnItemClickListener { parent, view, position, id ->
			selected(listAdapter.getItem(position)!!)
		}
		listView.adapter = listAdapter
		listAdapter.add("蓝牙地址列表")
		listAdapter.notifyDataSetChanged()
	}

	//======================View点击======================

	fun permission(v: View) {
		requestPermission()
	}

	fun search(v: View) {
		searchBle()
	}

	fun selected(address: String) {
		selectedAddress = address
	}

	fun connection(v: View) {
		if (selectedAddress.isNullOrEmpty()) {
			Toast.makeText(this, "未选中设备", Toast.LENGTH_SHORT).show()
		} else
			connectionBle(selectedAddress!!)
	}

	fun gotoStream(v: View) {
		startActivity(Intent(this, NordicsemiStreamActivity::class.java))
	}

	//======================权限模块======================

	private val REQUEST_PERMISSION_CODE = 3

	private fun requestPermission() {
		//所需权限
		val permissions = arrayOf(
			Manifest.permission.BLUETOOTH,//允许程序 使用蓝牙 与 连接到已配对的蓝牙设备
			Manifest.permission.BLUETOOTH_ADMIN,//允许程序发现和配对蓝牙设备
			Manifest.permission.ACCESS_FINE_LOCATION,//允许设备定位
			Manifest.permission.ACCESS_COARSE_LOCATION//允许程序访问特点来定位
		)
		//允许否
		val grants = permissions.map { permission ->
			ContextCompat.checkSelfPermission(this, permission)
		}
		//若不允许
		if (!verifyPermissions(*grants.toIntArray()))
			ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION_CODE)
	}

	override fun onRequestPermissionsResult(
		requestCode: Int, permissions: Array<out String>, grantResults: IntArray
	) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (verifyPermissions(*grantResults)) {
			Toast.makeText(this, "权限请求成功", Toast.LENGTH_LONG).show()
		}
	}

	/** 摘自 permissionsdispatcher 判断权限是否都允许
	 * Checks all given permissions have been granted.
	 *
	 * @param grantResults results
	 * @return returns true if all permissions have been granted.
	 */
	private fun verifyPermissions(vararg grantResults: Int): Boolean {
		if (grantResults.isEmpty())
			return false

		for (result in grantResults) {
			if (result != PackageManager.PERMISSION_GRANTED) {
				return false
			}
		}
		return true
	}

	//======================搜索BLE======================

	/**过滤重复后的搜索结果*/
	val mScanResults = mutableListOf<ScanResult>()

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
			//真的搜到了东西,更新设备列表
			if (isScanResultChanged) {
//				MLog.d("onBatchScanResults() called with: " + "results = [" + mScanResults + "]")
				val list = mScanResults.mapTo(mutableListOf()) { it.device.address }
				listAdapter.clear()
				listAdapter.addAll(list)
			}
		}
	}

	private fun searchBle() {
		// 搜索配置。注意,这里用的不是原生的 ScanSettings,而是 'no.nordicsemi.android.support.v18:scanner:1.1.0'
		val settings = ScanSettings.Builder()
			.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)//扫描模式。SCAN_MODE_LOW_LATENCY: 低延迟,高消耗,建议仅当前台搜索时使用此模式
			.setReportDelay(500)//扫描报告延时
			.setUseHardwareBatchingIfSupported(false)//批处理,Demo为false
			// 硬件过滤在所选设备上存在一些问题
			.setUseHardwareFilteringIfSupported(false)//Demo为false
			.build()
		// 开搜 - 必须有定位权限，必须已打开蓝牙
		BluetoothLeScannerCompat.getScanner().startScan(null, settings, scanCallback)
	}

	//======================连接BLE======================

	/**连接回调*/
	private val managerCallback = object : BleManagerCallbacks {

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
			manager?.mLedCharacteristic?.descriptors?.forEach { callback ->
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

	private fun connectionBle(address: String) {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)
		if (manager == null) manager = NordicManager(applicationContext)
		manager!!.setGattCallbacks(managerCallback)
		manager!!.connect(device)
			.fail { device1, status ->
				//连接失败
				MLog.d("connection fail() called with: " + "device = [" + device1 + "], status = [" + status + "]")
			}.invalid {
				//连接无效
				MLog.d("connection invalid() called with: " + "")
			}.done {
				MLog.d("connection done() called with: " + "")
			}
			.retry(3, 1000)//失败后重试的次数与延时
			.timeout(10 * 1000L)//连接超时时间 0=关闭超时功能
			.useAutoConnect(false)//失败后自动重复连接
			.enqueue()
	}


}

//写到外面只是由于懒得写Service什么的中介来传给第二个Activity
@SuppressLint("StaticFieldLeak")//内存泄露警告
var manager: NordicManager? = null

/**必须创建继承 BleManager 的蓝牙管理类,才能管理蓝牙设备*/
class NordicManager(context: Context) : BleManager<BleManagerCallbacks>(context) {

	/**在[isRequiredServiceSupported]==true 后可用*/
	var mGatt: BluetoothGatt? = null
	//写入ID
//	val write_SID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
//	val write_CID = "6e400002-b5a3-f393-e0a9-e50e24dcca9e"
	//读取ID
//	val read_SID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
//	val read_CID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
	var mLedCharacteristic: BluetoothGattCharacteristic? = null

	private val mGattCallback = object : BleManagerGattCallback() {

		/**判断 设备是否支持 传输需要的Service，因为Service是设备人员定的协议，所以肯定是自己判断
		 * 在连接操作时调用
		 *
		 * 根据我的经验,只有连上设备并发现服务,才能看到里面的 SID与CID,所以执行到这里时应该已经连接了一次,
		 * 而源码中确实是发现了服务 -> onServicesDiscoveredSafe() 后才回调此方法.
		 *
		 * @return 是否保留此设备,建议保留所有设备,之后自行筛选.
		 */
		override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
			MLog.d("isRequiredServiceSupported() called with: " + "gatt = [" + gatt + "]")

			//查看所有特征码
			fun logCharacteristics(service: BluetoothGattService) {
				for (characteristic in service.characteristics) {
					MLog.d("CID:" + characteristic.uuid)
				}
				//6e400004-b5a3-f393-e0a9-e50e24dcca9e
				//6e400003-b5a3-f393-e0a9-e50e24dcca9e
				//6e400002-b5a3-f393-e0a9-e50e24dcca9e
			}

			//查看所有Service
			fun logServices(gatt: BluetoothGatt) {
				for (service in gatt.services) {
					MLog.d("write_SID:" + service.uuid)
					logCharacteristics(service)
				}
				//00001800-0000-1000-8000-00805f9b34fb
				//00001801-0000-1000-8000-00805f9b34fb
				//8e400001-f315-4f60-9fb8-838830daea50
				//6e400001-b5a3-f393-e0a9-e50e24dcca9e
			}

			logServices(gatt)

//			return (gatt.getService(UUID.fromString(write_SID)) != null)
//				.also { if (it) mGatt = gatt }
			mGatt = gatt
			return true
		}

		/**在这里初始化连接配置,如注册信息回调监听,此方法在 [isRequiredServiceSupported]==true 后调用*/
		override fun initialize() {
			//监听某个CID
//			val characteristic =
//				mGatt!!.getService(UUID.fromString(sid))
//					.getCharacteristic(UUID.fromString(cid))
			for (service in mGatt!!.services) {
				for (characteristic in service.characteristics) {


					//启用通知监听
					enableNotifications(characteristic).fail { device, status ->
						MLog.d("通知开启")
					}.done {

					}.enqueue()
				}
			}
			Toast.makeText(context, "连接成功", Toast.LENGTH_SHORT).show()
		}

		/**连接断开*/
		override fun onDeviceDisconnected() {
			MLog.d("onDeviceDisconnected() called with: " + "")
		}
	}

	fun onReceiveData(callback: (cid: String, value: ByteArray) -> Unit) {
		//监听发来信息
		mGatt?.services?.forEach { service ->
			for (characteristic in service.characteristics) {
				//监听
				val c = characteristic
				setNotificationCallback(c).with { device, data ->
					MLog.d("收到信息-${Arrays.toString(data.value)}")
					callback(c.uuid.toString(), data.value!!)
				}
			}
		}
	}

	//BleManager的createBond只能在连接后才可用,并且不是 public 修饰
	fun createBondSuper() = super.createBond()

	/**API要求：一直返回一个实例，不要重复创建*/
	override fun getGattCallback(): BleManagerGattCallback = mGattCallback

	/**必须覆盖此方法才能打印日志*/
	override fun log(priority: Int, message: String) {
		//日志发现此包内已经注册了 Broadcast 监听绑定结果
		Log.println(priority, "Li_ke-BleManager", message)
	}

	//======================BLE通讯======================

	fun writeData(sid: String, cid: String, data: ByteArray): WriteRequest {
		val characteristic =
			mGatt?.getService(UUID.fromString(sid))?.getCharacteristic(UUID.fromString(cid))
		return writeCharacteristic(characteristic, data)
			.invalid { }
			.fail { device, status -> }
	}
}