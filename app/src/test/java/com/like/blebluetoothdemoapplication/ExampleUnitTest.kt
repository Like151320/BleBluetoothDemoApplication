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

		testListenerCallError()


//		JavaDo().start()
	}


	fun testListenerCallError() {
		val set = mutableSetOf<Int>(1, 2, 3, 4, 5)

//		for (i in set) {//出错,在循环时修改set
//			set.remove(i)
//			set.add(it)
//		}

//		set.forEach {//出错,同上
//			set.remove(it)
//			set.add(it)
//		}

		for (i in HashSet(set)) {//使用新的集合
			set.remove(i)
			set.add(i)
		}

	}

	/**字符串转byte*/
	fun StringToByte() {
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
	}
}
