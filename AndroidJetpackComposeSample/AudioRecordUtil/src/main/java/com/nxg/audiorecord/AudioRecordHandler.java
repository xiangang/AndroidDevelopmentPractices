package com.nxg.audiorecord;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.text.TextUtils;

/**
 * AudioRecord帮助类
 */
public class AudioRecordHandler implements IAudioRecordHandle {

    private static final String TAG = "AudioRecordHandler";
    private final Context context;//Android上年文
    private AudioRecord audioRecord;//AudioRecord实例
    private final byte[] audioData;// 录制的缓冲数组
    private final int maxSegmentDataLength;//录音数据单次回调数组的最大长度
    private final AudioRecordingCallback audioRecordingCallback;//实时录音数据回调，录音停止回调
    private boolean saveRecordFile = false;//是否生成录音文件
    private final String pcmFilePath;//原始PCM格式录音文件路径
    private final String wavFilePath;//PCM格式转WAV格式录音文件路径
    private volatile boolean isRecording;//是否正在录制音频


    @SuppressLint("MissingPermission")
    private AudioRecordHandler(Builder builder) {
        //根据定义好的配置，来获取minBufferSize
        int minBufferSize = AudioRecord.getMinBufferSize(builder.sampleRateInHz, builder.channelConfig, builder.audioFormat);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, builder.sampleRateInHz, builder.channelConfig, builder.audioFormat, minBufferSize);
        //定义缓冲数组
        audioData = new byte[minBufferSize];
        maxSegmentDataLength = minBufferSize / 2;
        audioRecordingCallback = builder.audioRecordingCallback;
        context = builder.context;
        saveRecordFile = builder.saveRecordFile;
        if (!TextUtils.isEmpty(builder.pcmFilePath)) {
            pcmFilePath = builder.pcmFilePath;
        } else {
            pcmFilePath = context.getFilesDir() + "/record.pcm";
        }
        if (!TextUtils.isEmpty(builder.wavFilePath)) {
            wavFilePath = builder.wavFilePath;
        } else {
            wavFilePath = context.getFilesDir() + "/record.wav";
        }
    }

    /**
     * 返回是否在录音中
     *
     * @return true正在录音，false录音结束
     */
    private synchronized boolean isRecording() {
        return isRecording;
    }

    /**
     * 设置是否录音中
     *
     * @param recording true正在录音，false录音结束
     */
    private synchronized void setRecording(boolean recording) {
        isRecording = recording;
    }

    @Override
    public void startRecord() {
        if (!isRecording()) {
            setRecording(true);
            new Thread(new RecordTask()).start();
        }
    }

    @Override
    public void stopRecord() {
        setRecording(false);
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
            //准备录音文件
            AudioFileHandler.getInstance().open(pcmFilePath, wavFilePath);
            // 录制的内容放置到了audioData中，result代表存储长度
            int offsetInBytes = 0;//写入数据的 audioData 中的索引，以字节表示，不能为负数，否则会导致数据访问越界。
            int sizeInBytes = audioData.length;//请求的字节数，不能为负数，否则会导致数据访问越界。
            byte[] segmentAudioData = new byte[maxSegmentDataLength];

            //开始录制，死循环
            while (isRecording) {
                //读取录音数据到audioData中
                int audioDataLength = audioRecord.read(audioData, offsetInBytes, sizeInBytes);
                //如果读取的数据长度和sizeInBytes不一致则录音失败，结束录音(按照文档的说法，audioDataResult不能大于sizeInBytes)
                if (audioDataLength != sizeInBytes) {
                    LogUtil.e(TAG, "录音失败!");
                    break;
                }
                if (saveRecordFile) {
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
                        AudioFileHandler.getInstance().write(segmentAudioData);
                    }
                }
                //音频数据回调
                int volume = (int) AudioFileHandler.getInstance().calculateVolume(audioData);
                LogUtil.i(TAG, "calculateVolume = " + volume);
                //将数据回调出去
                if (audioRecordingCallback != null) {
                    audioRecordingCallback.onRecording(audioData, audioDataLength, volume);
                }
            }
            //死循环结束，停止录音
            if (audioRecord != null) {
                audioRecord.stop();
            }

        }
    }


    /**
     * 构建者模式
     */
    static class Builder {
        private Context context;
        private int sampleRateInHz = 16000;//采样率
        private int channelConfig = AudioFormat.CHANNEL_IN_MONO;//定义采样通道（过时，但是使用其他的又不行)
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;//音频数据格式：PCM 16 位每个样本。 保证受设备支持。
        private boolean saveRecordFile = false;//是否生成录音文件
        private String pcmFilePath;//原始PCM格式录音文件路径
        private String wavFilePath;//PCM格式转WAV格式录音文件路径
        private AudioRecordingCallback audioRecordingCallback;//实时录音数据回调，录音停止回调

        public void setContext(Context context) {
            this.context = context;
        }

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

        public void setSaveRecordFile(boolean saveRecordFile) {
            this.saveRecordFile = saveRecordFile;
        }

        public void setPcmFilePath(String pcmFilePath) {
            this.pcmFilePath = pcmFilePath;
        }

        public void setWavFilePath(String wavFilePath) {
            this.wavFilePath = wavFilePath;
        }

        public void setAudioRecordingCallback(AudioRecordingCallback audioRecordingCallback) {
            this.audioRecordingCallback = audioRecordingCallback;
        }

        public AudioRecordHandler build(Context context) {
            return new AudioRecordHandler(this);
        }
    }

}