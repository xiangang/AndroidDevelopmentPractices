package com.nxg.commonui.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.widget.NestedScrollView
import android.view.MotionEvent

class LockableNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr)  {

    // by default is scrollable
    var scrollable = true

    fun setScrollingEnabled(enabled: Boolean) {
        scrollable = enabled
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return scrollable && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return scrollable && super.onInterceptTouchEvent(ev)
    }
}
