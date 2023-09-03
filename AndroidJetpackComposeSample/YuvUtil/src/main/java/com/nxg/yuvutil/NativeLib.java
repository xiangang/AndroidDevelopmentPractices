package com.nxg.yuvutil;

public class NativeLib {

    // Used to load the 'yuvutil' library on application startup.
    static {
        System.loadLibrary("yuvutil");
    }

    /**
     * A native method that is implemented by the 'yuvutil' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}