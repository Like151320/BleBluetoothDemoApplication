package com.like.blebluetoothdemoapplication.bleLib.bleBond

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter
import com.like.blebluetoothdemoapplication.MLog
import com.like.blebluetoothdemoapplication.bleLib.bleBond.BleBondManager.createBond
import com.like.blebluetoothdemoapplication.bleLib.bleBond.BleBondManager.removeBond

/**
 * 作者: Li_ke
 * 日期: 2019/1/7 17:47
 * 作用: 蓝牙绑定管理，使用[createBond]或[removeBond]时需注意延时回调引起的内存泄漏
 * 1、同一提供绑定操作，自己写多麻烦，也不好管理。
 * 2、排坑：“只能一个一个设备绑定”和“同时解绑绑定一个设备无法判断结果”
 */
/*
坑—只能一个一个设备绑定：
	若执行 device1.createBond(); device2.createBond()， 那么实际上只有 device1是正常走绑定流程的，device2 不会走绑定流程，也不会发出绑定广播
坑—绑定后可能搜不到设备：
	我的手机绑定设备后，重开应用就搜不到此设备了。重开蓝牙或解绑后才能再次搜到。
 */
object BleBondManager : IBondStateListener {
	private var initialized = false
	private val mBondReceiver = BondStateBroadcastReceiver()

	//要绑定与解绑的设备列表,列表的第一个就是正在操作的设备
	private val mCreateBondList = mutableListOf<String>()
	private val mRemoveBondList = mutableListOf<String>()
	//绑定与解绑的回调列表
	private val mCreateBondCallback = mutableMapOf<String, MutableSet<ICreateBondListener>>()
	private val mRemoveBondCallback = mutableMapOf<String, MutableSet<IRemoveBondListener>>()
	//设备的状态记录
	private val mDeviceBondState = mutableMapOf<String, BondState>()

	//监听广播
	fun init(context: Context) {
		if (!initialized) {
			mBondReceiver.addBondListener(this)
			context.registerReceiver(
				mBondReceiver, IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
			)
			initialized = true
		}
	}

