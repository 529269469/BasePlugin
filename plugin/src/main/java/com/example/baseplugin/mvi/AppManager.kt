package com.example.baseplugin.mvi

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Bundle
import org.jetbrains.annotations.NotNull
import java.lang.ref.WeakReference
import java.util.*
import kotlin.system.exitProcess

/**
 * Description : activity管理工具类
 */
class AppManager private constructor() {
    private val activityStack: Stack<Activity> by lazy { Stack() }
    private var app: Application? = null

    companion object {
        val instance: AppManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppManager()
        }
    }

    fun init(app: Application?) {
        app?.let {
            if (this.app == null) {
                this.app = app
                register(this.app)
                return@let
            }
            if (this.app == app) return@let
            unRegister(this.app)
            this.app = app
            register(this.app)
        }
    }

    private fun register(app: Application?) {
        app?.registerActivityLifecycleCallbacks(call)
    }

    private fun unRegister(app: Application?) {
        app?.unregisterActivityLifecycleCallbacks(call)
    }

    private val call = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            activityStack.add(activity)
        }

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
        }

        override fun onActivityPaused(activity: Activity) {
        }

        override fun onActivityStopped(activity: Activity) {
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        }

        override fun onActivityDestroyed(activity: Activity) {
            activityStack.remove(activity)
        }
    }

    fun getApp(): Application {
        return app!!
    }

    fun getTopActivity(): Activity? {//获取最上层activity
        if (!activityStack.isEmpty()) {
            return activityStack.lastElement()
        }
        return null
    }

    fun getContext(): Context? {
        return WeakReference(getTopActivity() as Context).get()
    }

    fun getAppContext(): Context {
        return app!!.applicationContext
    }

    /**
     * 判断应用是否在前台
     */
    fun isAppForeground(): Boolean {
        val am =
            this.app?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager ?: return false
        val info = am.runningAppProcesses
        if (info == null || info.size == 0) return false
        info.forEach {
            if (it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (it.processName.equals(this.app!!.packageName)) {
                    return true
                }
            }
        }
        return false
    }

    fun finishTopActivity() = activityStack.lastElement()?.finish()//关闭最上层activity

    fun finishActivity(@NotNull activity: Activity) {
        activityStack.remove(activity)
        activity.finish()
    }

    fun finishActivity(@NotNull cla: Class<Any>) {
        activityStack.forEach {
            if (it.javaClass == cla) {
                finishActivity(it)
            }
        }
    }

    fun finishAllActivity() {
        for ((i) in activityStack.reversed().withIndex()) {
            activityStack[i].finish()
        }
        activityStack.clear()
    }

    fun appExit(context: Context) {
        finishAllActivity()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.killBackgroundProcesses(context.packageName)
        exitProcess(0)
    }
}