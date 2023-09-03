package com.nxg.composeplane

import android.os.Handler
import org.junit.Test

class KotlinUnitTest {

    private var handler: Handler? = null

    /**
     * 密封类
     */
    sealed class WeekDays(value: Int) {
        object SUNDAY : WeekDays(0)
        object MONDAY : WeekDays(1)
        object TUESDAY : WeekDays(2)
        object WEDNESDAY : WeekDays(3)
        object THURSDAY : WeekDays(4)
        object FRIDAY : WeekDays(5)
        object SATURDAY : WeekDays(6)
    }

    private var today: WeekDays = WeekDays.SUNDAY

    fun getToday(): WeekDays {
        return today
    }

    fun setToday(today: WeekDays) {
        this.today = today
    }

    @Test
    fun testEnum() {
        when (today) {
            WeekDays.FRIDAY -> {
               
            }
            WeekDays.MONDAY -> {

            }
            WeekDays.SATURDAY -> {

            }
            WeekDays.SUNDAY -> {

            }
            WeekDays.THURSDAY -> {

            }
            WeekDays.TUESDAY -> {

            }
            WeekDays.WEDNESDAY -> {

            }
        }
    }

    @Test
    fun testIfNull() {
        handler?.removeCallbacksAndMessages(null)
    }

    @Test
    fun testIfNullInit() {
        handler = handler ?: Handler()
    }


    @Test
    fun testIfElse() {
        testIfNullInit()
        handler?.apply {
            println("handler not null 1")
        } ?: apply {
            println("handler null 1")
        }

        handler?.apply {
            println("handler not null 2 ")
        } ?: let {
            println("handler null 2")
        }

        handler?.apply {
            println("handler not null 3")
        } ?: run {
            println("handler null 3")
        }

        handler?.let {
            println("handler not null 4")
            null
        } ?: let {
            println("handler null 4")
        }
    }


    val a = fun (param: Int): String {
        return param.toString()
    }

    @Test
    fun testLambda(){
        val stringPlus: (String, String) -> String = String::plus
        val intPlus: Int.(Int) -> Int = Int::plus

        println(stringPlus.invoke("<-", "->"))
        println(stringPlus("Hello, ", "world!"))
    }

}