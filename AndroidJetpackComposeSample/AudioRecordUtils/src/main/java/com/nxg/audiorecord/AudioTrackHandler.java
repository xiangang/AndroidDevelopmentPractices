package com.nxg.audiorecord;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.File;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

import javax.security.auth.login.LoginException;

/**
 * 实时音频播放处理类<br/>
 * 使用示例代码如下:<br/>
 *
 * <pre>
 * AudioTrackHandler audioTrackHandler = new AudioTrackHandler();
 * 用法一：
 * audioTrackHandler.prepare();// 播放前需要prepare。可以重复prepare
 * audioTrackHandler.start();// prepare之后调用
 * audioTrackHandler.play();// start之后调用
 * audioPlayerHandler.onPlaying(data, 0, data.length); //直接将需要播放的数据传入即可
 *
 * 用法二：
 * audioTrackHandler.prepare();// 播放前需要prepare。可以重复prepare
 * audioTrackHandler.start();// prepare之后调用
 * audioTrackHandler.startPlayRecordFile(path);// start之后调用
 *
 * </pre>
 */
public class AudioTrackHandler {

    private static final String TAG = "AudioTrackHandler";

    private AudioTrack audioTrack;// 用于还是播放音频的录音文件播放对象
    private final int sampleRateInHz;// 采样率
    private final int channelConfig;// 定义采样为双声道（过时，但是用其他的又不行
    private final int audioFormat;// 定义音频编码（16位）
    private final int bufferSizeInBytes;// 播放缓冲大小
    private final LinkedBlockingDeque<Object> dataQueue = new LinkedBlockingDeque<>();
    private final Semaphore semaphore = new Semaphore(1);//互斥信号量
    private volatile boolean isPlaying = false;//标记是否正播放中
    private volatile boolean release = false;//是否释放资源的标志位

    private AudioTrackHandler(Builder builder) {
        this.sampleRateInHz = builder.sampleRateInHz;
        this.channelConfig = builder.channelConfig;
        this.audioFormat = builder.audioFormat;
        // 获取缓冲区大小
        bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig,
                audioFormat);
    }

    /**
     * 构建者模式
     */
    public static class Builder {

        private int sampleRateInHz = 44100;//采样率
        private int channelConfig = AudioFormat.CHANNEL_OUT_STEREO;//定义采样通道（过时，但是使用其他的又不行)
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//音频数据格式：PCM 16位每个样本，理论上所有设备都支持。

        public AudioTrackHandler.Builder setSampleRateInHz(int sampleRateInHz) {
            this.sampleRateInHz = sampleRateInHz;
            return this;
        }

        public AudioTrackHandler.Builder setChannelConfig(int channelConfig) {
            this.channelConfig = channelConfig;
            return this;
        }

        public AudioTrackHandler.Builder setAudioFormat(int audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }

        public AudioTrackHandler build() {
            return new AudioTrackHandler(this);
        }
    }

    /**
     * 必须调用prepare，用于初始化准备工作
     */
    public void prepare() {
        Log.i(TAG, "prepare: ");
        //实例化AudioTrack
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
        //设置音量
        audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
        try {
            // 默认需要抢占一个信号量,防止播放线程执行
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 音频数据梳理线程
     */
    class PlayRunnable implements Runnable {
        @Override
        public void run() {
            while (isPlaying) {
                if (release) {
                    return;
                }
                if (dataQueue.size() > 0) {
                    byte[] data = (byte[]) dataQueue.pollFirst();
                    if (data != null) {
                        LogUtil.d(TAG, " audioTrack.write(data, 0, data.length) " + data.length);
                        audioTrack.write(data, 0, data.length);
                    }
                } else {
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 录音数据回到进入队列，用于实时播放录制的音频，需要调用start方法，结束播放则调用stop方法
     *
     * @param audioData  音频byte数组
     * @param startIndex 开始的偏移量
     * @param length     数据长度
     */
    public synchronized void onPlaying(byte[] audioData, int startIndex, int length) {
        if (AudioTrack.ERROR_BAD_VALUE == bufferSizeInBytes) {// 初始化错误
            Log.i(TAG, "onPlaying: Denotes a failure due to the use of an invalid value.");
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
     * 检测当前状态
     *
     * @return ture正常可播放，false不能播放
     */
    private boolean checkState() {
        //判断是否已经释放资源
        if (release || audioTrack == null) {
            Log.i(TAG, "start: 资源已释放，请重新调用prepare方法");
            return false;
        }
        if (audioTrack.getState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.i(TAG, "start: AudioTrack未初始化成功，请稍后重试，或重新调用prepare方法");
            return false;
        }
        if (isPlaying) {
            Log.i(TAG, "start: 正在播放中，请勿重复调用");
            return false;
        }
        return true;
    }

    /**
     * 启动线程读取onPlaying回调的数据，在prepare之后调用
     */
    public void start() {
        if (!checkState()) {
            return;
        }
        isPlaying = true;
        if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            // 开启播放线程
            new Thread(Thread.currentThread().getThreadGroup(), new PlayRunnable(), "AudioTrackPlay").start();
        }
    }

    /**
     * 播放指定path指定的音频文件，在prepare之后调用
     *
     * @param path 音频文件路径
     */
    public void startPlayRecordFile(String path, AudioTrack.OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener) {
        if (!checkState()) {
            return;
        }
        //判断文件是否存在
        File file = new File(path);
        if (!file.exists() || file.isDirectory()) {
            Log.i(TAG, "startPlayRecordFile: 无效的文件");
        }
        //读取音频文件数据
        final byte[] sound = AudioRecordFileHandler.getInstance().getByteData(path);
        if (sound == null) {
            Log.i(TAG, "startPlayRecordFile: 没有录音数据");
            return;
        }

        Log.i(TAG, "AudioTrack 开始播放");
        int audioLen = sound.length / 2;
        audioTrack.setNotificationMarkerPosition(audioLen);
        audioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
            @Override
            public void onMarkerReached(AudioTrack track) {
                Log.i(TAG, "onMarkerReached: ");
                if (onPlaybackPositionUpdateListener != null) {
                    onPlaybackPositionUpdateListener.onMarkerReached(track);
                }
            }

            @Override
            public void onPeriodicNotification(AudioTrack track) {
                Log.i(TAG, "onPeriodicNotification: ");
                if (onPlaybackPositionUpdateListener != null) {
                    onPlaybackPositionUpdateListener.onPeriodicNotification(track);
                }
            }
        });
        if (audioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            //启动一个播放线程，写入音频数据
            new Thread(Thread.currentThread().getThreadGroup(), () -> audioTrack.write(sound, 0, sound.length), "AudioTrackPlay").start();
        }
    }

    /**
     * 播放，必须先调用start或startPlayRecordFile
     */
    public void play() {
        Log.i(TAG, "play");
        if (audioTrack != null) {
            audioTrack.play();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        Log.i(TAG, "pause");
        if (audioTrack != null) {
            audioTrack.pause();
        }
    }

    /**
     * 停止播放
     */
    public void stop() {
        Log.i(TAG, "stop");
        if (audioTrack != null) {
            audioTrack.stop();
            isPlaying = false;
        }
    }

    public void flush() {
        Log.i(TAG, "flush");
        if (audioTrack != null) {
            audioTrack.flush();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        Log.i(TAG, "release");
        release = true;
        semaphore.release();
        if (audioTrack != null) {
            audioTrack.release();
            audioTrack = null;
        }
    }
}