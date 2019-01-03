package com.like.blebluetoothdemoapplication.useNordicsemiBle

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.like.blebluetoothdemoapplication.R
import kotlinx.android.synthetic.main.activity_my_blestream.*
import java.util.*

/**
 * 作者: Li_ke
 * 日期: 2018/12/29 10:18
 * 作用:
 */
class NordicsemiStreamActivity : AppCompatActivity() {

	private lateinit var listAdapter: ArrayAdapter<String>
	private lateinit var logListAdapter: ArrayAdapter<String>

	private var selectedCharacteristic: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_my_blestream)

		title = "侧滑查看日志信息"

		//init UI
		listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
//		listAdapter.add("特征码列表,点击选中")
		listView.adapter = listAdapter
		listView.setOnItemClickListener { parent, view, position, id ->
			selectedCharacteristic(listAdapter.getItem(position)!!)
		}

		logListAdapter = ArrayAdapter(
			this,
			R.layout.small_list_item
		)
		logListView.adapter = logListAdapter
		logListAdapter.add("日志信息")

		//CID -> UI
		manager?.mGatt?.services?.forEach { service ->
			for (characteristic in service.characteristics) {
				listAdapter.add(characteristic.uuid.toString())
			}
		}


		//监听
		//监听发来信息
		onReceiveData()
	}

	/**选中特征码*/
	fun selectedCharacteristic(characteristic: String) {
		if (manager?.mGatt != null) {
			selectedCharacteristic = characteristic
			Toast.makeText(this, "已选中\n$characteristic", Toast.LENGTH_LONG).show()
		} else {
			Toast.makeText(this, "还未连接成功", Toast.LENGTH_LONG).show()
		}
	}

	fun send(v: View) {
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

			//找SID
			val sid =
				manager?.mGatt?.services?.find { service ->
					service.getCharacteristic(
						UUID.fromString(selectedCharacteristic)
					) != null
				}?.uuid.toString()
			//发送
			manager?.writeData(sid, selectedCharacteristic!!, byteArray)?.done {
				Toast.makeText(this, "发送成功 - ${Arrays.toString(byteArray)}", Toast.LENGTH_SHORT)
					.show()
				logListAdapter.add(
					"发送信息:" +
							"cid:$selectedCharacteristic\n" +
							"value:${Arrays.toString(byteArray)} - true"
				)
			}?.enqueue()
		} else {
			Toast.makeText(this, "发送数据格式不对,请填入16进制数字,如“01DD”", Toast.LENGTH_LONG).show()
		}
	}

	/**收到信息*/
	private fun onReceiveData() {
		manager?.onReceiveData { cid, value ->
			logListAdapter.add(
				"收到信息:" +
						"cid:$cid\n" +
						"value:${Arrays.toString(value)}"
			)
		}
	}
}