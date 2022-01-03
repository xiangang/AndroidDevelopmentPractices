package com.nxg.audiorecord;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;


import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * 实时音频播放处理类<br/>
 * 使用示例代码如下:<br/>
 *
 * <pre>
 * audioTrackHandler = new AudioTrackHandler();
 * audioTrackHandler.prepare();// 播放前需要prepare。可以重复prepare
 * // 直接将需要播放的数据传入即可
 * audioPlayerHandler.onPlaying(data, 0, data.length);
 * </pre>
 */
public class AudioTrackHandler implements Runnable {

    private static final String TAG = "AudioTrackHandler";

    private AudioTrack audioTrack;// 录音文件播放对象
    private boolean isPlaying = false;// 标记是否正在录音中
    private static final int samplingRate = 16000;// 采样率
    private static final int channelConfig = AudioFormat.CHANNEL_OUT_MONO;// 定义采样为双声道（过时，但是使用其他的又不行
    private static final int audioFormat = AudioFormat.ENCODING_PCM_16BIT;// 定义音频编码（16位）
    private final int bufferSizeInBytes;// 播放缓冲大小
    private final LinkedBlockingDeque<Object> dataQueue = new LinkedBlockingDeque<>();
    // 互斥信号量
    private final Semaphore semaphore = new Semaphore(1);
    // 是否释放资源的标志位
    private boolean release = false;

    private static class Holder {
        private static final AudioTrackHandler INSTANCE = new AudioTrackHandler();
    }

    public static AudioTrackHandler getInstance() {
        return Holder.INSTANCE;
    }

    private AudioTrackHandler() {
        // 获取缓冲 大小
        bufferSizeInBytes = AudioTrack.getMinBufferSize(samplingRate, channelConfig,
                audioFormat);

        // 实例AudioTrack
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, samplingRate,
                channelConfig, audioFormat, bufferSizeInBytes,
                AudioTrack.MODE_STREAM);
        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(),
                AudioTrack.getMaxVolume());
        try {
            // 默认需要抢占一个信号量,防止播放进程执行
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 开启播放线程
        new Thread(this, "AudioPlayer").start();
    }

    /**
     * 录音数据回到进入队列
     *
     * @param audioData  语音byte数组
     * @param startIndex 开始的偏移量
     * @param length     数据长度
     */
    public synchronized void onRecording(byte[] audioData, int startIndex, int length) {
        if (AudioTrack.ERROR_BAD_VALUE == bufferSizeInBytes) {// 初始化错误
            return;
        }
        byte[] frameBuffer = new byte[length];
        System.arraycopy(audioData, startIndex, frameBuffer, 0, length);
        try {
            dataQueue.putLast(frameBuffer);
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始播放
     */
    public void play() {
        if (audioTrack != null && !isPlaying) {
            audioTrack.play();
            isPlaying = true;
        }

    }

    /**
     * 停止播放
     */
    public void stop() {
        if (audioTrack != null) {
            audioTrack.stop();
            isPlaying = false;
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        release = true;
        semaphore.release();
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }

    @Override
    public void run() {
        while (true) {
            if (release) {
                return;
            }
            if (dataQueue.size() > 0) {
                byte[] data = (byte[]) dataQueue.pollFirst();
                audioTrack.write(data, 0, data.length);
            } else {
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private AudioTrack audioTrackForTest;

    public void startPlayRecordFile(Context context) {
        if (audioTrackForTest != null) {
            Log.i(TAG, "startPlay: 请等待播放完成");
            return;
        }
        final byte[] sound = AudioFileHandler.getInstance().getByteData(context.getFilesDir() + "/record.pcm");
        if (sound == null) {
            Log.i(TAG, "startPlay: 没有录音数据");
            return;
        }
        int bufferSize = AudioTrack.getMinBufferSize(samplingRate, channelConfig, audioFormat);
        audioTrackForTest = new AudioTrack(AudioManager.STREAM_VOICE_CALL, samplingRate, channelConfig, audioFormat,
                bufferSize, AudioTrack.MODE_STREAM);
        int audioLen = sound.length / 2;
        Log.i(TAG, "AudioTrack 开始播放");
        audioTrackForTest.setNotificationMarkerPosition(audioLen);
        audioTrackForTest.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.e(TAG, "AudioTrack 播放完成");
                audioTrackForTest = null;
            }

            @Override
            public void onPeriodicNotification(AudioTrack audioTrackForTest) {

            }
        });
        audioTrackForTest.play();
        new Thread(new Runnable() {
            @Override
            public void run() {
                audioTrackForTest.write(sound, 0, sound.length);
            }
        }).start();
    }
}