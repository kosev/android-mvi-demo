package net.kosev.mvidemo.ui.trade

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
import net.kosev.mvidemo.ui.Event
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

@HiltViewModel
class TradeViewModel @Inject constructor(
    private val balancesRepository: BalancesRepository,
    private val priceRepository: PriceRepository,
    private val amountFormatter: AmountFormatter
) : ViewModel() {

    private val _state = MutableLiveData<TradeState>()
    val state: LiveData<TradeState> = _state

    private val _effect = MutableLiveData<Event<TradeEffect>>()
    val effect: LiveData<Event<TradeEffect>> = _effect

    fun onEvent(event: TradeEvent): Unit =
        when (event) {
            TradeEvent.ScreenLoad -> handleScreenLoad()
            TradeEvent.BuyCryptoClick -> TODO()
            TradeEvent.SettingsClick -> handleSettingsClick()
            is TradeEvent.AmountChange -> handleAmountChange(event.value)
        }

    private fun handleScreenLoad() {
        _state.value = TradeState.Loading
        viewModelScope.launch {
            try {
                val balances = balancesRepository.getBalances()
                val cryptoBalance = amountFormatter.formatCryptoWithSymbol(balances.cryptoBalance)
                val fiatBalance = amountFormatter.formatFiatWithSymbol(balances.fiatBalance)

                val cryptoPrice = priceRepository.getCryptoPrice()
                val rate = amountFormatter.formatExchangeRate(cryptoPrice)

                _state.value = TradeState.Success(cryptoBalance, fiatBalance, cryptoPrice, rate, defaultResult())
            } catch (e: Exception) {
                _state.value = TradeState.Error
            }
        }
    }

    private fun handleSettingsClick() {
        _effect.value = Event(TradeEffect.NavigateToSettings)
    }

    private fun handleAmountChange(value: String) {
        (state.value as? TradeState.Success)?.let {
            try {
                val result = calculateNewResult(value, it.cryptoPrice)
                _state.value = it.copy(result = amountFormatter.formatCrypto(result))
            } catch (e: NumberFormatException) {
                _state.value = it.copy(result = defaultResult())
            }
        }
    }

    private fun calculateNewResult(value: String, cryptoPrice: BigDecimal): BigDecimal =
        BigDecimal(value).divide(cryptoPrice, 8, RoundingMode.HALF_UP)

    private fun defaultResult(): String = amountFormatter.formatCrypto(BigDecimal.ZERO)

    @VisibleForTesting
    fun setStateForTesting(state: TradeState) {
        _state.value = state
    }

}

sealed class TradeState {
    object Loading : TradeState()
    object Error : TradeState()
    data class Success(
        val cryptoBalance: String,
        val fiatBalance: String,
        val cryptoPrice: BigDecimal,
        val rate: String,
        val result: String
    ) : TradeState()
}

sealed class TradeEvent {
    object ScreenLoad : TradeEvent()
    object SettingsClick : TradeEvent()
    object BuyCryptoClick : TradeEvent()
    data class AmountChange(val value: String) : TradeEvent()
}

sealed class TradeEffect {
    object NavigateToSettings : TradeEffect()
}
