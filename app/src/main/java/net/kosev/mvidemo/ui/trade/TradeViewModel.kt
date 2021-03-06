package net.kosev.mvidemo.ui.trade

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.kosev.mvidemo.R
import net.kosev.mvidemo.repository.BalancesRepository
import net.kosev.mvidemo.repository.PriceRepository
import net.kosev.mvidemo.ui.AmountFormatter
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

    fun setEvent(event: TradeEvent): Unit =
        when (event) {
            TradeEvent.ScreenLoad -> handleScreenLoad()
            TradeEvent.BuyCryptoClick -> handleBuyCryptoClick()
            TradeEvent.SettingsClick -> handleSettingsClick()
            is TradeEvent.AmountChange -> handleAmountChange(event.value)
        }

    private fun setState(state: TradeState) {
        _state.value = state
    }

    private fun setEffect(effect: TradeEffect) {
        _effect.value = Event(effect)
    }

    private fun handleScreenLoad() {
        viewModelScope.launch {
            try {
                setState(TradeState.Loading)

                val balances = balancesRepository.getBalances()
                val cryptoBalance = amountFormatter.formatCryptoWithSymbol(balances.cryptoBalance)
                val fiatBalance = amountFormatter.formatFiatWithSymbol(balances.fiatBalance)

                val cryptoPrice = priceRepository.getCryptoPrice()
                val rate = amountFormatter.formatExchangeRate(cryptoPrice)

                setState(TradeState.Success(
                    formattedCryptoBalance = cryptoBalance,
                    formattedFiatBalance = fiatBalance,
                    fiatBalance = balances.fiatBalance,
                    cryptoPrice = cryptoPrice,
                    formattedExchangeRate = rate,
                    amount = BigDecimal.ZERO,
                    formattedResult = defaultResult(),
                    isBuyingAllowed = false,
                    noBalanceError = null
                ))
            } catch (e: Exception) {
                setState(TradeState.Error)
            }
        }
    }

    private fun handleBuyCryptoClick() {
        viewModelScope.launch {
            try {
                successOrNull()?.let {
                    setState(TradeState.Loading)
                    balancesRepository.buyCrypto(it.amount, it.cryptoPrice)
                    setEvent(TradeEvent.ScreenLoad)
                }
            } catch (e: Exception) {
                setEffect(TradeEffect.ShowBuyError)
            }
        }
    }

    private fun handleSettingsClick() {
        setEffect(TradeEffect.NavigateToSettings)
    }

    private fun handleAmountChange(value: String) {
        successOrNull()?.let {
            try {
                val amount = BigDecimal(value)
                val result = calculateNewResult(amount, it.cryptoPrice)
                val isBalanceShort = it.fiatBalance < amount

                setState(it.copy(
                    amount = amount,
                    formattedResult = amountFormatter.formatCrypto(result),
                    isBuyingAllowed = !isBalanceShort,
                    noBalanceError = if (isBalanceShort) R.string.no_balance_error else null
                ))
            } catch (e: NumberFormatException) {
                setState(it.copy(
                    formattedResult = defaultResult(),
                    isBuyingAllowed = false,
                    noBalanceError = null
                ))
            }
        }
    }

    private fun calculateNewResult(amount: BigDecimal, cryptoPrice: BigDecimal): BigDecimal =
        amount.divide(cryptoPrice, 8, RoundingMode.HALF_UP)

    private fun defaultResult(): String =
        amountFormatter.formatCrypto(BigDecimal.ZERO)

    private fun successOrNull(): TradeState.Success? =
        (state.value as? TradeState.Success)

    @VisibleForTesting
    fun setStateForTesting(state: TradeState): Unit =
        setState(state)

}

sealed class TradeState {
    object Loading : TradeState()
    object Error : TradeState()
    data class Success(
        val formattedCryptoBalance: String,
        val formattedFiatBalance: String,
        val fiatBalance: BigDecimal,
        val cryptoPrice: BigDecimal,
        val formattedExchangeRate: String,
        val amount: BigDecimal,
        val formattedResult: String,
        val isBuyingAllowed: Boolean,
        val noBalanceError: Int?
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
    object ShowBuyError : TradeEffect()
}
