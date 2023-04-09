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

/**
 * @file
 * video decoding with libavcodec API example
 *
 * @example decode_video.c
 */
#include "decode_av.h"

using namespace std;

static void pgm_save(unsigned char *buf, int wrap, int xsize, int ysize,
                     char *filename) {
    FILE *f;
    int i;

    f = fopen(filename, "wb");
    fprintf(f, "P5\n%d %d\n%d\n", xsize, ysize, 255);
    for (i = 0; i < ysize; i++)
        fwrite(buf + i * wrap, 1, xsize, f);
    fclose(f);
}

/**
 *这我自己添加上去的，主要解析planar模式的yuv
 *保存的是planner模式的yuv数据，这里直接保存yuv数据裸流
 *使用ffplay播放解码的yuv：ffplay -f rawvideo -video_size 宽x高 -pixel_format 数据格式（如yuv420p） 文件名
 */
static void pgm_planner_save(AVFrame *frame, int xsize, int ysize, char *filename) {
    FILE *f;
    int i;

    f = fopen(filename, "wb");
    // fprintf(f, "P6\n%d %d\n%d\n", xsize, ysize, 255);
    //按行存储
    for (i = 0; i < ysize; i++)
        fwrite(frame->data[0] + i * frame->linesize[0], 1, xsize, f);

    for (i = 0; i < ysize / 2; i++)
        fwrite(frame->data[1] + i * frame->linesize[1], 1, xsize / 2, f);

    for (i = 0; i < ysize / 2; i++)
        fwrite(frame->data[2] + i * frame->linesize[2], 1, xsize / 2, f);

    fflush(f);
    fclose(f);

}

/**
 * 回调planar模式的yuv数据
 */
static void AVFrame2Yuv420p(AVFrame *pFrame) {
    int frameWidth = pFrame->width;
    int frameHeight = pFrame->height;
    int channels = 3;
    //反转图像
    /*pFrame->data[0] += pFrame->linesize[0] * (frameHeight - 1);
    pFrame->linesize[0] *= -1;
    pFrame->data[1] += pFrame->linesize[1] * (frameHeight / 2 - 1);
    pFrame->linesize[1] *= -1;
    pFrame->data[2] += pFrame->linesize[2] * (frameHeight / 2 - 1);
    pFrame->linesize[2] *= -1;*/

    //创建保存yuv数据的buffer
    int length = frameWidth * frameHeight * (int) sizeof(uint8_t) * channels;
    auto *pDecodedBuffer = (uint8_t *) malloc(length);
    //LOGI("decodeVideo: decoded buffer size is %u \n", length);
    //从AVFrame中获取yuv420p数据，并保存到buffer
    int i, j, k;
    //拷贝y分量
    for (i = 0; i < frameHeight; i++) {
        memcpy(pDecodedBuffer + frameWidth * i,
               pFrame->data[0] + pFrame->linesize[0] * i,
               frameWidth);
    }
    //拷贝u分量
    for (j = 0; j < frameHeight / 2; j++) {
        memcpy(pDecodedBuffer + frameWidth * i + frameWidth / 2 * j,
               pFrame->data[1] + pFrame->linesize[1] * j,
               frameWidth / 2);
    }
    //拷贝v分量
    for (k = 0; k < frameHeight / 2; k++) {
        memcpy(pDecodedBuffer + frameWidth * i + frameWidth / 2 * j + frameWidth / 2 * k,
               pFrame->data[2] + pFrame->linesize[2] * k,
               frameWidth / 2);
    }
    //回调给Java上层
    if (pDecodedBuffer != nullptr && Decoder::jni_decode_video_buffer_callback != nullptr) {
        jbyteArray data = Decoder::jni_decode_video_buffer_callback->env->NewByteArray(length);
        Decoder::jni_decode_video_buffer_callback->env->SetByteArrayRegion(data, 0, length,
                                                                           reinterpret_cast<const jbyte *>(pDecodedBuffer));
        Decoder::jni_decode_video_buffer_callback->env->CallVoidMethod(
                Decoder::jni_decode_video_buffer_callback->decode_buffer_listener,
                Decoder::jni_decode_video_buffer_callback->decode_buffer_listener_method_id,
                frameWidth, frameHeight, data);
        Decoder::jni_decode_video_buffer_callback->env->DeleteLocalRef(data);
    }
    //释放buffer内存空间，否则会内存溢出
    free(pDecodedBuffer);
}

