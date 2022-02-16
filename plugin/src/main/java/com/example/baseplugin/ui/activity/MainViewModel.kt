package com.example.baseplugin.ui.activity

import androidx.lifecycle.MutableLiveData
import com.example.baseplugin.mvi.BaseMviViewModel
import com.example.baseplugin.utils.*


/**
 * Author      : wyw
 * Date        : on 2022-02-13 16:17.
 * Description :
 */
class MainViewModel : BaseMviViewModel() {

    private val _viewStates: MutableLiveData<MainViewState> = MutableLiveData(MainViewState())
    val viewStates = _viewStates.asLiveData()
    private val _viewEvents: SingleLiveEvent<MainViewEvent> = SingleLiveEvent() //一次性的事件，与页面状态分开管理
    val viewEvents = _viewEvents.asLiveData()


    fun dispatch(viewAction: MainViewAction) {
        when (viewAction) {
            is MainViewAction.NewsItemClicked -> newsItemClicked(viewAction.newsItem)
            MainViewAction.FabClicked -> fabClicked()
            MainViewAction.OnSwipeRefresh -> fetchNews()
            MainViewAction.FetchNews -> fetchNews()
        }
    }

    private fun newsItemClicked(newsItem: NewsItem) {
        "newsItemClicked".loge()
        _viewEvents.setEvent(MainViewEvent.ShowSnackbar(newsItem.title))
    }

    private fun fabClicked() {
        "fabClicked".loge()
    }

    private fun fetchNews() {
        "fetchNews".loge()
        _viewStates.setState {
            copy(fetchStatus = FetchStatus.Fetching)
        }
    }

}

sealed class MainViewEvent {
    data class ShowSnackbar(val message: String) : MainViewEvent()
    data class ShowToast(val message: String) : MainViewEvent()
}


data class MainViewState(
    val fetchStatus: FetchStatus = FetchStatus.NotFetched,
    val newsList: List<NewsItem> = emptyList()
)

sealed class MainViewAction {
    data class NewsItemClicked(val newsItem: NewsItem) : MainViewAction()
    object FabClicked : MainViewAction()
    object OnSwipeRefresh : MainViewAction()
    object FetchNews : MainViewAction()
}

data class NewsItem(
    val title: String,
    val description: String,
    val imageUrl: String
)

