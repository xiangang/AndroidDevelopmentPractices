// IArcFaceCallback.aidl
package com.arcsoft.arcfacedemo;

// Declare any non-default types here with import statements

interface IArcFaceCallback {
    /**
    * 注册或识别的回调
    * @param code  结果码
    * @param msg   描述信息
    * @param data  json数据
    */
    void onFaceInfoGet(int code, String msg, String data);
}
