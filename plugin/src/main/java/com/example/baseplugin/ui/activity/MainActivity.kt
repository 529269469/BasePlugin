package com.example.baseplugin.ui.activity

import com.example.baseplugin.R
import com.example.baseplugin.mvi.BaseMviActivity
import com.example.baseplugin.utils.FetchStatus
import com.example.baseplugin.utils.observeState

class MainActivity : BaseMviActivity<MainViewModel>() {
    override fun initView() {
        viewModel.dispatch(MainViewAction.OnSwipeRefresh)


    }

    override fun initViewModel() {
        viewModel.viewStates.run {
            observeState(this@MainActivity, MainViewState::newsList) {

            }
            observeState(this@MainActivity, MainViewState::fetchStatus) {
                when (it) {
                    FetchStatus.Fetched -> {

                    }
                    FetchStatus.NotFetched -> {

                    }
                    FetchStatus.Fetching -> {

                    }
                }
            }
        }
    }

    override fun initVariableId() = R.layout.activity_main


}