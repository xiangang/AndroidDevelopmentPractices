package com.nxg.httpsserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.NetworkUtils;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.AsyncHttpResponse;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private HandlerThread httpHandlerThread;
    private Handler httpHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (httpHandlerThread == null) {
            httpHandlerThread = new HandlerThread("HttpThread");
            httpHandlerThread.start();
            httpHandler = new Handler(httpHandlerThread.getLooper());
        }
        init();
        testRequestSSLHttpServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (httpHandlerThread != null) {
            httpHandlerThread.quit();
            httpHandler.removeCallbacksAndMessages(null);
            httpHandlerThread = null;
            httpHandler = null;
        }
    }

    /**
     * 初始化并且启动HttpServer
     */
    private void init() {
        Log.i(TAG, "init: ");
        SSLHttpClient.setContext(getApplicationContext());
        SSLHttpServer.setContext(getApplicationContext());
        SSLHttpServer.getInstance().startServer();
    }

    /**
     * 测试请求Https接口
     */
    private void testRequestSSLHttpServer() {
        Log.i(TAG, "testRequestSSLHttpServer: ");
        String hostIP = NetworkUtils.getIPAddress(true);
        String uri = String.format(SSLHttpServer.URL_GET_HOST_SOFTWARE_VERSION, hostIP);
        Log.i(TAG, "testRequestSSLHttpServer: uri " + uri);
        if (httpHandler != null) {
            httpHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //耗时操作
                    try {
                        SSLHttpClient.getInstance().getAsyncHttpClient().executeString(new AsyncHttpGet(uri), new AsyncHttpClient.StringCallback() {
                            @Override
                            public void onCompleted(Exception e, AsyncHttpResponse source, String result) {
                                Log.i(TAG, "onCompleted: " + result);
                            }
                        }).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, 0);
        }
    }


}