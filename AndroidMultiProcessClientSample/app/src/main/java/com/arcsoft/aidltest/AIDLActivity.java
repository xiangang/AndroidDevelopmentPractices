package com.arcsoft.aidltest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.arcsoft.arcfacedemo.IArcFace;
import com.arcsoft.arcfacedemo.IArcFaceCallback;

public class AIDLActivity extends AppCompatActivity {

    private static final String TAG = "AIDLActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aidl);
        bindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消AIDL服务绑定
        unbindService(serviceConnection);
        if (iArcFace != null && callback.asBinder().isBinderAlive()) {
            try {
                // 取消注册
                iArcFace.unregisterIArcFaceCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private IArcFace iArcFace;

    private IArcFaceCallback callback = new IArcFaceCallback.Stub() {
        @Override
        public void onFaceInfoGet(int code, String msg, String data) throws RemoteException {
            LoggerUtil.d(TAG, "onFaceInfoGet: code " + code );
            LoggerUtil.d(TAG, "onFaceInfoGet: msg " + msg );
            LoggerUtil.d(TAG, "onFaceInfoGet: data " + data );
        }
    };


    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LoggerUtil.d(TAG, "onServiceConnected");
            iArcFace = IArcFace.Stub.asInterface(service);
            try {
                iArcFace.registerIArcFaceCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            try {
                //注册死亡回调
                service.linkToDeath(mDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoggerUtil.d(TAG, "btnRegister");
                    if(iArcFace != null){
                        try {
                            iArcFace.registerFace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            findViewById(R.id.btnRecognize).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoggerUtil.d(TAG, "btnRecognize");
                    if(iArcFace != null){
                        try {
                            iArcFace.recognizeFace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LoggerUtil.d(TAG, "onServiceDisconnected");

        }
    };


    private void bindService() {
        //确定包名，绑定AIDL服务，包名不可变
        Intent bindIntent = new Intent();
        bindIntent.setComponent(new ComponentName("com.arcsoft.arcfacedemo", "com.arcsoft.arcfacedemo.ARCFaceService"));
        bindService(bindIntent, serviceConnection, BIND_AUTO_CREATE);
    }

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            LoggerUtil.i(TAG, "binderDied: ");
            if (iArcFace == null) {
                return;
            }
            iArcFace.asBinder().unlinkToDeath(mDeathRecipient, 0);
            iArcFace = null;
            //Binder死亡，重新绑定服务
            bindService();
        }
    };


}
