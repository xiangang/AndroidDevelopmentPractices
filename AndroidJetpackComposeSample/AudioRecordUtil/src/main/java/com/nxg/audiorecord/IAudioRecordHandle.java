package com.nxg.audiorecord;

/**
 * 录音处理接口
 */
public interface IAudioRecordHandle {

    /**
     * 开始录制音频
     */
    void startRecord();

    /**
     * 停止录制音频
     */
    void stopRecord();

    /**
     * 释放资源
     */
    void release();
}
