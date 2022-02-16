package com.example.baseplugin.mvi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Author      : wyw
 * Date        : on 2022-02-13 15:49.
 * Description :
 */
abstract class BaseMviFragment<VM : BaseMviViewModel> : Fragment() {

    var mRootView: View? = null

    private var _viewModel: VM? = null
    val viewModel: VM
        get() = _viewModel!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(getLayoutId(), null)
        initView()
        initViewModel()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            //隐藏
            hiddenGone()
        } else {
            //显示
            hiddenVisible()
        }
    }

    abstract fun getLayoutId(): Int


    /**
     * 初始化页面
     */
    abstract fun initView()

    /**
     * 数据初始化
     */
    abstract fun initViewModel()

    /**
     * 隐藏
     */
    abstract fun hiddenVisible()

    /**
     * 显示
     */
    abstract fun hiddenGone()

}