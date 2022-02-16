package com.example.baseplugin.utils

import android.animation.Animator
import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.baseplugin.R
import com.example.baseplugin.mvi.AppManager
import com.example.baseplugin.mvi.ShowOrientation


/**
 *View扩展类相关
 */
/**
 * 设置View的高度
 */
fun View.height(height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View高度，限制在min和max范围之内
 * @param h
 * @param min 最小高度
 * @param max 最大高度
 */
fun View.limitHeight(h: Int, min: Int, max: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    when {
        h < min -> params.height = min
        h > max -> params.height = max
        else -> params.height = h
    }
    layoutParams = params
    return this
}

/**
 * 设置View的宽度
 */
fun View.width(width: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    layoutParams = params
    return this
}

/**
 * 设置View宽度，限制在min和max范围之内
 * @param w
 * @param min 最小宽度
 * @param max 最大宽度
 */
fun View.limitWidth(w: Int, min: Int, max: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    when {
        w < min -> params.width = min
        w > max -> params.width = max
        else -> params.width = w
    }
    layoutParams = params
    return this
}

/**
 * 设置View的宽度和高度
 * @param width 要设置的宽度
 * @param height 要设置的高度
 */
fun View.widthAndHeight(width: Int, height: Int): View {
    val params = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
    params.width = width
    params.height = height
    layoutParams = params
    return this
}

/**
 * 设置View的margin
 * @param leftMargin 默认保留原来的
 * @param topMargin 默认是保留原来的
 * @param rightMargin 默认是保留原来的
 * @param bottomMargin 默认是保留原来的
 */
fun View.margin(
    leftMargin: Int = Int.MAX_VALUE,
    topMargin: Int = Int.MAX_VALUE,
    rightMargin: Int = Int.MAX_VALUE,
    bottomMargin: Int = Int.MAX_VALUE
): View {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    if (leftMargin != Int.MAX_VALUE)
        params.leftMargin = leftMargin
    if (topMargin != Int.MAX_VALUE)
        params.topMargin = topMargin
    if (rightMargin != Int.MAX_VALUE)
        params.rightMargin = rightMargin
    if (bottomMargin != Int.MAX_VALUE)
        params.bottomMargin = bottomMargin
    layoutParams = params
    return this
}

/**
 * 设置宽度，带有过渡动画
 * @param targetValue 目标宽度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateWidth(
    targetValue: Int, duration: Long = 400, listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
) {
    post {
        ValueAnimator.ofInt(width, targetValue).apply {
            addUpdateListener {
                width(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置高度，带有过渡动画
 * @param targetValue 目标高度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateHeight(
    targetValue: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
) {
    post {
        ValueAnimator.ofInt(height, targetValue).apply {
            addUpdateListener {
                height(it.animatedValue as Int)
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置宽度和高度，带有过渡动画
 * @param targetWidth 目标宽度
 * @param targetHeight 目标高度
 * @param duration 时长
 * @param action 可选行为
 */
fun View.animateWidthAndHeight(
    targetWidth: Int,
    targetHeight: Int,
    duration: Long = 400,
    listener: Animator.AnimatorListener? = null,
    action: ((Float) -> Unit)? = null
) {
    post {
        val startHeight = height
        val evaluator = IntEvaluator()
        ValueAnimator.ofInt(width, targetWidth).apply {
            addUpdateListener {
                widthAndHeight(
                    it.animatedValue as Int,
                    evaluator.evaluate(it.animatedFraction, startHeight, targetHeight)
                )
                action?.invoke((it.animatedFraction))
            }
            if (listener != null) addListener(listener)
            setDuration(duration)
            start()
        }
    }
}

/**
 * 设置点击监听, 并实现事件节流
 */
var _viewClickFlag = false
var _clickRunnable = Runnable { _viewClickFlag = false }
fun View.click(action: (view: View) -> Unit) {
    setOnClickListener {
        if (!_viewClickFlag) {
            _viewClickFlag = true
            action(it)
        }
        removeCallbacks(_clickRunnable)
        postDelayed(_clickRunnable, 350)
    }
}

/**
 * 设置长按监听
 */
fun View.longClick(action: (view: View) -> Boolean) {
    setOnLongClickListener {
        action(it)
    }
}


/*** 可见性相关 ****/
fun View.gone() {
    visibility = View.GONE

}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val View.isGone: Boolean
    get() {
        return visibility == View.GONE
    }

val View.isVisible: Boolean
    get() {
        return visibility == View.VISIBLE
    }

val View.isInvisible: Boolean
    get() {
        return visibility == View.INVISIBLE
    }

/**
 * 切换View的可见性
 */
fun View.toggleVisibility() {
    visibility = if (visibility == View.GONE) View.VISIBLE else View.GONE
}

/**
 * 设置圆形背景
 */
fun View.setRoundRectBg(
    color: Int = Color.WHITE,
    cornerRadius: Int = 10.dp2Px(),
    strokeWidth: Int = -1,
    strokeColor: Int = color
) {
    background = GradientDrawable().apply {
        setColor(color)
        setCornerRadius(cornerRadius.toFloat())
        if (strokeWidth != -1) {
            setStroke(strokeWidth, strokeColor)
        }
    }
}

// 所有子View
inline val ViewGroup.children
    get() = (0 until childCount).map { getChildAt(it) }

/**
 * 以下为fragment事物扩展方法、添加替换显示隐藏fragment、批量添加隐藏、显示隐藏fragment
 */
private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) =
    supportFragmentManager.inTransaction { add(frameId, fragment) }

