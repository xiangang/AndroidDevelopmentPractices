package com.nxg.asantest;

public class ASanTestUtil {

    // Used to load the 'asantest' library on application startup.
    static {
        System.loadLibrary("asantest");
    }

    /**
     * A native method that is implemented by the 'asantest' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    public static native void testUseAfterFree();

    public static native void testHeapBufferOverflow();

    public static native void testStackBufferOverflow();

    public static native void testGlobalBufferOverflow();

    public static native void testUseAfterReturn();

    public static native void testUseAfterScope();

    public static native void testRepeatFree();

    public static native void testMemoryLeak();
}
