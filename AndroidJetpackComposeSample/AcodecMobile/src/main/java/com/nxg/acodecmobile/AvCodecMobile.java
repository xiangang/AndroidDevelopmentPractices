package com.nxg.acodecmobile;

import android.view.Surface;

public class AvCodecMobile {

    static {
        System.loadLibrary("acodecmobile");
    }

    /**
     * A native method that is implemented by the 'acodecmobile' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    /**
     * 渲染yuv数据到Surface
     */
    public static native void renderYuvDataOnSurface(int width, int height, byte[] yuvData, Surface surface);

}
