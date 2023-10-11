package com.nxg.im.core.module.map

import android.annotation.SuppressLint
import android.content.Context
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.MapsInitializer
import com.nxg.im.core.IMService
import com.nxg.im.core.dispatcher.IMCoroutineScope
import com.nxg.mvvm.logger.SimpleLogger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


interface MapService : IMService {
    fun getAMapLocationClient(): AMapLocationClient?

    fun init(context: Context)
    fun startLocation()
    fun getMapUiStateFlow(): StateFlow<MapUiState>
}

data class MapUiState(
    val aMapLocation: AMapLocation? = null
)


object MapServiceImpl : MapService, SimpleLogger {

    @SuppressLint("StaticFieldLeak")
    private var mLocationClient: AMapLocationClient? = null

    private var mLocationListener: AMapLocationListener? = null

    private val currentMapStateFlow = MutableStateFlow(MapUiState(null))

    private val mLocationOption by lazy {
        AMapLocationClientOption()
    }

    override fun getAMapLocationClient(): AMapLocationClient? {
        return mLocationClient
    }

    override fun init(context: Context) {
        MapsInitializer.updatePrivacyShow(context.applicationContext, true, true)
        MapsInitializer.updatePrivacyAgree(context.applicationContext, true)
        //初始化定位
        mLocationClient = mLocationClient ?: AMapLocationClient(context.applicationContext)
        //可以通过类implement方式实现AMapLocationListener接口，也可以通过创造接口类对象的方法实现
        mLocationListener = mLocationListener ?: AMapLocationListener {
            logger.debug { "onLocationChanged: $it" }
            if (it != null) {
                if (it.errorCode == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    IMCoroutineScope.launch {
                        currentMapStateFlow.emit(MapUiState(it))
                    }
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    logger.error {
                        "AmapError location Error, ErrCode: ${it.errorCode} errInfo: ${it.errorInfo}"
                    }
                }
            }
        }
        //设置定位回调监听
        mLocationClient?.setLocationListener(mLocationListener)

        mLocationClient?.apply {
            setLocationOption(mLocationOption)
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            stopLocation()
            startLocation()
        }
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.isNeedAddress = true
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.httpTimeOut = 20000
        //获取一次定位结果，该方法默认为false。
        mLocationOption.isOnceLocation = true
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.isOnceLocationLatest = true
        //给定位客户端对象设置定位参数
        mLocationClient?.setLocationOption(mLocationOption)
    }

    override fun startLocation() {
        mLocationClient?.apply {
            setLocationOption(mLocationOption)
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            stopLocation()
            startLocation()
        }
    }

    override fun getMapUiStateFlow(): StateFlow<MapUiState> {
        return currentMapStateFlow
    }
}