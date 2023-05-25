#include <jni.h>
#include <string>
#include <assert.h>
#include "common.h"
#include "decode_av.h"
#include <pthread.h>

//由于 FFmpeg 库是 C 语言实现的，告诉编译器按照 C 的规则进行编译
extern "C" {
#include <libavcodec/version.h>
#include <libavcodec/avcodec.h>
#include <libavformat/version.h>
#include <libavutil/version.h>
#include <libavfilter/version.h>
#include <libswresample/version.h>
#include <libswscale/version.h>
#include <libavformat/avformat.h>
#include <libavutil/dict.h>
}

using namespace std;

// 全局的JavaVM，在JNI_OnLoad时赋值
static JavaVM *globalJavaVM = nullptr;
// 编码结构
jni_decode_buffer_callback_t *Decoder::jni_decode_video_buffer_callback = nullptr;
jni_decode_buffer_callback_t *Decoder::jni_decode_audio_buffer_callback = nullptr;

static void av_log_callback(void *ptr, int level, const char *fmt, va_list vl) {
    int ffmpeg_level = FF_LOG_VERBOSE;
    if (level <= AV_LOG_ERROR)
        ffmpeg_level = FF_LOG_ERROR;
    else if (level <= AV_LOG_WARNING)
        ffmpeg_level = FF_LOG_WARN;
    else if (level <= AV_LOG_INFO)
        ffmpeg_level = FF_LOG_INFO;
    else if (level <= AV_LOG_VERBOSE)
        ffmpeg_level = FF_LOG_VERBOSE;
    else
        ffmpeg_level = FF_LOG_DEBUG;
    VLOG(ffmpeg_level, TAG, fmt, vl);
}


/**
 * 设置回调对象
 */
jobject ffmpegCallback = nullptr;
jclass ffmpegCallbackCls = nullptr;
jmethodID ffmpegCallbackMID = nullptr;

extern "C"
JNIEXPORT jint JNICALL
Java_com_nxg_ffmpeg_mobile_FFmpegMobile_setFFmpegCallback(JNIEnv *env, jobject instance,
                                                          jobject ffmpegCallback_) {
    //转换为全局变量
    ffmpegCallback = env->NewGlobalRef(ffmpegCallback_);
    if (ffmpegCallback == nullptr) {
        return -3;
    }
    ffmpegCallbackCls = env->GetObjectClass(ffmpegCallback);
    if (ffmpegCallbackCls == nullptr) {
        return -1;
    }
    ffmpegCallbackMID = env->GetMethodID(ffmpegCallbackCls, "callback", "(JJJJ)V");
    if (ffmpegCallbackMID == nullptr) {
        return -2;
    }
    env->CallVoidMethod(ffmpegCallback, ffmpegCallbackMID, (jlong) 0, (jlong) 0, (jlong) 0,
                        (jlong) 0);
    return 0;
}


