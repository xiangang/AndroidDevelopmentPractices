//
// Created by Administrator on 1/16/2017.
//

#ifndef RTMP_LANG_H
#define RTMP_LANG_H

#include "android/log.h"
#define TAG "RTMP"
#define LOGD(fmt, ...) \
        __android_log_print(ANDROID_LOG_DEBUG, TAG,fmt, ##__VA_ARGS__)

#define LOGI(fmt,...) \
        __android_log_print(ANDROID_LOG_INFO, TAG,fmt, ##__VA_ARGS__)
#endif //RTMP_LANG_H
