//
// Created by xiangang on 2022/4/30.
//


/*
 * Copyright (c) 2001 Fabrice Bellard
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

#ifndef FFMPEG_DECODE_VIDEO_H
#define FFMPEG_DECODE_VIDEO_H


/**
 * @file
 * video decoding with libavcodec API example
 *
 * @example decode_video.c
 */
#include <jni.h>
#include "common.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#include <thread>         // std::this_thread::sleep_for
#include <chrono>         // std::chrono::seconds

//由于 FFmpeg 库是 C 语言实现的，告诉编译器按照 C 的规则进行编译
extern "C" {
#include <libavcodec/version.h>
#include <libavcodec/avcodec.h>
#include <libavformat/version.h>
#include <libavformat/avformat.h>
#include <libavutil/version.h>
#include <libavfilter/version.h>
#include <libswresample/version.h>
#include <libswscale/version.h>
#include <libavutil/frame.h>
#include <libavutil/mem.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libswresample/swresample.h>
#include <libavutil/opt.h>
#include <libavutil/channel_layout.h>
#include <libavutil/samplefmt.h>
}

#define INBUF_SIZE 4096
#define AUDIO_INBUF_SIZE 20480
#define AUDIO_REFILL_THRESH 4096
/**
 * 解码回调结构体
 */
typedef struct {
    JavaVM *jvm;//Java virtual machine，
    JNIEnv *env;//Java Native Interface Environment
    jstring input_file_path;
    jstring output_file_path;
    jobject decode_buffer_listener;
    jclass decode_buffer_listener_clazz;
    jmethodID decode_buffer_listener_method_id;

} jni_decode_buffer_callback_t;

/**
 * Decoder对象
 */
class Decoder {

private:

    // 定义承载FFmpegMobile$DecodeBufferListener的结构体实例
    //static jni_decode_buffer_callback_t *jni_decode_video_buffer_callback;

public:

    static jni_decode_buffer_callback_t *jni_decode_video_buffer_callback;

    static jni_decode_buffer_callback_t *jni_decode_audio_buffer_callback;

    virtual int decodeVideo(const char *inputFileName, const char *outputFileName);

    virtual int decodeAudio(const char *inputFileName, const char *outputFileName);

    virtual ~Decoder();

    //static jni_decode_buffer_callback_t *GetJniDecoderBufferCallback();
};


#endif