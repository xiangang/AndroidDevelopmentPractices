package com.nxg.audiorecord;

import android.content.Context;
import android.util.Log;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 用来保存本地pcm文件，以及将pcm转化为wav文件
 */
public class AudioFileHandler {
    public static final String TAG = "AudioFileHandler";
    private FileOutputStream mFileOutputStream;
    private File mWavFile;
    private File mPcmFileName;

    int db = 40;
    private final double factor = Math.pow(10, (double) db / 20);

    private static class Holder {
        private static final AudioFileHandler INSTANCE = new AudioFileHandler();
    }

    public static AudioFileHandler getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * 打开文件输出
     *
     * @param pcmFilePath 原始PCM格式录音文件
     * @param wavFilePath CM格式转WAV格式录音文件
     */
    public void open(String pcmFilePath, String wavFilePath) {
        close();
        try {
            mPcmFileName = new File(pcmFilePath);
            mWavFile = new File(wavFilePath);
            mFileOutputStream = new FileOutputStream(mPcmFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        if (mFileOutputStream != null) {
            try {
                mFileOutputStream.write(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void close() {
        try {
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
                mFileOutputStream.flush();
                mFileOutputStream = null;
                final String pcmFileName = mPcmFileName.getAbsolutePath();
                final String wavFileName = mWavFile.getAbsolutePath();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pcmToWave(pcmFileName, wavFileName);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] getByteData(String path) {
        try {
            FileInputStream input = new FileInputStream(new File(path));
            byte[] buf = new byte[input.available()];
            input.read(buf);
            input.close();
            return buf;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public void pcmToWave(String inFileName, String outFileName) {
        LogUtil.i(TAG, "pcm转wav文件，输出路径：" + outFileName);
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen = 0;
        long longSampleRate = 16000;
        long totalDataLen;//由于不包括RIFF和WAV
        int channels = 1; //单通道
        long byteRate = 16 * longSampleRate * channels / 8;
        byte[] data = new byte[1024];
        try {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate, int channels, long byteRate) {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (channels * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        try {
            out.write(header, 0, 44);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public boolean isOpen() {
        return mFileOutputStream != null;
    }

    double calculateVolume(byte[] buffer) {
        double sumVolume = 0.0;
        double avgVolume;
        double volume;
        for (int i = 0; i < buffer.length; i += 2) {
            int v1 = buffer[i] & 0xFF;
            int v2 = buffer[i + 1] & 0xFF;
            int temp = v1 + (v2 << 8);// 小端
            if (temp >= 0x8000) {
                temp = 0xffff - temp;
            }
            sumVolume += Math.abs(temp);
        }
        avgVolume = sumVolume / buffer.length / 2;
        volume = Math.log10(1 + avgVolume) * 10;
        return volume;
    }

}

