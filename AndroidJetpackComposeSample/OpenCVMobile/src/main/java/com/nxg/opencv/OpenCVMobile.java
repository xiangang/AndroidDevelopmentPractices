package com.nxg.opencv;

import android.view.Surface;

public class OpenCVMobile {

    static {
        System.loadLibrary("opencv_mobile");
    }

    /**
     * 渲染yuv数据到Surface
     */
    public static native void renderYuvDataOnSurface(int width, int height, byte[] yuvData, Surface surface);

}
