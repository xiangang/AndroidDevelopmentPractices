#ifndef COMMON_H
#define COMMON_H

#include <jni.h>
#include <exception>

#define TAG "AcodeMobile"

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
