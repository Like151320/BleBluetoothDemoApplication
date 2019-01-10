package com.like.blebluetoothdemoapplication.bleLib

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.like.blebluetoothdemoapplication.MLog
import com.like.blebluetoothdemoapplication.R
import com.like.blebluetoothdemoapplication.bleLib.bleBond.BleBondManager
import com.like.blebluetoothdemoapplication.bleLib.bleConnect.BleConnectManager
import com.like.blebluetoothdemoapplication.bleLib.bleScan.BleScanManager
import com.like.blebluetoothdemoapplication.bleLib.bleScan.IScanResultChangedCallback
import kotlinx.android.synthetic.main.activity_my_ble.*
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 14:17
 * 作用:
 */
class BleLibDemoActivity : AppCompatActivity(),
	IScanResultChangedCallback {

	private lateinit var listAdapter: ArrayAdapter<String>
	private var selectedAddress: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_my_ble)

		//init UI
		listAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
		listView.setOnItemClickListener { parent, view, position, id ->
			selected(listAdapter.getItem(position)!!)
		}
		listView.adapter = listAdapter
		listAdapter.add("蓝牙地址列表")
		listAdapter.notifyDataSetChanged()

		//初始化
		BleConnectManager.initialize(application)

		//扫描结果
		BleScanManager.addScanResultChangedCallback(this)

		BleBondManager.init(this)
	}

	fun search(v: View) {
		BleScanManager.scanBle()
	}

	override fun onScanResultChanged(scanResults: List<ScanResult>) {
		listAdapter.clear()
		listAdapter.addAll(scanResults.map { it.device.address })
	}

	private fun selected(address: String) {
		selectedAddress = address
	}

	fun permission(v: View) {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedAddress)

		//创建deviceManager
		BleConnectManager.getBleDevice(device, StepDevice::class.java).run {
			//回调监听
			this.notifyCallback.add { c, v ->
				MLog.d("返回信息- $c : ${Arrays.toString(v)}")
			}
			//连接
			MLog.d("开始连接")
			this.connect()
			//开始拿步数
			this.requestStep()
				?.done {
					MLog.d("成功发送步数指令")
				}?.enqueue()


			null
		}
	}

	fun connection(v: View) {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedAddress)

		BleBondManager.createBond(device) {
			MLog.d("绑定成功?:$it")
		}
	}

	fun gotoStream(v: View) {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedAddress)

		val b = BleBondManager.removeBond(device) {
			MLog.d("解绑成功?:$it")
		}
		MLog.d("发起解绑?:$b")
		/*//创建deviceManager
		BleConnectManager.getBleDevice(device, StepDevice::class.java).run {
			//回调监听
			this.notifyCallback.add { c, v ->
				MLog.d("返回信息2- $c : ${Arrays.toString(v)}")
			}
			//连接
			MLog.d("开始连接2")
			this.connect()
			//开始拿步数
			this.requestStep()?.done {
				MLog.d("成功发送步数指令2")
			}?.enqueue()

			null
		}*/
	}

	override fun onDestroy() {
		BleScanManager.removeScanResultChangedCallback(this)
		super.onDestroy()
	}
}
/*

11-57-12_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-3
11-57-12_d：NewHomeFrament-手环连接成功
11-57-12_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@d4496f0
11-57-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-12_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-12_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@baa5eee 写入数据=[3] 写入结果=false
11-57-12_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@d4496f0
11-57-12_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@baa5eee 写入数据=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4] 写入结果=false
11-57-12_d：HandBondPresenter.checkHandDisconnection(143) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-12_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=22 newState=0
11-57-12_i：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(374) : gatt.close()
11-57-12_d：NewHomeFrament - 开始连接
11-57-12_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-12_d：NewHomeFrament-手环连接成功
11-57-12_d：AndBleDevice$5.onConnectionStateChange(378) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-12_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@baa5eee 写入数据=[3] 写入结果=true
11-57-12_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@d4496f0
11-57-12_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-22
11-57-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-40_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-40_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-57-40_d：NewHomeFrament - 开始连接
11-57-40_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
11-57-40_i：NewHomeFrament开始连接手环,已监听否-true,连接状态22
11-57-40_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
11-57-40_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
11-57-40_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-40_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-57-40_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-57-40_d：AndBleScanner.scanBLE(127) -> HandBondPresenter.getBondDevice(401) : F8:50:A7:DC:BB:41
11-57-40_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
11-57-40_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(124) : 搜索的设备已绑定-{F8:50:A7:DC:BB:41}
11-57-40_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-57-40_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
11-57-40_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
11-57-40_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@8fd7187 - 127
11-57-40_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
11-57-40_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-57-40_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
11-57-40_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(90) : 手环当前未连接 - F8:50:A7:DC:BB:41
11-57-40_d：HandBondPresenter.isBond(364) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-57-40_d：HandBondPresenter.removeBond(319) -> HandBondPresenter$1.onUnBondResult(98) : 手环解绑成功 - false-F8:50:A7:DC:BB:41
11-57-40_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(93) : 手环已绑定 - F8:50:A7:DC:BB:41
11-57-40_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-99
11-57-40_d：HandBondPresenter$1.onUnBondResult(114) -> AndBleDevice$4$2.onBondResult(301) : error = 1
11-57-40_d：HandBondPresenter.checkHandBond(95) -> HandBondPresenter.removeBond(324) : removeBond() - false
11-57-40_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-57-40_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-57-40_d：首页空白 - GridView添加了数据
11-57-40_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092679905)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-57-40_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-40_d：首页空白 - GridView添加了数据
11-57-40_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092679905)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-57-40_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-57-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-44_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
11-57-44_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
11-57-44_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
11-57-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-48_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
11-57-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-52_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-52_i：NewHomeFrament开始连接手环,已监听否-true,连接状态99
11-57-52_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-57-52_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-57-52_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-57-52_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
11-57-52_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
11-57-52_d：NewHomeFrament - 开始连接
11-57-52_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
11-57-52_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-52_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
11-57-52_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-57-52_d：首页空白 - GridView添加了数据
11-57-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092691749)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-57-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-57-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092691749)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-57-52_d：首页空白 - GridView添加了数据
11-57-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-57-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-52_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
11-57-52_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
11-57-52_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-57-52_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
11-57-52_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(90) : 手环当前未连接 - F8:50:A7:DC:BB:41
11-57-52_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
11-57-52_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@45c3f66 - 127
11-57-52_d：HandBondPresenter.isBond(364) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-57-52_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(119) : 手环未绑定 - F8:50:A7:DC:BB:41
11-57-52_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(130) : 发起绑定 - F8:50:A7:DC:BB:41-true
11-57-52_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-57-52_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-57-52_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
11-57-52_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
11-57-52_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
11-57-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-57-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-58-00_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
11-58-00_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
11-58-00_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
11-58-00_d：HandBondPresenter$3$2.run(236) -> HandBondPresenter$2.onBondResult(124) : 手环绑定成功 - F8:50:A7:DC:BB:41true
11-58-00_d：HandBondPresenter$2.onBondResult(127) -> AndBleDevice$4$2.onBondResult(301) : error = 0
11-58-00_i：BondStateBroadcastReceiver.onReceive(47) -> HandBondPresenter$3.bondStateChanged(245) : 绑定回调走完了-F8:50:A7:DC:BB:41
11-58-00_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=0 newState=2
11-58-00_d：BluetoothGatt$1$5.run(306) -> AndBleDevice$5.onServicesDiscovered(408) : status=0
11-58-00_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(478) : service =android.bluetooth.BluetoothGattService@35cdf9f
11-58-00_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(473) : android.bluetooth.BluetoothGatt@d49d53e, [NotifyCharacteristic 6e400001-b5a3-f393-e0a9-e50e24dcca9e;6e400003-b5a3-f393-e0a9-e50e24dcca9e]
11-58-00_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(481) : charac =android.bluetooth.BluetoothGattCharacteristic@ea7d8ec
11-58-00_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(484) : bRet=true
11-58-00_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-3
		-- 连接成功 ----

11-58-00_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-00_d：NewHomeFrament-手环连接成功
11-58-00_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@35cdf9f
11-58-00_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@35cdf9f
11-58-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 58, 0, 0, 4]
11-58-00_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@41774b5 写入数据=[4, 24, 20, 19, 1, 10, 11, 58, 0, 0, 4] 写入结果=false
11-58-00_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@41774b5 写入数据=[3] 写入结果=false
11-58-00_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@35cdf9f
11-58-00_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@41774b5 写入数据=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4] 写入结果=false
11-58-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-58-01_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@35cdf9f
11-58-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 11, 58, 0, 0, 4]
11-58-01_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@41774b5 写入数据=[4, 24, 20, 19, 1, 10, 11, 58, 0, 0, 4] 写入结果=true
11-58-01_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@35cdf9f
11-58-01_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@41774b5 写入数据=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4] 写入结果=true
11-58-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 11, 57, 12, 0, 4]
11-58-01_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
11-58-07_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=0 newState=0
11-58-07_i：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(365) : gatt.close()
11-58-07_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-4
11-58-07_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-07_d：NewHomeFrament - 开始连接
11-58-07_i：NewHomeFrament开始连接手环,已监听否-true,连接状态7
11-58-07_d：NewHomeFrament - 开始连接
11-58-07_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-58-07_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-07_d：首页空白 - GridView添加了数据
11-58-07_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092707608)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-58-07_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-08_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092707608)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-58-08_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-58-08_d：首页空白 - GridView添加了数据
11-58-19_d：DeviceBlueToothManager.connectionHandler(85) -> AndBleDevice.connect(241) : connect() mDeviceAddress=A0:11:37:11:22:3B
11-58-19_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=A0:11:37:11:22:3B
11-58-19_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - A0:11:37:11:22:3B-2
11-58-19_d：DeviceBlueToothManager.connectionHandler(85) -> AndBleDevice.connect(241) : connect() mDeviceAddress=
11-58-19_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-58-19_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-19_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=A0:11:37:11:22:3B
11-58-21_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-58-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-21_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
11-58-21_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
11-58-22_i：NewHomeFrament开始连接手环,已监听否-true,连接状态4
11-58-22_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-22_d：NewHomeFrament - 开始连接
11-58-22_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-58-22_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
11-58-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-22_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-58-22_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
11-58-22_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-22_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
11-58-22_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
11-58-22_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-58-22_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
11-58-22_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(124) : 搜索的设备已绑定-{F8:50:A7:DC:BB:41}
11-58-22_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
11-58-22_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
11-58-22_d：AndBleScanner.scanBLE(127) -> HandBondPresenter.getBondDevice(401) : F8:50:A7:DC:BB:41
11-58-22_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
11-58-22_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-58-22_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(85) : 手环当前已连接 - F8:50:A7:DC:BB:41
11-58-22_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@d2f1a3c - 127
11-58-22_d：HandBondPresenter.checkHandBond(87) -> AndBleDevice$4$2.onBondResult(301) : error = 0
11-58-22_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-58-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-22_d：首页空白 - GridView添加了数据
11-58-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092721780)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-58-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-22_d：首页空白 - GridView添加了数据
11-58-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092721780)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-58-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-58-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-22_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=A0:11:37:11:22:3B
11-58-22_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-58-24_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-58-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-24_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
11-58-24_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
11-58-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-25_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=A0:11:37:11:22:3B
11-58-25_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-58-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-58-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
11-58-28_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=0 newState=2
11-58-28_d：BluetoothGatt$1$5.run(306) -> AndBleDevice$5.onServicesDiscovered(408) : status=0
11-58-28_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(478) : service =android.bluetooth.BluetoothGattService@40a2ce4
11-58-28_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(481) : charac =android.bluetooth.BluetoothGattCharacteristic@e5ac54d
11-58-28_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(484) : bRet=true
11-58-28_d：NewHomeFrament-手环连接成功
11-58-28_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(473) : android.bluetooth.BluetoothGatt@474a877, [NotifyCharacteristic 6e400001-b5a3-f393-e0a9-e50e24dcca9e;6e400003-b5a3-f393-e0a9-e50e24dcca9e]
11-58-28_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-28_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-3
11-58-28_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@40a2ce4
11-58-28_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@b51a802 写入数据=[3] 写入结果=false
11-58-28_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=22 newState=0
11-58-28_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-28_d：HandBondPresenter.checkHandDisconnection(143) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-28_i：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(374) : gatt.close()
11-58-28_d：NewHomeFrament - 开始连接
11-58-28_d：NewHomeFrament-手环连接成功
11-58-28_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-22
11-58-28_d：AndBleDevice$5.onConnectionStateChange(378) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态22
11-58-51_d：NewHomeFrament - 开始连接
11-58-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-58-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
11-58-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-58-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
11-58-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
11-58-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
11-58-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-58-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(124) : 搜索的设备已绑定-{F8:50:A7:DC:BB:41}
11-58-51_d：AndBleScanner.scanBLE(127) -> HandBondPresenter.getBondDevice(401) : F8:50:A7:DC:BB:41
11-58-51_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
11-58-51_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
11-58-51_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-58-51_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
11-58-51_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@b4c538b - 127
11-58-51_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
11-58-51_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(90) : 手环当前未连接 - F8:50:A7:DC:BB:41
11-58-51_d：HandBondPresenter.isBond(364) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-58-51_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(93) : 手环已绑定 - F8:50:A7:DC:BB:41
11-58-51_d：HandBondPresenter.removeBond(319) -> HandBondPresenter$1.onUnBondResult(98) : 手环解绑成功 - false-F8:50:A7:DC:BB:41
11-58-51_d：HandBondPresenter$1.onUnBondResult(114) -> AndBleDevice$4$2.onBondResult(301) : error = 1
11-58-51_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-99
11-58-51_d：HandBondPresenter.checkHandBond(95) -> HandBondPresenter.removeBond(324) : removeBond() - false
11-58-51_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-58-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-51_d：首页空白 - GridView添加了数据
11-58-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092751811)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-58-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092751811)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-58-52_d：首页空白 - GridView添加了数据
11-58-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-58-55_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
11-58-55_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
11-58-55_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
11-58-56_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
11-58-59_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-59_d：NewHomeFrament - 开始连接
11-58-59_i：NewHomeFrament开始连接手环,已监听否-true,连接状态99
11-58-59_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
11-58-59_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
11-58-59_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-58-59_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-58-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-58-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
11-58-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
11-58-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-58-59_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092759608)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-58-59_d：首页空白 - GridView添加了数据
11-58-59_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-58-59_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092759608)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-58-59_d：首页空白 - GridView添加了数据
11-58-59_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-59-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-59-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-59-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
11-59-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
11-59-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
11-59-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-59-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
11-59-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-59-07_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
11-59-07_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
11-59-07_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
11-59-07_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@e3a0298 - 127
11-59-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
11-59-07_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
11-59-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(90) : 手环当前未连接 - F8:50:A7:DC:BB:41
11-59-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(119) : 手环未绑定 - F8:50:A7:DC:BB:41
11-59-07_d：HandBondPresenter.isBond(364) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
11-59-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(130) : 发起绑定 - F8:50:A7:DC:BB:41-true
11-59-07_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
11-59-07_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
11-59-07_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
11-59-07_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
11-59-07_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
11-59-12_d：HandBondPresenter$2.onBondResult(127) -> AndBleDevice$4$2.onBondResult(301) : error = 0
11-59-12_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
11-59-12_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=0 newState=2
11-59-12_d：HandBondPresenter$3$2.run(236) -> HandBondPresenter$2.onBondResult(124) : 手环绑定成功 - F8:50:A7:DC:BB:41true
11-59-12_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
11-59-12_i：BondStateBroadcastReceiver.onReceive(47) -> HandBondPresenter$3.bondStateChanged(245) : 绑定回调走完了-F8:50:A7:DC:BB:41
11-59-12_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
11-59-12_d：BluetoothGatt$1$5.run(306) -> AndBleDevice$5.onServicesDiscovered(408) : status=0
11-59-12_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(473) : android.bluetooth.BluetoothGatt@562e04f, [NotifyCharacteristic 6e400001-b5a3-f393-e0a9-e50e24dcca9e;6e400003-b5a3-f393-e0a9-e50e24dcca9e]
11-59-12_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(478) : service =android.bluetooth.BluetoothGattService@78bc3dc
11-59-12_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(481) : charac =android.bluetooth.BluetoothGattCharacteristic@4579ce5
11-59-12_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(484) : bRet=true
11-59-12_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-12_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-3


	-- 连接成功 --


11-59-12_d：NewHomeFrament-手环连接成功
11-59-12_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[3] 写入结果=false
11-59-12_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-12_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 11, 59, 12, 0, 4]
11-59-13_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 11, 59, 12, 0, 4] 写入结果=true
11-59-13_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
11-59-29_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-29_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-59-29_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
11-59-29_d：首页空白 - GridView添加了数据
11-59-29_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092789639)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-59-29_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-30_d：首页空白 - GridView添加了数据
11-59-30_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092789623)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-59-30_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-59-30_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
11-59-33_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
11-59-33_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-33_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-59-33_d：首页空白 - GridView添加了数据
11-59-33_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092793405)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-59-33_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-33_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092793405)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-59-33_d：首页空白 - GridView添加了数据
11-59-34_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
11-59-37_d：NewHomeFrament-手环连接成功
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[3] 写入结果=false
11-59-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 11, 59, 37, 0, 4]
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 11, 59, 37, 0, 4] 写入结果=true
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[18, 0] 写入结果=false
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-37_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[19, 0] 写入结果=false
11-59-38_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
11-59-38_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
11-59-38_d：HandRingSettingActivity设备返回数据:[1, 0]
11-59-39_d：NewHomeFrament-手环连接成功
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4] 写入结果=false
11-59-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4]
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[3] 写入结果=false
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[18, 0] 写入结果=false
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-39_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[19, 0] 写入结果=false
11-59-39_d：版本号:V1.1
11-59-39_d：CID 核对成功
11-59-40_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
11-59-40_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-40_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4] 写入结果=false
11-59-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4]
11-59-41_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-41_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4] 写入结果=false
11-59-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4]
11-59-41_d：CID 核对成功
11-59-41_d：版本号:V1.1
11-59-42_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
11-59-42_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4] 写入结果=true
11-59-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 11, 59, 39, 0, 4]
11-59-43_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
11-59-43_d：HandRingSettingActivity设备返回数据:[1, 0]
11-59-43_d：HandRingSettingActivity设备返回数据:[1, 0]
11-59-44_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-44_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
11-59-44_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
11-59-44_d：首页空白 - GridView添加了数据
11-59-44_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092803951)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
11-59-44_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
11-59-44_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092803951)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
11-59-44_d：首页空白 - GridView添加了数据
11-59-44_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-00-03_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[3, 30]
12-00-03_d：HandRingSettingActivity设备返回数据:[1, 0]
12-00-03_d：HandRingSettingActivity设备返回数据:[1, 0]
12-00-04_d：NewHomeFrament-手环连接成功
12-00-04_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
12-00-04_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 12, 0, 4, 0, 4] 写入结果=true
12-00-04_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
12-00-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 12, 0, 4, 0, 4]
12-00-04_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[3] 写入结果=true
12-00-04_i：后台计步获取: 0
12-00-06_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
12-00-06_i：计步写入 data=[1] ;lWrite=false
12-00-07_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
12-00-07_d：HandRingSettingActivity设备返回数据:[1, 0]
12-00-07_d：HandRingSettingActivity设备返回数据:[1, 0]
12-00-10_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-00-10_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-00-10_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
12-00-10_d：首页空白 - GridView添加了数据
12-00-10_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092829701)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-00-10_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-00-10_d：首页空白 - GridView添加了数据
12-00-10_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092829701)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-00-10_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-00-40_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-00-40_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
12-00-40_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-00-40_d：首页空白 - GridView添加了数据
12-00-40_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092859764)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-00-40_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-00-40_d：首页空白 - GridView添加了数据
12-00-40_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092859764)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-00-40_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-01-07_d：HandBondPresenter.checkHandDisconnection(143) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-01-07_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=8 newState=0
12-01-07_i：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(374) : gatt.close()

	 -- 因距离太远断开 --

12-01-07_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-01-07_d：NewHomeFrament - 开始连接
12-01-07_d：NewHomeFrament-手环连接成功
12-01-07_d：AndBleDevice$5.onConnectionStateChange(378) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-01-07_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc
12-01-07_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4] 写入结果=false
12-01-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-07_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@78bc3dc

	-- 断开，并设 device.state = 8

12-01-07_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-8
12-01-07_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@939c3ba 写入数据=[3] 写入结果=false
12-01-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-19_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41

	-- 首页尝试重练

12-01-19_d：NewHomeFrament - 开始连接
12-01-19_i：NewHomeFrament开始连接手环,已监听否-true,连接状态8
12-01-19_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-01-19_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-01-19_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-01-19_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-01-19_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-01-19_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-01-19_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-01-19_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-01-19_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(124) : 搜索的设备已绑定-{F8:50:A7:DC:BB:41}
12-01-19_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-01-19_d：AndBleScanner.scanBLE(127) -> HandBondPresenter.getBondDevice(401) : F8:50:A7:DC:BB:41
12-01-19_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
12-01-19_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-01-19_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
12-01-19_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@6e44eeb - 127
12-01-19_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
12-01-19_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
12-01-19_d：HandBondPresenter.isBond(364) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-01-19_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(90) : 手环当前未连接 - F8:50:A7:DC:BB:41
12-01-19_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(93) : 手环已绑定 - F8:50:A7:DC:BB:41
12-01-19_d：HandBondPresenter.checkHandBond(95) -> HandBondPresenter.removeBond(324) : removeBond() - true
12-01-19_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
12-01-19_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-01-19_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-01-19_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
12-01-19_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092898716)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-01-19_d：首页空白 - GridView添加了数据
12-01-19_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-01-19_d：首页空白 - GridView添加了数据
12-01-19_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092898716)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-01-19_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-01-19_d：HandBondPresenter$4.bondStateChanged(306) -> HandBondPresenter$1.onUnBondResult(98) : 手环解绑成功 - true-F8:50:A7:DC:BB:41
12-01-19_d：HandBondPresenter$4.bondStateChanged(306) -> HandBondPresenter$1.onUnBondResult(110) : 发起绑定 - F8:50:A7:DC:BB:41-true
12-01-19_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
12-01-19_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
12-01-19_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
12-01-19_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
12-01-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-49_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
12-01-49_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
12-01-49_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(38) : 配对状态 - 配对已解除 - F8:50:A7:DC:BB:41
12-01-49_i：BondStateBroadcastReceiver.onReceive(47) -> HandBondPresenter$3.bondStateChanged(245) : 绑定回调走完了-F8:50:A7:DC:BB:41
12-01-49_d：HandBondPresenter$3$2.run(240) -> HandBondPresenter$1$1.onBondResult(104) : 手环绑定成功 - F8:50:A7:DC:BB:41false
12-01-49_d：HandBondPresenter$1$1.onBondResult(107) -> AndBleDevice$4$2.onBondResult(301) : error = 2
12-01-49_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-100
12-01-49_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-100

	--	至此第一次连接失败,解绑了但配对不上

12-01-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-01-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-08_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-02-08_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-02-08_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-08_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消

	-- 第二次连接

12-02-08_d：NewHomeFrament - 开始连接
12-02-08_i：NewHomeFrament开始连接手环,已监听否-true,连接状态100
12-02-08_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-02-08_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-02-08_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-02-08_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-08_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-02-08_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-02-08_d：首页空白 - GridView添加了数据
12-02-08_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092947888)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-02-08_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-08_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092947888)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-02-08_d：首页空白 - GridView添加了数据
12-02-08_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-02-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-17_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel

	-- 第二次连接搜不到，开始重复的搜索

12-02-17_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-17_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-02-17_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-02-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-18_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
12-02-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-18_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-02-18_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-18_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-02-18_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-02-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-02-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态2
12-02-21_d：首页空白 - GridView添加了数据
12-02-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092961591)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-02-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092961591)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-02-21_d：首页空白 - GridView添加了数据
12-02-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-02-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-24_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-02-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-24_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-02-24_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-02-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-25_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-02-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-25_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-25_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-02-25_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-02-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-31_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-02-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-31_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-02-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-02-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41

	因没搜到，连接状态为 5 ，mDevManager.getDevice(mac) == null 时 得到 5

12-02-51_d：NewHomeFrament - 开始连接
12-02-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-02-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-02-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-02-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-02-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-02-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-02-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092991638)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-02-51_d：首页空白 - GridView添加了数据
12-02-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547092991638)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-02-52_d：首页空白 - GridView添加了数据
12-02-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-02-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-02-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-02-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-02-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-02-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-02-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-02-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-02-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-02-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-02-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-03-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-03-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-03-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-03-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-03-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-03-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-03-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-03-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-03-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-03-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-03-21_d：NewHomeFrament - 开始连接
12-03-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-03-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-03-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-03-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-03-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-03-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093021653)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-03-21_d：首页空白 - GridView添加了数据
12-03-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-21_d：首页空白 - GridView添加了数据
12-03-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093021653)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-03-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-03-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-03-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-03-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-03-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-03-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-03-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-03-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-03-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-03-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-03-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-03-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-03-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-03-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-03-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-03-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-51_d：NewHomeFrament - 开始连接
12-03-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-03-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-03-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-03-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-03-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-03-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-03-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-03-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-03-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093051622)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-03-51_d：首页空白 - GridView添加了数据
12-03-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-52_d：首页空白 - GridView添加了数据
12-03-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093051622)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-03-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-03-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-03-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-03-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-03-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-03-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-03-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-03-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-03-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-03-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-03-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-04-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-04-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-04-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-04-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-04-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-04-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-04-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-04-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-04-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-04-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-04-21_d：NewHomeFrament - 开始连接
12-04-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-04-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-04-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-04-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-04-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-04-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093081653)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-04-21_d：首页空白 - GridView添加了数据
12-04-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093081653)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-04-22_d：首页空白 - GridView添加了数据
12-04-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-04-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-04-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-04-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-04-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-04-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-04-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-04-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-04-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-04-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-04-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-04-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-04-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-04-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-04-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-04-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-04-51_d：NewHomeFrament - 开始连接
12-04-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-04-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-04-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-04-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-04-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-04-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-04-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-04-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093111684)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-04-51_d：首页空白 - GridView添加了数据
12-04-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-52_d：首页空白 - GridView添加了数据
12-04-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093111684)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-04-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-04-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-04-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-04-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-04-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-04-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-04-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-04-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-04-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-04-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-04-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-05-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-05-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-05-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-05-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-05-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-05-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-05-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-05-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-21_d：NewHomeFrament - 开始连接
12-05-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-05-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-05-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-05-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-05-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-05-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-05-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-05-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-05-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093141684)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-05-22_d：首页空白 - GridView添加了数据
12-05-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093141684)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-05-22_d：首页空白 - GridView添加了数据
12-05-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-05-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-05-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-05-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-05-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-05-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-05-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-29_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-05-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-05-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-05-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-05-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-05-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-05-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-05-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-05-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-05-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-05-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-05-51_d：NewHomeFrament - 开始连接
12-05-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-05-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-05-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-05-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-05-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-05-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-05-52_d：首页空白 - GridView添加了数据
12-05-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093171762)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-05-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-52_d：首页空白 - GridView添加了数据
12-05-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093171762)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-05-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-05-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-05-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-05-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-05-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-05-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-05-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-05-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-05-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-05-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-05-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-06-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-06-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-06-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-06-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-06-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-06-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-06-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-06-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-06-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-06-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-06-21_d：NewHomeFrament - 开始连接
12-06-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-06-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-06-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-06-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-06-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-06-21_d：首页空白 - GridView添加了数据
12-06-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093201683)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-06-22_d：首页空白 - GridView添加了数据
12-06-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-06-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093201683)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-06-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-06-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-06-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-06-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-06-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-06-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-06-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-06-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-06-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-06-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-06-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-06-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-06-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-06-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-06-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-06-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-06-51_d：NewHomeFrament - 开始连接
12-06-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-06-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-06-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-06-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-06-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-06-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-06-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093231667)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-06-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-51_d：首页空白 - GridView添加了数据
12-06-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093231667)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-06-52_d：首页空白 - GridView添加了数据
12-06-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-06-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-06-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-06-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-06-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-06-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-06-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-06-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-06-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-06-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-06-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-07-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-07-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-07-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-07-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-07-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-07-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-07-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-07-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-21_d：NewHomeFrament - 开始连接
12-07-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-07-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-07-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-07-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-07-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-07-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-07-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-07-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-07-21_d：首页空白 - GridView添加了数据
12-07-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093261683)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-07-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-22_d：首页空白 - GridView添加了数据
12-07-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093261683)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-07-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-07-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-07-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-07-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-07-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-07-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-07-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-07-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-07-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-07-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-07-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-07-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-07-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-07-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-07-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-07-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-07-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-07-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-07-51_d：NewHomeFrament - 开始连接
12-07-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-07-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-07-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-07-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-07-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-07-51_d：首页空白 - GridView添加了数据
12-07-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093291698)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-07-52_d：首页空白 - GridView添加了数据
12-07-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093291698)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-07-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-07-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-07-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-07-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-07-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-07-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-07-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-07-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-07-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-07-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-07-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-08-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-08-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-08-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-08-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-08-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-08-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-08-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-08-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-08-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-08-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-08-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-08-21_d：NewHomeFrament - 开始连接
12-08-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-08-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-08-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-08-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-08-21_d：首页空白 - GridView添加了数据
12-08-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093321667)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-08-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-22_d：首页空白 - GridView添加了数据
12-08-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093321667)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-08-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-08-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-08-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-08-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-08-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-08-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-08-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-08-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-08-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-08-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-08-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-08-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-08-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-08-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-08-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-08-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-51_d：NewHomeFrament - 开始连接
12-08-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-08-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-08-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-08-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-08-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-08-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-08-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-08-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-08-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093351698)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-08-51_d：首页空白 - GridView添加了数据
12-08-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-52_d：首页空白 - GridView添加了数据
12-08-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093351698)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-08-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-08-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-08-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-08-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-08-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-08-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-08-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-08-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-08-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-08-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-08-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-09-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-09-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-09-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-09-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-09-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-09-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-09-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-09-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-09-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-09-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-09-21_d：NewHomeFrament - 开始连接
12-09-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-09-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-09-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-09-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-09-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-09-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093381713)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-09-22_d：首页空白 - GridView添加了数据
12-09-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093381713)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-09-22_d：首页空白 - GridView添加了数据
12-09-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-09-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-09-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-09-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-09-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-09-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-09-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-09-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-09-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-09-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-09-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-09-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-09-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-09-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-09-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-09-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-09-51_d：NewHomeFrament - 开始连接
12-09-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-09-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-09-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-09-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-09-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-09-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-09-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-09-51_d：首页空白 - GridView添加了数据
12-09-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093411666)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-09-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093411666)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-09-52_d：首页空白 - GridView添加了数据
12-09-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-09-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-09-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-09-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-09-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-09-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-09-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-09-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-09-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-09-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-09-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-10-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-10-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-10-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-10-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-10-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-10-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-10-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-10-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-21_d：NewHomeFrament - 开始连接
12-10-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-10-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-10-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-10-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-10-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-10-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-10-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-10-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-10-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093441666)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-10-22_d：首页空白 - GridView添加了数据
12-10-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-22_d：首页空白 - GridView添加了数据
12-10-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093441666)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-10-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-10-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-10-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-10-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-10-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-10-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-10-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-10-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-10-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-10-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-10-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-10-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-10-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-10-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-10-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-10-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-10-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-10-51_d：NewHomeFrament - 开始连接
12-10-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-10-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-10-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-10-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-10-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-10-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-10-52_d：首页空白 - GridView添加了数据
12-10-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093471713)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-10-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093471713)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-10-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-10-52_d：首页空白 - GridView添加了数据
12-10-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-10-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-10-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-10-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-10-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-10-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-10-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-10-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-10-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-10-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-11-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-11-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-11-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-11-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-11-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-11-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-11-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-11-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-11-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-11-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-11-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-11-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-11-21_d：NewHomeFrament - 开始连接
12-11-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-11-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-11-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-11-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093501728)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-11-22_d：首页空白 - GridView添加了数据
12-11-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-22_d：首页空白 - GridView添加了数据
12-11-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093501728)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-11-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-11-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-11-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-11-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-11-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-11-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-11-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-11-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-11-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-11-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-11-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-11-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-11-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-11-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-11-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-11-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-51_d：NewHomeFrament - 开始连接
12-11-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-11-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-11-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-11-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-11-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-11-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-11-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-11-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-11-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093531634)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-11-51_d：首页空白 - GridView添加了数据
12-11-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093531634)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-11-52_d：首页空白 - GridView添加了数据
12-11-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-11-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-11-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-11-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-11-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-11-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-11-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-11-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-11-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-11-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-11-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-12-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-12-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-12-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-12-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-12-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-12-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-12-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-12-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-21_d：NewHomeFrament - 开始连接
12-12-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-12-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-12-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-12-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-12-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-12-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-12-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-12-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-12-21_d：首页空白 - GridView添加了数据
12-12-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093561634)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-12-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093561634)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-12-22_d：首页空白 - GridView添加了数据
12-12-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-12-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-12-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-12-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-12-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-12-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-12-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-12-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-12-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-12-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-12-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-12-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-12-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-12-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-12-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-12-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-51_d：NewHomeFrament - 开始连接
12-12-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-12-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-12-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-12-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-12-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-12-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-12-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-12-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-12-51_d：首页空白 - GridView添加了数据
12-12-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093591681)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-12-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-52_d：首页空白 - GridView添加了数据
12-12-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093591681)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-12-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-12-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-12-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-12-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-12-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-12-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-12-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-12-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-12-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-12-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-12-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-13-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-13-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-13-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-13-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-13-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-13-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-13-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-13-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-13-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-13-21_d：NewHomeFrament - 开始连接
12-13-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-13-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-13-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-13-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-13-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-13-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-13-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093621681)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-13-22_d：首页空白 - GridView添加了数据
12-13-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-22_d：首页空白 - GridView添加了数据
12-13-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093621681)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-13-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-13-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-13-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-13-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-13-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-13-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-13-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-13-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-13-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-13-35_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-13-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-13-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-13-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-13-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-13-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-13-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-51_d：NewHomeFrament - 开始连接
12-13-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-13-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-13-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-13-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-13-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-13-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-13-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-13-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-13-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093651727)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-13-52_d：首页空白 - GridView添加了数据
12-13-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093651727)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-13-52_d：首页空白 - GridView添加了数据
12-13-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-13-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-13-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-13-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-13-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-13-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-13-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-13-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-13-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-13-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-13-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-14-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-14-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-14-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-14-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-14-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-14-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-14-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-14-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-14-21_d：NewHomeFrament - 开始连接
12-14-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-14-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-14-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-14-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-14-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-14-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-14-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-14-22_d：首页空白 - GridView添加了数据
12-14-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093681711)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-14-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-22_d：首页空白 - GridView添加了数据
12-14-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093681711)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-14-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-14-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-14-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-14-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-14-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-29_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-14-29_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-14-29_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-14-29_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-14-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-14-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-14-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-14-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-14-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-14-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-14-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-14-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-14-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-14-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-14-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-14-51_d：NewHomeFrament - 开始连接
12-14-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-14-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-14-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-14-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-14-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093711664)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-14-51_d：首页空白 - GridView添加了数据
12-14-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093711664)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-14-52_d：首页空白 - GridView添加了数据
12-14-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-14-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-14-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-14-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-14-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-14-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-14-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-14-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-14-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-14-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-14-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-15-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-15-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-15-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-15-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-15-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-15-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-15-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-15-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-15-21_d：NewHomeFrament - 开始连接
12-15-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-15-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-15-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-15-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-15-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-15-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-15-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-15-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093741695)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-15-22_d：首页空白 - GridView添加了数据
12-15-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-22_d：首页空白 - GridView添加了数据
12-15-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093741695)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-15-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-15-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-15-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-15-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-15-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-15-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-15-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-15-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-15-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-15-35_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-15-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-15-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-15-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-15-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-15-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-15-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-15-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-15-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-15-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-51_d：NewHomeFrament - 开始连接
12-15-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-15-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-15-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-15-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-15-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-15-52_d：首页空白 - GridView添加了数据
12-15-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093771664)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-15-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-52_d：首页空白 - GridView添加了数据
12-15-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093771664)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-15-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-15-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-15-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-15-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-15-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-15-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-15-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-15-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-15-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-15-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-15-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-16-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-16-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-16-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-16-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-16-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-16-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-16-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-16-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-16-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-16-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-16-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-21_d：NewHomeFrament - 开始连接
12-16-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-16-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-16-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-16-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-16-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-16-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093801679)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-16-21_d：首页空白 - GridView添加了数据
12-16-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-21_d：首页空白 - GridView添加了数据
12-16-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093801679)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-16-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-16-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-16-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-16-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-16-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-16-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-16-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-16-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-16-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-16-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-16-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-16-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-16-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-16-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-16-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-16-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-16-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-16-51_d：NewHomeFrament - 开始连接
12-16-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-16-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-16-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-16-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-16-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-16-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-16-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093831648)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-16-52_d：首页空白 - GridView添加了数据
12-16-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093831648)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-16-52_d：首页空白 - GridView添加了数据
12-16-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-16-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-16-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-16-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-16-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-16-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-16-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-16-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-16-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-16-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-16-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-17-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-17-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-17-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-17-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-17-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-17-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-17-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-17-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-17-21_d：NewHomeFrament - 开始连接
12-17-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-17-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-17-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-17-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-17-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-17-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-17-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-17-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-21_d：首页空白 - GridView添加了数据
12-17-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093861663)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-17-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093861663)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-17-22_d：首页空白 - GridView添加了数据
12-17-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-17-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-17-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-17-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-17-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-17-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-17-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-17-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-17-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-17-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-17-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-17-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-17-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-17-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-17-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-17-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-51_d：NewHomeFrament - 开始连接
12-17-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-17-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-17-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-17-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-17-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-17-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-17-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-17-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-17-51_d：首页空白 - GridView添加了数据
12-17-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093891663)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-17-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093891663)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-17-52_d：首页空白 - GridView添加了数据
12-17-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-17-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-17-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-17-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-17-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-17-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-17-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-17-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-17-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-17-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-17-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-18-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-18-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-18-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-18-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-18-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-18-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-18-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-18-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-18-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-18-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-18-21_d：NewHomeFrament - 开始连接
12-18-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-18-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-18-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-18-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-18-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-18-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-21_d：首页空白 - GridView添加了数据
12-18-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093921632)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-18-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093921632)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-18-21_d：首页空白 - GridView添加了数据
12-18-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-18-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-18-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-18-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-18-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-18-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-18-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-18-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-18-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-18-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-18-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-18-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-18-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-18-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-18-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-18-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-18-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-18-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-18-51_d：NewHomeFrament - 开始连接
12-18-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-18-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-18-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-18-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-18-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-18-52_d：首页空白 - GridView添加了数据
12-18-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093951710)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-18-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-52_d：首页空白 - GridView添加了数据
12-18-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093951710)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-18-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-18-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-18-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-18-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-18-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-18-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-18-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-18-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-18-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-18-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-18-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-19-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-19-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-19-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-19-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-19-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-19-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-19-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-19-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-19-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-19-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-19-21_d：NewHomeFrament - 开始连接
12-19-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-19-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-19-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-19-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-19-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-19-21_d：首页空白 - GridView添加了数据
12-19-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093981663)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-19-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-22_d：首页空白 - GridView添加了数据
12-19-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547093981663)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-19-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-19-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-19-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-19-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-19-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-19-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-19-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-19-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-19-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-19-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-19-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-19-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-19-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-19-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-19-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-19-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-51_d：NewHomeFrament - 开始连接
12-19-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-19-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-19-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-19-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-19-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-19-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-19-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-19-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-19-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094011709)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-19-51_d：首页空白 - GridView添加了数据
12-19-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094011709)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-19-52_d：首页空白 - GridView添加了数据
12-19-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-19-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-19-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-19-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-19-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-19-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-19-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-19-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-19-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-19-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-19-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-20-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-20-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-20-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-20-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-20-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-20-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-20-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-20-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-20-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-20-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-20-21_d：NewHomeFrament - 开始连接
12-20-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-20-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-20-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-20-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-20-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-20-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094041678)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-20-21_d：首页空白 - GridView添加了数据
12-20-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-22_d：首页空白 - GridView添加了数据
12-20-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094041678)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-20-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-20-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-20-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-20-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-20-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-20-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-20-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-20-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-20-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-20-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-20-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-20-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-20-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-20-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-20-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-20-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-20-51_d：NewHomeFrament - 开始连接
12-20-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-20-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-20-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-20-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-20-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-20-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-52_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-52_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-20-52_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-20-52_d：首页空白 - GridView添加了数据
12-20-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094071709)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-20-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-52_d：首页空白 - GridView添加了数据
12-20-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094071709)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-20-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-20-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-20-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-20-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-20-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-20-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-20-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-20-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-20-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-20-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-20-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-21-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-21-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-21-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-21-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-21-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-21-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-21-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-21-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-24_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-24_d：NewHomeFrament - 开始连接
12-21-24_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-21-24_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-21-24_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-21-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-24_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-21-24_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-21-24_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-21-24_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-24_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-21-24_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-21-24_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094101724)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-21-24_d：首页空白 - GridView添加了数据
12-21-24_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-24_d：首页空白 - GridView添加了数据
12-21-24_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094101724)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-21-24_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-21-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-30_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-21-30_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-30_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-21-30_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-21-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-31_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-21-31_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-31_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-21-31_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-21-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-37_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-21-37_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-21-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-21-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-38_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-38_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-21-38_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-38_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-21-38_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-21-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-44_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-21-44_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-44_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-21-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-21-51_d：NewHomeFrament - 开始连接
12-21-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-21-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-21-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-21-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-21-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-21-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-21-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-21-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094131693)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-21-51_d：首页空白 - GridView添加了数据
12-21-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-52_d：首页空白 - GridView添加了数据
12-21-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094131693)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-21-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-21-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-21-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-21-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-21-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-21-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-21-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-21-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-21-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-21-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-21-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-22-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-22-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-22-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-22-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-22-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-22-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-22-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-22-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-22-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-22-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-22-21_d：NewHomeFrament - 开始连接
12-22-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-22-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-22-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-22-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-22-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-22-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-22_d：首页空白 - GridView添加了数据
12-22-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094161693)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-22-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-22_d：首页空白 - GridView添加了数据
12-22-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094161693)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-22-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-22-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-22-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-22-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-22-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-22-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-22-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-22-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-22-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-22-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-22-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-22-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-22-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-22-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-22-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-22-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-22-51_d：NewHomeFrament - 开始连接
12-22-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-22-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-22-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-22-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-22-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-52_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-22-52_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-52_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-22-52_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-22-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094191739)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-22-52_d：首页空白 - GridView添加了数据
12-22-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-52_d：首页空白 - GridView添加了数据
12-22-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094191739)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-22-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-22-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-22-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-22-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-22-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-22-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-22-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-22-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-22-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-22-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-22-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-23-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-23-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-23-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-23-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-23-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-23-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-23-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-23-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-23-21_d：NewHomeFrament - 开始连接
12-23-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-23-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-23-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-23-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-23-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-23-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-23-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-23-22_d：首页空白 - GridView添加了数据
12-23-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094221677)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-23-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094221677)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-23-22_d：首页空白 - GridView添加了数据
12-23-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-23-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-23-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-23-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-23-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-23-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-23-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-23-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-23-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-23-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-23-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-23-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-23-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-23-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-23-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-23-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-52_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-52_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-23-52_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-23-52_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-23-52_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-23-52_d：NewHomeFrament - 开始连接
12-23-52_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-23-52_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-23-52_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-52_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-52_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-23-52_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-23-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094251802)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-23-52_d：首页空白 - GridView添加了数据
12-23-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094251802)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-23-52_d：首页空白 - GridView添加了数据
12-23-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-23-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-58_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-23-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-58_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-23-58_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-23-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-23-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-23-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-23-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-23-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-23-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-23-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-24-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-24-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-24-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-24-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-24-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-24-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-24-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-24-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-24-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-24-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-24-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-24-21_d：NewHomeFrament - 开始连接
12-24-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-24-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-24-21_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-24-21_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-21_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-24-21_d：首页空白 - GridView添加了数据
12-24-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094281661)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-24-21_d：首页空白 - GridView添加了数据
12-24-21_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094281661)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-24-21_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-24-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-24-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-24-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-24-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-24-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-24-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-24-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-24-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-24-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-24-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-24-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-24-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-24-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-24-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-24-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-24-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-24-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-24-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-24-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-24-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-24-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-51_d：NewHomeFrament - 开始连接
12-24-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-24-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-24-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094311692)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-24-51_d：首页空白 - GridView添加了数据
12-24-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094311692)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-24-52_d：首页空白 - GridView添加了数据
12-24-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-24-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-24-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-24-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-24-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-24-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-24-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-24-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-24-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-24-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-24-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-25-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-25-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-25-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-25-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-25-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-25-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-25-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-25-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-21_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-25-21_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-21_d：NewHomeFrament - 开始连接
12-25-21_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-25-21_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-25-21_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-25-21_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-25-21_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-25-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-22_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-22_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-25-22_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-25-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094341707)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-25-22_d：首页空白 - GridView添加了数据
12-25-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094341707)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-25-22_d：首页空白 - GridView添加了数据
12-25-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-25-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-25-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-25-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-25-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-29_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-29_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-25-29_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-29_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-25-29_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-25-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-35_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-25-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-35_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-25-35_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-25-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-25-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-25-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-25-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-25-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-25-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-25-51_d：NewHomeFrament - 开始连接
12-25-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-25-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-25-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-25-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-25-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-25-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-25-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-25-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094371707)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-25-52_d：首页空白 - GridView添加了数据
12-25-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-52_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094371707)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-25-52_d：首页空白 - GridView添加了数据
12-25-52_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-25-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-25-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-58_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-25-58_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-25-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-25-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-25-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-25-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-25-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-25-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-25-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-26-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-26-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-26-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-26-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-26-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-26-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-26-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-26-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-26-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-26-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-26-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-26-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-23_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-26-23_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-26-23_d：NewHomeFrament - 开始连接
12-26-23_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-26-23_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-26-23_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-26-23_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-26-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-26-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-26-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-26-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-26-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-26-23_d：首页空白 - GridView添加了数据
12-26-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094403519)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-26-23_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-26-23_d：首页空白 - GridView添加了数据
12-26-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094403519)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-26-24_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-26-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-26-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-26-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-26-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-26-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-43_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-26-43_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-26-43_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-26-43_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-26-43_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-26-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-26-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-10_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-27-10_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-10_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-27-10_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-27-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-27-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-27-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-27-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-27-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-27-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-28_d：NewHomeFrament - 开始连接
12-27-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-27-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-27-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-27-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-27-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-27-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-27-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-27-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-27-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094468238)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-27-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-28_d：首页空白 - GridView添加了数据
12-27-28_d：首页空白 - GridView添加了数据
12-27-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094468222)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-27-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-27-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-27-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-27-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-27-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-27-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-27-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-27-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-27-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-27-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-27-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-27-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-27-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-27-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-27-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-27-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-27-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-27-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-27-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-27-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-27-58_d：NewHomeFrament - 开始连接
12-27-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-27-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-27-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-27-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-27-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094498206)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-27-58_d：首页空白 - GridView添加了数据
12-27-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-27-58_d：首页空白 - GridView添加了数据
12-27-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094498206)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-27-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-27-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-27-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-28-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-28-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-28-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-28-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-28-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-28-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-28-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-28-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-28-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-28-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-28-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-28-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-28-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-28-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-28-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-28-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-28-28_d：NewHomeFrament - 开始连接
12-28-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-28-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-28-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-28-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-28-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-28-28_d：首页空白 - GridView添加了数据
12-28-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094528206)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-28-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094528206)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-28-28_d：首页空白 - GridView添加了数据
12-28-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-28-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-28-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-28-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-28-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-28-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-28-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-28-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-28-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-28-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-28-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-28-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-28-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-28-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-28-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-28-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-58_d：NewHomeFrament - 开始连接
12-28-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-28-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-28-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-28-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-28-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-28-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-28-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-28-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-28-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-28-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094558221)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-28-58_d：首页空白 - GridView添加了数据
12-28-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-28-58_d：首页空白 - GridView添加了数据
12-28-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094558221)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-28-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-28-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-28-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-29-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-29-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-29-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-29-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-29-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-29-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-29-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-29-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-29-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-29-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-29-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-29-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-29-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-29-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-29-28_d：NewHomeFrament - 开始连接
12-29-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-29-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-29-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-29-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-29-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-29-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-29-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-29-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094588206)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-29-28_d：首页空白 - GridView添加了数据
12-29-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094588206)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-29-28_d：首页空白 - GridView添加了数据
12-29-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-29-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-29-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-29-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-29-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-29-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-29-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-29-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-29-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-29-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-29-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-29-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-29-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-29-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-29-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-29-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-29-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-29-58_d：NewHomeFrament - 开始连接
12-29-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-29-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-29-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-29-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-29-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-29-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-29-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-29-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-29-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094618221)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-29-58_d：首页空白 - GridView添加了数据
12-29-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-29-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094618221)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-29-58_d：首页空白 - GridView添加了数据
12-29-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-29-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-30-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-30-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-30-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-30-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-30-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-30-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-30-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-30-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-30-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-30-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-30-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-30-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-30-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-30-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-30-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-28_d：NewHomeFrament - 开始连接
12-30-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-30-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-30-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-30-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-30-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-30-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-30-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-30-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094648190)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-30-28_d：首页空白 - GridView添加了数据
12-30-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-28_d：首页空白 - GridView添加了数据
12-30-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-30-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094648190)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-30-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-30-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-30-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-30-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-30-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-30-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-30-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-30-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-30-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-30-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-30-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-30-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-30-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-30-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-30-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-30-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-30-58_d：NewHomeFrament - 开始连接
12-30-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-30-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-30-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-30-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-30-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-30-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-30-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-30-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-30-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094678236)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-30-58_d：首页空白 - GridView添加了数据
12-30-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-30-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094678236)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-30-58_d：首页空白 - GridView添加了数据
12-30-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-30-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-31-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-31-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-31-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-31-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-31-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-31-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-31-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-31-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-31-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-31-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-31-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-31-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-31-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-31-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-31-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-31-28_d：NewHomeFrament - 开始连接
12-31-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-31-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-31-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-31-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-31-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-31-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-31-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094708189)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-31-28_d：首页空白 - GridView添加了数据
12-31-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-28_d：首页空白 - GridView添加了数据
12-31-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094708189)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-31-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-31-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-31-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-31-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-31-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-31-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-31-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-31-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-31-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-31-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-31-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-31-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-31-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-31-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-31-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-31-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-31-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-31-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-31-58_d：NewHomeFrament - 开始连接
12-31-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-31-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-31-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-31-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-31-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-31-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-31-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-31-58_d：首页空白 - GridView添加了数据
12-31-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094738205)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-31-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-31-58_d：首页空白 - GridView添加了数据
12-31-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094738205)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-31-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-31-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-32-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-32-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-32-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-32-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-32-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-32-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-32-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-32-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-32-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-32-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-32-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-32-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-32-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-32-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-32-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-32-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-32-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-32-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-32-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-32-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-28_d：NewHomeFrament - 开始连接
12-32-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-32-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-32-28_d：首页空白 - GridView添加了数据
12-32-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094768204)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-32-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094768204)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-32-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-32-28_d：首页空白 - GridView添加了数据
12-32-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-32-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-32-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-32-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-32-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-32-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-32-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-32-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-32-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-32-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-32-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-32-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-32-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-32-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-32-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-32-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-32-58_d：NewHomeFrament - 开始连接
12-32-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-32-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-32-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-32-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-32-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-32-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-32-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-32-58_d：首页空白 - GridView添加了数据
12-32-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094798204)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-32-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-32-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094798204)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-32-58_d：首页空白 - GridView添加了数据
12-32-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-32-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-32-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-33-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-33-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-33-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-33-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-33-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-33-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-33-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-33-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-33-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-33-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-33-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-33-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-33-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-33-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-28_d：NewHomeFrament - 开始连接
12-33-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-33-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-33-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-33-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-33-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-33-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-33-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-33-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-33-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094828235)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-33-28_d：首页空白 - GridView添加了数据
12-33-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094828235)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-33-28_d：首页空白 - GridView添加了数据
12-33-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-33-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-33-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-33-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-33-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-33-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-33-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-33-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-33-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-33-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-33-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-33-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-33-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-33-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-33-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-33-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-33-58_d：NewHomeFrament - 开始连接
12-33-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-33-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-33-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-33-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-33-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-33-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-33-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-33-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-33-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094858251)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-33-58_d：首页空白 - GridView添加了数据
12-33-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-33-58_d：首页空白 - GridView添加了数据
12-33-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094858251)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-33-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-33-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-33-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-34-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-34-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-34-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-34-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-34-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-34-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-34-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-34-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-34-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-34-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-34-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-34-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-34-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-34-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-34-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-34-28_d：NewHomeFrament - 开始连接
12-34-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-34-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-34-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-34-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-34-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-34-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-34-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094888219)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-34-28_d：首页空白 - GridView添加了数据
12-34-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094888219)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-34-28_d：首页空白 - GridView添加了数据
12-34-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-34-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-34-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-34-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-34-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-34-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-34-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-34-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-34-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-34-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-34-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-34-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-34-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-34-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-34-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-34-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-34-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-58_d：NewHomeFrament - 开始连接
12-34-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-34-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-34-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-34-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-34-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-34-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-34-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-34-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-34-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-34-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094918235)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-34-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-34-58_d：首页空白 - GridView添加了数据
12-34-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094918235)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-34-58_d：首页空白 - GridView添加了数据
12-34-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-34-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-35-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-35-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-35-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-35-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-35-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-35-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-35-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-35-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-35-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-35-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-35-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-35-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-35-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-35-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-28_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-28_d：NewHomeFrament - 开始连接
12-35-28_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-35-28_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-35-28_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-35-28_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-35-28_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-35-28_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-35-28_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-35-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-28_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-28_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-35-28_d：首页空白 - GridView添加了数据
12-35-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094948219)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-35-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-28_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094948219)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-35-28_d：首页空白 - GridView添加了数据
12-35-28_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-35-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-35-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-35-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-35-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-35-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-35-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-35-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-35-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-35-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-35-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-35-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-35-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-35-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-35-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-35-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-35-58_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-58_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-35-58_d：NewHomeFrament - 开始连接
12-35-58_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-35-58_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-35-58_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-35-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-35-58_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-35-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-35-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-35-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-35-58_d：首页空白 - GridView添加了数据
12-35-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094978234)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-35-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-35-58_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094978234)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-35-58_d：首页空白 - GridView添加了数据
12-35-58_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-35-58_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
12-35-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-02_i：NewHomeFrament开始连接手环,已监听否-true,连接状态2
12-36-02_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-02_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-36-02_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094981891)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-36-02_d：首页空白 - GridView添加了数据
12-36-02_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-02_d：首页空白 - GridView添加了数据
12-36-02_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547094981891)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-36-02_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-36-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-04_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-36-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-36-04_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-36-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-05_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-36-05_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-36-05_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-05_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-36-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-36-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-36-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-36-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-12_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-12_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-36-12_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-36-12_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-36-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-22_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-36-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-36-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-41_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-36-41_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-41_d：NewHomeFrament - 开始连接
12-36-41_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-36-41_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-36-41_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-36-41_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-36-41_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-36-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-41_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-36-41_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-36-41_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-41_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095021312)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-36-41_d：首页空白 - GridView添加了数据
12-36-41_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-41_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095021312)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-36-41_d：首页空白 - GridView添加了数据
12-36-41_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-36-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-54_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-36-54_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-36-54_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-54_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-36-55_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-36-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-55_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-36-55_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-36-55_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-36-55_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-36-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-36-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-02_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-37-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-37-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-37-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-04_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-37-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-04_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-37-04_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-37-04_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-37-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-14_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-14_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-37-14_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-37-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-26_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-37-26_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-37-26_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-26_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-37-26_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-37-26_d：NewHomeFrament - 开始连接
12-37-26_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-37-26_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-37-26_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-37-26_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-37-26_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-37-26_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-37-26_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095066171)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-37-26_d：首页空白 - GridView添加了数据
12-37-26_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-37-26_d：首页空白 - GridView添加了数据
12-37-26_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095066171)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-37-26_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-37-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-37_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-37-37_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-37-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-37-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-39_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-37-39_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-39_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-37-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-39_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-37-39_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-37-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-46_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-37-46_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-46_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-37-46_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-37-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-47_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-47_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-37-47_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-37-47_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-37-47_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-37-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-55_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-37-56_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-37-56_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-37-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-37-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-06_d：NewHomeFrament - 开始连接
12-38-06_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-38-06_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-38-06_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-06_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-38-06_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-38-06_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-38-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-38-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-38-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-38-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-06_d：首页空白 - GridView添加了数据
12-38-06_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095106484)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-38-06_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-07_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095106484)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-38-07_d：首页空白 - GridView添加了数据
12-38-07_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-38-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-14_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-38-14_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-14_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-38-14_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-38-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-15_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-15_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-38-15_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-15_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-38-15_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-38-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-22_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-38-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-23_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-38-23_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-38-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-24_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-38-24_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-24_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-38-24_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-38-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-30_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-38-30_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-30_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-38-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-40_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-40_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-38-40_d：NewHomeFrament - 开始连接
12-38-40_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-38-40_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-38-40_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-38-40_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-38-40_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-40_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-38-40_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-40_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-38-40_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-38-41_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095140718)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-38-41_d：首页空白 - GridView添加了数据
12-38-41_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-41_d：首页空白 - GridView添加了数据
12-38-41_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095140718)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-38-41_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-38-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-49_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-38-49_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-49_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-38-49_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-38-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-50_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-50_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-38-50_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-50_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-38-50_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-38-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-57_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-38-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-38-57_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-38-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-38-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-38-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-38-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-38-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-38-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-39-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-10_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-39-10_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-10_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-39-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-20_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-39-20_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-39-20_d：NewHomeFrament - 开始连接
12-39-20_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-39-20_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-39-20_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-39-20_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-39-20_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-39-20_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-39-20_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-39-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-20_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-39-20_d：首页空白 - GridView添加了数据
12-39-20_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095179889)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-39-20_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-39-20_d：首页空白 - GridView添加了数据
12-39-20_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095179889)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-39-20_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-39-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-33_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-39-33_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-33_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-39-33_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-39-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-39-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-39-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-39-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-39-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-39-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-39-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-39-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-43_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-43_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-39-43_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-39-43_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-39-43_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-39-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-50_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-39-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-39-51_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-39-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-39-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-06_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-40-06_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-06_d：NewHomeFrament - 开始连接
12-40-06_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-40-06_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-40-06_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-40-06_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-40-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-40-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-40-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-40-06_d：首页空白 - GridView添加了数据
12-40-06_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095226233)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-40-06_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-06_d：首页空白 - GridView添加了数据
12-40-06_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-40-06_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095226233)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-40-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-14_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-14_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-40-14_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-40-14_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-40-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-15_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-15_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-40-15_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-15_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-40-15_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-40-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-22_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-40-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-40-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-40-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-40-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-40-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-40-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-35_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-40-35_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-40-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-45_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-45_d：NewHomeFrament - 开始连接
12-40-45_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-40-45_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-40-45_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-40-45_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-40-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-45_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-40-45_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-40-45_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-45_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-40-45_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-40-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-45_d：首页空白 - GridView添加了数据
12-40-45_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095265529)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-40-46_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-46_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095265529)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-40-46_d：首页空白 - GridView添加了数据
12-40-46_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-40-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-52_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-40-52_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-52_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-40-52_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-40-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-53_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-40-53_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-40-53_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-40-53_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-40-53_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-40-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-40-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-01_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-41-01_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-41-01_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-01_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-41-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-02_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-41-02_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-41-02_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-41-02_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-41-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-10_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-41-10_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-10_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-41-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-25_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-41-25_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-41-25_d：NewHomeFrament - 开始连接
12-41-25_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-41-25_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-41-25_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-41-25_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-41-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-25_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-41-25_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-41-25_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-41-25_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-41-25_d：首页空白 - GridView添加了数据
12-41-25_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095305029)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-41-25_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-41-25_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095305029)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-41-25_d：首页空白 - GridView添加了数据
12-41-25_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-41-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-36_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-41-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-36_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-41-36_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-41-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-39_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-39_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-41-39_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-41-39_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-41-39_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-41-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-48_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-41-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-41-48_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-41-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-50_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-50_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-41-50_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-41-50_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-41-50_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-41-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-41-59_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-41-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-41-59_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-41-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-14_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-42-14_d：NewHomeFrament - 开始连接
12-42-14_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-42-14_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-42-14_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-42-14_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-42-14_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-42-14_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-42-14_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-42-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-14_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-42-14_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-42-14_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-42-14_d：首页空白 - GridView添加了数据
12-42-14_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095354060)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-42-14_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-42-14_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095354060)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-42-14_d：首页空白 - GridView添加了数据
12-42-14_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-42-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-22_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-42-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-42-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-42-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-42-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-42-24_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-42-24_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-42-24_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-42-24_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-42-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-37_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-42-37_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-42-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-42-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-42-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-39_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-42-39_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-42-39_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-42-39_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-42-39_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-42-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-50_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-42-50_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-42-50_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-42-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-42-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-01_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-01_d：NewHomeFrament - 开始连接
12-43-01_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-43-01_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-43-01_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-43-01_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-43-01_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-01_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-01_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-43-01_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-43-01_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-43-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-01_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-43-01_d：首页空白 - GridView添加了数据
12-43-01_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095401091)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-43-01_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-01_d：首页空白 - GridView添加了数据
12-43-01_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095401091)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-43-01_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-43-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-17_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-43-17_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-17_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-43-17_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-43-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-18_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-43-18_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-43-18_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-18_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-43-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-29_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-43-29_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-43-29_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-43-29_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-30_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-43-30_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-30_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-30_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-43-30_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-43-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-43-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-43-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-55_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-43-55_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-43-55_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-55_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-43-55_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-43-55_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-43-55_d：NewHomeFrament - 开始连接
12-43-55_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-43-55_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-55_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-43-55_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-43-55_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-43-55_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095455403)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-43-55_d：首页空白 - GridView添加了数据
12-43-55_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-43-55_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095455403)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-43-55_d：首页空白 - GridView添加了数据
12-43-55_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-43-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-43-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-44-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-44-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-44-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-44-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-44-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-44-06_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-44-06_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-44-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-44-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-44-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-44-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-44-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-44-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-19_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-44-19_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-44-19_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-44-19_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-44-19_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-44-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-32_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-44-32_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-44-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-32_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-44-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-50_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-44-50_d：NewHomeFrament - 开始连接
12-44-50_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-44-50_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-44-50_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-44-50_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-44-50_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-44-50_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-44-50_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-44-50_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-44-50_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-44-50_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-44-50_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095509840)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-44-50_d：首页空白 - GridView添加了数据
12-44-50_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-44-50_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095509840)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-44-50_d：首页空白 - GridView添加了数据
12-44-50_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-44-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-44-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-00_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-45-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-45-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-45-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-45-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-03_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-45-03_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-45-03_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-45-03_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-45-03_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-45-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-45-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-45-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-45-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-45-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-45-25_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-45-25_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-45-25_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-45-25_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-45-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-38_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-45-38_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-45-38_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-45-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-51_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-45-51_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-45-51_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-45-51_d：NewHomeFrament - 开始连接
12-45-51_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-45-51_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-45-51_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-45-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-45-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-45-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-45-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-45-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-45-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-51_d：首页空白 - GridView添加了数据
12-45-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095570559)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-45-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-45-51_d：首页空白 - GridView添加了数据
12-45-51_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095570559)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-45-51_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-45-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-45-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-00_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-46-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-46-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-46-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-46-03_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-46-03_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-46-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-03_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-46-03_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-46-03_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-46-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-26_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-46-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-26_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-46-26_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-46-26_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-46-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-46-34_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-46-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-34_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-46-34_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-46-34_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-46-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-53_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-46-53_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-46-53_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-46-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-46-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-20_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-20_d：NewHomeFrament - 开始连接
12-47-20_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-47-20_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-47-20_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-47-20_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-47-20_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-47-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-20_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-47-20_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-20_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-47-20_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-47-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-20_d：首页空白 - GridView添加了数据
12-47-20_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095659527)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-47-20_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-20_d：首页空白 - GridView添加了数据
12-47-20_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095659527)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-47-20_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-47-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-28_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-47-28_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-28_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-47-28_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-47-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-29_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-29_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-47-29_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-29_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-47-29_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-47-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-37_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-47-37_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-47-37_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-47-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-38_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-38_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-47-38_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-38_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-47-38_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-47-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-45_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-47-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-45_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-47-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-59_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-59_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-47-59_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-47-59_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-47-59_d：NewHomeFrament - 开始连接
12-47-59_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-47-59_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-47-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-47-59_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-47-59_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-59_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-47-59_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-47-59_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095699449)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-47-59_d：首页空白 - GridView添加了数据
12-47-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-47-59_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-47-59_d：首页空白 - GridView添加了数据
12-47-59_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095699449)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-47-59_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-48-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-10_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-48-10_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-48-10_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-48-10_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-48-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-13_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-48-13_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-48-13_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-48-13_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-48-13_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-48-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-29_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-48-29_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-48-29_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-48-29_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-48-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-48-31_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-48-31_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-48-31_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-48-31_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-48-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-44_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-48-44_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-48-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-48-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-48-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-02_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-02_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-49-02_d：NewHomeFrament - 开始连接
12-49-02_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-49-02_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-49-02_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-49-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-02_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-49-02_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-02_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-49-02_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-49-02_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-49-02_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095762261)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-49-02_d：首页空白 - GridView添加了数据
12-49-02_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-02_d：首页空白 - GridView添加了数据
12-49-02_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095762261)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-49-02_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-49-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-49-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-49-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-49-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-13_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-13_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-49-13_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-13_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-49-13_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-49-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-21_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-49-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-49-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-49-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-24_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-49-24_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-24_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-49-24_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-49-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-33_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-49-33_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-33_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-49-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-49_d：NewHomeFrament - 开始连接
12-49-49_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-49_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-49-49_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-49-49_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-49-49_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-49-49_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-49-49_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-49-49_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-49-49_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-49-49_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-49_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-49-49_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095809135)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-49-49_d：首页空白 - GridView添加了数据
12-49-49_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-49-49_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095809135)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-49-49_d：首页空白 - GridView添加了数据
12-49-49_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-49-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-49-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-50-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-50-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-50-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-50-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-09_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-50-09_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-50-09_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-50-09_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-50-09_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-50-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-25_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-50-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-50-25_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-50-25_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-50-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-50-27_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-50-27_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-50-27_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-50-27_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-50-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-50-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-50-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-50-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-50-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-02_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-51-02_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-51-02_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-51-02_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-02_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-51-02_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-51-02_d：NewHomeFrament - 开始连接
12-51-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-02_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-51-03_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-03_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-51-03_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-51-03_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095882838)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-51-03_d：首页空白 - GridView添加了数据
12-51-03_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-03_d：首页空白 - GridView添加了数据
12-51-03_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095882838)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-51-03_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-51-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-13_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-51-13_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-13_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-51-13_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-51-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-14_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-14_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-51-15_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-15_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-51-15_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-51-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-30_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-51-30_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-30_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-51-30_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-51-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-31_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-31_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-51-31_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-51-31_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-51-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-51-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-51-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-57_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-57_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-51-57_d：NewHomeFrament - 开始连接
12-51-57_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-51-57_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-51-57_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-51-57_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-51-57_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-51-57_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-51-57_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-57_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-51-57_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-51-57_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095937400)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-51-57_d：首页空白 - GridView添加了数据
12-51-57_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-51-57_d：首页空白 - GridView添加了数据
12-51-57_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547095937400)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-51-57_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-51-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-51-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-07_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-52-07_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-52-07_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-52-07_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-52-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-10_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-52-10_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-52-10_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-52-10_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-52-10_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-52-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-52-20_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-52-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-52-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-52-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-52-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-52-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-52-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-52-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-52-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-52-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-52-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-52-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-52-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-11_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-11_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-53-11_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-53-11_d：NewHomeFrament - 开始连接
12-53-11_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-53-11_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-53-11_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-11_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-53-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-11_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-53-11_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-53-11_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-53-11_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096011337)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-53-11_d：首页空白 - GridView添加了数据
12-53-11_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-11_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096011337)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-53-11_d：首页空白 - GridView添加了数据
12-53-11_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-53-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-18_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-53-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-53-18_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-53-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-20_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-20_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-53-20_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-53-20_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-53-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-31_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-53-31_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-53-31_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-53-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-32_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-32_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-53-32_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-32_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-53-32_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-53-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-39_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-53-39_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-39_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-53-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-54_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-53-54_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-53-54_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-53-54_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-54_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-53-54_d：NewHomeFrament - 开始连接
12-53-54_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-53-54_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-54_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-53-54_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-53-54_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-53-54_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-53-54_d：首页空白 - GridView添加了数据
12-53-54_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096054024)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-53-54_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-53-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-54_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096054024)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-53-54_d：首页空白 - GridView添加了数据
12-53-54_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-53-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-53-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-02_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-54-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-54-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-54-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-04_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-04_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-54-04_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-54-04_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-54-04_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-54-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-15_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-15_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-54-15_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-54-15_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-54-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-18_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-54-18_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-54-18_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-54-18_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-54-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-31_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-54-31_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-54-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-47_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-54-47_d：NewHomeFrament - 开始连接
12-54-47_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-54-47_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-54-47_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-54-47_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-54-47_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-54-47_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-54-47_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-54-47_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-54-47_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-47_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-54-47_d：首页空白 - GridView添加了数据
12-54-47_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096107618)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-54-48_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-54-48_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096107618)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-54-48_d：首页空白 - GridView添加了数据
12-54-48_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-54-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-54-55_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-54-55_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-54-55_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-54-55_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-54-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-00_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-55-00_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-00_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-55-00_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-55-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-08_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-55-08_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-55-08_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-08_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-55-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-11_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-11_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-55-11_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-55-11_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-55-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-23_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-55-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-23_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-55-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-37_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-37_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-55-37_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-55-37_d：NewHomeFrament - 开始连接
12-55-37_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-55-37_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-55-37_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-37_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-55-37_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-55-37_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-37_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-55-37_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-55-37_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096157492)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-55-37_d：首页空白 - GridView添加了数据
12-55-37_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-37_d：首页空白 - GridView添加了数据
12-55-37_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096157492)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-55-37_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-55-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-45_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-55-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-45_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-55-45_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-55-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-46_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-46_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-55-46_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-55-46_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-46_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-55-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-55_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-55-55_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-55_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-55-55_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-55-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-55-58_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-55-58_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-55-58_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-55-58_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-55-58_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-55-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-56-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-56-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-56-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-26_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-56-26_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-56-26_d：NewHomeFrament - 开始连接
12-56-26_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-56-26_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-56-26_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-56-26_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-56-26_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-56-26_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-56-26_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-56-26_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-56-26_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-56-26_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096206102)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-56-26_d：首页空白 - GridView添加了数据
12-56-26_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-56-26_d：首页空白 - GridView添加了数据
12-56-26_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096206102)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-56-26_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-56-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-40_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-56-40_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-56-40_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-56-40_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-56-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-56-42_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-56-42_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-56-42_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-56-42_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-56-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-54_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-56-54_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-56-55_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-56-55_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-56-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-56_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-56-56_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-56-56_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-56-56_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-56-56_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-56-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-56-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-09_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-57-09_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-57-09_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-57-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-22_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-57-22_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-57-22_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-57-22_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-57-22_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-57-22_d：NewHomeFrament - 开始连接
12-57-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-57-22_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-57-22_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-57-22_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-57-22_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-57-22_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-57-22_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096262632)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-57-22_d：首页空白 - GridView添加了数据
12-57-22_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-57-23_d：首页空白 - GridView添加了数据
12-57-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096262632)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-57-23_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-57-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-38_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-57-38_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-57-38_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-57-38_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-57-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-57-41_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-57-41_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-57-41_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-57-41_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-57-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-49_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-57-49_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-57-49_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-57-49_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-57-51_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-57-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-51_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-57-51_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-57-51_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-57-51_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-57-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-57-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-00_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-58-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-58-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-58-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-13_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-58-13_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-58-13_d：NewHomeFrament - 开始连接
12-58-13_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-58-13_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-58-13_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-58-13_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-58-13_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-58-13_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-58-13_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-58-13_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-58-13_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-58-13_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096312820)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-58-13_d：首页空白 - GridView添加了数据
12-58-13_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-58-13_d：首页空白 - GridView添加了数据
12-58-13_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096312820)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-58-13_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-58-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-21_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-58-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-58-21_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-58-21_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-58-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-58-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-58-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-58-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-58-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-58-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-58-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-58-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-58-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-58-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-45_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-58-45_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-58-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-58-45_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-58-45_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-58-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-58-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-01_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-59-01_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-59-01_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-59-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-26_d：NewHomeFrament - 开始连接
12-59-26_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
12-59-26_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-59-26_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
12-59-26_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
12-59-26_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
12-59-26_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
12-59-26_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-59-26_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-59-26_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-59-26_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-59-26_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-59-26_d：首页空白 - GridView添加了数据
12-59-26_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096386444)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
12-59-26_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-59-26_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096386444)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
12-59-26_d：首页空白 - GridView添加了数据
12-59-26_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
12-59-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-43_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
12-59-43_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-59-43_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
12-59-43_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
12-59-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-44_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
12-59-44_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
12-59-44_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
12-59-44_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
12-59-44_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
12-59-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
12-59-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-02_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-00-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-00-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-00-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-00-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-07_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-00-07_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-00-07_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-00-07_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-00-07_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-00-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-00-23_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-00-23_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-00-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-44_d：NewHomeFrament - 开始连接
13-00-44_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-00-44_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-00-44_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-00-44_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-00-44_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-00-44_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-00-44_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-00-44_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-00-44_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-00-44_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-00-44_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-00-44_d：首页空白 - GridView添加了数据
13-00-44_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096464506)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-00-44_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-00-44_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096464506)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-00-44_d：首页空白 - GridView添加了数据
13-00-44_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-00-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-59_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-00-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-00-59_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-00-59_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-00-59_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-01-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-01-00_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-01-00_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-01-00_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-01-00_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-01-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-17_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-01-17_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-01-17_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-01-17_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-01-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-19_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-01-19_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-01-19_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-01-19_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-01-19_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-01-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-36_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-01-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-01-36_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-01-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-01-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-02-00_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-02-00_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-02-00_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-02-00_d：NewHomeFrament - 开始连接
13-02-00_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-02-00_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-02-00_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-02-00_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-02-00_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-02-00_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-02-00_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-02-00_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096539881)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-02-00_d：首页空白 - GridView添加了数据
13-02-00_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-02-00_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096539881)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-02-00_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-02-00_d：首页空白 - GridView添加了数据
13-02-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-22_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-02-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-02-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-02-22_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-02-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-02-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-02-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-02-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-02-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-02-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-31_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-02-31_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-02-31_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-02-31_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-02-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-32_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-02-32_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-02-32_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-02-32_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-02-32_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-02-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-41_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-02-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-02-41_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-02-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-02-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-01_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-03-01_d：NewHomeFrament - 开始连接
13-03-01_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-03-01_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-03-01_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-03-01_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-03-01_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-03-01_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-03-01_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-03-01_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-03-01_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-03-01_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-03-01_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096601365)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-03-01_d：首页空白 - GridView添加了数据
13-03-01_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-03-01_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096601365)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-03-01_d：首页空白 - GridView添加了数据
13-03-01_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-03-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-16_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-03-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-16_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-03-16_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-03-16_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-03-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-19_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-03-19_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-03-19_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-03-19_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-03-19_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-03-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-36_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-03-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-03-36_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-03-36_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-03-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-41_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-03-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-41_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-03-41_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-03-41_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-03-41_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-03-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-56_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-03-56_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-03-56_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-03-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-03-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-15_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-04-15_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-04-15_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-04-15_d：NewHomeFrament - 开始连接
13-04-15_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-04-15_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-04-15_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-04-15_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-04-15_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-04-15_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-04-15_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-04-15_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-04-15_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096675333)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-04-15_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-04-15_d：首页空白 - GridView添加了数据
13-04-15_d：首页空白 - GridView添加了数据
13-04-15_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096675333)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-04-15_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-04-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-25_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-04-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-04-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-25_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-04-25_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-04-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-35_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-04-35_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-04-35_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-04-35_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-04-35_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-04-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-04-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-02_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-05-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-05-02_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-05-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-05-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-08_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-05-08_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-05-08_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-05-08_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-05-08_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-05-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-05-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-05-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-05-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-48_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-05-48_d：NewHomeFrament - 开始连接
13-05-48_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-05-48_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-05-48_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-05-48_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-05-48_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-05-48_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-05-48_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-05-48_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-05-48_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-05-48_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-05-48_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096768567)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-05-48_d：首页空白 - GridView添加了数据
13-05-48_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-05-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-49_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096768567)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-05-49_d：首页空白 - GridView添加了数据
13-05-49_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-05-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-53_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-05-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-01_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-06-01_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-06-01_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-06-01_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-06-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-02_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-06-02_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-06-02_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-06-02_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-06-02_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-06-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-32_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-06-32_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-06-32_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-06-32_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-06-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-06-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-06-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-06-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-06-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-06-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-06-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-00_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-07-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-07-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-07-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-36_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-07-36_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-07-36_d：NewHomeFrament - 开始连接
13-07-36_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-07-36_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-07-36_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-07-36_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-07-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-07-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-07-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-07-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-07-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-07-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-36_d：首页空白 - GridView添加了数据
13-07-36_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096876363)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-07-36_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-07-36_d：首页空白 - GridView添加了数据
13-07-36_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547096876363)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-07-36_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-07-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-07-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-03_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-08-03_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-08-03_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-08-03_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-08-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-08_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-08-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-08_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-08-08_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-08-08_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-08-08_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-08-15_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-29_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-30_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-42_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-08-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-08-42_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-08-42_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-08-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-08-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-45_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-08-45_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-08-45_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-08-45_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-08-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-08-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-20_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-09-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-09-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-09-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-31_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-09-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-18_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-10-18_d：NewHomeFrament - 开始连接
13-10-18_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-10-18_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-10-18_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-10-18_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-10-18_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-10-18_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-10-18_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-10-18_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-10-18_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-10-18_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-10-18_d：首页空白 - GridView添加了数据
13-10-18_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097038065)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-10-18_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-10-18_d：首页空白 - GridView添加了数据
13-10-18_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097038065)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-10-18_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-10-18_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-39_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-10-39_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-10-39_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-10-39_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-10-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-40_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-10-40_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-10-40_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-10-40_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-10-41_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-10-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-10-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-20_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-11-20_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-11-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-11-20_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-11-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-11-29_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-11-29_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-11-29_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-11-29_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-11-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-11-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-12-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-12-06_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-12-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-12-07_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-12-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-12-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-12-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-12-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-08_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-24_d：NewHomeFrament - 开始连接
13-13-24_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-13-24_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-13-24_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-13-24_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-13-24_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-13-24_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-13-24_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-13-24_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-13-24_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-13-24_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-13-24_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-13-24_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097224361)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-13-24_d：首页空白 - GridView添加了数据
13-13-24_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-13-24_d：首页空白 - GridView添加了数据
13-13-24_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097224361)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-13-24_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-13-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-38_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-13-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-14-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-14-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-14-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-14-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-13_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-14-13_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-14-13_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-14-13_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-14-13_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-14-14_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-14-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-12_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-15-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-15-12_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-15-12_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-15-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-16_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-15-16_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-15-16_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-15-16_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-15-16_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-15-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-39_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-15-39_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-15-39_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-15-40_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-44_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-15-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-06_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-22_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-16-22_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-16-22_d：NewHomeFrament - 开始连接
13-16-22_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-16-22_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-16-23_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-16-23_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-16-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-16-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-16-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-16-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-16-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-16-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097402359)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-16-23_d：首页空白 - GridView添加了数据
13-16-23_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-16-23_d：首页空白 - GridView添加了数据
13-16-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097402359)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-16-23_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-16-32_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-16-55_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-17-11_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-17-16_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-17-27_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-17-27_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-17-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-17-27_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-17-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-17-46_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-17-46_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-17-46_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-17-46_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-17-46_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-17-46_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-17_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-33_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-34_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-18-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-18-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-18-34_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-18-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-18-36_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-18-36_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-18-36_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-18-36_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-18-36_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-18-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-12_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-19_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-21_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-19-21_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-19-21_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-19-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-19-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-05_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-13_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-21_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-23_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-20-23_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-20-23_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-20-23_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-20-23_d：NewHomeFrament - 开始连接
13-20-23_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-20-23_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-20-23_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-20-23_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-20-23_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-20-23_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-20-23_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-20-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097643514)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-20-23_d：首页空白 - GridView添加了数据
13-20-23_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-20-23_d：首页空白 - GridView添加了数据
13-20-23_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097643514)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-20-23_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-20-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-39_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-42_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-43_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-44_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-20-44_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-20-44_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-20-44_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-20-45_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-45_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-20-45_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-20-45_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-20-45_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-20-45_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-20-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-20-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-07_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-07_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-21-07_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-21-07_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-21-07_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-21-22_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-22_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-21-22_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-21-22_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-21-22_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-21-22_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-21-23_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-47_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-48_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-49_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-50_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-50_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-21-50_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-21-50_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-21-51_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-21-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-09_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-20_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-24_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-25_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-28_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-34_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-34_d：NewHomeFrament - 开始连接
13-22-34_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-22-34_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-22-34_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-22-34_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-22-34_d：首页空白 - 请求成功NewHomeFrament{982206c #1 NewHomeFrament},走updateAdapter
13-22-34_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-22-34_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-22-34_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-22-34_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-22-34_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-22-34_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-22-34_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097774513)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-22-34_d：首页空白 - GridView添加了数据
13-22-34_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-22-34_d：首页空白 - GridView添加了数据
13-22-34_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547097774513)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-22-34_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-22-35_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-36_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-37_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-52_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-22-54_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-25_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-23-25_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-23-25_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-23-25_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-23-26_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-26_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-23-26_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-23-26_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-23-26_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-23-26_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-23-27_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-41_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-56_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-57_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-58_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-23-59_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-24-00_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-24-00_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-24-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-24-00_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-24-00_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-24-01_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-24-01_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-24-01_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-24-01_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-24-01_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-24-01_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-24-02_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-24-02_d：首页空白 - 首页NewHomeFrament{982206c #1 NewHomeFrament}.onResume()
13-24-03_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-24-04_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=false ;data=[4, 24, 20, 19, 1, 10, 12, 1, 7, 0, 4]
13-30-03_d：短信监听 初始化完成
13-30-03_d：qiloo.sz.mainfun.activity.MainActivity@cf35cb0onCreated()
13-30-03_d：首页空白 - 首页NewHomeFrament{490643b #1 NewHomeFrament}.onCreate() 结束
13-30-03_d：首页空白 - 首页NewHomeFrament{490643b #1 NewHomeFrament}.onResume()
13-30-03_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-03_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-03_d：首页空白 - 首页NewHomeFrament{490643b #1 NewHomeFrament}.onResume()
13-30-03_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-03_i：NewHomeFrament开始连接手环,已监听否-true,连接状态5
13-30-03_d：AndBleService.relConnection(167) -> AndBleDevice.connect(241) : connect() mDeviceAddress=F8:50:A7:DC:BB:41
13-30-03_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-30-03_d：NewHomeFrament - 开始连接
13-30-03_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=F8:50:A7:DC:BB:41
13-30-03_d：首页空白 - 请求成功NewHomeFrament{490643b #1 NewHomeFrament},走updateAdapter
13-30-03_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-03_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=F8:50:A7:DC:BB:41
13-30-03_d：AndBleScanner.scanBLE(118) -> HandBondPresenter.isBonding(387) : F8:50:A7:DC:BB:41
13-30-03_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-03_d：AndBleScanner.scanBLE(122) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-30-03_d：首页空白 - GridView添加了数据
13-30-03_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547098223573)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-30-03_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-03_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547098223573)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-30-03_d：首页空白 - GridView添加了数据
13-30-03_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-03_d：DeviceBlueToothManager.connectionHandler(85) -> AndBleDevice.connect(241) : connect() mDeviceAddress=A0:11:37:11:22:3B
13-30-03_i：AndBleDevice.connect(245) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - A0:11:37:11:22:3B-2
13-30-03_d：AndBleDevice.connect(246) -> AndBleScanner.searchBle(70) : searchBle() searchAddress=A0:11:37:11:22:3B
13-30-03_d：DeviceBlueToothManager.connectionHandler(85) -> AndBleDevice.connect(241) : connect() mDeviceAddress=
13-30-03_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=A0:11:37:11:22:3B
13-30-03_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-03_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-05_d：首页空白 - 首页NewHomeFrament{490643b #1 NewHomeFrament}.onResume()
13-30-05_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-30-05_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-30-05_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-30-06_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-06_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-06_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=A0:11:37:11:22:3B
13-30-07_d：AndBleScanner$1.test(93) -> AndBleScanner$1.test(98) : F8:50:A7:DC:BB:41 Filter Test true
13-30-07_d：FlowableCreate.subscribeActual(72) -> AndBleDevice$4.subscribe(295) : 手环绑定调试 开始绑定- com.qiloo.sz.common.andBle.ble.AndBleDevice$4@1cf7c2c - 127
13-30-07_d：AndBleDevice.access$200(53) -> AndBleDevice.connectionHandler(283) : 开始连接 - gatt.create() F8:50:A7:DC:BB:41
13-30-07_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-2
13-30-07_d：HandBondPresenter.checkHandBond(80) -> HandBondPresenter.isHand(340) : F8:50:A7:DC:BB:41
13-30-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(78) : 调用了 checkHandBond - F8:50:A7:DC:BB:41
13-30-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(90) : 手环当前未连接 - F8:50:A7:DC:BB:41
13-30-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(119) : 手环未绑定 - F8:50:A7:DC:BB:41
13-30-07_d：HandBondPresenter.isBond(364) -> HandBondPresenter.isBond(371) : F8:50:A7:DC:BB:41
13-30-07_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-07_d：AndBleDevice$4.subscribe(298) -> HandBondPresenter.checkHandBond(130) : 发起绑定 - F8:50:A7:DC:BB:41-true
13-30-07_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-30-07_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
13-30-07_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
13-30-07_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(32) : 配对状态 - 正在配对 - F8:50:A7:DC:BB:41
13-30-08_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-08_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-30-08_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-30-08_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(47)
13-30-09_i：BondStateBroadcastReceiver.onReceive(47) -> HandBondPresenter$3.bondStateChanged(245) : 绑定回调走完了-F8:50:A7:DC:BB:41
13-30-09_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
13-30-09_d：HandBondPresenter$2.onBondResult(127) -> AndBleDevice$4$2.onBondResult(301) : error = 0
13-30-09_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
13-30-09_d：LoadedApk$ReceiverDispatcher$Args.lambda$-android_app_LoadedApk$ReceiverDispatcher$Args_51571(1304) -> BondStateBroadcastReceiver.onReceive(35) : 配对状态 - 配对已成功 - F8:50:A7:DC:BB:41
13-30-09_d：HandBondPresenter$3$2.run(236) -> HandBondPresenter$2.onBondResult(124) : 手环绑定成功 - F8:50:A7:DC:BB:41true
13-30-09_d：BluetoothGatt$1$4.run(244) -> AndBleDevice$5.onConnectionStateChange(358) : status=0 newState=2
13-30-09_d：AndBleScanner.scanBLE(115) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-09_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-09_d：AndBleScanner.access$200(47) -> AndBleScanner.scanBLE(110) : scanBLE() 蓝牙开启否=true 要搜索=A0:11:37:11:22:3B
13-30-10_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(473) : android.bluetooth.BluetoothGatt@80579a9, [NotifyCharacteristic 6e400001-b5a3-f393-e0a9-e50e24dcca9e;6e400003-b5a3-f393-e0a9-e50e24dcca9e]
13-30-10_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(478) : service =android.bluetooth.BluetoothGattService@83ca6cf
13-30-10_d：BluetoothGatt$1$5.run(306) -> AndBleDevice$5.onServicesDiscovered(408) : status=0
13-30-10_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(481) : charac =android.bluetooth.BluetoothGattCharacteristic@fc0485c
13-30-10_d：NewHomeFrament.handReConn(297) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-10_d：AndBleDevice.access$900(53) -> AndBleDevice.registeredNotification(484) : bRet=true
13-30-10_i：AndBleDevice.access$300(53) -> AndBleDevice.setConnectStateChange(463) : setConnectStateChange - F8:50:A7:DC:BB:41-3
13-30-10_d：NewHomeFrament-手环连接成功
13-30-10_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@83ca6cf
13-30-10_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@7791c3a 写入数据=[3] 写入结果=false
13-30-10_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@83ca6cf
13-30-10_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@7791c3a 写入数据=[4, 24, 20, 19, 1, 10, 13, 30, 10, 0, 4] 写入结果=true
13-30-10_d：AndBleService.write(120) -> AndBleDevice.write(192) : service=android.bluetooth.BluetoothGattService@83ca6cf
13-30-10_d：AndBleService.write(120) -> AndBleDevice.write(197) : Characteristic=android.bluetooth.BluetoothGattCharacteristic@7791c3a 写入数据=[25, 0] 写入结果=true
13-30-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[4, 24, 20, 19, 1, 10, 13, 30, 10, 0, 4]
13-30-10_i：下发时间或语言 mac=F8:50:A7:DC:BB:41 ;成功否=true ;data=[25, 0]
13-30-10_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
13-30-10_i：BluetoothGatt$1$8.run(436) -> AndBleDevice$5.onCharacteristicChanged(445) : onCharacteristicChanged() 新建 notifyData = ServiceUUID=6e400001-b5a3-f393-e0a9-e50e24dcca9e;UUID=6e400003-b5a3-f393-e0a9-e50e24dcca9e;Data=[121]
13-30-10_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-10_d：首页空白 - 请求成功NewHomeFrament{490643b #1 NewHomeFrament},走updateAdapter
13-30-10_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
13-30-10_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547098230292)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-30-10_d：首页空白 - GridView添加了数据
13-30-10_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-10_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547098230292)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-30-10_d：首页空白 - GridView添加了数据
13-30-10_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-11_d：CancellableDisposable.dispose(49) -> AndBleScanner$3$1.cancel(77) : cancel
13-30-11_d：AndBleScanner.access$100(47) -> AndBleScanner.stopScanBLE(212) : BLE搜索取消
13-30-11_d：RetryWithDelay$1.apply(41) -> RetryWithDelay$1.apply(44) : class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException, class com.qiloo.sz.common.andBle.ble.exception.ScanTimeOutException
13-30-38_d：首页空白 - 请求成功NewHomeFrament{490643b #1 NewHomeFrament},走updateAdapter
13-30-38_i：NewHomeFrament开始连接手环,已监听否-true,连接状态3
13-30-38_d：NewHomeFrament.conntectBleDeviceToProxyManager(826) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-38_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4812, phone='8615132042162', child='', tsn='', name='F850A7DCBB41', isInvite=false, Battery=30, BatteryStr='30%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='ZNSH', IsChina=true, Online='在线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547098258542)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=1, Mac='F8-50-A7-DC-BB-41', RightMac='', DeviceStats=0}
13-30-38_d：首页空白 - GridView添加了数据
13-30-38_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : F8:50:A7:DC:BB:41
13-30-38_d：首页空白 - HomeView.initData()中添加了一条功能项TsnEntityDB{id=4777, phone='8615132042162', child='', tsn='', name='A0113711223B', isInvite=false, Battery=90, BatteryStr='90%', RightBattery=100, RightBatteryStr='100%', GpsTime='1900-01-01', GpsStatus='', Geo='', SampleName='backpack', IsChina=true, Online='离线', UserPicHead='', TerminalPhone='', DeviceisNull=false, TsnImei='', PhoneStartTime='null', PhoneEndTime='null', DayCount='null', GpsTimeUtc='/Date(1547098258542)/', UnReadLogCount=0, SourceKey='', Other_CodeNum='', Sex=0, IsStartup=false, IsOutOfService=false, UnInsuranceCount=0, ShowStepNumber=0, IsOpenRestState=0, FangState=0, OnlineStat=0, Mac='A0-11-37-11-22-3B', RightMac='', DeviceStats=0}
13-30-38_d：HomeView.initSettingSwitch(194) -> HandBondPresenter.isHand(350) : A0:11:37:11:22:3B
13-30-38_d：首页空白 - GridView添加了数据


 */