extern "C" JNIEXPORT jstring JNICALL
Java_com_nxg_ffmpeg_1mobile_FFmpegMobile_stringFromJNI(JNIEnv *env, jclass clazz) {
    std::string hello = "Hello from C++";
    char strBuffer[1024 * 4] = {0};
    strcat(strBuffer, "libavcodec : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVCODEC_VERSION));
    strcat(strBuffer, "\nlibavformat : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFORMAT_VERSION));
    strcat(strBuffer, "\nlibavutil : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVUTIL_VERSION));
    strcat(strBuffer, "\nlibavfilter : ");
    strcat(strBuffer, AV_STRINGIFY(LIBAVFILTER_VERSION));
    strcat(strBuffer, "\nlibswresample : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWRESAMPLE_VERSION));
    strcat(strBuffer, "\nlibswscale : ");
    strcat(strBuffer, AV_STRINGIFY(LIBSWSCALE_VERSION));
    strcat(strBuffer, "\navcodec_configure : \n");
    strcat(strBuffer, avcodec_configuration());
    strcat(strBuffer, "\navcodec_license : ");
    strcat(strBuffer, avcodec_license());
    LOGI("GetFFmpegVersion\n%s", strBuffer);
    return env->NewStringUTF(hello.c_str());
}

/**
 * 相关的native方法
 */
#ifdef __cplusplus
extern "C" {
#endif

jstring nativeGetAvcodecVersion(
        JNIEnv *env,
        jclass clazz) {
    char version[100];
    av_log_set_callback(av_log_callback);
    sprintf(version, "avcodec version : %s", AV_STRINGIFY(LIBAVCODEC_VERSION));
    return env->NewStringUTF(version);
}


jlong nativeInit(JNIEnv *env, jclass clazz) {
    auto *decoder = new Decoder();
    return reinterpret_cast<long> (decoder);
}


jint nativeRelease(JNIEnv *env, jclass clazz, jlong decoder_ptr) {
    auto *decoder = reinterpret_cast<Decoder *> (decoder_ptr);
    delete decoder;
    return 0;
}

static void *async_thread_decode_h264_file(void *arg) {
    // JNI_OnLoad时保存JavaVM，这里使用JavaVM->AttachCurrentThread获取JNIEnv
    long decoder_ptr = *(long *) arg;
    JNIEnv *env = nullptr;
    int status = globalJavaVM->GetEnv((void **) &env, JNI_VERSION_1_6);
    LOGI("async_thread_decode_h264_file: status is %d", status);
    if (status < JNI_OK) {
        globalJavaVM->AttachCurrentThread(&env, nullptr);
        LOGI("async_thread_decode_h264_file: AttachCurrentThread");
    }
    //LOGI("async_thread_decode_h264_file: Decoder::jni_decode_video_buffer_callback %d",Decoder::jni_decode_video_buffer_callback);
    if (env != nullptr) {
        // 生成解码对象并调用解码方法
        const char *input_file_path = env->GetStringUTFChars(
                Decoder::jni_decode_video_buffer_callback->input_file_path, nullptr);
        const char *output_file_path = env->GetStringUTFChars(
                Decoder::jni_decode_video_buffer_callback->output_file_path, nullptr);
        //LOGI("async_thread_decode_h264_file: %s %s ", input_file_path, output_file_path);

        /*int length = 1280 * 720 * sizeof(uint8_t) * 3;
        auto *pDecodedBuffer = (uint8_t *) malloc(1280 * 720 * sizeof(uint8_t) * 3);
        jbyteArray jbytes = env->NewByteArray(length);
        env->SetByteArrayRegion(jbytes, 0, length, (jbyte *) pDecodedBuffer);*/
        /*auto *decoder = reinterpret_cast<Decoder *> (decoder_ptr);
        decoder->decodeVideo(input_file_path, output_file_path);*/

        // 调用 DecodeBufferListener 的 onBufferListener 函数，完成调用流程.
        /*env->CallVoidMethod(Decoder::jni_decode_video_buffer_callback->decode_buffer_listener,
                            Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_method_id, jbytes);*/

        // 销毁全局引用 --- 如果有多处或多次回调，自行判断销毁时机
        //env->DeleteLocalRef(jbytes);
        env->ReleaseStringUTFChars(Decoder::jni_decode_video_buffer_callback->input_file_path,
                                   input_file_path);
        env->ReleaseStringUTFChars(Decoder::jni_decode_video_buffer_callback->output_file_path,
                                   output_file_path);
        env->DeleteGlobalRef(Decoder::jni_decode_video_buffer_callback->decode_buffer_listener);
        env->DeleteGlobalRef(
                Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_clazz);
        Decoder::jni_decode_video_buffer_callback->input_file_path = nullptr;
        Decoder::jni_decode_video_buffer_callback->output_file_path = nullptr;
        Decoder::jni_decode_video_buffer_callback->decode_buffer_listener = nullptr;
        Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_clazz = nullptr;
        Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_method_id = nullptr;
        free(Decoder::jni_decode_video_buffer_callback);
    }
    //如果GetENV返回值小于0，则说明当前线程没有被附加到JVM上，因此要手动调用AttachCurrentThread，因此最后要DetachCurrentThread
    //否则代码运行时则可能会抛出错误：attempting to detach while still running code
    if (status < JNI_OK) {
        globalJavaVM->DetachCurrentThread();
    }
}
jint
nativePlayAudio(JNIEnv *env, jclass clazz, jstring audio_path) {

    const char *path = env->GetStringUTFChars(audio_path, 0);

    AVFormatContext *fmt_ctx;
    // 初始化格式化上下文
    fmt_ctx = avformat_alloc_context();

    // 使用ffmpeg打开文件
    int re = avformat_open_input(&fmt_ctx, path, nullptr, nullptr);
    if (re != 0) {
        LOGE("打开文件失败：%s", av_err2str(re));
        return re;
    }

    //探测流索引
    re = avformat_find_stream_info(fmt_ctx, nullptr);

    if (re < 0) {
        LOGE("索引探测失败：%s", av_err2str(re));
        return re;
    }

    //寻找视频流索引
    int audio_idx = av_find_best_stream(
            fmt_ctx, AVMEDIA_TYPE_AUDIO, -1, -1, nullptr, 0);

    if (audio_idx == -1) {
        LOGE("获取音频流索引失败");
        return -1;
    }
    //解码器参数
    AVCodecParameters *c_par;
    //解码器上下文
    AVCodecContext *cc_ctx;
    //声明一个解码器
    const AVCodec *codec;

    c_par = fmt_ctx->streams[audio_idx]->codecpar;

    //通过id查找解码器
    codec = avcodec_find_decoder(c_par->codec_id);

    if (!codec) {

        LOGE("查找解码器失败");
        return -2;
    }

    //用参数c_par实例化编解码器上下文，，并打开编解码器
    cc_ctx = avcodec_alloc_context3(codec);

    // 关联解码器上下文
    re = avcodec_parameters_to_context(cc_ctx, c_par);

    if (re < 0) {
        LOGE("解码器上下文关联失败:%s", av_err2str(re));
        return re;
    }

    //打开解码器
    re = avcodec_open2(cc_ctx, codec, nullptr);

    if (re != 0) {
        LOGE("打开解码器失败:%s", av_err2str(re));
        return re;
    }

    //数据包
    AVPacket *pkt;
    //数据帧
    AVFrame *frame;

    //初始化
    pkt = av_packet_alloc();
    frame = av_frame_alloc();

    //音频重采样

    int dataSize = av_samples_get_buffer_size(NULL, av_get_channel_layout_nb_channels(
            AV_CH_LAYOUT_STEREO), cc_ctx->frame_size, AV_SAMPLE_FMT_S16, 0);

    uint8_t *resampleOutBuffer = (uint8_t *) malloc(dataSize);

    //音频重采样上下文初始化
    SwrContext *actx = swr_alloc();
    actx = swr_alloc_set_opts(actx,
                              AV_CH_LAYOUT_STEREO,
                              AV_SAMPLE_FMT_S16, 44100,
                              cc_ctx->channels,
                              cc_ctx->sample_fmt, cc_ctx->sample_rate,
                              0, 0);
    LOGE("Failed to initialize the resampling channels:%d", cc_ctx->channels);
    LOGE("Failed to initialize the resampling sample_fmt:%d", cc_ctx->sample_fmt);
    LOGE("Failed to initialize the resampling sample_rate:%d", cc_ctx->sample_rate);
    re = swr_init(actx);
    if (re != 0) {
        LOGE("swr_init failed:%s", av_err2str(re));
        return re;
    }

    // JNI创建AudioTrack

    jclass jAudioTrackClass = env->FindClass("android/media/AudioTrack");
    jmethodID jAudioTrackCMid = env->GetMethodID(jAudioTrackClass, "<init>", "(IIIIII)V"); //构造

    //  public static final int STREAM_MUSIC = 3;
    int streamType = 3;
    int sampleRateInHz = 44100;
    // public static final int CHANNEL_OUT_STEREO = (CHANNEL_OUT_FRONT_LEFT | CHANNEL_OUT_FRONT_RIGHT);
    int channelConfig = (0x4 | 0x8);
    // public static final int ENCODING_PCM_16BIT = 2;
    int audioFormat = 2;
    // getMinBufferSize(int sampleRateInHz, int channelConfig, int audioFormat)
    jmethodID jGetMinBufferSizeMid = env->GetStaticMethodID(jAudioTrackClass, "getMinBufferSize",
                                                            "(III)I");
    int bufferSizeInBytes = env->CallStaticIntMethod(jAudioTrackClass, jGetMinBufferSizeMid,
                                                     sampleRateInHz, channelConfig, audioFormat);
    // public static final int MODE_STREAM = 1;
    int mode = 1;

    //创建了AudioTrack
    jobject jAudioTrack = env->NewObject(jAudioTrackClass, jAudioTrackCMid, streamType,
                                         sampleRateInHz, channelConfig, audioFormat,
                                         bufferSizeInBytes, mode);

    //play方法
    jmethodID jPlayMid = env->GetMethodID(jAudioTrackClass, "play", "()V");
    env->CallVoidMethod(jAudioTrack, jPlayMid);

    // write method
    jmethodID jAudioTrackWriteMid = env->GetMethodID(jAudioTrackClass, "write", "([BII)I");

    while (av_read_frame(fmt_ctx, pkt) >= 0) {//持续读帧
        // 只解码音频流
        if (pkt->stream_index == audio_idx) {

            //发送数据包到解码器
            avcodec_send_packet(cc_ctx, pkt);

            //清理
            av_packet_unref(pkt);

            //这里为什么要使用一个for循环呢？
            // 因为avcodec_send_packet和avcodec_receive_frame并不是一对一的关系的
            //一个avcodec_send_packet可能会出发多个avcodec_receive_frame
            for (;;) {
                // 接受解码的数据
                re = avcodec_receive_frame(cc_ctx, frame);
                if (re != 0) {
                    break;
                } else {

                    //音频重采样
                    int len = swr_convert(actx, &resampleOutBuffer,
                                          frame->nb_samples,
                                          (const uint8_t **) frame->data,
                                          frame->nb_samples);

                    jbyteArray jPcmDataArray = env->NewByteArray(dataSize);
                    // native 创建 c 数组
                    jbyte *jPcmData = env->GetByteArrayElements(jPcmDataArray, NULL);

                    //内存拷贝
                    memcpy(jPcmData, resampleOutBuffer, dataSize);

                    // 同步刷新到 jbyteArray ，并释放 C/C++ 数组
                    env->ReleaseByteArrayElements(jPcmDataArray, jPcmData, 0);


                    LOGE("解码成功%d  dataSize:%d ", len, dataSize);

                    // 写入播放数据
                    env->CallIntMethod(jAudioTrack, jAudioTrackWriteMid, jPcmDataArray, 0,
                                       dataSize);

                    // 解除 jPcmDataArray 的持有，让 javaGC 回收
                    env->DeleteLocalRef(jPcmDataArray);

                }
            }

        }
    }

    //关闭环境
    avcodec_free_context(&cc_ctx);
    // 释放资源
    av_frame_free(&frame);
    av_packet_free(&pkt);

    avformat_free_context(fmt_ctx);

    LOGE("音频播放完毕");

    env->ReleaseStringUTFChars(audio_path, path);

    return 0;
}

jint
nativeDecodeVideo(JNIEnv *env, jclass clazz, jlong decoder_ptr, jstring input_file_path_,
                  jstring output_file_path_,
                  jobject decode_buffer_listener) {
    if (!decode_buffer_listener)
        return -1;
    // 获得回调接口FFmpegMobile$DecodeBufferListener的onBufferListener的methodID.
    jclass clazz_decode_buffer_listener = env->GetObjectClass(decode_buffer_listener);
    jmethodID decode_buffer_listener_method_id = env->GetMethodID(clazz_decode_buffer_listener,
                                                                  "onBufferListener",
                                                                  "(II[B)V");
    // 这里创建Decoder::jni_decode_buffer_callback_t的实例，创建DecodeBufferListener的全局引用并赋值.
    Decoder::jni_decode_video_buffer_callback = (jni_decode_buffer_callback_t *) malloc(
            sizeof(jni_decode_buffer_callback_t));
    memset(Decoder::jni_decode_video_buffer_callback, 0, sizeof(jni_decode_buffer_callback_t));
    Decoder::jni_decode_video_buffer_callback->jvm = globalJavaVM;
    Decoder::jni_decode_video_buffer_callback->env = env;
    // jobject 也是不能跨线程调用的。解决的方法是通过全局引用来获取，env->NewGlobalRef(obj)
    Decoder::jni_decode_video_buffer_callback->decode_buffer_listener = env->NewGlobalRef(
            decode_buffer_listener);
    Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_clazz = (jclass) env->NewGlobalRef(
            clazz_decode_buffer_listener);
    Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_method_id = decode_buffer_listener_method_id;
    Decoder::jni_decode_video_buffer_callback->input_file_path = input_file_path_;
    Decoder::jni_decode_video_buffer_callback->output_file_path = output_file_path_;

    // 销毁局部引用
    env->DeleteLocalRef(clazz_decode_buffer_listener);
    clazz_decode_buffer_listener = nullptr;

    const char *input_file_path = env->GetStringUTFChars(
            Decoder::jni_decode_video_buffer_callback->input_file_path, nullptr);
    const char *output_file_path = env->GetStringUTFChars(
            Decoder::jni_decode_video_buffer_callback->output_file_path, nullptr);

    LOGI("nativeDecodeVideo -> %s %s %ld", input_file_path, output_file_path,
         (long) decoder_ptr);
    /*env->ReleaseStringUTFChars(input_file_path_, input_file_path);
    env->ReleaseStringUTFChars(output_file_path_, output_file_path);*/

    /*int length = 1280 * 720 * sizeof(uint8_t) * 3;
    auto *pDecodedBuffer = (uint8_t *) malloc(1280 * 720 * sizeof(uint8_t) * 3);
    jbyteArray data = env->NewByteArray(length);
    env->SetByteArrayRegion(data, 0, length, (jbyte *) pDecodedBuffer);*/
    auto *decoder = reinterpret_cast<Decoder *>((long) decoder_ptr);
    decoder->decodeVideo(input_file_path, output_file_path);

    // 调用 DecodeBufferListener 的 onBufferListener 函数，完成调用流程.
    /*env->CallVoidMethod(Decoder::jni_decode_video_buffer_callback->decode_buffer_listener,
                        Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_method_id,
                        nullptr);*/

    // 销毁全局引用 --- 如果有多处或多次回调，自行判断销毁时机
    //env->DeleteLocalRef(data);
    env->ReleaseStringUTFChars(Decoder::jni_decode_video_buffer_callback->input_file_path,
                               input_file_path);
    env->ReleaseStringUTFChars(Decoder::jni_decode_video_buffer_callback->output_file_path,
                               output_file_path);
    env->DeleteGlobalRef(Decoder::jni_decode_video_buffer_callback->decode_buffer_listener);
    env->DeleteGlobalRef(Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_clazz);
    Decoder::jni_decode_video_buffer_callback->input_file_path = nullptr;
    Decoder::jni_decode_video_buffer_callback->output_file_path = nullptr;
    Decoder::jni_decode_video_buffer_callback->decode_buffer_listener = nullptr;
    Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_clazz = nullptr;
    Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_method_id = nullptr;
    Decoder::jni_decode_video_buffer_callback->jvm = nullptr;
    Decoder::jni_decode_video_buffer_callback->env = nullptr;
    free(Decoder::jni_decode_video_buffer_callback);
    // 这里创建子线程异步回调
    // JNIEnv指针只在它所在的线程中有效，不能跨线程传递和使用。不同线程调用一个本地方法时，传入的JNIEnv指针是不同的.
    // 比如，在jni的方法中起了线程去处理事件，处理完后希望能通知java层，线程中是不能使用参数JNIEnv的。
    // 解决的方法可以通过JavaVM 的AttachCurrentThread方法去获取当前线程的JNIEnv
    /*pthread_t decode_h264_file_tid;
    pthread_create(&decode_h264_file_tid, nullptr, async_thread_decode_h264_file,reinterpret_cast<void *>((long) decoder_ptr));*/
    return 0;
}
jint
nativeDecodeAudio(JNIEnv *env, jclass clazz, jlong decoder_ptr, jstring input_file_path_,
                  jstring output_file_path_,
                  jobject decode_buffer_listener) {
    if (!decode_buffer_listener)
        return -1;
    // 获得回调接口FFmpegMobile$DecodeBufferListener的onBufferListener的methodID.
    jclass clazz_decode_buffer_listener = env->GetObjectClass(decode_buffer_listener);
    jmethodID decode_buffer_listener_method_id = env->GetMethodID(clazz_decode_buffer_listener,
                                                                  "onBufferListener",
                                                                  "(II[B)V");
    // 这里创建Decoder::jni_decode_buffer_callback_t的实例，创建DecodeBufferListener的全局引用并赋值.
    Decoder::jni_decode_audio_buffer_callback = (jni_decode_buffer_callback_t *) malloc(
            sizeof(jni_decode_buffer_callback_t));
    memset(Decoder::jni_decode_audio_buffer_callback, 0, sizeof(jni_decode_buffer_callback_t));
    Decoder::jni_decode_audio_buffer_callback->jvm = globalJavaVM;
    Decoder::jni_decode_audio_buffer_callback->env = env;
    // jobject 也是不能跨线程调用的。解决的方法是通过全局引用来获取，env->NewGlobalRef(obj)
    Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener = env->NewGlobalRef(
            decode_buffer_listener);
    Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_clazz = (jclass) env->NewGlobalRef(
            clazz_decode_buffer_listener);
    Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_method_id = decode_buffer_listener_method_id;
    Decoder::jni_decode_audio_buffer_callback->input_file_path = input_file_path_;
    Decoder::jni_decode_audio_buffer_callback->output_file_path = output_file_path_;

    // 销毁局部引用
    env->DeleteLocalRef(clazz_decode_buffer_listener);
    clazz_decode_buffer_listener = nullptr;

    const char *input_file_path = env->GetStringUTFChars(
            Decoder::jni_decode_audio_buffer_callback->input_file_path, nullptr);
    const char *output_file_path = env->GetStringUTFChars(
            Decoder::jni_decode_audio_buffer_callback->output_file_path, nullptr);

    LOGI("nativeDecodeAudio -> %s %s %ld", input_file_path, output_file_path,
         (long) decoder_ptr);

    auto *decoder = reinterpret_cast<Decoder *>((long) decoder_ptr);
    decoder->decodeAudio(input_file_path, output_file_path);

    // 销毁全局引用 --- 如果有多处或多次回调，自行判断销毁时机
    //env->DeleteLocalRef(data);
    env->ReleaseStringUTFChars(Decoder::jni_decode_audio_buffer_callback->input_file_path,
                               input_file_path);
    env->ReleaseStringUTFChars(Decoder::jni_decode_audio_buffer_callback->output_file_path,
                               output_file_path);
    env->DeleteGlobalRef(Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener);
    env->DeleteGlobalRef(Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_clazz);
    Decoder::jni_decode_audio_buffer_callback->input_file_path = nullptr;
    Decoder::jni_decode_audio_buffer_callback->output_file_path = nullptr;
    Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener = nullptr;
    Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_clazz = nullptr;
    Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_method_id = nullptr;
    Decoder::jni_decode_audio_buffer_callback->jvm = nullptr;
    Decoder::jni_decode_audio_buffer_callback->env = nullptr;
    free(Decoder::jni_decode_audio_buffer_callback);
    // 这里创建子线程异步回调
    // JNIEnv指针只在它所在的线程中有效，不能跨线程传递和使用。不同线程调用一个本地方法时，传入的JNIEnv指针是不同的.
    // 比如，在jni的方法中起了线程去处理事件，处理完后希望能通知java层，线程中是不能使用参数JNIEnv的。
    // 解决的方法可以通过JavaVM 的AttachCurrentThread方法去获取当前线程的JNIEnv
    /*pthread_t decode_h264_file_tid;
    pthread_create(&decode_h264_file_tid, nullptr, async_thread_decode_h264_file,reinterpret_cast<void *>((long) decoder_ptr));*/
    return 0;
}


jint
nativeGetMetadata(JNIEnv *env, jclass clazz, jstring input_file_path_) {

    const char *input_file_path = env->GetStringUTFChars(input_file_path_, nullptr);
    LOGI("nativeGetMetadata: input_file_path -> %s ", input_file_path);
    AVFormatContext *fmt_ctx = nullptr;
    const AVDictionaryEntry *tag = nullptr;
    int ret;
    if ((ret = avformat_open_input(&fmt_ctx, input_file_path, nullptr, nullptr))) {
        LOGE("nativeGetMetadata: avformat_open_input ret %d \n", ret);
        return ret;
    }

    if ((ret = avformat_find_stream_info(fmt_ctx, nullptr)) < 0) {
        LOGE("nativeGetMetadata: Cannot find stream information\n");
        return ret;
    }
    while ((tag = av_dict_get(fmt_ctx->metadata, "", tag, AV_DICT_IGNORE_SUFFIX)))
        LOGI("nativeGetMetadata: %s=%s\n", tag->key, tag->value);
    LOGE("nativeGetMetadata: avformat_close_input ");
    avformat_close_input(&fmt_ctx);
    env->ReleaseStringUTFChars(input_file_path_, input_file_path);
    return ret;
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
        {"nativeGetAvcodecVersion", "()Ljava/lang/String;",                                                                              (void *) nativeGetAvcodecVersion},
        {"nativeDecodeVideo",       "(JLjava/lang/String;Ljava/lang/String;Lcom/nxg/ffmpeg_mobile/FFmpegMobile$DecodeBufferListener;)I", (void *) nativeDecodeVideo},
        {"nativeDecodeAudio",       "(JLjava/lang/String;Ljava/lang/String;Lcom/nxg/ffmpeg_mobile/FFmpegMobile$DecodeBufferListener;)I", (void *) nativeDecodeAudio},
        {"nativePlayAudio",       "(Ljava/lang/String;)I",                                                                             (void *) nativePlayAudio},
        {"nativeGetMetadata",       "(Ljava/lang/String;)I",                                                                             (void *) nativeGetMetadata},
        {"nativeInit",              "()J",                                                                                               (void *) nativeInit},
        {"nativeRelease",           "(J)I",                                                                                              (void *) nativeRelease},
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
    globalJavaVM = vm;
    JNIEnv *env = nullptr;
    //获取JNIEnv
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    //断言
    assert(env != nullptr);
    //指定类的路径，通过FindClass 方法来找到对应的类
    const char *className = "com/nxg/ffmpeg_mobile/FFmpegMobile";

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
    //返回jni的版本
    return JNI_VERSION_1_6;
}
