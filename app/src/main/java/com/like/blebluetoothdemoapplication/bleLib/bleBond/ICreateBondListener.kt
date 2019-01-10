package com.like.blebluetoothdemoapplication.bleLib.bleBond

/**
 * 作者: Li_ke
 * 日期: 2019/1/9 14:41
 * 作用:
 */
interface ICreateBondListener {
	/**[isCreated]-绑定成功否*/
	fun onCreateBondResult(isCreated: Boolean)
}