static void decode_video(AVCodecContext *dec_ctx, AVFrame *frame, AVPacket *pkt,
                         const char *filename) {
    char buf[1024];
    int ret;

    ret = avcodec_send_packet(dec_ctx, pkt);
    if (ret < 0) {
        LOGE("decode_video: Error sending a packet for decoding\n");
        return;
    }

    while (ret >= 0) {
        ret = avcodec_receive_frame(dec_ctx, frame);
        if (ret == AVERROR(EAGAIN)) {
            //LOGE("decode_video: Error during again\n");
            return;
        } else if (ret == AVERROR_EOF) {
            LOGE("decode_video: Error during eof\n");
            return;
        } else if (ret < 0) {
            LOGE("decode_video: Error during decoding\n");
            return;
        }
        //LOGI("decode_video: saving frame %3d\n", dec_ctx->frame_number);
        /* the picture is allocated by the decoder. no need to free it */
        snprintf(buf, sizeof(buf), "decode_video: %s-%d", filename, dec_ctx->frame_number);
        //pgm_save(frame->data[0], frame->linesize[0], frame->width, frame->height, buf);
        //休眠指定时间（40=1000/25）
        std::this_thread::sleep_for(std::chrono::milliseconds(40));
        AVFrame2Yuv420p(frame);
    }
}


