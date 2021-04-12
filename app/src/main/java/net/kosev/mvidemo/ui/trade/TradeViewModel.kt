package net.kosev.mvidemo.ui.trade

import android.util.Log
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
            TradeEvent.BuyCryptoClick -> handleBuyCryptoClick()
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

                _state.value = TradeState.Success(
                    formattedCryptoBalance = cryptoBalance,
                    formattedFiatBalance = fiatBalance,
                    cryptoPrice = cryptoPrice,
                    formattedExchangeRate = rate,
                    amount = BigDecimal.ZERO,
                    formattedAmount = "",
                    formattedResult = defaultResult()
                )
            } catch (e: Exception) {
                _state.value = TradeState.Error
            }
        }
    }

    private fun handleBuyCryptoClick() {
        viewModelScope.launch {
            try {
                successOrNull()?.let {
                    _state.value = TradeState.Loading
                    balancesRepository.buyCrypto(it.amount, it.cryptoPrice)
                    onEvent(TradeEvent.ScreenLoad)
                }
            } catch (e: Exception) {
                Log.d("BLA", "error $e")
            }
        }
    }

    private fun handleSettingsClick() {
        _effect.value = Event(TradeEffect.NavigateToSettings)
    }

    private fun handleAmountChange(value: String) {
        successOrNull()?.let {
            try {
                val amount = BigDecimal(value)
                val result = calculateNewResult(amount, it.cryptoPrice)

                _state.value = it.copy(
                    amount = amount,
                    formattedAmount = amount.toString(),
                    formattedResult = amountFormatter.formatCrypto(result)
                )
            } catch (e: NumberFormatException) {
                _state.value = it.copy(formattedResult = defaultResult())
            }
        }
    }

    private fun calculateNewResult(amount: BigDecimal, cryptoPrice: BigDecimal): BigDecimal =
        amount.divide(cryptoPrice, 8, RoundingMode.HALF_UP)

    private fun defaultResult(): String = amountFormatter.formatCrypto(BigDecimal.ZERO)

    private fun successOrNull(): TradeState.Success? = (state.value as? TradeState.Success)

    @VisibleForTesting
    fun setStateForTesting(state: TradeState) {
        _state.value = state
    }

}

sealed class TradeState {
    object Loading : TradeState()
    object Error : TradeState()
    data class Success(
        val formattedCryptoBalance: String,
        val formattedFiatBalance: String,
        val cryptoPrice: BigDecimal,
        val formattedExchangeRate: String,
        val amount: BigDecimal,
        val formattedAmount: String,
        val formattedResult: String
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
