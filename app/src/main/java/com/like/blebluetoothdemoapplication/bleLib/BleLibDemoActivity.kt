package com.like.blebluetoothdemoapplication.bleLib

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import com.like.blebluetoothdemoapplication.MLog
import com.like.blebluetoothdemoapplication.R
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

		//创建deviceManager
		BackpackDevice(device).run {
			//回调监听
			this.notifyCallback.add { c, v ->
				MLog.d("返回信息- $c : ${Arrays.toString(v)}")
			}
			//连接
			MLog.d("开始连接")
			this.connect()
			//开始拿步数
			this.testSend1()
//						?.done {
//						MLog.d("成功发送步数指令")
//					}?.enqueue()

			null
		}
	}

	fun gotoStream(v: View) {
		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(selectedAddress)

		//创建deviceManager
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
		}
	}

	override fun onDestroy() {
		BleScanManager.removeScanResultChangedCallback(this)
		super.onDestroy()
	}
}