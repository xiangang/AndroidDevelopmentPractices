package com.nxg.ssq.ui.home

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.nxg.ssq.databinding.FragmentHomeBinding
import java.lang.Math.PI
import kotlin.math.pow
import kotlin.math.sin


class HomeFragment : Fragment() {


    companion object {
        private const val ANIMATION_VALUES = 1f
        private const val ANIMATION_SPRING_VALUES = 1f
        private const val SCROLL_Y_STEP = 30
        private const val TAG = "HomeFragment"
    }

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val numberList = mutableListOf<NumberBean>()
    private var currentNum = ""

    private val animator = ValueAnimator.ofFloat(ANIMATION_VALUES)
    private val animatorSpring = ValueAnimator.ofFloat(ANIMATION_SPRING_VALUES)
    private val animatorSpringReverse = ValueAnimator.ofFloat(ANIMATION_SPRING_VALUES)

    private var firstVisibleItemPosition = 0

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        animator.duration = 3000
        animator.interpolator = AccelerateDecelerateInterpolator()

        animatorSpring.duration = 500
        animatorSpring.interpolator = SpringAnimationInterpolator(0.8f)
        animatorSpringReverse.duration = 1000
        animatorSpringReverse.interpolator = SpringAnimationInterpolator()

        val recyclerView: RecyclerView = binding.recyclerView
        val numberAdapter = NumberAdapter(requireContext(), numberList)
        recyclerView.adapter = numberAdapter
        val layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        val disabler: OnItemTouchListener = RecyclerViewDisabler()

        recyclerView.addOnItemTouchListener(disabler)
        //recyclerView.removeOnItemTouchListener(disabler)

        val springForce = SpringForce(0f)
            .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
            .setDampingRatio(0.2f)
            .setStiffness(SpringForce.STIFFNESS_VERY_LOW)
            .setStiffness(350f)
        /*val anim = SpringAnimation(recyclerView, DynamicAnimation.TRANSLATION_Y).setSpring(
            springForce
        )*/

        homeViewModel.listNumber.observe(viewLifecycleOwner, Observer {
            numberList.clear()
            numberList.addAll(it)
            if (currentNum.isNotEmpty()) {
                numberList[numberList.size - 1].number = currentNum
            }
            numberAdapter.notifyDataSetChanged()
            recyclerView.scrollToPosition(numberList.size - 1)
            animator.cancel()
            animatorSpring.cancel()
            animator.start()
        })

        //PagerSnapHelper().attachToRecyclerView(recyclerView)

        binding.buttonStart.setOnClickListener {
            homeViewModel.refreshNum()
        }
        binding.buttonScrollByUp.setOnClickListener {
            recyclerView.scrollBy(0, SCROLL_Y_STEP)
        }
        binding.buttonScrollByDown.setOnClickListener {
            recyclerView.scrollBy(0, -SCROLL_Y_STEP)
        }
        animator.addUpdateListener {
            val value = it.animatedValue as Float
            Log.i(TAG, "animator addUpdateListener: $value")
            recyclerView.scrollBy(0, -(value * SCROLL_Y_STEP).toInt())
            if (value == ANIMATION_VALUES) {
                val position = layoutManager.findLastVisibleItemPosition() - 1
                Log.i(TAG, "animator addUpdateListener: $position")
                Log.i(TAG, "animator current position text: ${numberList[position]}")
                recyclerView.smoothScrollToPosition(position)
                currentNum = numberList[position].number
                /*animatorSpring.cancel()
                animatorSpring.startDelay = 120
                animatorSpring.start()*/
            }
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                val position = layoutManager.findFirstVisibleItemPosition()
                Log.i(TAG, "animatorSpring addUpdateListener: $position")
                Log.i(TAG, "animatorSpring current position text: ${numberList[position]}")
                SpringAnimation(recyclerView, DynamicAnimation.TRANSLATION_Y).apply {
                    spring = springForce
                    cancel()
                    setStartValue(-60f)
                    start()
                }
                /*SpringAnimation(binding.layoutScrollView, DynamicAnimation.TRANSLATION_Y).apply {
                    spring = SpringForce(0f)
                        .setDampingRatio(SpringForce.DAMPING_RATIO_HIGH_BOUNCY)
                        .setStiffness(SpringForce.STIFFNESS_LOW)
                    cancel()
                    setStartValue(20f)
                    start()
                }*/
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })

        animatorSpring.addUpdateListener {
            val value = it.animatedValue as Float
            Log.i(TAG, "animatorSpring addUpdateListener: value = $value")
            recyclerView.scrollBy(0, -value.toInt())
        }
        animatorSpring.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                val position = layoutManager.findFirstVisibleItemPosition()
                Log.i(TAG, "animatorSpring addUpdateListener: $position")
                Log.i(TAG, "animatorSpring current position text: ${numberList[position]}")
                //recyclerView.scrollToPosition(position)
                //animatorSpringReverse.cancel()
                //animatorSpringReverse.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })

        animatorSpringReverse.addUpdateListener {
            val value = it.animatedValue as Float
            val y = value * 10
            Log.i(
                TAG,
                "animatorSpringReverse addUpdateListener: value = $value, y = $y"
            )
            recyclerView.scrollBy(0, y.toInt())
        }

        animatorSpringReverse.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                val position = layoutManager.findLastVisibleItemPosition()
                Log.i(TAG, "animatorSpringReverse addUpdateListener: $position")
                Log.i(TAG, "animatorSpringReverse current position text: ${numberList[position]}")
                //recyclerView.smoothScrollToPosition(position)
                currentNum = numberList[position].number
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })


        //  RecyclerView设置滑动监听
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE ->                         //获得当前显示在第一个item的位置
                        firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    RecyclerView.SCROLL_STATE_DRAGGING ->                         //获得当前显示在第一个item的位置
                        firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                }
            }
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun dp2px(dp: Int): Int? {
        return requireContext().resources?.displayMetrics?.density?.let { (dp * it + 0.5).toInt() }
    }

    /**
     * 模拟弹簧的插值器
     */
    class SpringInterpolator : Interpolator {
        override fun getInterpolation(input: Float): Float {
            return sin(input * 2 * PI).toFloat()
        }
    }

    /**
     * 模拟弹簧的插值器
     */
    class SpringAnimationInterpolator(
        private val factor: Float = 0.3f,
        private val step: Int = 30
    ) :
        Interpolator {

        private var lastInterpolation = 0f

        override fun getInterpolation(x: Float): Float {
            val interpolation =
                (2.0.pow((-10 * x).toDouble()) * sin((x - factor / 4) * (2 * PI) / factor) + 1).toFloat()
            return if (interpolation >= lastInterpolation) {
                lastInterpolation = interpolation
                step * 1f
            } else {
                lastInterpolation = interpolation
                step * 1f * -1
            }
        }
    }

    class RecyclerViewDisabler : RecyclerView.OnItemTouchListener {

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            return true
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }


}