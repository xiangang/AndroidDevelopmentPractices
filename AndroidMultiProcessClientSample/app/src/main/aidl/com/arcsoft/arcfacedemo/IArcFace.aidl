// IArcFace.aidl
package com.arcsoft.arcfacedemo;

// Declare any non-default types here with import statements
import com.arcsoft.arcfacedemo.IArcFaceCallback;

interface IArcFace {
      //注册人脸
     void registerFace();
     //识别人脸
     void recognizeFace();
     //结束注册/识别
     void finish();
     //注册回调
     void registerIArcFaceCallback(IArcFaceCallback callback);
     //取消回调
     void unregisterIArcFaceCallback(IArcFaceCallback callback);
}
