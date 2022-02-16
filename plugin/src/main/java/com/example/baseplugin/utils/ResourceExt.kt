package com.example.baseplugin.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Paint
import android.util.Log
import android.view.View
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.baseplugin.mvi.AppManager
import com.google.gson.GsonBuilder
import java.util.*

/**
 *资源操作扩展类相关
 */
fun Context.color(@ColorRes id: Int) = ContextCompat.getColor(this, id)

fun Context.string(@StringRes id: Int) = resources.getString(id)

fun Context.stringArray(@ArrayRes id: Int): Array<String> = resources.getStringArray(id)

fun Context.drawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun Context.dimenPx(@DimenRes id: Int) = resources.getDimensionPixelSize(id)

fun View.color(@ColorRes id: Int) = context.color(id)

fun View.string(@StringRes id: Int) = context.string(id)

fun View.stringArray(@ArrayRes id: Int): Array<String> = context.stringArray(id)

fun View.drawable(@DrawableRes id: Int) = context.drawable(id)

fun View.dimenPx(@DimenRes id: Int) = context.dimenPx(id)

fun Fragment.color(@ColorRes id: Int) = context!!.color(id)

fun Fragment.string(@StringRes id: Int) = context!!.string(id)

fun Fragment.stringArray(@ArrayRes id: Int) = context!!.stringArray(id)

fun Fragment.drawable(@DrawableRes id: Int) = context!!.drawable(id)

fun Fragment.dimenPx(@DimenRes id: Int) = context!!.dimenPx(id)

fun Int.dp2Px() = dp2px(this.toFloat())

fun Int.px2Dp() = px2dp(this.toFloat())

fun Int.sp2px() = sp2px(this.toFloat())

fun Int.px2sp() = px2sp(this.toFloat())

fun Int.color() = ContextCompat.getColor(AppManager.instance.getApp(), this)

fun Int.string() = Resources.getSystem().getString(this)

fun Any.toJson() = GsonBuilder().serializeNulls().disableHtmlEscaping().create().toJson(this)

@IntDef(value = [Log.ERROR, Log.ASSERT, Log.DEBUG, Log.INFO, Log.VERBOSE, Log.WARN])
annotation class LogLevel

private var isLog = false
fun isPrintLog(): Boolean {
    return isLog
}

fun isPrintLog(isPrintLog: Boolean) {
    isLog = isPrintLog
}

fun Any. loge(distinguishTag: String? = null, @LogLevel level: Int? = Log.ERROR) {
    if (isPrintLog()) {
        val stackTrace = Throwable().stackTrace
        val targetElement = stackTrace[2]
        val fileName = getFileName(targetElement)
        val tName = Thread.currentThread().name
        val methodName = if (targetElement.methodName.contains("lambda"))
            stackTrace[4].methodName else targetElement.methodName

        val head = Formatter().format("%s, %s(%s:%d)---->", tName,
            methodName, fileName, targetElement.lineNumber).toString()

        val tag = if (distinguishTag != null) "$distinguishTag<---->$head" else head
        when (level) {
            Log.ERROR -> Log.e(tag, "$this")
            Log.ASSERT -> Log.e(tag, "$this")
            Log.DEBUG -> Log.e(tag, "$this")
            Log.INFO -> Log.e(tag, "$this")
            Log.VERBOSE -> Log.e(tag, "$this")
            Log.WARN -> Log.e(tag, "$this")
        }
    }
}

private fun getFileName(targetElement: StackTraceElement): String {
    val fileName = targetElement.fileName
    if (fileName != null) return fileName
    var className = targetElement.className
    val classInfo = className.split("\\.").toTypedArray()
    if (classInfo.isNotEmpty()) {
        className = classInfo[classInfo.size - 1]
    }
    val index = className.indexOf('$')
    if (index != -1) {
        className = className.substring(0, index)
    }
    return "$className.java"
}

fun dp2px(dpValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}

fun px2dp(pxValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (pxValue / scale + 0.5f).toInt()
}

fun sp2px(spValue: Float): Int {
    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
    return (spValue * fontScale + 0.5f).toInt()
}

fun px2sp(pxValue: Float): Int {
    val fontScale = Resources.getSystem().displayMetrics.scaledDensity
    return (pxValue / fontScale + 0.5f).toInt()
}

/**
 * 设置带文字布局控件宽度
 */
fun String.setTextWidth2View(view: View) {
    val layoutParams = view.layoutParams
    layoutParams.width = Paint().measureText(this).toInt() * length + 14
    view.layoutParams = layoutParams
}

/**
 * 设置带文字布局控件宽度
 */
fun String.getTextWidth() = Paint().measureText(this).toInt()

fun FragmentActivity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        return resources.getDimensionPixelSize(resourceId)
    }
    return 0
}

