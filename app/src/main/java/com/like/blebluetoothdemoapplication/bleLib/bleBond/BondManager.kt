package com.like.blebluetoothdemoapplication.bleLib.bleBond

/**
 * 作者: Li_ke
 * 日期: 2019/1/7 17:47
 * 作用: 蓝牙绑定管理
 */
/*
关于蓝牙绑定有个坑，
1、若执行 device1.createBond(); device2.createBond()， 那么实际上只有 device1是正常走绑定流程的，device2 不会走绑定流程，也不会发出绑定广播
2、BOND_NONE - 代表设备解绑，无论是解绑成功 还是 绑定失败 都是这个状态，需要区分

解决1：无论外部如何操作，内部都需要一个一个的绑定，所以外部必须做出妥协，要么用自己的逻辑分开两个设备的绑定时间，要么执行 createBond 等待它们逐个绑定完成
那么 createBond 要做的就是 - 1、当被多次调用时，逐个绑定设备。 2、防止重复绑定设备。 3、对外提供绑定状态接口以开放更灵活的操作。

解决2：若同时绑定与解绑 device1，那肯定是无法通过BOND_NONE来判断的，绑定与解绑必然不能同时存在，在逻辑上估计不会存在（抛出异常）
		那就区分开要么单走绑定，要么单走解绑，即通过这样的区分来区分 BOND_NONE 结果
 */
object BondManager {

	fun createBond() {

	}

	fun removeBond() {

	}
}