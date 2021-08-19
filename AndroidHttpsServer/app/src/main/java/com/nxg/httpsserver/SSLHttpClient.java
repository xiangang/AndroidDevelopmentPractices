package com.nxg.httpsserver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.koushikdutta.async.http.AsyncHttpClient;

import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

/**
 * 和SSLHttpServer配套的客户端
 */
public class SSLHttpClient {

    private static final String TAG = "HttpServer";

    private static final String X509 = "X509";
    private static final String PASSWORD = "123456";

    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    @SuppressLint("StaticFieldLeak")
    private static SSLHttpClient mInstance;

    private AsyncHttpClient asyncHttpClient;

    public static SSLHttpClient getInstance() {
        if (mInstance == null) {
            synchronized (SSLHttpClient.class) {
                if (mInstance == null) {
                    mInstance = new SSLHttpClient();
                }
            }
        }
        return mInstance;
    }

    private SSLHttpClient() {
        try {
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(X509);
            KeyStore ks = KeyStore.getInstance("BKS");

            ks.load(mContext.getResources().openRawResource(R.raw.server), PASSWORD.toCharArray());
            kmf.init(ks, PASSWORD.toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore ts = KeyStore.getInstance(KeyStore.getDefaultType());
            ts.load(mContext.getResources().openRawResource(R.raw.server), PASSWORD.toCharArray());
            tmf.init(ts);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            if (asyncHttpClient == null) {
                asyncHttpClient = AsyncHttpClient.getDefaultInstance();
            }
            AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setSSLContext(sslContext);
            AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setTrustManagers(tmf.getTrustManagers());
            AsyncHttpClient.getDefaultInstance().getSSLSocketMiddleware().setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    Log.i(TAG, "verify: " + hostname);
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AsyncHttpClient getAsyncHttpClient() {
        return asyncHttpClient;
    }
}
