package net.kosev.mvidemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableLiveData<CalculatorState>()
    val state: LiveData<CalculatorState> = _state

    private val _effect = MutableLiveData<Event<CalculatorEffect>>()
    val effect: LiveData<Event<CalculatorEffect>> = _effect

    fun onEvent(event: CalculatorEvent): Unit =
        when (event) {
            CalculatorEvent.ScreenLoad -> _state.value = CalculatorState("BTC 0.14000000", "EUR 48550.00")
            CalculatorEvent.BuyBitcoinClick -> TODO()
            CalculatorEvent.SettingsClick -> _effect.value = Event(CalculatorEffect.NavigateToSettings)
        }
}

data class CalculatorState(
    val cryptoBalance: String,
    val fiatBalance: String
)

sealed class CalculatorEvent {
    object ScreenLoad : CalculatorEvent()
    object SettingsClick : CalculatorEvent()
    object BuyBitcoinClick : CalculatorEvent()
}

sealed class CalculatorEffect {
    object NavigateToSettings : CalculatorEffect()
}
