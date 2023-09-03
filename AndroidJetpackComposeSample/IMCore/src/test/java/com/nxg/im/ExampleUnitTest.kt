package com.nxg.im

import com.nxg.im.core.IMCoreMessage
import com.nxg.im.core.IMCoreMessage.newBuilder
import com.nxg.im.core.IMCoreMessageOrBuilder
import com.nxg.im.core.IMCoreMessageOuterClass
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testIMCoreMessage() {
        //构建 Protobuf 对象
        val message = IMCoreMessage.getDefaultInstance()
        println("testIMCoreMessage = $message")
    }
}