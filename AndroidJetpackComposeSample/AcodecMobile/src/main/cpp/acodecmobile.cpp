#include <jni.h>
#include <string>
#include "common.h"
#include <android/bitmap.h>
// for native window JNI
#include <android/native_window_jni.h>
#include <android/native_window.h>
// for native OpenCV JNI
#include "opencv2/core/core.hpp"
#include "opencv2/core/mat.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"

using namespace cv;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_nxg_acodecmobile_AvCodecMobile_stringFromJNI(JNIEnv *env, jclass clazz) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_nxg_acodecmobile_AvCodecMobile_renderYuvDataOnSurface(JNIEnv *env, jclass clazz,
                                                               jint width, jint height,
                                                               jbyteArray yuv_data,
                                                               jobject surface) {
  /*  // yuv转rgba
    jbyte *data = env->GetByteArrayElements(yuv_data, nullptr);
    cv::Mat yuvImg(height + height / 2, width, CV_8UC1, data);
    cv::Mat rgbImg;
    cv::cvtColor(yuvImg, rgbImg, COLOR_YUV2RGBA_IYUV);
    // 这两行代码用于翻转的，用不到，这里注释掉
    //cv::transpose(yuvImg, rgbImg);
    //cv::flip(yuvImg, rgbImg, 1);
    //获取ANativeWindow
    ANativeWindow *window = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_acquire(window);
    //设置ANativeWindow相关参数，包括宽/高/像素格式
    ANativeWindow_setBuffersGeometry(window, rgbImg.cols, rgbImg.rows, WINDOW_FORMAT_RGBA_8888);
    ANativeWindow_Buffer outBuffer;
    // 调用ANativeWindow_lock锁定后台的缓冲部分，并获取surface缓冲区的地址
    if (int32_t err = ANativeWindow_lock(window, &outBuffer, nullptr)) {
        LOGE("ANativeWindow_lock failed with error code: %d\n", err);
        ANativeWindow_release(window);
    }
    // 拷贝rgb数据到缓冲区
    auto *outPtr = reinterpret_cast<uint8_t *>(outBuffer.bits);
    int dst_line_size = outBuffer.stride * 4;
    //一行一行拷贝
    for (int i = 0; i < outBuffer.height; ++i) {
        //从源src所指的内存地址的起始位置开始拷贝n个字节到目标dest所指的内存地址的起始位置中
        memcpy(outPtr + i * dst_line_size, rgbImg.data + i * rgbImg.cols * 4, dst_line_size);
    }
    //绘制
    ANativeWindow_unlockAndPost(window);
    //用完释放
    ANativeWindow_release(window);
    env->ReleaseByteArrayElements(yuv_data, data, 0);
    yuvImg.release();
    rgbImg.release();*/
}