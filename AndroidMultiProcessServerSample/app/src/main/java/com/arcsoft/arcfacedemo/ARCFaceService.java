package com.arcsoft.arcfacedemo;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity;
import com.arcsoft.arcfacedemo.util.LoggerUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class ARCFaceService extends Service {

    private static final String TAG = "ARCFaceService";

    // 系统提供的专门用于保存、删除跨进程 listener 的类
    private RemoteCallbackList<IArcFaceCallback> mListenerList = new RemoteCallbackList<>();

    public ARCFaceService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        LoggerUtil.d(TAG, "onCreate: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        LoggerUtil.d(TAG, "onDestroy: ");
    }

    private Binder mBinder = new IArcFace.Stub() {
        @Override
        public void registerFace() throws RemoteException {
            LoggerUtil.d(TAG, "registerFace: ");
            /*Intent intent = new Intent(ARCFaceService.this, RegisterAndRecognizeActivity.class);
            ComponentName componentName = new ComponentName("com.arcsoft.arcfacedemo", "com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity");//这里是 包名  以及 页面类的全称
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ARCFaceService.this.startActivity(intent);*/
            EventBus.getDefault().post(new MessageEvent.ArcFaceEvent(MessageEvent.ArcFaceEvent.ACTIVITY_START,"start activity",""));
        }

        @Override
        public void recognizeFace() throws RemoteException {
            LoggerUtil.d(TAG, "recognizeFace: ");
            Intent intent = new Intent(ARCFaceService.this, RegisterAndRecognizeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        @Override
        public void finish() throws RemoteException {
            LoggerUtil.d(TAG, "finish: ");
            EventBus.getDefault().post(new MessageEvent.ArcFaceEvent(MessageEvent.ArcFaceEvent.ACTIVITY_FINISH,"finish activity",""));
        }

        @Override
        public void registerIArcFaceCallback(IArcFaceCallback callback) throws RemoteException {
            mListenerList.register(callback);
        }

        @Override
        public void unregisterIArcFaceCallback(IArcFaceCallback callback) throws RemoteException {
            mListenerList.unregister(callback);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onArcFaceEvent(MessageEvent.ArcFaceEvent event) {
        LoggerUtil.d(TAG, "onArcFaceEvent: code " + event.code );
        LoggerUtil.d(TAG, "onArcFaceEvent: msg " + event.msg );
        LoggerUtil.d(TAG, "onArcFaceEvent: data " + event.data );
        int code = event.code;
        switch (code){
            case MessageEvent.ArcFaceEvent.REGISTER_STATUS_FAILED:
            case MessageEvent.ArcFaceEvent.REGISTER_STATUS_SUCCESS:
            case MessageEvent.ArcFaceEvent.RECOGNIZE_STATUS_SUCCESS:
            case MessageEvent.ArcFaceEvent.RECOGNIZE_STATUS_FAILED:
                onFaceInfoGet(event);
            case MessageEvent.ArcFaceEvent.ACTIVITY_START:
                Intent intent = new Intent(ARCFaceService.this, RegisterAndRecognizeActivity.class);
                /*ComponentName componentName = new ComponentName("com.arcsoft.arcfacedemo", "com.arcsoft.arcfacedemo.activity.RegisterAndRecognizeActivity");//这里是 包名  以及 页面类的全称
                intent.setComponent(componentName);*/
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }
    private MessageEvent.ArcFaceEvent unknownMessageEven = new MessageEvent.ArcFaceEvent(MessageEvent.ArcFaceEvent.UNKNOWN_ERROR,"unknown error!","");
    private void onFaceInfoGet(MessageEvent.ArcFaceEvent event){
        if(event == null){
            event = unknownMessageEven;
        }
        int n = mListenerList.beginBroadcast();
        for (int i = 0; i < n; i++) {
            IArcFaceCallback listener = mListenerList.getBroadcastItem(i);
            if (listener != null) {
                try {
                    listener.onFaceInfoGet(event.code,event.msg,event.data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        mListenerList.finishBroadcast();
    }
}