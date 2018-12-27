package com.like.blebluetoothdemoapplication

import android.Manifest
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_my_ble.*
import java.util.*

/**
 * 蓝牙搜索连接页
 */
class MyBLEActivity : AppCompatActivity() {
	private val TAG = "BluetoothLEDemo"
	lateinit var listAdapter: ArrayAdapter<String>

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

	/**请求权限*/
	fun permission(v: View) {
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

	/**搜索蓝牙*/
	fun search(v: View) {
		listAdapter.clear()
		//搜蓝牙
		searchBle(30 * 1000) { device ->
			listAdapter.add(device.address)
			listAdapter.notifyDataSetChanged()
		}
	}

	/**选中蓝牙*/
	fun selected(address: String) {
		if (BluetoothAdapter.checkBluetoothAddress(address)) {//是否是蓝牙地址
			selectedAddress = address
			Toast.makeText(this, "选中蓝牙: $address", Toast.LENGTH_LONG).show()
		} else {
			Toast.makeText(this, "点击的不是蓝牙地址", Toast.LENGTH_LONG).show()
		}
	}

	/**连接蓝牙*/
	fun connection(v: View) {
		connection(selectedAddress)
	}

	/**去蓝牙通讯*/
	fun gotoStream(v: View) {
		if (BluetoothAdapter.checkBluetoothAddress(selectedAddress))
			startActivity(MyBLEStreamActivity.getStartIntent(this, selectedAddress))
		else
			Toast.makeText(this, "未选中蓝牙,无法通讯", Toast.LENGTH_LONG).show()
	}

	data class DeviceInfo(
		val address: String,
		val name: String,
		val characteristicAndService: Map<String, String>//特征码 与对应的 service
	)

	//======================权限模块======================

	private val REQUEST_PERMISSION_CODE = 3

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

	//======================搜索模块======================

	/**是否正在搜索中*/
	private var isSearching = false
	/**停止搜索计时器,若为null则意味着计时器不存在,及没在搜索中*/
	private var stopScanTimer: Timer? = null
	/**搜到的设备-不重复*/
	private val searchedDeviceList = mutableListOf<BluetoothDevice>()

	/**开始搜索。超时时间[outTime]、搜索结果不重复[searchCallback]*/
	private fun searchBle(outTime: Long, searchCallback: (BluetoothDevice) -> Unit) {
		if (isSearching) {
			Toast.makeText(this, "已在搜索…", Toast.LENGTH_LONG).show()
			return
		}
		isSearching = true
		searchedDeviceList.clear()

		val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

		//开始搜索LE蓝牙 - PS:注意Android官方警告 - 执行设备发现对于蓝牙适配器而言是一个非常繁重的操作过程，并且会消耗大量资源。 在找到要连接的设备后，确保始终使用 cancelDiscovery() 停止发现，然后再尝试连接。 此外，如果您已经保持与某台设备的连接，那么执行发现操作可能会大幅减少可用于该连接的带宽，因此不应该在处于连接状态时执行发现操作。
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			//AndroidAPI 21 更新的新搜索方式
			val scanCallback = object : ScanCallback() {
				/**搜索失败回调*/
				override fun onScanFailed(errorCode: Int) {
					isSearching = false
					clearStopScanTimer()
					Log.e(TAG, "onScanFailed: $errorCode")
				}

				/**搜到LE蓝牙后立即回调-[result]会重复*/
				override fun onScanResult(callbackType: Int, result: ScanResult?) {
					if (result?.device?.address != null) {
						//过滤重复
						val isContains = searchedDeviceList.any { device ->
							device.address == result.device?.address
						}
						//不重复时保存
						if (!isContains) {
							searchedDeviceList.add(result.device)
							searchCallback.invoke(result.device)
						}
					}
				}

				/**搜索结果列表回调,但[results]也有重复,在制定上报延时后使用,比[onScanResult]效果好*/
				override fun onBatchScanResults(results: MutableList<ScanResult>?) {
					Log.e(
						TAG, "onBatchScanResults: " +
								"${results?.map {
									"\n[${it.device?.address}]"
								}}"
					)

					val noContainsDevice = mutableListOf<BluetoothDevice>()
					results?.forEach { result ->
						if (result.device?.address != null) {
							//过滤重复
							val isContains = searchedDeviceList.any { device ->
								device.address == result.device?.address
							}
							//不重复时保存
							if (!isContains) {
								noContainsDevice.add(result.device)
							}
						}
					}

					if (noContainsDevice.isNotEmpty()) {
						searchedDeviceList.addAll(noContainsDevice)
						noContainsDevice.forEach { device ->
							searchCallback.invoke(device)
						}
					}
				}
			}
			val scanSetting = ScanSettings.Builder()
				.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)//扫描模式 - SCAN_MODE_LOW_LATENCY: 低延迟,高消耗,建议仅当前台搜索时使用此模式
				.setReportDelay(500)//扫描报告延时,若设置此项,则搜到设备后不会立即通知,而是加入队列一起通知,回调会走 onBatchScanResults 而非 onScanResult
				.build()
			//扫描设置scanSetting决不能null PS:建议手动设置scanSetting,并添加ReportDelay
			bluetoothAdapter.bluetoothLeScanner.startScan(null, scanSetting, scanCallback)
