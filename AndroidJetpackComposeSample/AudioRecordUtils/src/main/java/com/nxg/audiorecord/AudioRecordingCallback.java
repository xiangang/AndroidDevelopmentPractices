package com.nxg.audiorecord;

/**
 * 音频录制过程中的数据回调接口用于实时获取录音数据
 * 一般可用于生成录音文件，语音聊天，推流直播
 */
public interface AudioRecordingCallback {

    /**
     * 录音开始的回调
     *
     * @param pcmFilePath 录音PCM文件存储的路径
     * @param wavFilePath 录音WAV文件存储的路径
     */
    void onStart(String pcmFilePath, String wavFilePath);

    /**
     * 录音数据获取回调
     *
     * @param audioData 音频数据字节数组
     * @param length    数据数据字节数组的长度
     * @param volume    音频音量大小（可用于绘制波形）
     */
    void onRecording(byte[] audioData, int length, int volume);

    /**
     * 录音结束后的回调
     *
     * @param pcmFilePath 录音PCM文件存储的路径
     * @param wavFilePath 录音WAV文件存储的路径
     */
    void onStop(String pcmFilePath, String wavFilePath);

    /**
     * 取消录音，删除录音文件
     */
    void onCancel();
}
