package com.nxg.composeplane

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
        //assertEquals(4, 2 + 2)

        for (i in 1..100) {
            val randoms = (0..1080).random()
            print(randoms)
            print("\n")
        }


    }
}