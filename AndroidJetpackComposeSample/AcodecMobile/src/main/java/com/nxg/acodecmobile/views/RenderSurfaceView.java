package com.nxg.acodecmobile.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.util.Pools;

import com.nxg.acodecmobile.AvCodecMobile;

import java.util.concurrent.LinkedBlockingDeque;

public class RenderSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final LinkedBlockingDeque<YuvData> dataDeque = new LinkedBlockingDeque<>();
    private RenderThread renderThread;
    private static final Pools.Pool<YuvData> sPool = new Pools.SimplePool<>(10);

    public RenderSurfaceView(Context context) {
        super(context);
        init();
    }

    public RenderSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RenderSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RenderSurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        //设置callback
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //启动渲染线程
        renderThread = new RenderThread(dataDeque, surfaceHolder);
        renderThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        //退出线程
        renderThread.interrupt();
    }


    /**
     * YUV数据
     */
    public static class YuvData {
        int width;
        int height;
        byte[] data;

        public YuvData(int width, int height, byte[] data) {
            this.width = width;
            this.height = height;
            this.data = data;
        }
    }

    /**
     * 调用Native方法渲染YUV数据到Surface的线程
     */
    static class RenderThread extends Thread {

        LinkedBlockingDeque<YuvData> dataDeque;
        SurfaceHolder surfaceHolder;

        public RenderThread(LinkedBlockingDeque<YuvData> dataDeque, SurfaceHolder surfaceHolder) {
            this.dataDeque = dataDeque;
            this.surfaceHolder = surfaceHolder;
        }

        @Override
        public void run() {
            super.run();
            while (!interrupted()) {
                try {
                    YuvData yuvData = dataDeque.take();
                    if (yuvData != null) {
                        AvCodecMobile.renderYuvDataOnSurface(
                                yuvData.width,
                                yuvData.height,
                                yuvData.data,
                                surfaceHolder.getSurface()
                        );
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 喂Yuv数据
     *
     * @param yuvData YuvData
     */
    public void onYuvData(YuvData yuvData) {
        try {
            dataDeque.put(yuvData);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}