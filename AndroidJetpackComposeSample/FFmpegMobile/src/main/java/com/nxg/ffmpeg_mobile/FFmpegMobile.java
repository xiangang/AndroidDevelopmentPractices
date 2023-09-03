package com.nxg.ffmpeg_mobile;

public class FFmpegMobile {

    static {
        System.loadLibrary("ffmpeg_mobile");
    }

    public static long decoderPtr;

    public interface FFmpegCallback {
        void callback(long pts, long dts, long duration, long index);
    }

    public interface DecodeBufferListener {
        void onBufferListener(int width, int height, byte[] data);
    }

    /**
     * A native method that is implemented by the 'ffmpeg_mobile' native library,
     * which is packaged with this application.
     */
    public static native String stringFromJNI();

    public static native long nativeInit();

    public static native int nativeRelease(long ptr);

    public static native int setFFmpegCallback(FFmpegCallback ffmpegCallback);

    /**
     * 返回Avcodec的版本号
     *
     * @return String
     */
    public static native String nativeGetAvcodecVersion();

    /**
     * 解码H264 File
     *
     * @param ptr                  decoderPtr
     * @param inputFileName        h264文件路径
     * @param outFileName          输出文件路径
     * @param decodeBufferListener 回调
     */
    public static native int nativeDecodeVideo(long ptr, String inputFileName, String outFileName, DecodeBufferListener decodeBufferListener);

    public static native int nativeDecodeAudio(long ptr, String inputFileName, String outFileName, DecodeBufferListener decodeBufferListener);

    public static native int nativePlayAudio(String audioPath);

    public static native int nativeGetMetadata(String inputFileName);
}