//			bluetoothAdapter.bluetoothLeScanner.startScan(scanCallback)//使用默认scanSetting

			if (stopScanTimer != null)//关闭上次计时
				clearStopScanTimer()
			//开启 停止搜索计时器
			stopScanTimer = Timer()
			stopScanTimer!!.schedule(object : TimerTask() {
				override fun run() {
					bluetoothAdapter.bluetoothLeScanner.stopScan(scanCallback)
					isSearching = false
				}
			}, outTime)
		} else {

			//AndroidAPI 21 前的旧搜索方式
			val scanCallback: (BluetoothDevice, Int, ByteArray) -> Unit =
				{ device: BluetoothDevice, rssi: Int, scanRecord: ByteArray ->
					if (device.address != null) {
						//过滤重复
						val isContains = searchedDeviceList.any { myDevice ->
							myDevice.address == device.address
						}
						//不重复时保存
						if (!isContains) {
							searchedDeviceList.add(device)
							searchCallback.invoke(device)
						}
						searchCallback.invoke(device)
					}
				}
			bluetoothAdapter.startLeScan(scanCallback)

			if (stopScanTimer != null)//关闭上次计时
				clearStopScanTimer()
			//开启 停止搜索计时器
			stopScanTimer = Timer()
			stopScanTimer!!.schedule(object : TimerTask() {
				override fun run() {
					bluetoothAdapter.stopLeScan(scanCallback)
					isSearching = false
				}
			}, outTime)
		}

	}

	/**清除停止搜索计时器*/
	private fun clearStopScanTimer() {
		stopScanTimer?.cancel()
		stopScanTimer?.purge()
		stopScanTimer = null
	}

	//======================连接模块======================

	private var selectedAddress = ""

	fun connection(address: String) {
		if (!BluetoothAdapter.checkBluetoothAddress(address))
			return

		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)

		//连接蓝牙来获得蓝牙信息 PS:按照Android官方的说法,连接前会自动提示绑定 - 注：如果两台设备之前尚未配对，则在连接过程中，Android 框架会自动向用户显示配对请求通知或对话框（如图 3 所示）。因此，在尝试连接设备时，您的应用无需担心设备是否已配对。 您的 RFCOMM 连接尝试将被阻塞，直至用户成功完成配对或配对失败（包括用户拒绝配对、配对失败或超时）。
		device.connectGatt(this, false, object : BluetoothGattCallback() {
			/**连接状态改变,著名的 22、133 都出自这里
			 * 要注意 [status]和[newState]是两个数据,而不是一个数据的变化前与变化后的值*/
			override fun onConnectionStateChange(
				gatt: BluetoothGatt?, status: Int, newState: Int
			) {
				super.onConnectionStateChange(gatt, status, newState)

				Log.d(TAG, "onConnectionStateChange: status = $status; newState = $newState")

				if (status == BluetoothGatt.GATT_SUCCESS
					&& newState == BluetoothProfile.STATE_CONNECTED
				) {
					//连接只是连接,不能通讯，发现服务后才算是准备好通讯了
					Log.d(TAG, "连接成功")
					gatt?.discoverServices()
				}
			}

			/**发现了连接服务*/
			override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
				super.onServicesDiscovered(gatt, status)
				//这里才可以看到service

				Log.d(TAG, "发现连接通道")
				//保存特征码列表
				val characteristicMap = mutableMapOf<String, String>()
				for (service in gatt!!.services) {
					//characteristic(特征码)列表
					for (characteristic in service.characteristics) {
						characteristicMap[characteristic.uuid.toString()] =
								service.uuid.toString()
					}
				}
				val deviceInfo = DeviceInfo(address, device.name, characteristicMap)

				"连接了$address\n$deviceInfo".run {
					Log.d(TAG, this)
					runOnUiThread {
						Toast.makeText(baseContext, this, Toast.LENGTH_LONG).show()
					}
				}

				gatt.disconnect()//断开连接
			}
		})
	}
}
