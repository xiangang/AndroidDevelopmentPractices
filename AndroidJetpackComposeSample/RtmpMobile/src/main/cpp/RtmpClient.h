#ifndef RTMP_CLIENT_H
#define RTMP_CLIENT_H

#include <string>
#include "string.h"
#include "rtmp.h"
#include "common.h"

#ifdef __cplusplus
extern "C" {
#endif
// include c header
#include "rtmp.h"
#include "rtmp_sys.h"
#include "log.h"
#include "android/log.h"
#include "time.h"
#include "stdlib.h"

#ifdef __cplusplus
}
#endif

#define BYTE uint8_t

#define RTMP_HEAD_SIZE (sizeof(RTMPPacket)+RTMP_MAX_HEADER_SIZE)
#define NAL_SLICE  1
#define NAL_SLICE_DPA  2
#define NAL_SLICE_DPB  3
#define NAL_SLICE_DPC  4
#define NAL_SLICE_IDR  5
#define NAL_SEI  6
#define NAL_SPS  7
#define NAL_PPS  8
#define NAL_AUD  9
#define NAL_FILLER  12

#define STREAM_CHANNEL_METADATA  0x03
#define STREAM_CHANNEL_VIDEO     0x04
#define STREAM_CHANNEL_AUDIO     0x05

/**
 * Rtmp类
 * Todo:缺少底层的回调
 */
class RtmpClient {

//RTMP指针
private:
    RTMP *rtmp;

public:
    /**
     * 开始推流
     * @param url 推流地址：rtmp://127.0.0.1:1935/live/livestream
     * @param w 视频流宽
     * @param h 视频流高
     * @param time_out 超时毫秒
     * @return 0成功，-1失败
     */
    virtual int init(std::string url, int w, int h, int time_out);

    /**
     * 发送sps、pps 帧
     */
    virtual int sendSpsAndPps(BYTE *sps, int spsLen, BYTE *pps, int ppsLen,
                              long timestamp);

    /**
     * 发送视频帧（包含I帧和普通帧）
     */
    virtual int sendVideoData(BYTE *data, int len, long timestamp);

    /**
     * 发送音频关键帧
     */
    virtual int sendAudioSpec(BYTE *data, int len);

    /**
     * 发送音频数据
     */
    virtual int sendAudioData(BYTE *data, int len, long timestamp);

    /**
     * 停止推流释放资源
     */
    virtual int stop() const;

    virtual ~RtmpClient();
};


#endif
