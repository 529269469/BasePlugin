package com.example.baseplugin.mvi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Author      : wyw
 * Date        : on 2022-02-13 15:36.
 * Description :
 */
abstract class BaseMviActivity<VM : BaseMviViewModel>: AppCompatActivity() {

    private var _viewModel: VM? = null
    val viewModel: VM
        get() = _viewModel!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initVariableId())

        initView()
        initViewModel()
    }

    /**
     * 初始化页面
     */
    abstract fun initView()

    /**
     * 数据初始化
     */
    abstract fun initViewModel()

    abstract fun initVariableId(): Int


}