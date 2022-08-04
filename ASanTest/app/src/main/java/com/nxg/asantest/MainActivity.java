package com.nxg.asantest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nxg.asantest.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tvBuildType.setText(BuildConfig.BUILD_TYPE);

    }

    public void onBtnUseAfterFreeClick(View view) {
        ASanTestUtil.testUseAfterFree();
    }

    public void onBtnHeapBufferOverflow(View view) {
        ASanTestUtil.testHeapBufferOverflow();
    }

    public void onBtnStackBufferOverflow(View view) {
        ASanTestUtil.testStackBufferOverflow();
    }

    public void onBtnGlobalBufferOverflow(View view) {
        ASanTestUtil.testGlobalBufferOverflow();
    }

    public void onBtnUseAfterReturn(View view) {
        ASanTestUtil.testUseAfterReturn();
    }

    public void onBtnUseAfterScope(View view) {
        ASanTestUtil.testUseAfterScope();
    }

    public void onBtnUseRepeatFree(View view) {
        ASanTestUtil.testRepeatFree();
    }

    public void onBtnMemoryLeak(View view) {
        ASanTestUtil.testMemoryLeak();
    }
}