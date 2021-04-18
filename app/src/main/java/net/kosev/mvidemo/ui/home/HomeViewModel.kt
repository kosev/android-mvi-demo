package net.kosev.mvidemo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import net.kosev.mvidemo.ui.Event
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _effect = MutableLiveData<Event<HomeEffect>>()
    val effect: LiveData<Event<HomeEffect>> = _effect

    fun onEvent(event: HomeEvent): Unit =
        when (event) {
            HomeEvent.FlowDemoClick -> _effect.value = Event(HomeEffect.NavigateToFlowDemo)
            HomeEvent.LiveDataDemoClick -> _effect.value = Event(HomeEffect.NavigateToLiveDataDemo)
        }

}

sealed class HomeEvent {
    object LiveDataDemoClick : HomeEvent()
    object FlowDemoClick : HomeEvent()
}

sealed class HomeEffect {
    object NavigateToLiveDataDemo : HomeEffect()
    object NavigateToFlowDemo : HomeEffect()
}
