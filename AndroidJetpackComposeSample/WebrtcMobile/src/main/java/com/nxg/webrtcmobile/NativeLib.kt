package com.nxg.webrtcmobile

class NativeLib {

    /**
     * A native method that is implemented by the 'webrtcmobile' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'webrtcmobile' library on application startup.
        init {
            System.loadLibrary("webrtcmobile")
        }
    }
}