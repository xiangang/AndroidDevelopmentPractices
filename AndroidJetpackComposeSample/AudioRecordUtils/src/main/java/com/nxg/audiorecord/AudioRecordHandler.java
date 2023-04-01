package com.nxg.audiorecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;

import java.util.Arrays;

/**
 * AudioRecord工具类
 */
public class AudioRecordHandler implements IAudioRecordHandle {

    private static final String TAG = "AudioRecordHandler";
    private AudioRecord audioRecord;//AudioRecord实例
    private final byte[] audioData;// 录制的缓冲数组
    private final int maxSegmentDataLength;//录音数据单次回调数组的最大长度
    private AudioRecordingCallback audioRecordingCallback;//实时录音数据回调，录音停止回调
    private final String recordFileDirectory;//原始PCM格式录音文件和转WAV格式录音文件所在目录路径频
    private volatile boolean pause;//是否正在写入音频文件
    private volatile boolean removeFile;//删除本次录音文件

    @SuppressLint("MissingPermission")
    private AudioRecordHandler(Builder builder, Context context) {
        LogUtil.i(TAG, "AudioRecordHandler()");
        //根据定义好的配置，来获取minBufferSize
        int minBufferSize = AudioRecord.getMinBufferSize(builder.sampleRateInHz, builder.channelConfig, builder.audioFormat);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, builder.sampleRateInHz, builder.channelConfig, builder.audioFormat, minBufferSize);
        //定义缓冲数组
        audioData = new byte[minBufferSize];
        maxSegmentDataLength = minBufferSize / 2;
        if (!TextUtils.isEmpty(builder.recordFileDirectory)) {
            recordFileDirectory = builder.recordFileDirectory;
        } else {
            recordFileDirectory = Environment.getExternalStorageDirectory() + "/ARecordMaster";
        }
        LogUtil.i(TAG, "recordFileDirectory = " + recordFileDirectory);
    }

    public void setAudioRecordingCallback(AudioRecordingCallback audioRecordingCallback) {
        this.audioRecordingCallback = audioRecordingCallback;
    }

    /**
     * 返回是否在录音中
     *
     * @return true正在录音，false录音结束
     */
    public synchronized boolean isRecording() {
        return audioRecord != null && audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }

    /**
     * 返回是否在写入音频文件
     *
     * @return true正在写入，false不写入
     */
    public synchronized boolean isPause() {
        return pause;
    }

    /**
     * 设置是否录音中
     *
     * @param pause true正在写入，false不写入
     */
    private synchronized void setPause(boolean pause) {
        this.pause = pause;
    }

    @Override
    public void start() {
        LogUtil.i(TAG, "start isRecording = " + isRecording());
        if (!isRecording()) {
            LogUtil.i(TAG, "start RecordTask ");
            if (audioRecord != null) {
                //TODO 首次执行初始化没那么快，如果还没初始化完成，直接调用startRecording会崩溃
                if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                    audioRecord.startRecording();
                    removeFile = false;
                    setPause(false);
                    new Thread(Thread.currentThread().getThreadGroup(), new RecordTask(), "Thread-AudioRecord").start();
                } else {
                    //TODO 提示
                }
            }
        }
    }

    @Override
    public void stop() {
        if (audioRecord != null) {
            audioRecord.stop();
        }
    }

    @Override
    public void resume() {
        setPause(false);
    }

    @Override
    public void pause() {
        setPause(true);
    }

    @Override
    public void cancel() {
        removeFile = true;
        LogUtil.i(TAG, "cancel removeFile = " + removeFile);
        stop();
    }

    @Override
    public void release() {
        if (audioRecord != null) {
            audioRecord.release();
            audioRecord = null;
        }
    }

    /**
     * 录制音频任务
     */
    private class RecordTask implements Runnable {

        @Override
        public void run() {
            LogUtil.i(TAG, "RecordTask run --->  ");
            //准备录音文件
            String pcmFilePath = AudioRecordFileHandler.getInstance().open(recordFileDirectory);
            // 录制的内容放置到了audioData中，result代表存储长度
            int offsetInBytes = 0;//写入数据的 audioData 中的索引，以字节表示，不能为负数，否则会导致数据访问越界。
            int sizeInBytes = audioData.length;//请求的字节数，不能为负数，否则会导致数据访问越界。
            byte[] segmentAudioData = new byte[maxSegmentDataLength];
            LogUtil.i(TAG, "isRecording = " + isRecording());
            LogUtil.i(TAG, "audioRecord = " + audioRecord);
            LogUtil.i(TAG, "sizeInBytes.length = " + sizeInBytes);
            LogUtil.i(TAG, "Thread.currentThread().isInterrupted() = " + Thread.currentThread().isInterrupted());
            if (audioRecordingCallback != null) {
                audioRecordingCallback.onStart(pcmFilePath, pcmFilePath.replace(AudioRecordFileHandler.PCM, AudioRecordFileHandler.WAV));
            }
            //开始录制，死循环
            while (isRecording() && !Thread.currentThread().isInterrupted()) {
                //LogUtil.i(TAG, "audioRecord.getState() = " + audioRecord.getState());
                //LogUtil.i(TAG, "audioRecord.getRecordingState() = " + audioRecord.getRecordingState());
                //LogUtil.i(TAG, "audioData.length = " + audioData.length);
                //读取录音数据到audioData中
                int audioDataLength = audioRecord.read(audioData, offsetInBytes, sizeInBytes);
                //如果读取的数据长度和sizeInBytes不一致则录音失败，结束录音(按照文档的说法，audioDataResult不能大于sizeInBytes)
                //LogUtil.i(TAG, "audioDataLength = " + audioDataLength);
                //LogUtil.i(TAG, "sizeInBytes = " + sizeInBytes);
                if (audioDataLength != sizeInBytes) {
                    LogUtil.e(TAG, "录音失败或结束!");
                    break;
                }
                //配置生成录音文件并且未暂停写入
                //LogUtil.i(TAG, "isWriting() = " + isPause());
                if (!isPause()) {
                    //对获取到的音频数据进行分组处理
                    int offset = audioDataLength % maxSegmentDataLength > 0 ? 1 : 0;
                    for (int i = 0; i < audioDataLength / maxSegmentDataLength + offset; i++) {
                        int length = maxSegmentDataLength;
                        if ((i + 1) * maxSegmentDataLength > audioDataLength) {
                            length = audioDataLength - i * maxSegmentDataLength;
                        }
                        //复制音频数据到frameBuffer
                        System.arraycopy(audioData, i * maxSegmentDataLength, segmentAudioData, 0, length);
                        //写入录音文件
                        //LogUtil.i(TAG, "audioData write = " + Arrays.toString(segmentAudioData));
                        AudioRecordFileHandler.getInstance().write(segmentAudioData);
                    }
                }
                //音频数据回调
                int volume = (int) AudioRecordFileHandler.getInstance().calculateVolume(audioData);
                //LogUtil.i(TAG, "audioData volume = " + volume);
                //将数据回调出去
                if (audioRecordingCallback != null) {
                    audioRecordingCallback.onRecording(audioData, audioDataLength, volume);
                }
            }
            //关闭文件输出
            AudioRecordFileHandler.getInstance().close();
            LogUtil.i(TAG, "removeFile = " + removeFile);
            if (removeFile) {
                AudioRecordFileHandler.getInstance().delete();
                if (audioRecordingCallback != null) {
                    audioRecordingCallback.onCancel();
                }
                return;
            }
            //循环结束，生成wav文件
            if (audioRecord != null) {
                AudioRecordFileHandler.getInstance().syncPcmToWave(audioRecord.getSampleRate());
            }
            //回调录音结束
            if (audioRecordingCallback != null) {
                audioRecordingCallback.onStop(pcmFilePath, pcmFilePath.replace(AudioRecordFileHandler.PCM, AudioRecordFileHandler.WAV));
            }
        }
    }


    /**
     * 构建者模式
     */
    public static class Builder {
        private int sampleRateInHz = 44100;//采样率
        private int channelConfig = AudioFormat.CHANNEL_IN_MONO;//定义采样通道（过时，但是使用其他的又不行)
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//音频数据格式：PCM 16 位每个样本。保证受设备支持。
        private String recordFileDirectory;//原始PCM格式录音文件和转WAV格式录音文件所在目录路径


        public Builder setMaxDataLength(int maxDataLength) {
            return this;
        }

        public Builder setSampleRateInHz(int sampleRateInHz) {
            this.sampleRateInHz = sampleRateInHz;
            return this;
        }

        public Builder setChannelConfig(int channelConfig) {
            this.channelConfig = channelConfig;
            return this;
        }

        public Builder setAudioFormat(int audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }

        public Builder setRecordFileDirectory(String recordFileDirectory) {
            this.recordFileDirectory = recordFileDirectory;
            return this;
        }

        public AudioRecordHandler build(Context context) {
            return new AudioRecordHandler(this, context);
        }
    }

}