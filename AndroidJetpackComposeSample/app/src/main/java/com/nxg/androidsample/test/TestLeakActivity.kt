package com.nxg.androidsample.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.nxg.androidsample.R

class TestLeakActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_leak)
    }
}