	/**广播通知*/
	override fun onBondStateChanged(
		context: Context, device: BluetoothDevice, previousBondState: Int, bondState: Int
	) {
		when (bondState) {
			BluetoothDevice.BOND_BONDING -> {
				mDeviceBondState[device.address] = BondState.BOND_BONDING
			}

			BluetoothDevice.BOND_NONE -> {
				mDeviceBondState[device.address] = BondState.BOND_NONE
				//是绑定失败
				if (mCreateBondList.remove(device.address)) {
					//通知+释放 绑定回调
					mCreateBondCallback.remove(device.address)?.forEach { listener ->
						listener.onCreateBondResult(false)//通知
					}
					//绑定下个设备,如果有
					if (mCreateBondList.isNotEmpty()) {
						startCreateBond(
							BluetoothAdapter.getDefaultAdapter().getRemoteDevice(
								mCreateBondList.first()
							)
						)
					}
				}
				//是解绑成功
				else if (mRemoveBondList.remove(device.address)) {
					//通知+释放 解绑回调
					mRemoveBondCallback.remove(device.address)?.forEach { listener ->
						listener.onRemoveBondResult(true)//通知
					}
					//解绑下个设备,如果有
					if (mRemoveBondList.isNotEmpty()) {
						startCreateBond(
							BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mRemoveBondList.first())
						)
					}
				}
			}

			BluetoothDevice.BOND_BONDED -> {
				mDeviceBondState[device.address] = BondState.BOND_BONDED
				//是绑定成功
				if (mCreateBondList.remove(device.address)) {
					//通知+释放 绑定回调
					mCreateBondCallback.remove(device.address)?.forEach { listener ->
						listener.onCreateBondResult(true)//通知
					}
					//绑定下个设备,如果有
					if (mCreateBondList.isNotEmpty()) {
						startCreateBond(
							BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mCreateBondList.first())
						)
					}
				}
			}
			else -> {
			}
		}
	}

	/**绑定操作*/
	private fun startCreateBond(
		device: BluetoothDevice, callback: ICreateBondListener? = null
	): Boolean {
		mDeviceBondState[device.address] = BondState.BOND_CREATE

		//确认无其它设备正在绑定
		val createBond =
			if (mCreateBondList.isEmpty() || mCreateBondList.first() == device.address) {
				//开始绑定
				device.createBond()
			} else true

		//成功发起绑定
		if (createBond) {
			mDeviceBondState[device.address] = BondState.BOND_CREATE_START//状态更新
			if (!mCreateBondList.contains(device.address))//绑定列表+1
				mCreateBondList.add(device.address)
			mCreateBondCallback.addCallback(device.address, callback)//绑定回调+1
		}
		//绑定发起失败
		else {
			mDeviceBondState[device.address] = BondState.BOND_CREATE_FAILURE//状态更新
			mCreateBondList.remove(device.address)//绑定列表-1
			mCreateBondCallback.remove(device.address)//绑定回调-1
		}

		return createBond
	}

	/**解绑操作*/
	private fun startRemoveBond(
		device: BluetoothDevice,
		callback: IRemoveBondListener? = null
	): Boolean {
		mDeviceBondState[device.address] = BondState.BOND_REMOVE

		//确认无其它设备正在解绑
		val removeBond =
			if (mRemoveBondList.isEmpty() || mRemoveBondList.first() == device.address) {
				//开始解绑
				try {
					(device.javaClass.getMethod("removeBond").invoke(device) as Boolean)
				} catch (e: RuntimeException) {
					MLog.d("解绑失败-$e")
					false
				}
			} else true

		//成功发起解绑
		if (removeBond) {
			mDeviceBondState[device.address] = BondState.BOND_REMOVE_START//状态更新
			if (!mRemoveBondList.contains(device.address))//解绑列表+1
				mRemoveBondList.add(device.address)
			mRemoveBondCallback.addCallback(device.address, callback)//解绑回调+1
		}
		//解绑发起失败
		else {
			mDeviceBondState[device.address] = BondState.BOND_REMOVE_FAILURE//状态更新
			mRemoveBondList.remove(device.address)//解绑列表-1
			mRemoveBondCallback.remove(device.address)//解绑回调-1
		}

		return removeBond
	}

	/**绑定监听+1*/
	private fun MutableMap<String, MutableSet<ICreateBondListener>>.addCallback(
		address: String, callback: ICreateBondListener?
	) {
		if (callback != null && this === mCreateBondCallback) {
			if (mCreateBondCallback[address] == null)
				mCreateBondCallback[address] = mutableSetOf()
			mCreateBondCallback[address]?.add(callback)
		}
	}

	/**解绑监听+1*/
	private fun MutableMap<String, MutableSet<IRemoveBondListener>>.addCallback(
		address: String, callback: IRemoveBondListener?
	) {
		if (callback != null && this === mRemoveBondCallback) {
			if (mRemoveBondCallback[address] == null)
				mRemoveBondCallback[address] = mutableSetOf()
			mRemoveBondCallback[address]?.add(callback)
		}
	}

	//======================对外接口======================

	/**绑定设备 [callback]-已绑定时会立即调用，若已绑定,则会添加绑定监听*/
	fun createBond(device: BluetoothDevice, callback: ICreateBondListener): Boolean {
		if (mRemoveBondList.contains(device.address)) {
			MLog.d("无法绑定设备${device.address},因为正在执行解绑操作,检查你的绑定逻辑")
			callback.onCreateBondResult(false)
			return false
		}

		//已绑定 or 发起绑定
		val createBond =
			if (device.bondState == BluetoothDevice.BOND_BONDED) {
				MLog.d("绑定设备${device.address},但设备已绑定")
				callback.onCreateBondResult(true);true
			} else
				startCreateBond(device, callback)

		//发起失败
		if (!createBond) callback.onCreateBondResult(false)

		return createBond
	}

	/**解绑设备，若已解绑,则会添加解绑监听*/
	fun removeBond(device: BluetoothDevice, callback: IRemoveBondListener): Boolean {
		if (mRemoveBondList.contains(device.address)) {
			MLog.d("无法解绑设备${device.address},因为正在执行绑定操作,检查你的绑定逻辑")
			callback.onRemoveBondResult(false)
			return false
		}

		//未绑定 or 发起解绑
		val removeBond =
			if (device.bondState == BluetoothDevice.BOND_NONE) {
				MLog.d("解绑设备${device.address},但设备已解绑")
				callback.onRemoveBondResult(true);true
			} else
				startRemoveBond(device, callback)

		//发起失败
		if (!removeBond) callback.onRemoveBondResult(false)

		return removeBond
	}

	fun createBond(device: BluetoothDevice, callback: (isCreated: Boolean) -> Unit) =
		createBond(device, object : ICreateBondListener {
			override fun onCreateBondResult(isCreated: Boolean) {
				callback.invoke(isCreated)
			}
		})

	fun removeBond(device: BluetoothDevice, callback: (isRemoved: Boolean) -> Unit) =
		removeBond(device, object : IRemoveBondListener {
			override fun onRemoveBondResult(isRemoved: Boolean) {
				callback.invoke(isRemoved)
			}
		})

	/**查看设备绑定状态*/
	fun bondState(address: String): BondState {
		if (!BluetoothAdapter.checkBluetoothAddress(address))
			error("设备地址不规范$address")

		return mDeviceBondState[address] ?: BondState.BOND_NULL
	}
}