//#include <jni.h>
//#include <string>
//
//extern "C" JNIEXPORT jstring JNICALL
//Java_com_nxg_rtmp_1mobile_RtmpMobile_stringFromJNI(
//        JNIEnv* env,
//        jobject /* this */) {
//    std::string hello = "Hello from C++";
//    return env->NewStringUTF(hello.c_str());
//}


#include <jni.h>
#include <string>
#include "io.h"
#include "RtmpClient.h"

/**
 * RTMP相关的native方法
 */
#ifdef __cplusplus
extern "C" {
#endif

jstring nativeGetRtmpVersion(
        JNIEnv *env,
        jclass clazz) {
    char version[100];
    sprintf(version, "rtmp version : %d", RTMP_LibVersion());
    return env->NewStringUTF(version);
}


jlong nativeInit(JNIEnv *env, jclass clazz, jstring url_, jint w, jint h,
                 jint timeOut) {
    const char *url = env->GetStringUTFChars(url_, nullptr);
    auto *rtmp = new RtmpClient();
    rtmp->init(url, w, h, timeOut);
    env->ReleaseStringUTFChars(url_, url);
    return reinterpret_cast<long> (rtmp);
}

jint nativeSendSpsAndPps(JNIEnv *env, jclass clazz, jlong rtmp_ptr, jbyteArray sps_, jint sps_len,
                         jbyteArray pps_, jint pps_len, jlong timestamp) {
    jbyte *sps = env->GetByteArrayElements(sps_, nullptr);
    jbyte *pps = env->GetByteArrayElements(pps_, nullptr);
    auto *rtmp = reinterpret_cast<RtmpClient *> (rtmp_ptr);
    int ret = rtmp->sendSpsAndPps((BYTE *) sps, sps_len, (BYTE *) pps, pps_len, timestamp);
    return ret;
}


jint nativeSendVideoData(JNIEnv *env, jclass clazz, jlong rtmp_ptr,
                         jbyteArray data_, jint len, jlong timestamp) {
    jbyte *data = env->GetByteArrayElements(data_, nullptr);
    auto *rtmp = reinterpret_cast<RtmpClient *> (rtmp_ptr);
    int ret = rtmp->sendVideoData((BYTE *) data, len, timestamp);
    env->ReleaseByteArrayElements(data_, data, 0);
    return ret;
}

jint nativeSendAudioSpec(JNIEnv *env, jclass clazz, jlong rtmp_ptr,
                         jbyteArray data_, jint len) {
    jbyte *data = env->GetByteArrayElements(data_, nullptr);
    auto *rtmp = reinterpret_cast<RtmpClient *> (rtmp_ptr);
    int ret = rtmp->sendAudioSpec((BYTE *) data, len);
    env->ReleaseByteArrayElements(data_, data, 0);
    return ret;
}

jint nativeSendAudioData(JNIEnv *env, jclass clazz, jlong rtmp_ptr,
                         jbyteArray data_, jint len, jlong timestamp) {
    jbyte *data = env->GetByteArrayElements(data_, nullptr);
    auto *rtmp = reinterpret_cast<RtmpClient *> (rtmp_ptr);
    int ret = rtmp->sendAudioData((BYTE *) data, len, timestamp);
    env->ReleaseByteArrayElements(data_, data, 0);
    return ret;
}

jint nativeRelease(JNIEnv *env, jclass clazz, jlong rtmp_ptr) {
    auto *rtmp = reinterpret_cast<RtmpClient *> (rtmp_ptr);
    delete rtmp;
    return 0;
}

#ifdef __cplusplus
}
#endif

/**
 * 需要注册的函数列表，放在JNINativeMethod 类型的数组中，
 * 以后如果需要增加函数，只需在这里添加就行了
 * 参数：
 * 1.java中用native关键字声明的函数名
 * 2.签名（传进来参数类型和返回值类型的说明）
 * 3.C/C++中对应函数的函数名（地址）
 */
static const JNINativeMethod nativeMethods[] = {
        {"nativeGetRtmpVersion", "()Ljava/lang/String;",     (void *) nativeGetRtmpVersion},
        {"nativeInit",          "(Ljava/lang/String;III)J", (void *) nativeInit},
        {"nativeRelease",           "(J)I",                     (void *) nativeRelease},
        {"nativeSendSpsAndPps",  "(J[BI[BIJ)I",              (void *) nativeSendSpsAndPps},
        {"nativeSendVideoData",  "(J[BIJ)I",                 (void *) nativeSendVideoData},
        {"nativeSendAudioSpec",  "(J[BI)I",                  (void *) nativeSendAudioSpec},
        {"nativeSendAudioData",  "(J[BIJ)I",                 (void *) nativeSendAudioData},
};

/**
 * 动态注册基本思想是在JNI_Onload()函数中通过JNI中提供的RegisterNatives()方法来将C/C++方法和Java方法对应起来(注册),
 * 我们在调用 System.loadLibrary的时候,会在C/C++文件中回调一个名为JNI_OnLoad()的函数,在这个函数中一般是做一些初始化相关操作,
 * 我们可以在这个方法里面注册函数, 注册整体流程如下：
 * 编写Java端的相关native方法
 * 编写C/C++代码, 实现JNI_Onload()方法
 * 将Java方法和 C/C++方法通过签名信息一一对应起来
 * 通过JavaVM获取JNIEnv, JNIEnv主要用于获取Java类和调用一些JNI提供的方法
 * 使用类名和对应的方法作为参数, 调用JNI提供的函数RegisterNatives()注册方法
 * @param vm
 * @param reserved
 * @return JNI_VERSION_1_6
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    //获取JNIEnv
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != nullptr);

    //指定类的路径，通过FindClass 方法来找到对应的类
    const char *className = "com/nxg/rtmp_mobile/RtmpMobile";

    //找到声明native方法的类
    jclass clazz = env->FindClass(className);
    if (clazz == nullptr) {
        return -1;
    }

    //注册函数 参数：java类 所要注册的函数数组 注册函数的个数
    int methodsNum = sizeof(nativeMethods) / sizeof(nativeMethods[0]);
    if (env->RegisterNatives(clazz, nativeMethods, methodsNum) < 0) {
        return -1;
    }
    //返回jni 的版本
    return JNI_VERSION_1_6;
}