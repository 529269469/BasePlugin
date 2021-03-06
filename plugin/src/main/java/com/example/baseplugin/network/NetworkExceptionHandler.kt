package com.example.baseplugin.network


import com.example.baseplugin.utils.showToast
import kotlinx.coroutines.CoroutineExceptionHandler
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * 通用异常处理
 * 在这里处理一些页面通用的异常逻辑
 * 可以根据业务需求自定义
 */
class NetworkExceptionHandler(
    private val onException: (e: Throwable) -> Unit = {}
) : AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        onException.invoke(exception)
        if (exception is UnknownHostException || exception is SocketTimeoutException) {
            "发生网络错误，请稍后重试".showToast()
        } else {
            "请求失败，请重试".showToast()
        }
    }
}