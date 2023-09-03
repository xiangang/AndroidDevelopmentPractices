package com.nxg.composeplane

import org.junit.Test

class KotlinLambda {

    var value = 0

    @Test
    fun testLambda() {
        val sum = { x: Int, y: Int -> x + y }
        println(sum.invoke(1, 1))
        println(sum(1, 2))

        1.sum(2)
        println(1.sum(2))
        println(this.sum(2))
    }

    @Test
    fun testLambdaWithReceiver() {
        val sum = fun Int.(other: Int): Int = this + other
        println(1.sum(2))
        println(sum(1, 2))
    }

    @Test
    fun testLambdaWithReceiver2() {
        val sum: Int.(Int) -> Int = { other -> plus(other) }
        println(1.sum(2))
        println(sum(1, 2))
    }

    @Test
    fun testLambdaWithReceiver3() {
        val sum: Int.(other: Int) -> Int = { other -> this.plus(other) }
        println(1.sum(2))
        println(sum(1, 2))
    }

    @Test
    fun testLambdaWithReceiver4() {
        val sum: KotlinLambda.(other: Int) -> Int = { other -> this.value + other }
        println(sum(2))
        println(sum(this, 2))
    }

    @Test
    fun testLambdaWithReceiver5() {
        val sum: (other: Int) -> Int = { other -> this.value + other }
        println(sum(2))
    }


}

fun Int.sum(other: Int): Int {
    return this.plus(other)
}

fun KotlinLambda.sum(other: Int): Int {
    return this.value + other
}
