package com.nxg.httpsserver;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);//初始化AndroidUtilCode

    }
}
