package net.kosev.mvidemo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val balancesRepository: BalancesRepository,
    private val rateRepository: RateRepository,
    private val amountFormatter: AmountFormatter
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
            is CalculatorEvent.AmountChange -> handleAmountChange(event.value)
        }

    private fun handleScreenLoad() {
        _state.value = CalculatorState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            val balances = balancesRepository.getBalances()
            val cryptoBalance = amountFormatter.formatCryptoWithSymbol(balances.cryptoBalance)
            val fiatBalance = amountFormatter.formatFiatWithSymbol(balances.fiatBalance)

            val cryptoPrice = rateRepository.getCryptoRate()
            val rate = amountFormatter.formatExchangeRate(cryptoPrice)

            withContext(Dispatchers.Main) {
                _state.value = CalculatorState.Success(cryptoBalance, fiatBalance, cryptoPrice, rate, "")
            }
        }
    }

    private fun handleAmountChange(value: String) {
        (state.value as? CalculatorState.Success)?.let {
            val result = BigDecimal(value).divide(it.cryptoPrice, 8, RoundingMode.HALF_UP)
            Log.d("BLA", "aa=$result")
            _state.value = it.copy(result = amountFormatter.formatCrypto(result))
        }
    }

}

sealed class CalculatorState {
    object Loading : CalculatorState()
    object Error : CalculatorState()
    data class Success(
        val cryptoBalance: String,
        val fiatBalance: String,
        val cryptoPrice: BigDecimal,
        val rate: String,
        val result: String
    ) : CalculatorState()
}

sealed class CalculatorEvent {
    object ScreenLoad : CalculatorEvent()
    object SettingsClick : CalculatorEvent()
    object BuyBitcoinClick : CalculatorEvent()
    data class AmountChange(val value: String) : CalculatorEvent()
}

sealed class CalculatorEffect {
    object NavigateToSettings : CalculatorEffect()
}
