package com.like.blebluetoothdemoapplication.bleLib.bleScan

import android.bluetooth.BluetoothAdapter
import android.os.Handler
import android.support.annotation.MainThread
import com.like.blebluetoothdemoapplication.MLog
import no.nordicsemi.android.support.v18.scanner.*

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 13:36
 * 作用: LE蓝牙搜索
 */
object BleScanManager {

	/**搜索配置。注意,这里用的不是原生的 ScanSettings,而是 'no.nordicsemi.android.support.v18:scanner:1.1.0'*/
	private val scanSettings: ScanSettings = ScanSettings.Builder()
		.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)//扫描模式。SCAN_MODE_LOW_LATENCY: 低延迟,高消耗,建议仅当前台搜索时使用此模式
		.setReportDelay(500)//扫描报告延时
		.setUseHardwareBatchingIfSupported(false)//批处理,Demo为false
		// 硬件过滤在所选设备上存在一些问题
		.setUseHardwareFilteringIfSupported(false)//Demo为false
		.build()

	/**搜索中*/
	var isScanIng = false
	/**搜索超时时间*/
	private const val scanOutTime = 30 * 1000L
	/**搜索超时关闭处理*/
	private val mScanOutTimeHandler = Handler {
		stopScanBle()
		true
	}

	/**不重复的搜索结果*/
	val scanResults = mutableListOf<ScanResult>()

	/**蓝牙搜索回调*/
	private val mScanCallback = object : ScanCallback() {
		override fun onScanFailed(errorCode: Int) {
			super.onScanFailed(errorCode)
			MLog.d("onScanFailed() called with: " + "errorCode = [" + errorCode + "]")
		}

		/**扫描结果列表(会重复),搜不到新的设备时size=0*/
		@MainThread
		override fun onBatchScanResults(results: MutableList<ScanResult>?) {
			super.onBatchScanResults(results)

			//搜索结果改变
			var isScanResultChanged = false
			//过滤重复
			results?.forEach { result ->
				//已重复
				val isContains = scanResults.any {
					it.device.address == result.device.address
				}
				if (!isContains) {
					scanResults.add(result)
					MLog.d("addNewDevice ${result.scanRecord?.deviceName} _ ${result.device.address}")
					isScanResultChanged = true
				}
			}
			//真的搜到了东西,更新设备列表
			if (isScanResultChanged) {
				onScanResultChanged(scanResults)
			}
		}
	}
	/**搜索回调*/
	private val mScanResultChangedCallback = mutableSetOf<IScanResultChangedCallback>()

	/**搜索结果更新. [scanResults]:开搜至今搜到的全部*/
	@MainThread
	private fun onScanResultChanged(scanResults: MutableList<ScanResult>) {
		for (callback in HashSet(mScanResultChangedCallback)) {//防止循环中操作集合
			callback.onScanResultChanged(scanResults)
		}
	}

	//======================搜索操作======================

	/**开搜 - 必须有定位权限，必须已打开蓝牙
	 * [scanOutTime]-扫描超时时间*/
	fun scanBle(
		scanOutTime: Long = BleScanManager.scanOutTime,
		scanFilters: MutableList<ScanFilter>? = null,
		scanSettings: ScanSettings? = BleScanManager.scanSettings,
		clearLastScanResults: Boolean = true
	) {
		if (!isScanIng) {

			if (clearLastScanResults)
				clearScanResults()

			when {
				BluetoothAdapter.getDefaultAdapter() == null -> MLog.d("不支持蓝牙")
				!BluetoothAdapter.getDefaultAdapter().isEnabled -> MLog.d("未开启蓝牙")
				else -> { //开搜
					BluetoothLeScannerCompat.getScanner().startScan(
						scanFilters,
						scanSettings,
						mScanCallback
					)
					mScanOutTimeHandler.sendEmptyMessageDelayed(0, scanOutTime)
					isScanIng = true
				}
			}
		}
	}

	/**停搜*/
	fun stopScanBle() {
		if (isScanIng) {
			BluetoothLeScannerCompat.getScanner().stopScan(mScanCallback)
			mScanOutTimeHandler.removeMessages(0)
			isScanIng = false
		}
	}

	/**重置扫描结果*/
	fun clearScanResults() {
		scanResults.clear()
	}

	//======================搜索回调======================

	/**扫描结果监听,在主线程回调 (Set集合)*/
	fun addScanResultChangedCallback(callback: IScanResultChangedCallback) {
		mScanResultChangedCallback.add(callback)
	}

	fun removeScanResultChangedCallback(callback: IScanResultChangedCallback) {
		mScanResultChangedCallback.remove(callback)
	}

	/**清除扫描监听*/
	fun clearScanResultChangedCallback() {
		mScanResultChangedCallback.clear()
	}
}