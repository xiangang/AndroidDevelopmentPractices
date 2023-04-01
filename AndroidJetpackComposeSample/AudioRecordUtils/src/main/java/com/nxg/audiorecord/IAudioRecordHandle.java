package com.nxg.audiorecord;

/**
 * 录音处理接口
 */
public interface IAudioRecordHandle {

    /**
     * 开始录制音频
     */
    void start();

    /**
     * 停止录制音频
     */
    void stop();

    /**
     * 恢复写入音频文件
     */
    void resume();

    /**
     * 暂停写入音频文件
     */
    void pause();

    /**
     * 取消录音
     */
    void cancel();

    /**
     * 释放资源
     */
    void release();
}
