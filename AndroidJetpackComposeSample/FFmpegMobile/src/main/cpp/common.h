#ifndef FFMPEG_LANG_H
#define FFMPEG_LANG_H

#include <jni.h>
#include <string>
#include <exception>
#include <iostream>

#define TAG "ffmpeg"
#define FF_LOG_UNKNOWN        ANDROID_LOG_UNKNOWN
#define FF_LOG_DEFAULT        ANDROID_LOG_DEFAULT
#define FF_LOG_VERBOSE        ANDROID_LOG_VERBOSE
#define FF_LOG_DEBUG          ANDROID_LOG_DEBUG
#define FF_LOG_INFO           ANDROID_LOG_INFO
#define FF_LOG_WARN           ANDROID_LOG_WARN
#define FF_LOG_ERROR          ANDROID_LOG_ERROR
#define FF_LOG_FATAL          ANDROID_LOG_FATAL
#define FF_LOG_SILENT         ANDROID_LOG_SILENT

extern "C" {
#ifdef ANDROID
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, TAG, format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  TAG, format, ##__VA_ARGS__)
#define LOGD(format, ...)  __android_log_print(ANDROID_LOG_DEBUG,  TAG, format, ##__VA_ARGS__)
#define LOGW(format, ...)  __android_log_print(ANDROID_LOG_WARN,  TAG, format, ##__VA_ARGS__)
// 打印可变参数
#define VLOG(level, TAG, ...)    ((void)__android_log_vprint(level, TAG, __VA_ARGS__))
#else
#define LOGE(format, ...)  printf(TAG format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf(TAG format "\n", ##__VA_ARGS__)
#define LOGD(format, ...)  printf(TAG format "\n", ##__VA_ARGS__)
#define LOGW(format, ...)  printf(TAG format "\n", ##__VA_ARGS__)
//打印可变参数
#define VLOG(level, TAG, ...)    ((void)__android_log_vprint(level, TAG, __VA_ARGS__))
#endif
}

#endif
