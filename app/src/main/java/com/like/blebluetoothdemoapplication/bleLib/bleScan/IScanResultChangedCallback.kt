package com.like.blebluetoothdemoapplication.bleLib.bleScan

import android.support.annotation.MainThread
import no.nordicsemi.android.support.v18.scanner.ScanResult

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 13:54
 * 作用:
 */
interface IScanResultChangedCallback {
	/**开搜至今搜到的全部*/
	@MainThread
	fun onScanResultChanged(scanResults: List<ScanResult>)
}