fun AppCompatActivity.replaceFragment(
    fragment: Fragment,
    frameId: Int,
    inAnim: Int? = null,
    outAnim: Int? = null
) =
    if (inAnim != null && outAnim != null)
        supportFragmentManager.inTransaction {
            setCustomAnimations(inAnim, outAnim)
                .replace(frameId, fragment)
        }
    else
        supportFragmentManager.inTransaction {
            replace(frameId, fragment)
        }


fun AppCompatActivity.hideFragment(fragment: Fragment) =
    supportFragmentManager.inTransaction { hide(fragment) }

fun AppCompatActivity.showFragment(fragment: Fragment) =
    supportFragmentManager.inTransaction { show(fragment) }

/**
 * 展示第0个fragment
 * 隐藏其他fragment
 */
fun AppCompatActivity.showHideFragment(
    mutableList: List<Fragment>,
    inAnim: Int? = null,
    outAnim: Int? = null
) {
    if (mutableList.isNotEmpty()) {
        if (inAnim != null && outAnim != null)
            supportFragmentManager.inTransaction {
                setCustomAnimations(inAnim, outAnim)
                    .show(mutableList[0])
            }
        else
            supportFragmentManager.inTransaction {
                show(mutableList[0])
            }
        for (index in 1 until mutableList.size) {
            supportFragmentManager.inTransaction { hide(mutableList[index]) }
        }
    }
}

/**
 * 添加第0个fragment
 * 隐藏其他fragment
 */
fun AppCompatActivity.addHideFragment(
    mutableList: List<Fragment>, frameId: Int,
    inAnim: Int? = null,
    outAnim: Int? = null
) {
    if (mutableList.isNotEmpty()) {
        for (element in mutableList) {
            if (inAnim != null && outAnim != null)
                supportFragmentManager.inTransaction {
                    setCustomAnimations(inAnim, outAnim)
                        .add(frameId, element)
                }
            else
                supportFragmentManager.inTransaction {
                    add(frameId, element)
                }
        }
        for (index in 1 until mutableList.size) {
            supportFragmentManager.inTransaction { hide(mutableList[index]) }
        }
    }
}

inline fun <reified A : Activity> Context.startToActivity() {
    startActivity(Intent(this, A::class.java))
}

inline fun <reified A : Activity> Context.startToPutActivity(init: Intent.() -> Unit) {
    val intent = Intent(this, A::class.java)
    intent.init()
    startActivity(intent)
}

inline fun <reified A : Activity> Context.startToPutActivity(vararg pair: Pair<String, Any?>) {
    startActivity(Intent(this, A::class.java).apply { putExtras(bundleOf(*pair)) })
}

