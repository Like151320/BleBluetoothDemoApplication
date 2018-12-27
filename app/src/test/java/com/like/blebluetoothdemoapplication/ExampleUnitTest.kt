package com.like.blebluetoothdemoapplication

import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
	@Test
	fun addition_isCorrect() {
//		assertEquals(4, 2 + 2)
		val msg = "111213AA"
		if (msg.length % 2 == 0) {
			val byteArray = ByteArray(msg.length / 2)
			for (i in 0 until msg.length / 2) {
				val byteStr = "${msg[i * 2]}${msg[i * 2 + 1]}"//取两位字符串
				val byteI = byteStr.toInt(16)//转16进制 int
				val byte = byteI.toByte()//int 转 byte
				byteArray[i] = byte
			}

			println(Arrays.toString(byteArray))
		} else {

		}
//		JavaDo().start()
	}
}
