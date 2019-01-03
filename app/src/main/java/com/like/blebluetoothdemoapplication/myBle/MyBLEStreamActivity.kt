package com.like.blebluetoothdemoapplication.myBle

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.like.blebluetoothdemoapplication.R
import kotlinx.android.synthetic.main.activity_my_blestream.*
import java.util.*

/**
 * LE蓝牙通讯页
 */
class MyBLEStreamActivity : AppCompatActivity() {
	private val TAG = "BluetoothLEStreamDemo"
	private val address: String by lazy { intent.getStringExtra(ADDRESS) }
	private lateinit var listAdapter: ArrayAdapter<String>
	private lateinit var logListAdapter: ArrayAdapter<String>

	private var mGatt: BluetoothGatt? = null
	private var mCharacteristicAndService: Map<String, String>? = null

	private var selectedCharacteristic: String? = null
	private var deviceInfo: MyBLEActivity.DeviceInfo? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_my_blestream)

		title = "侧滑查看日志信息"

		//init UI
		listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
		listAdapter.add("特征码列表,点击选中")
		listView.adapter = listAdapter
		listView.setOnItemClickListener { parent, view, position, id ->
			selectedCharacteristic(listAdapter.getItem(position)!!)
		}

		logListAdapter = ArrayAdapter(this,
			R.layout.small_list_item
		)
		logListView.adapter = logListAdapter
		logListAdapter.add("日志信息")


		connection(address)
	}

	/**开始连接*/
	fun connection(address: String) {
		if (!BluetoothAdapter.checkBluetoothAddress(address))
			return

		val device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address)

		//连接蓝牙来获得蓝牙信息 PS:按照Android官方的说法,连接前会自动提示绑定 - 注：如果两台设备之前尚未配对，则在连接过程中，Android 框架会自动向用户显示配对请求通知或对话框（如图 3 所示）。因此，在尝试连接设备时，您的应用无需担心设备是否已配对。 您的 RFCOMM 连接尝试将被阻塞，直至用户成功完成配对或配对失败（包括用户拒绝配对、配对失败或超时）。
		device.connectGatt(this, false, object : BluetoothGattCallback() {
			/**连接状态改变,著名的 22、133 都出自这里
			 * 要注意 [status]和[newState]是两个数据,而不是一个数据的变化前与变化后的值
			 * [status]的值比较多,参考: GattError
			 * [newState]的值就很少了,参考: BluetoothProfile.STATE_DISCONNECTED */
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

				Log.d(TAG, "发现连接通道 status = $status")
				if (status == BluetoothGatt.GATT_SUCCESS) {
					//保存特征码列表
					val characteristicMap = mutableMapOf<String, String>()
					for (service in gatt!!.services) {
						//characteristic(特征码)列表
						for (characteristic in service.characteristics) {
							characteristicMap[characteristic.uuid.toString()] =
									service.uuid.toString()
						}
					}

					//保存特征码通道ID
					deviceInfo =
							MyBLEActivity.DeviceInfo(
								address,
								device.name,
								characteristicMap
							)


					"连接了$address\n$deviceInfo".run {
						Log.d(TAG, this)
						runOnUiThread {
							Toast.makeText(baseContext, this, Toast.LENGTH_LONG).show()
						}
					}

					onServicesDiscovered(gatt, deviceInfo!!)

					//监听通道发来的讯息
					registerNotification(gatt, deviceInfo!!)
				}
			}

			override fun onDescriptorWrite(
				gatt: BluetoothGatt?, descriptor: BluetoothGattDescriptor?, status: Int
			) {
				super.onDescriptorWrite(gatt, descriptor, status)
				if (status == BluetoothGatt.GATT_SUCCESS
					&& descriptor?.value?.contentEquals(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE) == true
				)
				//正在注册特征码
					if (isRegisteringNotification)
						registerNotification(gatt!!, deviceInfo!!, descriptor.uuid.toString())

			}

			var isRegisteringNotification = false
			/**开始注册返回信息监听。 [deviceInfo]-所有需要写入注册的Descriptor;
			 * [registeredDescriptorID]-当前注册到哪个Descriptor了,会去注册下一个,null则从头开始注册。
			 * 配合 [isRegisteringNotification] 与 [onDescriptorWrite] 能注册全部的特征码。
			 * 为何不直接全部写入注册信息? 我也希望能一下子全部写入descriptor,但是有个大坑文档中都没有提到。
			 *
			 * 坑：底层与蓝牙的交互是一条通道，数据只能一条一条的处理，好比一个缆车连接两座山，当我 writeDescriptor 把数据丢进缆车后，
			 * 不等缆车运过去又 writeDescriptor 丢一条数据，但缆车中已经有数据了，所以我就会写入失败 writeDescriptor() 返回 false。
			 * 我必须得等缆车把数据运过去了，在等缆车运回来“成功”两个字，我才能写下一条数据，WTF。
			 */
			private fun registerNotification(
				gatt: BluetoothGatt,
				deviceInfo: MyBLEActivity.DeviceInfo,
				registeredDescriptorID: String? = null
			) {
				isRegisteringNotification = true

				//开启所有特征码的监听通知,开启一次即可,并且仅仅 setCharacteristicNotification 不能真的监听回调,还需要向设备写入监听注册
				if (registeredDescriptorID == null) {
					for (service in gatt.services) {
						val characteristics = service.characteristics
						for (characteristic in characteristics) {
							val notificationResult =
								gatt.setCharacteristicNotification(characteristic, true)//监听发来的通道数据
							Log.d(TAG, "注册数据监听否? - ${characteristic.uuid} $notificationResult")
						}
					}
				}

				//寻找下个要写入注册的 descriptor
				var nextDescriptor: BluetoothGattDescriptor? = null
				var findDescriptor = registeredDescriptorID == null//从头开始 = 注册第一个 else 查找下一个
				for (entry in deviceInfo.characteristicAndService) {
					for (descriptor in gatt.getService(UUID.fromString(entry.value))
						.getCharacteristic(UUID.fromString(entry.key))
						.descriptors) {
						//找到了已写入的 descriptor
						if (descriptor.uuid.toString() == registeredDescriptorID) {
							findDescriptor = true
							continue
						}
						//这是下一个要写入的 descriptor
						if (findDescriptor) {
							nextDescriptor = descriptor
						}
					}
				}
				//去写入注册 descriptor
				if (nextDescriptor != null) {
					nextDescriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
					val writeResult = gatt.writeDescriptor(nextDescriptor)
					Log.d(TAG, "注册监听数据写入否? - ${nextDescriptor.uuid} $writeResult")
				} else {
					//已经全部写入注册
					isRegisteringNotification = false
				}
			}

			override fun onCharacteristicChanged(
				gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
			) {
				super.onCharacteristicChanged(gatt, characteristic)
				val value = characteristic?.value
				if (value != null) {
					Log.e(TAG, Arrays.toString(value))
					onReceiveData(gatt!!, characteristic, value)
				}
			}

			override fun onCharacteristicRead(
				gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int
			) {
				super.onCharacteristicRead(gatt, characteristic, status)
			}
		})
	}

	/**连接成功了*/
	@WorkerThread
	private fun onServicesDiscovered(gatt: BluetoothGatt, deviceInfo: MyBLEActivity.DeviceInfo) {
		mGatt = gatt
		mCharacteristicAndService = deviceInfo.characteristicAndService

		runOnUiThread {
			listAdapter.clear()
			listAdapter.addAll(deviceInfo.characteristicAndService.keys)
			listAdapter.notifyDataSetChanged()
		}
	}

	/**选中特征码*/
	fun selectedCharacteristic(characteristic: String) {
		if (mGatt != null) {
			selectedCharacteristic = characteristic
			Toast.makeText(this, "已选中\n$characteristic", Toast.LENGTH_LONG).show()
		} else {
			Toast.makeText(this, "还未连接成功", Toast.LENGTH_LONG).show()
		}
	}

	/**按下发送数据*/
	fun send(v: View) {
		if (selectedCharacteristic != null) {
			//格式化发送的数据
			val service = mCharacteristicAndService!![selectedCharacteristic!!]
			val msg = edit_sendMsg.text.toString()
			//字符串转byteArray
			if (msg.length % 2 == 0) {
				val byteArray = ByteArray(msg.length / 2)
				try {
					for (i in 0 until msg.length / 2) {
						val byteStr = "${msg[i * 2]}${msg[i * 2 + 1]}"//取两位字符串
						val byteI = byteStr.toInt(16)//转16进制 int
						val byte = byteI.toByte()//int 转 byte
						byteArray[i] = byte
					}
				} catch (e: NumberFormatException) {
					Toast.makeText(this, "发送数据格式不对,请填入16进制数字,如“01DD”", Toast.LENGTH_LONG).show()
				}
				//发送
				writeData(mGatt!!, service!!, selectedCharacteristic!!, byteArray)
			} else {
				Toast.makeText(this, "发送数据格式不对,请填入16进制数字,如“01DD”", Toast.LENGTH_LONG).show()
			}
		}
	}

	/**实际发送数据操作*/
	fun writeData(
		gatt: BluetoothGatt, serviceID: String, characteristicID: String, data: ByteArray
	) {
		Log.d(TAG, "开始发送数据 ${Arrays.toString(data)} - s = $serviceID c = $characteristicID")
		//获取发送通道
		val service = gatt.getService(UUID.fromString(serviceID))
		val characteristic = service.getCharacteristic(UUID.fromString(characteristicID))
		//填入并发送数据
		characteristic.value = data
		val writeResult = gatt.writeCharacteristic(characteristic)
		Log.d(TAG, "数据成功否? $writeResult")
		logListAdapter.add(
			"发送信息:" +
					"cid:${characteristic.uuid}\n" +
					"value:${Arrays.toString(data)} - $writeResult"
		)
		logListAdapter.notifyDataSetChanged()
		Toast.makeText(this, if (writeResult) "发送成功" else "发送失败", Toast.LENGTH_LONG).show()
	}

	//1、调用API开启设备数据通知。2、向设备发送开启通知
	/**接收数据*/
	@WorkerThread
	private fun onReceiveData(
		gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray
	) {
		runOnUiThread {
			logListAdapter.add(
				"收到信息:" +
						"cid:${characteristic.uuid}\n" +
						"value:${Arrays.toString(value)}"
			)
			logListAdapter.notifyDataSetChanged()
		}
	}

	override fun onDestroy() {
		super.onDestroy()
		mGatt?.discoverServices()
		mGatt?.close()
	}

	companion object {
		private const val ADDRESS = "address"
		fun getStartIntent(context: Context, address: String): Intent {
			return Intent(context, MyBLEStreamActivity::class.java).putExtra(
				ADDRESS, address)
		}
	}
}
