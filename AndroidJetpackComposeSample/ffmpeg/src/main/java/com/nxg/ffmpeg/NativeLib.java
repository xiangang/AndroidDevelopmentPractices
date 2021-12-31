package com.nxg.ffmpeg;

public class NativeLib {

    // Used to load the 'ffmpeg' library on application startup.
    static {
        System.loadLibrary("ffmpeg");
    }

    /**
     * A native method that is implemented by the 'ffmpeg' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();
}