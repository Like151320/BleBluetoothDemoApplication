package com.like.blebluetoothdemoapplication.bleLib.bleBond

/**
 * 作者: Li_ke
 * 日期: 2019/1/9 14:46
 * 作用:
 */
enum class BondState {
	/**刚发起绑定操作*/
	BOND_CREATE,
	/**成功执行createBond()*/
	BOND_CREATE_START,
	/**失败执行createBond()*/
	BOND_CREATE_FAILURE,

	/**刚发起解绑操作*/
	BOND_REMOVE,
	/**成功执行removeBond()*/
	BOND_REMOVE_START,
	/**失败执行removeBond()*/
	BOND_REMOVE_FAILURE,

	/**无此设备*/
	BOND_NULL,

	//绑定结果
	BOND_BONDING,
	BOND_BONDED,
	BOND_NONE


}