fun TextView.setUnderlineSpan() {
    val spannableString = SpannableString(this.text.toString())
    spannableString.setSpan(
        UnderlineSpan(),
        0,
        spannableString.length,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    this.text = spannableString
}

fun String.setTextColorSpan(
    context: Context,
    @ColorRes color: Int,
    startPosition: Int,
    endPosition: Int
): SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(
        ForegroundColorSpan(ContextCompat.getColor(context, color)),
        startPosition,
        endPosition,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannableString
}

/**
 * 代码写布局扩展函数
 *
 * 例子：var editText = view {
hint = "请输入密码"
setHintTextColor(ContextCompat.getColor(context, R.color.hint_color))
background = null
setTextColor(ColorUtils.getColor(R.color.hint_color))
}
 */
inline fun <reified V : View> Context.view(init: V.() -> Unit): V {
    val constr = V::class.java.getConstructor(Context::class.java)
    val tv = constr.newInstance(this)
    tv.init()
    return tv
}

/**
 * Toast扩展函数 可直接Int.showToast或String.showToast
 * 例子： "请安装微信".showToast()    R.string.open_net_service2.showToast() 12.showToast()
 */
fun Any.showToast() {
    this.showToast(0)
}

/**
 *
 * @receiver Any string和int类型
 * @param type Int 0弹出短时toast  1弹出长时toast
 */
fun Any.showToast(type: Int) {
    AppManager.instance.getTopActivity()?.let {
        it.runOnUiThread {
            when (type) {
                0 -> this.showShort()
                1 -> this.showLong()
            }
        }
    }
}

/**
 * toast提示
 * @receiver Any
 * @param view Drawable?
 * @param showOrientation Int
 * @param showType Int?
 */
fun Any.showToast(
    view: Drawable?,
    @ShowOrientation showOrientation: Int? = ShowOrientation.START,
    showType: Int? = 1
) {
    when (showOrientation) {
        ShowOrientation.START -> AppManager.instance.getTopActivity()?.let {
            it.runOnUiThread {
                when (showType) {
                    0 -> this.showShort(view, ShowOrientation.START)
                    1 -> this.showLong(view, ShowOrientation.START)
                }
            }
        }
        ShowOrientation.TOP -> AppManager.instance.getTopActivity()?.let {
            it.runOnUiThread {
                when (showType) {
                    0 -> this.showShort(view, ShowOrientation.TOP)
                    1 -> this.showLong(view, ShowOrientation.TOP)
                }
            }
        }
        ShowOrientation.END -> AppManager.instance.getTopActivity()?.let {
            it.runOnUiThread {
                when (showType) {
                    0 -> this.showShort(view, ShowOrientation.END)
                    1 -> this.showLong(view, ShowOrientation.END)
                }
            }
        }
        ShowOrientation.BOTTOM -> AppManager.instance.getTopActivity()?.let {
            it.runOnUiThread {
                when (showType) {
                    0 -> this.showShort(view, ShowOrientation.BOTTOM)
                    1 -> this.showLong(view, ShowOrientation.BOTTOM)
                }
            }
        }
    }
}

private fun Any.showShort() {
    when (this) {
        is Int -> {
            try {
                val string = AppManager.instance.getApp().resources.getString(this)
                show(string, Toast.LENGTH_SHORT)
            } catch (e: Resources.NotFoundException) {
                show(this.toString(), Toast.LENGTH_SHORT)
            }
        }
        else -> show(this.toString(), Toast.LENGTH_SHORT)
    }
}

private fun Any.showShort(view: Drawable?, showOrientation: Int) {
    when (this) {
        is Int -> {
            try {
                val string = AppManager.instance.getApp().resources.getString(this)
                show(string, Toast.LENGTH_SHORT, view, showOrientation)
            } catch (e: Resources.NotFoundException) {
                show(this.toString(), Toast.LENGTH_SHORT, view, showOrientation)
            }
        }
        else -> show(this.toString(), Toast.LENGTH_SHORT, view, showOrientation)
    }
}

private fun Any.showLong() {
    when (this) {
        is Int -> {
            try {
                val string = AppManager.instance.getApp().resources.getString(this)
                show(string, Toast.LENGTH_LONG)
            } catch (e: Resources.NotFoundException) {
                show(this.toString(), Toast.LENGTH_LONG)
            }
        }
        else -> show(this.toString(), Toast.LENGTH_LONG)
    }
}

private fun Any.showLong(view: Drawable?, showOrientation: Int) {
    when (this) {
        is Int -> {
            try {
                val string = AppManager.instance.getApp().resources.getString(this)
                show(string, Toast.LENGTH_LONG, view, showOrientation)
            } catch (e: Resources.NotFoundException) {
                show(this.toString(), Toast.LENGTH_LONG, view, showOrientation)
            }
        }
        else -> show(this.toString(), Toast.LENGTH_LONG, view, showOrientation)
    }
}

private fun show(text: String, duration: Int) {
    AbsToast().setTextView(text).show(duration)
}

private fun show(text: String, duration: Int, view: Drawable?, showOrientation: Int) {
    val drawables = arrayOfNulls<Drawable>(4)
    when (showOrientation) {
        ShowOrientation.START -> drawables[0] = view
        ShowOrientation.TOP -> drawables[1] = view
        ShowOrientation.END -> drawables[2] = view
        ShowOrientation.BOTTOM -> drawables[3] = view
    }
    AbsToast().setDrawables(drawables).setTextView(text).show(duration)
}

class AbsToast {
    private var toast: Toast? = null
    private var HANDLER: Handler? = null
    private val drawableArray = arrayOfNulls<Drawable>(4)

    init {
        toast = Toast(AppManager.instance.getApp())
        HANDLER = Handler(Looper.getMainLooper())
    }

    fun setDrawables(
        drawables: Array<Drawable?>
    ): AbsToast {
        drawableArray[0] = drawables[0]
        drawableArray[1] = drawables[1]
        drawableArray[2] = drawables[2]
        drawableArray[3] = drawables[3]
        return this
    }

    fun setTextView(text: String): AbsToast {
        val inflate =
            AppManager.instance.getApp()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val v = inflate.inflate(R.layout.util_toast_view, null)
        val messageTv = v.findViewById<TextView>(android.R.id.message)
        val rlToastRootBg = v.findViewById<RelativeLayout>(R.id.rlToastRootBg)
        if (drawableArray[0] != null) {
            val leftIconView = v.findViewById<View>(R.id.utvLeftIconView)
            ViewCompat.setBackground(leftIconView, drawableArray[0])
            leftIconView.visibility = View.VISIBLE
            setBg(rlToastRootBg)
        }
        if (drawableArray[1] != null) {
            val topIconView = v.findViewById<View>(R.id.utvTopIconView)
            ViewCompat.setBackground(topIconView, drawableArray[1])
            topIconView.visibility = View.VISIBLE
            setBg(rlToastRootBg)
        }
        if (drawableArray[2] != null) {
            val rightIconView = v.findViewById<View>(R.id.utvRightIconView)
            ViewCompat.setBackground(rightIconView, drawableArray[2])
            rightIconView.visibility = View.VISIBLE
            setBg(rlToastRootBg)
        }
        if (drawableArray[3] != null) {
            val bottomIconView = v.findViewById<View>(R.id.utvBottomIconView)
            ViewCompat.setBackground(bottomIconView, drawableArray[3])
            bottomIconView.visibility = View.VISIBLE
            setBg(rlToastRootBg)
        }
        messageTv.text = text
        toast?.view = v
        return this
    }

    fun setCenterGravity() {
        toast?.setGravity(Gravity.CENTER, 0, 0)
    }

    fun show(duration: Int) {
        toast?.duration = duration
        toast?.show()
        HANDLER?.postDelayed({ cancel() }, if (duration == Toast.LENGTH_SHORT) 2000 else 3500)
    }

    private fun cancel() {
        toast?.cancel()
        toast = null
        HANDLER = null
    }

    fun setBg(rlToastRootBg: RelativeLayout) {
        rlToastRootBg.post {
            val layoutParams = rlToastRootBg.layoutParams
            layoutParams.width = 219.dp2Px()
            layoutParams.height = 55.dp2Px()
            rlToastRootBg.layoutParams = layoutParams
        }
        setCenterGravity()
    }
}


/**
 * 获取edittext以外的区域
 * @receiver FragmentActivity
 * @return Boolean
 */
fun FragmentActivity.softKeyboardIsShouldShowed(v: View?, event: MotionEvent): Boolean {
    if (v != null && (v is EditText)) {
        val intArray = intArrayOf(0, 0)
        v.getLocationInWindow(intArray)
        val left = intArray[0]
        val top = intArray[1]
        val bottom = top + v.height
        val right = left + v.width
        return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
    }
    return false
}

/**
 * @return 是否是主线程
 */
fun isInMainThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

fun <T> MutableLiveData<T>.asLiveData(): LiveData<T> {
    return this
}