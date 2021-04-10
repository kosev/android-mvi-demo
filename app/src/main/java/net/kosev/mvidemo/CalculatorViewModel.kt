package net.kosev.mvidemo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val balancesRepository: BalancesRepository
) : ViewModel() {

    private val _state = MutableLiveData<CalculatorState>()
    val state: LiveData<CalculatorState> = _state

    private val _effect = MutableLiveData<Event<CalculatorEffect>>()
    val effect: LiveData<Event<CalculatorEffect>> = _effect

    fun onEvent(event: CalculatorEvent): Unit =
        when (event) {
            CalculatorEvent.ScreenLoad -> handleScreenLoad()
            CalculatorEvent.BuyBitcoinClick -> TODO()
            CalculatorEvent.SettingsClick -> _effect.value = Event(CalculatorEffect.NavigateToSettings)
        }

    private fun handleScreenLoad() {
        _state.value = CalculatorState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val balances = balancesRepository.getBalances()
            withContext(Dispatchers.Main) {
                _state.value = CalculatorState.Success("111", "222")
            }
        }
    }
}

sealed class CalculatorState {
    object Loading : CalculatorState()
    object Error : CalculatorState()
    data class Success(
        val cryptoBalance: String,
        val fiatBalance: String
    ) : CalculatorState()
}

sealed class CalculatorEvent {
    object ScreenLoad : CalculatorEvent()
    object SettingsClick : CalculatorEvent()
    object BuyBitcoinClick : CalculatorEvent()
}

sealed class CalculatorEffect {
    object NavigateToSettings : CalculatorEffect()
}
