package com.nxg.opencv.views

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.util.Pools
import com.nxg.opencv.OpenCVMobile
import java.util.concurrent.LinkedBlockingDeque

class RenderSurfaceView : SurfaceView, SurfaceHolder.Callback {
    private val dataDeque = LinkedBlockingDeque<YuvData>()
    private var renderThread: RenderThread? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        //设置callback
        holder.addCallback(this)
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        //启动渲染线程
        renderThread = RenderThread(dataDeque, surfaceHolder)
        renderThread!!.start()
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}
    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        //退出线程
        release()
    }

    /**
     * YUV数据
     */
    class YuvData(var width: Int, var height: Int, var data: ByteArray)

    /**
     * 调用Native方法渲染YUV数据到Surface的线程
     */
    internal class RenderThread(
        var dataDeque: LinkedBlockingDeque<YuvData>,
        var surfaceHolder: SurfaceHolder
    ) : Thread() {
        override fun run() {
            super.run()
            while (!interrupted()) {
                try {
                    val yuvData = dataDeque.take()
                    if (yuvData != null) {
                        OpenCVMobile.renderYuvDataOnSurface(
                            yuvData.width,
                            yuvData.height,
                            yuvData.data,
                            surfaceHolder.surface
                        )
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * 喂Yuv数据
     *
     * @param yuvData YuvData
     */
    fun onYuvData(yuvData: YuvData) {
        try {
            dataDeque.put(yuvData)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun release() {
        renderThread?.interrupt()
    }

    companion object {
        private val sPool: Pools.Pool<YuvData> = Pools.SimplePool(10)
    }
}