package com.nxg.srollview

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.annotation.LayoutRes
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.nxg.srollview.adapter.ScrollViewTextAdapter
import com.nxg.srollview.bean.TextBean
import com.nxg.srollview.utils.LogUtil
import com.nxg.srollview.widget.LooperLinearLayoutManager
import kotlin.math.pow
import kotlin.math.sin

/**
 * 基于RecyclerView的，类似于老虎机的自定义View
 *
 */
class ScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    companion object {

        private const val TAG = "ScrollView"

        /**
         * 此处用法见：
         * 你会用Kotlin实现构建者模式吗？
         * https://zhuanlan.zhihu.com/p/267145868
         */
        inline fun build(block: Configuration.() -> Unit) = Configuration().apply(block)
    }

    /**
     * 配置
     */
    class Configuration {
        /**
         * 第一阶段的滚动动画时长
         */
        var scrollAnimatorDuration = 3000L

        /**
         * 第一阶段的滚动动画的插值器
         */
        var scrollAnimatorInterpolator = AccelerateDecelerateInterpolator()

        /**
         * 第一阶段的滚动动画针对item的滚动步长（scrollBy方法的y参数）
         */
        var scrollAnimatorScrollByYStep = 30

        /**
         * 第二阶段的弹簧动画的SpringForce
         */
        var springAnimatorForce: SpringForce = SpringForce(0f)
            .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
            .setStiffness(SpringForce.STIFFNESS_VERY_LOW)
            .setDampingRatio(0.2f)
            .setStiffness(350f)

        /**
         * 弹簧动画的起始位置
         */
        var springAnimatorStartValue = -50f

        /**
         * Item的布局ID
         */
        @LayoutRes
        var itemLayoutId: Int = R.layout.scroll_view_item_text

    }

    /**
     * 配置
     */
    private var configuration: Configuration = build {}

    /**
     * 第一阶段的滚动动画
     */
    private var scrollAnimator: ValueAnimator = ValueAnimator.ofFloat(1f)

    /**
     * 数据源
     */
    private val data = mutableListOf<TextBean>()

    /**
     * 当前的选中的值
     */
    private var currentValue = ""

    /**
     * Item触摸事件监听器
     */
    private val recyclerViewDisableItemTouch: OnItemTouchListener = RecyclerViewDisableItemTouch()

    /**
     * init代码块，执行顺序在主函数和次构造函数之间
     */
    init {
        LogUtil.i(TAG, "init block called!")
    }

    /**
     * 第一节阶段的滚动动画的动画更新监听器
     * 用于实现Item滚动的效果
     */
    private val scrollAnimatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener { animation ->
            val value = animation.animatedValue as Float
            LogUtil.i(TAG, "scrollAnimatorUpdateListener : value = $value")
            LogUtil.i(
                TAG,
                "scrollAnimatorUpdateListener : y = ${(-value * configuration.scrollAnimatorScrollByYStep).toInt()}"
            )
            //支持伪无限循环
            val position =
                (this@ScrollView.layoutManager as LooperLinearLayoutManager).findLastVisibleItemPosition() - 1
            if (position <= 0) {
                //this.scrollToPosition(this.data.size - 1)
            }
            this.scrollBy(0, -(value * configuration.scrollAnimatorScrollByYStep).toInt())

        }

    /**
     * 第一阶段的滚动动画的动画监听器
     * 主要用于动画结束后执行第二阶段的弹簧动画
     */
    private val scrollAnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            val position =
                (this@ScrollView.layoutManager as LooperLinearLayoutManager).findLastVisibleItemPosition() - 1
            if (position < data.size && position >= 0) {
                this@ScrollView.smoothScrollToPosition(position)
                LogUtil.i(
                    TAG,
                    "animatorSpring current position is $position, text: ${data[position]}"
                )
                this@ScrollView.currentValue = data[position].text
            }
            startSpringAnimator()
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationRepeat(animation: Animator?) {
        }
    }

    /**
     * 启动弹簧动画
     * 介绍见：https://developer.android.google.cn/guide/topics/graphics/spring-animation?hl=zh-cn
     */
    private fun startSpringAnimator() {
        SpringAnimation(this@ScrollView, DynamicAnimation.TRANSLATION_Y).apply {
            spring = configuration.springAnimatorForce
            cancel()
            setStartValue(configuration.springAnimatorStartValue)
            start()
        }
    }

    /**
     * 弹簧插值器
     * @factor 值越小，代表弹簧的弹性越大，即上下振幅的频率越高
     * 弹簧算法： (2.0.pow((-10 * x).toDouble()) * sin((x - factor / 4) * (2 * Math.PI) / factor) + 1)
     * 算法来源于：http://inloop.github.io/interpolator/
     */
    class SpringAnimationInterpolator(private val factor: Float = 0.3f) : Interpolator {

        override fun getInterpolation(x: Float): Float {
            return (2.0.pow((-10 * x).toDouble()) * sin((x - factor / 4) * (2 * Math.PI) / factor) + 1).toFloat()
        }
    }

    /**
     * 拦截RecyclerView的item的touch事件，达到无法通过手指滑动RecyclerView的效果
     */
    class RecyclerViewDisableItemTouch : OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    /***************************** Public API below ***********************************************/

    /**
     * 自定义配置
     */
    fun setConfiguration(configuration: Configuration) {
        LogUtil.i(TAG, "setConfiguration function called! ${configuration.scrollAnimatorDuration}")
        this.configuration = configuration
    }

    /**
     * 初始化数据，这里的适配器是自由的，随你
     * @adapter 适配器
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<TextBean>) {
        LogUtil.i(TAG, "setData function called!")
        this.data.clear()
        this.data.addAll(data)
        if (adapter == null) {
            //初始化adapter
            this.adapter = ScrollViewTextAdapter(
                context = context,
                resource = this.configuration.itemLayoutId,
                dataList = this.data
            )
            //this.layoutManager = LinearLayoutManager = LinearLayoutManager(context, VERTICAL, false)
            this.layoutManager = LooperLinearLayoutManager(context)
            //禁止Item触摸滑动
            //this.addOnItemTouchListener(recyclerViewDisableItemTouch)
            //恢复Item触摸滑动
            //this.removeOnItemTouchListener(disabler)
            this.scrollAnimator.addUpdateListener(scrollAnimatorUpdateListener)
            this.scrollAnimator.addListener(scrollAnimatorListener)
        }
        //设置最后一个的Text为上次选中的值
        if (currentValue.isNotEmpty()) {
            this.data[this.data.size - 1].text = currentValue
        }
        adapter!!.notifyDataSetChanged()
        if (this.data.size > 1) {
            this.scrollToPosition(this.data.size - 1)
        }
    }

    /**
     * 开始滚动动画
     */
    fun start(delay: Long = 0L) {
        LogUtil.i(TAG, "start function called! ${configuration.scrollAnimatorDuration}")
        this.scrollAnimator.duration = configuration.scrollAnimatorDuration
        this.scrollAnimator.interpolator = configuration.scrollAnimatorInterpolator
        this.scrollAnimator.cancel()
        this.scrollAnimator.startDelay = delay
        this.scrollAnimator.start()
    }
}