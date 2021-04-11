package net.kosev.mvidemo.ui.trade

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.kosev.mvidemo.ui.Event
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
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
            TradeEvent.BuyBitcoinClick -> TODO()
            TradeEvent.SettingsClick -> _effect.value = Event(TradeEffect.NavigateToSettings)
            is TradeEvent.AmountChange -> handleAmountChange(event.value)
        }

    private fun handleScreenLoad() {
        _state.value = TradeState.Loading
        viewModelScope.launch {
            val balances = balancesRepository.getBalances()
            val cryptoBalance = amountFormatter.formatCryptoWithSymbol(balances.cryptoBalance)
            val fiatBalance = amountFormatter.formatFiatWithSymbol(balances.fiatBalance)

            val cryptoPrice = priceRepository.getCryptoPrice()
            val rate = amountFormatter.formatExchangeRate(cryptoPrice)

            withContext(Dispatchers.Main) {
                _state.value = TradeState.Success(
                    cryptoBalance,
                    fiatBalance,
                    cryptoPrice,
                    rate,
                    amountFormatter.formatCrypto(BigDecimal.ZERO)
                )
            }
        }
    }

    private fun handleAmountChange(value: String) {
        (state.value as? TradeState.Success)?.let {
            try {
                val result = BigDecimal(value).divide(it.cryptoPrice, 8, RoundingMode.HALF_UP)
                _state.value = it.copy(result = amountFormatter.formatCrypto(result))
            } catch (e: NumberFormatException) {
                _state.value = it.copy(result = amountFormatter.formatCrypto(BigDecimal.ZERO))
            }
        }
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
    object BuyBitcoinClick : TradeEvent()
    data class AmountChange(val value: String) : TradeEvent()
}

sealed class TradeEffect {
    object NavigateToSettings : TradeEffect()
}
