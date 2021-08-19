package com.nxg.httpsserver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpGet;
import com.koushikdutta.async.http.Multimap;
import com.koushikdutta.async.http.body.AsyncHttpRequestBody;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.nxg.httpsserver.api.ApiCodeMsg;
import com.nxg.httpsserver.api.ApiResult;

import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * 轻量的Http服务器
 */
public class SSLHttpServer implements HttpServerRequestCallback {

    private static final String TAG = "SSLHttpServer";
    private static final String X509 = "X509";
    private static final String PASSWORD = "123456";
    public static int DEFAULT_PORT = 8888;
    public static final String GET_HOST_SOFTWARE_VERSION = "/getHostSoftwareVersion";
    public static String URL_GET_HOST_SOFTWARE_VERSION = "https://%s:" + DEFAULT_PORT + GET_HOST_SOFTWARE_VERSION;

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;


    public static void setContext(Context context) {
        SSLHttpServer.mContext = context;
    }

    @SuppressLint("StaticFieldLeak")
    private static SSLHttpServer mInstance;

    private AsyncHttpServer asyncHttpServer;

    public static SSLHttpServer getInstance() {
        if (mInstance == null) {
            synchronized (SSLHttpServer.class) {
                if (mInstance == null) {
                    mInstance = new SSLHttpServer();
                }
            }
        }
        return mInstance;
    }

    private SSLContext sslContext = null;

    private SSLHttpServer() {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(X509);
            KeyStore ks = KeyStore.getInstance("BKS");

            ks.load(mContext.getResources().openRawResource(R.raw.server), PASSWORD.toCharArray());
            kmf.init(ks, PASSWORD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            ts.load(mContext.getResources().openRawResource(R.raw.server), PASSWORD.toCharArray());
            tmf.init(ts);

            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (asyncHttpServer == null) {
            asyncHttpServer = new AsyncHttpServer();
        }
    }

    /**
     * 开启本地服务
     */
    public void startServer() {
        Log.i(TAG, "startServer: ");
        asyncHttpServer.addAction("OPTIONS", "[\\d\\D]*", this);
        asyncHttpServer.get("[\\d\\D]*", this);
        asyncHttpServer.post("[\\d\\D]*", this);
        asyncHttpServer.listenSecure(DEFAULT_PORT, sslContext);

    }

    @Override
    public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

        Log.d(TAG, "onRequest: uri = " + request.getPath());

        String uri = request.getPath();

        //这个是获取header参数的地方，一定要谨记哦
        Multimap headers = request.getHeaders().getMultiMap();

        if (headers != null) {
            Log.d(TAG, "onRequest: headers = " + headers.toString());
        }

        //注意：这个地方是获取post请求的参数的地方，一定要谨记哦
        Multimap multimap = ((AsyncHttpRequestBody<Multimap>) request.getBody()).get();

        if (multimap != null) {
            Log.d(TAG, "onRequest: multimap = " + multimap.toString());
        }

        //GET/POST等请求方式
        String method = request.getMethod();
        Log.i(TAG, "onRequest: method = " + method);
        //query 是GET请求方式的参数
        String query = request.getQuery().toString();
        Log.i(TAG, "onRequest: query = " + query);

        //目前主要使用GET请求
        if (TextUtils.equals(method, AsyncHttpGet.METHOD)) {
            Log.i(TAG, "onRequest: request:" + uri);
            response.send(newApiResult(uri, request.getQuery()));

        } else {

            response.send(newApiResult(uri, multimap));
        }
    }

    /**
     * 返回ApiResult的Json格式字符串
     *
     * @param uri      接口
     * @param multimap 参数
     * @return String
     */
    private static String newApiResult(String uri, Multimap multimap) {
        switch (uri) {
            case "/test":
                return GsonUtils.getInstance().toJson(ApiResult.success("Test is ok!"));
            case "/getHostSoftwareVersion":
                //获取软件版本信息
                if (mContext == null) {
                    return GsonUtils.getInstance().toJson(ApiResult.fail(ApiCodeMsg.REQUEST_ERROR_CONTEXT_IS_NULL));
                }
                PackageInfo packageInfo = null;
                try {
                    packageInfo = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String localVersion = null;
                if (packageInfo != null) localVersion = packageInfo.versionName;
                return GsonUtils.getInstance().toJson(ApiResult.success(localVersion));
            default:
                return GsonUtils.getInstance().toJson(ApiResult.fail(ApiCodeMsg.REQUEST_ERROR_404));

        }

    }
}