int Decoder::decodeVideo(const char *inputFileName, const char *outputFileName) {
    LOGI("decodeVideo: input file is %s, output file is %s ", inputFileName, outputFileName);
    const AVCodec *codec;
    AVCodecParserContext *parser;
    AVCodecContext *avctx = nullptr;
    FILE *f;
    AVFrame *frame;
    uint8_t inbuf[INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
    uint8_t *data;
    size_t data_size;
    int ret;
    int eof;
    AVPacket *pkt;
    pkt = av_packet_alloc();
    if (!pkt) {
        LOGE("decodeVideo: can not alloc av packet!");
        return -1;
    }

    /* set end of buffer to 0 (this ensures that no overreading happens for damaged MPEG streams) */
    memset(inbuf + INBUF_SIZE, 0, AV_INPUT_BUFFER_PADDING_SIZE);

    /* find the MPEG-1 video decoder */
    // 查找解码器
    codec = avcodec_find_decoder(AV_CODEC_ID_H264);
    if (!codec) {
        LOGE("decodeVideo: Codec not found!\n");
        return -2;
    }
    // 获取裸流的解析器 AVCodecParserContext(数据)  +  AVCodecParser(方法)
    parser = av_parser_init(codec->id);
    if (!parser) {
        LOGE("decodeVideo: parser not found\n");
        return -3;
    }

    // 分配codec上下文
    avctx = avcodec_alloc_context3(codec);
    if (!avctx) {
        LOGE("decodeVideo: Could not allocate video codec context\n");
        return -4;
    }

    /* For some codecs, such as msmpeg4 and mpeg4, width and height
      MUST be initialized there because this information is not
      available in the bitstream. */

    /* open it */
    // 将解码器和解码器上下文进行关联
    if (avcodec_open2(avctx, codec, nullptr) < 0) {
        LOGE("decodeVideo: Could not open codec\n");
        return -4;
    }

    // 打开输入文件
    f = fopen(inputFileName, "rb");
    if (!f) {
        LOGE("decodeVideo: Could not open %s\n", inputFileName);
        return -5;
    }

    //分配AVFrame
    frame = av_frame_alloc();
    if (!frame) {
        LOGE("decodeVideo: Could not allocate video frame\n");
        return -6;
    }

    // 读取文件进行解码
    do {
        /* read raw data from the input file */
        // 每次读取inbuf大小的数据
        data_size = fread(inbuf, 1, INBUF_SIZE, f);
        //读取文件失败，结束
        if (ferror(f))
            break;
        //是否读取到文件尾部，data_size=-1代表，读取到尾部
        eof = !data_size;

        /* use the parser to split the data into frames */
        // data指向将读到的数据
        data = inbuf;
        //LOGI("decodeVideo: read data from input file size %d ", data_size);
        // 如果读到的数据大小大于0或者到达文件尾部
        while (data_size > 0 || eof) {
            //LOGI("read data from input file size %d ", data_size);
            // 解析数据获得一个AVPacket，从输入的数据流中分离出一帧一帧的压缩编码数据AVPacket，返回已经使用的输入流的字节数。
            // 当pkt->size为0代表没解析好，还需要再次调用av_parser_parse2()解析一部分数据才可以得到解析后的数据帧
            ret = av_parser_parse2(parser, avctx, &pkt->data, &pkt->size,
                                   data, data_size, AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
            // ret小于0解析失败，否则达标已解析的数据的大小
            if (ret < 0) {
                LOGE("Error while parsing\n");
                return -1;
            }
            // 跳过已经解析的数据
            data += ret;
            // 对应的缓存大小也做相应减小
            data_size -= ret;
            // 开始解码读取到的帧数据（pkt->size>0代表解析到一个完整的帧数据AVPacket，如果没解析到完整的帧数据，则继续解析，直到data_siz<=0）
            if (pkt->size)
                decode_video(avctx, frame, pkt, outputFileName);
                // 如果到达文件尾部，结束
            else if (eof)
                break;
        }
    } while (!eof);

    /* flush the decoder */
    decode_video(avctx, frame, nullptr, outputFileName);

    fclose(f);

    av_parser_close(parser);
    avcodec_free_context(&avctx);
    av_frame_free(&frame);
    av_packet_free(&pkt);
    return 0;
}

static int get_format_from_sample_fmt(const char **fmt,
                                      enum AVSampleFormat sample_fmt) {
    int i;
    struct sample_fmt_entry {
        enum AVSampleFormat sample_fmt;
        const char *fmt_be, *fmt_le;
    } sample_fmt_entries[] = {
            {AV_SAMPLE_FMT_U8,  "u8",    "u8"},
            {AV_SAMPLE_FMT_S16, "s16be", "s16le"},
            {AV_SAMPLE_FMT_S32, "s32be", "s32le"},
            {AV_SAMPLE_FMT_FLT, "f32be", "f32le"},
            {AV_SAMPLE_FMT_DBL, "f64be", "f64le"},
    };
    *fmt = nullptr;

    for (i = 0; i < FF_ARRAY_ELEMS(sample_fmt_entries); i++) {
        struct sample_fmt_entry *entry = &sample_fmt_entries[i];
        if (sample_fmt == entry->sample_fmt) {
            *fmt = AV_NE(entry->fmt_be, entry->fmt_le);
            return 0;
        }
    }

    fprintf(stderr,
            "sample format %s is not supported as output format\n",
            av_get_sample_fmt_name(sample_fmt));
    return -1;
}

static void decoder_audio(AVCodecContext *dec_ctx, AVPacket *pkt, AVFrame *frame,
                          FILE *outfile) {
    int i, ch;
    int ret, data_size;

    /* send the packet with the compressed data to the decoder */
    ret = avcodec_send_packet(dec_ctx, pkt);
    if (ret == AVERROR(EAGAIN)) {
        LOGI("decoder_audio: Receive_frame and send_packet both returned EAGAIN, which is an API violation.\n");
    } else if (ret < 0) {
        LOGE("decoder_audio: Error submitting the packet to the decoder\n");
        return;
    }

    /* read all the output frames (in general there may be any number of them */
    while (ret >= 0) {
        // 对于frame, avcodec_receive_frame内部每次都先调用
        ret = avcodec_receive_frame(dec_ctx, frame);
        if (ret == AVERROR(EAGAIN)) {
            LOGE("decoder_audio: Error during again\n");
            return;
        } else if (ret == AVERROR_EOF) {
            LOGE("decoder_audio: Error during eof\n");
            return;
        } else if (ret < 0) {
            LOGE("decoder_audio: Error during decoding\n");
            return;
        }
        data_size = av_get_bytes_per_sample(dec_ctx->sample_fmt);
        if (data_size < 0) {
            /* This should not occur, checking just for paranoia */
            LOGE("decoder_audio: Failed to calculate data size\n");
            return;
        }
        LOGI("decodeAudio -> data_size %d", data_size);
        /**
         * P表示Planar（平面），其数据格式排列方式为 :
         * LLLLLLRRRRRRLLLLLLRRRRRRLLLLLLRRRRRRL...（每个LLLLLLRRRRRR为一个音频帧）
         * 而不带P的数据格式（即交错排列）排列方式为：
         * LRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRLRL...（每个LR为一个音频样本）
         * 播放范例：ffplay -ar 48000 -ac 2 -f f32le believe.pcm
         */
        for (i = 0; i < frame->nb_samples; i++) {
            for (ch = 0; ch < dec_ctx->channels; ch++) {
                //fwrite(frame->data[ch] + data_size * i, 1, data_size, outfile);
                // 回调音频数据给Java上层
                /*if (Decoder::jni_decode_audio_buffer_callback != nullptr) {
                    jbyteArray data = Decoder::jni_decode_audio_buffer_callback->env->NewByteArray(
                            data_size);
                    const auto *buf = reinterpret_cast<const jbyte *>((frame->data[ch] +
                                                                       data_size * i));
                    Decoder::jni_decode_audio_buffer_callback->env->SetByteArrayRegion(data, 0,
                                                                                       data_size,
                                                                                       buf);
                    Decoder::jni_decode_audio_buffer_callback->env->CallVoidMethod(
                            Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener,
                            Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_method_id,
                            0, 0, data);
                    Decoder::jni_decode_audio_buffer_callback->env->DeleteLocalRef(data);
                }*/
            }
        }
    }
}

/**
 * 解码音频
 * @param inputFileName
 * @return
 */
int Decoder::decodeAudio(const char *inputFileName, const char *outputFileName) {
    LOGI("decodeAudio -> %s %s", inputFileName, outputFileName);
    const AVCodec *codec;
    AVCodecContext *av_codec_ctx = nullptr;
    AVCodecParserContext *av_codec_parse_ctx = nullptr;
    int len, ret;
    FILE *f, *outfile;
    uint8_t inbuf[AUDIO_INBUF_SIZE + AV_INPUT_BUFFER_PADDING_SIZE];
    uint8_t *data;
    size_t data_size;
    AVPacket *pkt;
    AVFrame *decoded_frame = nullptr;
    enum AVSampleFormat sfmt;
    int n_channels = 0;
    const char *fmt;

    pkt = av_packet_alloc();

    //申请avFormatContext空间，记得要释放
    AVFormatContext *pFormatContext = avformat_alloc_context();

    //打开媒体文件
    ret = avformat_open_input(&pFormatContext, inputFileName, nullptr, nullptr);
    if (ret != 0) {
        LOGE("avformat_open_input: error url=%s, result=%d", inputFileName, ret);
        return ret;
    }
    //读取媒体文件信息，给avFormatContext赋值
    ret = avformat_find_stream_info(pFormatContext, nullptr);
    if (ret < 0) {
        LOGE("avformat_find_stream_info: result=%d", ret);
        return ret;
    }

    //匹配到音频流的index
    int audioIndex = -1;
    for (int i = 0; i < pFormatContext->nb_streams; ++i) {
        AVMediaType codecType = pFormatContext->streams[i]->codecpar->codec_type;
        if (AVMEDIA_TYPE_AUDIO == codecType) {
            audioIndex = i;
            break;
        }
    }
    if (audioIndex == -1) {
        LOGE("not find a audio stream");
        return -1;
    }

    //寻找视频流索引
    int audio_idx = av_find_best_stream(pFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, nullptr, 0);

    if (audio_idx == -1) {
        LOGE("获取音频流索引失败");
        return -1;
    }
    LOGE("decodeAudio: audioIndex = %d, audio_idx = %d", audioIndex, audio_idx);
    AVCodecParameters *pCodecParameters = pFormatContext->streams[audioIndex]->codecpar;

    // 根据流信息的codec_id找到对应的解码器
    codec = avcodec_find_decoder(pCodecParameters->codec_id);
    if (!codec) {
        LOGE("decodeAudio: Codec not found\n");
        return -1;
    }
    LOGE("decodeAudio: codec_id = %d", pCodecParameters->codec_id);
    // 获取裸流的解析器 AVCodecParserContext(数据)  +  AVCodecParser(方法)
    av_codec_parse_ctx = av_parser_init(codec->id);
    if (!av_codec_parse_ctx) {
        LOGE("decodeAudio: Parser not found\n");
        return -2;
    }
    // 分配codec上下文
    av_codec_ctx = avcodec_alloc_context3(codec);
    if (!av_codec_ctx) {
        LOGE("decodeAudio: Could not allocate audio codec context\n");
        return -3;
    }

    // 关联解码器上下文
    ret = avcodec_parameters_to_context(av_codec_ctx,
                                        pFormatContext->streams[audioIndex]->codecpar);
    if (ret < 0) {
        LOGE("decodeAudio: avcodec_parameters_to_context failed:%s", av_err2str(ret));
        return ret;
    }

    // 将解码器和解码器上下文进行关联
    if (avcodec_open2(av_codec_ctx, codec, nullptr) < 0) {
        LOGE("decodeAudio: Could not open codec\n");
        return -4;
    }
    //准备音频重采样的参数
    int dataSize = av_samples_get_buffer_size(nullptr,
                                              av_get_channel_layout_nb_channels(
                                                      AV_CH_LAYOUT_STEREO),
                                              av_codec_ctx->frame_size,
                                              AV_SAMPLE_FMT_S16,
                                              0);
    auto *resampleOutBuffer = (uint8_t *) malloc(dataSize);

    // 申请重采样SwrContext上下文
    SwrContext *swrCtr = swr_alloc();
    if (!swrCtr) {
        LOGE("Could not allocate resampler context\n");
        ret = AVERROR(ENOMEM);
        return ret;
    }

    swrCtr = swr_alloc_set_opts(swrCtr,
                                AV_CH_LAYOUT_STEREO,
                                AV_SAMPLE_FMT_S16,
                                44100,
                                av_codec_ctx->channels,
                                av_codec_ctx->sample_fmt,
                                av_codec_ctx->sample_rate,
                                0, 0);

    LOGE("swr_alloc_set_opts channels:%d", av_codec_ctx->channels);
    LOGE("swr_alloc_set_opts sample_fmt:%d", av_codec_ctx->sample_fmt);
    LOGE("swr_alloc_set_opts sample_rate:%d", av_codec_ctx->sample_rate);

    // 音频重采样上下文初始化
    ret = swr_init(swrCtr);
    if (ret != 0) {
        LOGE("Failed to initialize the resampling context:%s", av_err2str(ret));
        return ret;
    }

    // JNI创建AudioTrack
    JNIEnv *env = Decoder::jni_decode_audio_buffer_callback->env;
    jclass jAudioTrackClass = env->FindClass("android/media/AudioTrack");
    jmethodID jAudioTrackCMid = env->GetMethodID(jAudioTrackClass, "<init>", "(IIIIII)V"); //构造

    // public static final int STREAM_MUSIC = 3;
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

    // 打开输入文件
    f = fopen(inputFileName, "rb");
    if (!f) {
        LOGE("decodeAudio: Could not open %s\n", inputFileName);
        return -5;
    }

    // 打开输出文件
    outfile = fopen(outputFileName, "wb");
    if (!outfile) {
        av_free(av_codec_ctx);
        return -6;
    }

    /* decoder_audio until eof */
    // 读取文件进行解码
    data = inbuf;
    data_size = fread(inbuf, 1, AUDIO_INBUF_SIZE, f);

    while (data_size > 0) {
        if (!decoded_frame) {
            if (!(decoded_frame = av_frame_alloc())) {
                LOGE("decodeAudio: Could not allocate audio frame\n");
                return -7;
            }
        }

        ret = av_parser_parse2(av_codec_parse_ctx, av_codec_ctx, &pkt->data, &pkt->size,
                               data, data_size,
                               AV_NOPTS_VALUE, AV_NOPTS_VALUE, 0);
        if (ret < 0) {
            LOGE("decodeAudio: Error while parsing\n");
            return -8;
        }
        data += ret;   // 跳过已经解析的数据
        data_size -= ret;   // 对应的缓存大小也做相应减小
        // 读取到完整的数据则开始解码
        if (pkt->size) {
            //decoder_audio(av_codec_ctx, pkt, decoded_frame, outfile);

            int i, ch;
            int ret, data_size;

            /* send the packet with the compressed data to the decoder */
            ret = avcodec_send_packet(av_codec_ctx, pkt);
            if (ret == AVERROR(EAGAIN)) {
                LOGI("decoder_audio: Receive_frame and send_packet both returned EAGAIN, which is an API violation.\n");
            } else if (ret < 0) {
                LOGE("decoder_audio: Error submitting the packet to the decoder\n");
            }

            /* read all the output frames (in general there may be any number of them */
            while (ret >= 0) {
                // 对于frame, avcodec_receive_frame内部每次都先调用
                ret = avcodec_receive_frame(av_codec_ctx, decoded_frame);
                if (ret == AVERROR(EAGAIN)) {
                    LOGE("decoder_audio: Error during again\n");
                    break;
                } else if (ret == AVERROR_EOF) {
                    LOGE("decoder_audio: Error during eof\n");
                    break;
                } else if (ret < 0) {
                    LOGE("decoder_audio: Error during decoding\n");
                    break;
                }
                data_size = av_get_bytes_per_sample(av_codec_ctx->sample_fmt);
                if (data_size < 0) {
                    /* This should not occur, checking just for paranoia */
                    LOGE("decoder_audio: Failed to calculate data size\n");
                    break;
                }
                LOGI("decodeAudio -> data_size %d", data_size);
                if (ret != 0) {
                    break;
                }
                //音频重采样
                int len = swr_convert(swrCtr, &resampleOutBuffer,
                                      decoded_frame->nb_samples,
                                      (const uint8_t **) decoded_frame->data,
                                      decoded_frame->nb_samples);

                jbyteArray jPcmDataArray = env->NewByteArray(dataSize);
                // native 创建 c 数组
                jbyte *jPcmData = env->GetByteArrayElements(jPcmDataArray, NULL);

                //内存拷贝
                memcpy(jPcmData, resampleOutBuffer, dataSize);

                // 同步刷新到 jbyteArray ，并释放 C/C++ 数组
                env->ReleaseByteArrayElements(jPcmDataArray, jPcmData, 0);

                LOGE("音频解码成功%d  dataSize:%d ", len, dataSize);

                // 写入播放数据（使用native发生生成的AudioTrack播放）
                /* env->CallIntMethod(jAudioTrack, jAudioTrackWriteMid, jPcmDataArray, 0,
                                    dataSize);*/
                // 回调给Java上层使用AudioTrack播放
                env->CallVoidMethod(
                        Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener,
                        Decoder::jni_decode_audio_buffer_callback->decode_buffer_listener_method_id,
                        0, 0, jPcmDataArray);

                // 解除 jPcmDataArray 的持有，让 javaGC 回收
                env->DeleteLocalRef(jPcmDataArray);
            }
        }
        // 如果数据少了则再次读取
        if (data_size < AUDIO_REFILL_THRESH) {
            memmove(inbuf, data, data_size);// 把之前剩的数据拷贝到buffer的起始位置
            data = inbuf;
            // 读取数据 长度: AUDIO_INBUF_SIZE - data_size
            len = fread(data + data_size, 1,
                        AUDIO_INBUF_SIZE - data_size, f);
            if (len > 0)
                data_size += len;
        }
    }

    /* flush the decoder */
    /* flush解码器 */
    pkt->data = nullptr;
    pkt->size = 0;
    decoder_audio(av_codec_ctx, pkt, decoded_frame, outfile);

    /* print output pcm infomations, because there have no metadata of pcm */
    sfmt = av_codec_ctx->sample_fmt;

    if (av_sample_fmt_is_planar(sfmt)) {
        const char *packed = av_get_sample_fmt_name(sfmt);
        printf("decodeAudio: Warning: the sample format the decoder produced is planar "
               "(%s). This example will output the first channel only.\n",
               packed ? packed : "?");
        sfmt = av_get_packed_sample_fmt(sfmt);
    }

    n_channels = av_codec_ctx->channels;
    if ((ret = get_format_from_sample_fmt(&fmt, sfmt)) < 0)
        goto end;

    LOGI("decodeAudio: Play the output audio file with the command:\n"
         "ffplay -f %s -ac %d -ar %d %s\n",
         fmt, n_channels, av_codec_ctx->sample_rate,
         outputFileName);
    end:
    fclose(outfile);
    fclose(f);
    avformat_close_input(&pFormatContext);
    avcodec_free_context(&av_codec_ctx);
    av_parser_close(av_codec_parse_ctx);
    av_frame_free(&decoded_frame);
    av_packet_free(&pkt);

    return 0;
}

Decoder::~Decoder() = default;
